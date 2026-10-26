package com.ruoyi.crm.whatsapp.realtime;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;

/**
 * Publishes WhatsApp inbox SSE events via Redis pub/sub (with local fallback).
 */
@Component
public class WhatsAppInboxEventPublisher
{
    private static final Logger log = LoggerFactory.getLogger(WhatsAppInboxEventPublisher.class);

    public static final String CHANNEL_PREFIX = "whatsapp:inbox:";

    @Autowired
    private WhatsAppInboxSseHub sseHub;

    @Autowired(required = false)
    private StringRedisTemplate stringRedisTemplate;

    public void publishMessageCreated(String tenantId, Map<String, Object> payload)
    {
        publish(tenantId, WhatsAppInboxSsePayloads.EVENT_MESSAGE_CREATED, payload);
    }

    public void publishMessageStatus(String tenantId, Map<String, Object> payload)
    {
        publish(tenantId, WhatsAppInboxSsePayloads.EVENT_MESSAGE_STATUS, payload);
    }

    public void publishConversationUpdated(String tenantId, Map<String, Object> payload)
    {
        publish(tenantId, WhatsAppInboxSsePayloads.EVENT_CONVERSATION_UPDATED, payload);
    }

    public void publish(String tenantId, String eventType, Map<String, Object> data)
    {
        if (!StringUtils.hasText(tenantId) || !StringUtils.hasText(eventType) || data == null)
        {
            return;
        }
        Runnable task = () -> doPublish(tenantId, eventType, data);
        if (TransactionSynchronizationManager.isSynchronizationActive()
                && TransactionSynchronizationManager.isActualTransactionActive())
        {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization()
            {
                @Override
                public void afterCommit()
                {
                    task.run();
                }
            });
        }
        else
        {
            task.run();
        }
    }

    private void doPublish(String tenantId, String eventType, Map<String, Object> data)
    {
        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("event", eventType);
        envelope.put("data", data);
        String json = JSON.toJSONString(envelope);
        String channel = CHANNEL_PREFIX + tenantId;

        if (stringRedisTemplate != null)
        {
            try
            {
                stringRedisTemplate.convertAndSend(channel, json);
                return;
            }
            catch (Exception e)
            {
                log.warn("WhatsApp inbox Redis publish failed tenant={} event={}: {}; falling back to local fanout",
                        tenantId, eventType, e.getMessage());
            }
        }
        else
        {
            log.debug("WhatsApp inbox Redis unavailable; local fanout tenant={} event={}", tenantId, eventType);
        }
        sseHub.broadcast(tenantId, eventType, data);
    }
}
