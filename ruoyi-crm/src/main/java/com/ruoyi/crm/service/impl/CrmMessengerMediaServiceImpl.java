package com.ruoyi.crm.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmMessengerConfig;
import com.ruoyi.crm.domain.CrmMessengerMessage;
import com.ruoyi.crm.domain.dto.messenger.MessengerMediaFetchResult;
import com.ruoyi.crm.mapper.CrmMessengerConfigMapper;
import com.ruoyi.crm.mapper.CrmMessengerMessageMapper;
import com.ruoyi.crm.messenger.MessengerClient;
import com.ruoyi.crm.service.ICrmMessengerMediaService;
import com.ruoyi.crm.service.support.TokenEncryptor;

@Service
public class CrmMessengerMediaServiceImpl implements ICrmMessengerMediaService
{
    private static final Logger log = LoggerFactory.getLogger(CrmMessengerMediaServiceImpl.class);

    @Autowired
    private CrmMessengerConfigMapper configMapper;

    @Autowired
    private CrmMessengerMessageMapper messageMapper;

    @Autowired
    private MessengerClient messengerClient;

    @Autowired
    private TokenEncryptor tokenEncryptor;

    @Override
    public MessengerMediaFetchResult fetchByMessageId(String tenantId, Long messageId)
    {
        if (!StringUtils.hasText(tenantId))
        {
            throw new ServiceException("Missing tenant", 400);
        }
        if (messageId == null)
        {
            throw new ServiceException("messageId is required", 400);
        }
        CrmMessengerMessage owned = messageMapper.selectById(tenantId, messageId);
        if (owned == null || !StringUtils.hasText(owned.getMediaUrl()))
        {
            throw new ServiceException("Media not found", 404);
        }
        return downloadOwned(tenantId, owned);
    }

    @Override
    public String resolveStoredMediaUrl(String tenantId, Long messageId)
    {
        if (!StringUtils.hasText(tenantId) || messageId == null)
        {
            return null;
        }
        CrmMessengerMessage owned = messageMapper.selectById(tenantId, messageId);
        if (owned == null || !StringUtils.hasText(owned.getMediaUrl()))
        {
            return null;
        }
        return owned.getMediaUrl().trim();
    }

    @Override
    public MessengerMediaFetchResult fetchByMediaUrl(String tenantId, String mediaUrl)
    {
        if (!StringUtils.hasText(tenantId))
        {
            throw new ServiceException("Missing tenant", 400);
        }
        if (!StringUtils.hasText(mediaUrl) || mediaUrl.contains(".."))
        {
            throw new ServiceException("Invalid mediaUrl", 400);
        }
        String trimmed = mediaUrl.trim();
        if (!(trimmed.startsWith("https://") || trimmed.startsWith("http://")))
        {
            throw new ServiceException("Invalid mediaUrl", 400);
        }
        CrmMessengerMessage owned = messageMapper.selectByMediaUrl(tenantId, trimmed);
        if (owned == null)
        {
            throw new ServiceException("Media not found", 404);
        }
        return downloadOwned(tenantId, owned);
    }

    private MessengerMediaFetchResult downloadOwned(String tenantId, CrmMessengerMessage owned)
    {
        String mediaUrl = owned.getMediaUrl();
        String accessToken = null;
        // Meta CDN 需要 Page Token；OSS 等公网 URL 不需要（也不应强制配置存在）。
        if (MessengerClient.isMetaMediaHost(mediaUrl))
        {
            CrmMessengerConfig config = configMapper.selectByTenantId(tenantId);
            if (config == null || !StringUtils.hasText(config.getAccessTokenEnc()))
            {
                throw new ServiceException("Messenger configuration not found", 400);
            }
            accessToken = tokenEncryptor.decrypt(config.getAccessTokenEnc());
        }
        MessengerMediaFetchResult result = messengerClient.downloadMediaUrl(mediaUrl, accessToken);
        if (!result.isSuccess())
        {
            log.warn("Messenger media download failed tenant={} messageId={} err={}",
                    tenantId, owned.getId(), result.getErrorMessage());
            return result;
        }
        String mime = result.getContentType();
        if (!StringUtils.hasText(mime) && StringUtils.hasText(owned.getMediaMimeType()))
        {
            mime = owned.getMediaMimeType();
        }
        return MessengerMediaFetchResult.success(mime, result.getBody(), owned.getFilename());
    }
}
