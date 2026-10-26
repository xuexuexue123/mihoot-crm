package com.ruoyi.crm.webchat.realtime;

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
import com.ruoyi.crm.domain.CrmWebchatConversation;
import com.ruoyi.crm.domain.CrmWebchatMessage;

/**
 * Publishes Web Chat inbox + widget SSE events via Redis pub/sub (with local fallback).
 */
@Component
public class WebchatInboxEventPublisher
{
    private static final Logger log = LoggerFactory.getLogger(WebchatInboxEventPublisher.class);

    public static final String INBOX_CHANNEL_PREFIX = "webchat:inbox:";
    public static final String WIDGET_CHANNEL_PREFIX = "webchat:widget:";

    @Autowired
    private WebchatInboxSseHub sseHub;

    @Autowired(required = false)
    private StringRedisTemplate stringRedisTemplate;

    public void publishMessageCreated(String tenantId, CrmWebchatMessage msg, CrmWebchatConversation preview)
    {
        if (msg == null || msg.getConversationId() == null)
        {
            return;
        }
        Map<String, Object> inboxPayload = WebchatInboxSsePayloads.messageCreated(tenantId, msg, preview, true);
        Map<String, Object> widgetPayload = WebchatInboxSsePayloads.messageCreated(tenantId, msg, preview, false);
        publishInbox(tenantId, WebchatInboxSsePayloads.EVENT_MESSAGE_CREATED, inboxPayload);
        publishWidget(tenantId, msg.getConversationId(), WebchatInboxSsePayloads.EVENT_MESSAGE_CREATED, widgetPayload);
    }

    @SuppressWarnings("unchecked")
    public void publishConversationUpdated(String tenantId, Map<String, Object> payload)
    {
        publishInbox(tenantId, WebchatInboxSsePayloads.EVENT_CONVERSATION_UPDATED, payload);
        Long conversationId = null;
        if (payload != null && payload.get("conversation") instanceof Map)
        {
            Object id = ((Map<?, ?>) payload.get("conversation")).get("id");
            if (id instanceof Number)
            {
                conversationId = ((Number) id).longValue();
            }
        }
        else if (payload != null && payload.get("conversationId") instanceof Number)
        {
            conversationId = ((Number) payload.get("conversationId")).longValue();
        }
        if (conversationId != null)
        {
            Map<String, Object> widgetPayload = WebchatInboxSsePayloads.conversationUpdated(tenantId,
                    payload.get("conversation") instanceof Map
                            ? (Map<String, Object>) payload.get("conversation")
                            : payload,
                    false);
            publishWidget(tenantId, conversationId, WebchatInboxSsePayloads.EVENT_CONVERSATION_UPDATED, widgetPayload);
        }
    }

    public void publishInbox(String tenantId, String eventType, Map<String, Object> data)
    {
        if (!StringUtils.hasText(tenantId) || !StringUtils.hasText(eventType) || data == null)
        {
            return;
        }
        Runnable task = () -> doPublish(INBOX_CHANNEL_PREFIX + tenantId, tenantId, null, eventType, data, false);
        afterCommit(task);
    }

    public void publishWidget(String tenantId, Long conversationId, String eventType, Map<String, Object> data)
    {
        if (!StringUtils.hasText(tenantId) || conversationId == null || !StringUtils.hasText(eventType) || data == null)
        {
            return;
        }
        Runnable task = () -> doPublish(WIDGET_CHANNEL_PREFIX + tenantId + ":" + conversationId,
                tenantId, conversationId, eventType, data, true);
        afterCommit(task);
    }

    private void afterCommit(Runnable task)
    {
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

    private void doPublish(String channel, String tenantId, Long conversationId,
            String eventType, Map<String, Object> data, boolean widget)
    {
        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("event", eventType);
        envelope.put("data", data);
        String json = JSON.toJSONString(envelope);

        if (stringRedisTemplate != null)
        {
            try
            {
                stringRedisTemplate.convertAndSend(channel, json);
                return;
            }
            catch (Exception e)
            {
                log.warn("Webchat Redis publish failed channel={} event={}: {}; falling back to local fanout",
                        channel, eventType, e.getMessage());
            }
        }
        if (widget)
        {
            sseHub.broadcastWidget(tenantId, conversationId, eventType, data);
        }
        else
        {
            sseHub.broadcast(tenantId, eventType, data);
        }
    }
}
