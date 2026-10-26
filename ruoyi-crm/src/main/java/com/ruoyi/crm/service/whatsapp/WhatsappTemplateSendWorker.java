package com.ruoyi.crm.service.whatsapp;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.ruoyi.crm.domain.CrmWhatsappConfig;
import com.ruoyi.crm.domain.CrmWhatsappConversation;
import com.ruoyi.crm.domain.CrmWhatsappMessage;
import com.ruoyi.crm.domain.CrmWhatsappTemplateSendItem;
import com.ruoyi.crm.domain.CrmWhatsappTemplateSendTask;
import com.ruoyi.crm.domain.dto.whatsapp.WhatsAppSendResult;
import com.ruoyi.crm.mapper.CrmWhatsappConfigMapper;
import com.ruoyi.crm.mapper.CrmWhatsappConversationMapper;
import com.ruoyi.crm.mapper.CrmWhatsappMessageMapper;
import com.ruoyi.crm.mapper.CrmWhatsappTemplateMapper;
import com.ruoyi.crm.mapper.CrmWhatsappTemplateSendItemMapper;
import com.ruoyi.crm.mapper.CrmWhatsappTemplateSendTaskMapper;
import com.ruoyi.crm.domain.CrmWhatsappTemplate;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.crm.service.support.TokenEncryptor;
import com.ruoyi.crm.whatsapp.WhatsAppClient;
import com.ruoyi.crm.whatsapp.WhatsappTemplateMessageStore;
import com.ruoyi.crm.whatsapp.realtime.WhatsAppInboxEventPublisher;
import com.ruoyi.crm.whatsapp.realtime.WhatsAppInboxSsePayloads;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.manager.AsyncManager;

/**
 * 营销模板群发 Worker：限并发调用 Meta，落库 message / 更新 item，不校验会话负责人。
 */
@Component
public class WhatsappTemplateSendWorker
{
    private static final Logger log = LoggerFactory.getLogger(WhatsappTemplateSendWorker.class);

    private static final int MAX_CONCURRENCY = 10;

    @Autowired
    private CrmWhatsappTemplateSendTaskMapper taskMapper;

    @Autowired
    private CrmWhatsappTemplateSendItemMapper itemMapper;

    @Autowired
    private CrmWhatsappConfigMapper configMapper;

    @Autowired
    private CrmWhatsappConversationMapper conversationMapper;

    @Autowired
    private CrmWhatsappMessageMapper messageMapper;

    @Autowired
    private CrmWhatsappTemplateMapper templateMapper;

    @Autowired
    private WhatsAppClient whatsappClient;

    @Autowired
    private TokenEncryptor tokenEncryptor;

    @Autowired
    private WhatsAppInboxEventPublisher inboxEventPublisher;

