package com.ruoyi.crm.service.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import com.ruoyi.crm.domain.CrmWhatsappConfig;
import com.ruoyi.crm.domain.CrmWhatsappMessage;
import com.ruoyi.crm.domain.dto.whatsapp.WhatsAppMediaFetchResult;
import com.ruoyi.crm.mapper.CrmWhatsappConfigMapper;
import com.ruoyi.crm.mapper.CrmWhatsappMessageMapper;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.crm.service.support.TokenEncryptor;
import com.ruoyi.crm.whatsapp.WhatsAppClient;
import com.ruoyi.crm.whatsapp.WhatsAppMediaPersistBffClient;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

/**
 * Webhook 入站媒体：事务提交后异步 Meta 下载 → Next BFF（租户 AliOSS）→ 回写 media_url。
 */
@Service
public class CrmWhatsappInboundMediaPersistService
{
    private static final Logger log = LoggerFactory.getLogger(CrmWhatsappInboundMediaPersistService.class);

    private static final Set<String> MEDIA_TYPES = new HashSet<>(Arrays.asList(
            "image", "document", "video", "audio", "sticker"));

    @Autowired
    private CrmWhatsappMessageMapper messageMapper;

    @Autowired
    private CrmWhatsappConfigMapper configMapper;

    @Autowired
    private WhatsAppClient whatsappClient;

    @Autowired
    private TokenEncryptor tokenEncryptor;

    @Autowired
    private WhatsAppMediaPersistBffClient mediaPersistBffClient;

    @Autowired
    @Qualifier("whatsappMediaTaskExecutor")
    private Executor whatsappMediaTaskExecutor;

    public static boolean isPersistableMediaType(String messageType)
    {
        return messageType != null && MEDIA_TYPES.contains(messageType.trim().toLowerCase());
    }

    /**
     * 若当前有事务则 afterCommit 再入队，避免异步线程读不到刚插入行。
     */
    public void schedulePersistAfterCommit(String tenantId, Long messagePk, String mediaId,
                                           String filename, String mimeType)
    {
        if (!StringUtils.hasText(tenantId) || messagePk == null || !StringUtils.hasText(mediaId))
        {
            return;
        }
        final String t = tenantId.trim();
        final Long id = messagePk;
        final String mid = mediaId.trim();
        final String fn = filename;
        final String mime = mimeType;

        Runnable enqueue = () -> schedulePersist(t, id, mid, fn, mime);

        if (TransactionSynchronizationManager.isSynchronizationActive())
        {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization()
            {
                @Override
                public void afterCommit()
                {
                    enqueue.run();
                }
            });
        }
        else
        {
            enqueue.run();
        }
    }

    public void schedulePersist(String tenantId, Long messagePk, String mediaId,
                                String filename, String mimeType)
    {
        if (!mediaPersistBffClient.isReady())
        {
            log.warn("WhatsApp media persist skipped (disabled or next-app-url/bff-secret missing) tenant={} mediaId={}",
                    tenantId, mediaId);
            return;
        }
        whatsappMediaTaskExecutor.execute(() -> persistSafely(tenantId, messagePk, mediaId, filename, mimeType));
    }

    void persistSafely(String tenantId, Long messagePk, String mediaId,
                       String filename, String mimeType)
    {
        try
        {
            TenantDataSourceSupport.bindTenantKey(tenantId);
            persist(tenantId, messagePk, mediaId, filename, mimeType);
        }
        catch (Exception e)
        {
            log.error("WhatsApp inbound media persist failed tenant={} messagePk={} mediaId={}: {}",
                    tenantId, messagePk, mediaId, e.getMessage(), e);
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    void persist(String tenantId, Long messagePk, String mediaId,
                 String filename, String mimeType)
    {
        CrmWhatsappMessage row = messageMapper.selectById(tenantId, messagePk);
        if (row == null)
        {
            log.warn("WhatsApp media persist: message not found tenant={} id={}", tenantId, messagePk);
            return;
        }
        if (isDurableMediaUrl(row.getMediaUrl()))
        {
            log.debug("WhatsApp media persist idempotent skip tenant={} id={}", tenantId, messagePk);
            return;
        }

        CrmWhatsappConfig config = configMapper.selectByTenantId(tenantId);
        if (config == null || config.getAccessTokenEnc() == null)
        {
            log.warn("WhatsApp media persist: config missing tenant={}", tenantId);
            return;
        }
        String accessToken = tokenEncryptor.decrypt(config.getAccessTokenEnc());
        WhatsAppMediaFetchResult downloaded = whatsappClient.downloadMedia(mediaId, accessToken);
        if (!downloaded.isSuccess() || downloaded.getBody() == null || downloaded.getBody().length == 0)
        {
            log.warn("WhatsApp media persist: Meta download failed tenant={} mediaId={} err={}",
                    tenantId, mediaId, downloaded.getErrorMessage());
            return;
        }

        String contentType = StringUtils.hasText(downloaded.getContentType())
                ? downloaded.getContentType()
                : (StringUtils.hasText(mimeType) ? mimeType : row.getMediaMimeType());
        String name = StringUtils.hasText(filename) ? filename
                : (StringUtils.hasText(row.getFilename()) ? row.getFilename() : "file");

        String durableUrl = mediaPersistBffClient.upload(tenantId, downloaded.getBody(), contentType, name);
        int updated = messageMapper.updateMediaUrl(tenantId, messagePk, durableUrl);
        log.info("WhatsApp media persist done tenant={} messagePk={} mediaId={} updated={}",
                tenantId, messagePk, mediaId, updated);
    }

    /**
     * 已有非代理 https URL 则视为已持久化。
     */
    static boolean isDurableMediaUrl(String mediaUrl)
    {
        if (!StringUtils.hasText(mediaUrl))
        {
            return false;
        }
        String u = mediaUrl.trim();
        if (u.contains("/crm/whatsapp/media/"))
        {
            return false;
        }
        return u.startsWith("http://") || u.startsWith("https://");
    }
}