    public void sendAsync(Long taskId, String tenantKey)
    {
        AsyncManager.me().execute(new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    TenantDataSourceSupport.bindTenantKey(tenantKey);
                    send(taskId, tenantKey);
                }
                catch (Exception ex)
                {
                    log.error("WhatsApp template send worker failed taskId={} tenant={}",
                            taskId, tenantKey, ex);
                    try
                    {
                        TenantDataSourceSupport.bindTenantKey(tenantKey);
                        taskMapper.markFinished(taskId, "failed",
                                truncate(ex.getMessage(), 500), new Date(), null, null, null);
                    }
                    catch (Exception ignore)
                    {
                        // ignore secondary failure
                    }
                }
                finally
                {
                    DynamicDataSourceContextHolder.clearDataSourceKey();
                }
            }
        });
    }

    public void send(Long taskId, String tenantKey)
    {
        CrmWhatsappTemplateSendTask task = taskMapper.selectById(taskId, tenantKey);
        if (task == null)
        {
            log.warn("WhatsApp send task not found taskId={} tenant={}", taskId, tenantKey);
            return;
        }

        Date now = new Date();
        int claimed = taskMapper.claimPending(taskId, tenantKey, now);
        if (claimed == 0)
        {
            log.info("WhatsApp send task already claimed or not pending taskId={} status={}",
                    taskId, task.getStatus());
            return;
        }

        CrmWhatsappConfig config = configMapper.selectByTenantId(tenantKey);
        if (config == null || config.getAccessTokenEnc() == null)
        {
            taskMapper.markFinished(taskId, "failed", "WhatsApp configuration not found",
                    new Date(), null, null, null);
            return;
        }
        String accessToken = tokenEncryptor.decrypt(config.getAccessTokenEnc());
        String phoneNumberId = task.getPhoneNumberId() != null && !task.getPhoneNumberId().isEmpty()
                ? task.getPhoneNumberId() : config.getPhoneNumberId();

        List<CrmWhatsappTemplateSendItem> pending = itemMapper.selectPendingByTaskId(taskId);
        AtomicInteger success = new AtomicInteger(task.getSuccessCount() != null ? task.getSuccessCount() : 0);
        AtomicInteger failed = new AtomicInteger(task.getFailedCount() != null ? task.getFailedCount() : 0);
        int skipped = task.getSkippedCount() != null ? task.getSkippedCount() : 0;

        if (pending.isEmpty())
        {
            taskMapper.markFinished(taskId, "completed", null, new Date(),
                    success.get(), failed.get(), skipped);
            return;
        }

        Semaphore semaphore = new Semaphore(MAX_CONCURRENCY);
        ExecutorService pool = Executors.newFixedThreadPool(Math.min(MAX_CONCURRENCY, pending.size()));
        try
        {
            for (CrmWhatsappTemplateSendItem item : pending)
            {
                pool.submit(() ->
                {
                    try
                    {
                        semaphore.acquire();
                        try
                        {
                            TenantDataSourceSupport.bindTenantKey(tenantKey);
                            sendOne(task, item, phoneNumberId, accessToken, success, failed);
                        }
                        finally
                        {
                            DynamicDataSourceContextHolder.clearDataSourceKey();
                            semaphore.release();
                        }
                    }
                    catch (InterruptedException ie)
                    {
                        Thread.currentThread().interrupt();
                        itemMapper.updateSendResult(item.getId(), "failed", null, "interrupted",
                                "Worker interrupted");
                        failed.incrementAndGet();
                    }
                    catch (Exception e)
                    {
                        log.error("WhatsApp send item failed taskId={} itemId={} waId={}",
                                taskId, item.getId(), item.getWaId(), e);
                        itemMapper.updateSendResult(item.getId(), "failed", null, "exception",
                                truncate(e.getMessage(), 500));
                        failed.incrementAndGet();
                    }
                });
            }
        }
        finally
        {
            pool.shutdown();
            try
            {
                if (!pool.awaitTermination(30, TimeUnit.MINUTES))
                {
                    pool.shutdownNow();
                }
            }
            catch (InterruptedException e)
            {
                pool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        TenantDataSourceSupport.bindTenantKey(tenantKey);
        taskMapper.markFinished(taskId, "completed", null, new Date(),
                success.get(), failed.get(), skipped);
        log.info("WhatsApp template send task done taskId={} success={} failed={} skipped={}",
                taskId, success.get(), failed.get(), skipped);
    }

    private void sendOne(CrmWhatsappTemplateSendTask task, CrmWhatsappTemplateSendItem item,
                         String phoneNumberId, String accessToken,
                         AtomicInteger success, AtomicInteger failed)
    {
        JSONArray comps = null;
        if (task.getComponents() != null && !task.getComponents().isEmpty())
        {
            comps = JSON.parseArray(task.getComponents());
        }

        WhatsAppSendResult result = whatsappClient.sendTemplateMessage(
                phoneNumberId, item.getWaId(), task.getTemplateName(), task.getLanguage(),
                comps, accessToken);

        if (!result.isSuccess())
        {
            itemMapper.updateSendResult(item.getId(), "failed", null,
                    null, truncate(result.getErrorMessage(), 500));
            failed.incrementAndGet();
            return;
        }

        Date now = new Date();
        CrmWhatsappConversation conv = ensureConversation(task.getTenantId(), item.getWaId(),
                phoneNumberId, item.getContactId(), now, task.getTemplateName());

        CrmWhatsappTemplate cached = templateMapper.selectByTemplate(
                task.getTenantId(), task.getTemplateName(), task.getLanguage());
        WhatsappTemplateMessageStore.StoredTemplate stored = WhatsappTemplateMessageStore.build(
                null,
                task.getTemplateName(),
                task.getLanguage(),
                comps,
                cached != null ? cached.getComponents() : null);

        CrmWhatsappMessage msg = new CrmWhatsappMessage();
        msg.setTenantId(task.getTenantId());
        msg.setConversationId(conv.getId());
        msg.setWaId(item.getWaId());
        msg.setMessageId(result.getMessageId());
        msg.setMessageType("template");
        msg.setContent(stored.content);
        msg.setRawJson(stored.rawJson);
        msg.setDirection(1);
        msg.setSendTime(System.currentTimeMillis());
        msg.setStatus("sent");
        msg.setCreateTime(now);
        messageMapper.insert(msg);

        CrmWhatsappConversation updated = new CrmWhatsappConversation();
        updated.setId(conv.getId());
        updated.setWaId(item.getWaId());
        updated.setLastMessageAt(now);
        updated.setLastMessagePreview(stored.preview);
        updated.setLastOutboundAt(now);
        conversationMapper.updateLastMessage(updated);

        CrmWhatsappConversation preview = conversationMapper.selectByWaId(task.getTenantId(), item.getWaId());
        if (preview == null)
        {
            preview = conv;
            preview.setLastMessageAt(now);
            preview.setLastMessagePreview(stored.preview);
            preview.setLastOutboundAt(now);
        }
        inboxEventPublisher.publishMessageCreated(task.getTenantId(),
                WhatsAppInboxSsePayloads.messageCreated(task.getTenantId(), msg, preview));

        itemMapper.updateSendResult(item.getId(), "sent", result.getMessageId(), null, null);
        success.incrementAndGet();
    }

    /**
     * 无会话则 upsert 进公共池（user_id 保持 null），便于 Inbox 可见。
     */
    private CrmWhatsappConversation ensureConversation(String tenantId, String waId,
                                                       String phoneNumberId, String contactId,
                                                       Date now, String templateName)
    {
        CrmWhatsappConversation existing = conversationMapper.selectByWaId(tenantId, waId);
        if (existing != null)
        {
            return existing;
        }

        CrmWhatsappConversation row = new CrmWhatsappConversation();
        row.setTenantId(tenantId);
        row.setWaId(waId);
        row.setPhoneNumber(waId);
        row.setPhoneNumberId(phoneNumberId);
        row.setContactId(contactId);
        row.setLastMessageAt(now);
        row.setLastMessagePreview(templateName != null ? templateName : "[模板]");
        row.setLastOutboundAt(now);
        row.setUnreadCount(0);
        row.setStatus("active");
        row.setCreateTime(now);
        row.setUpdateTime(now);
        conversationMapper.upsertConversation(row);

        CrmWhatsappConversation created = conversationMapper.selectByWaId(tenantId, waId);
        if (created == null)
        {
            throw new IllegalStateException("Failed to upsert WhatsApp conversation for waId=" + waId);
        }
        return created;
    }

    private static String truncate(String s, int max)
    {
        if (s == null)
        {
            return null;
        }
        return s.length() <= max ? s : s.substring(0, max);
    }
}
