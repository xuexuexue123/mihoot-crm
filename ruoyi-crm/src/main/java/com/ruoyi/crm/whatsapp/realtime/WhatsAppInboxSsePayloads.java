package com.ruoyi.crm.whatsapp.realtime;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.ruoyi.crm.domain.CrmWhatsappConversation;
import com.ruoyi.crm.domain.CrmWhatsappMessage;
import com.ruoyi.crm.service.impl.CrmWhatsappConversationServiceImpl;

/**
 * Builds SSE {@code data} JSON maps aligned with frontend WhatsApp inbox types.
 */
public final class WhatsAppInboxSsePayloads
{
    public static final String EVENT_MESSAGE_CREATED = "message.created";
    public static final String EVENT_MESSAGE_STATUS = "message.status";
    public static final String EVENT_CONVERSATION_UPDATED = "conversation.updated";
    public static final String EVENT_PING = "ping";

    private WhatsAppInboxSsePayloads()
    {
    }

    public static Map<String, Object> ping()
    {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("type", EVENT_PING);
        data.put("ts", System.currentTimeMillis());
        return data;
    }

    public static Map<String, Object> messageCreated(String tenantId, CrmWhatsappMessage msg,
            CrmWhatsappConversation conversationPreview)
    {
        Map<String, Object> data = baseEnvelope(tenantId, msg != null ? msg.getConversationId() : null);
        data.put("type", EVENT_MESSAGE_CREATED);
        data.put("message", toMessageRecord(msg));
        if (conversationPreview != null && conversationPreview.getId() != null)
        {
            data.put("conversation", toConversationPartial(conversationPreview));
        }
        return data;
    }

    public static Map<String, Object> messageStatus(String tenantId, Long conversationId, String messageId,
            String status, String errorCode, String errorMessage)
    {
        Map<String, Object> data = baseEnvelope(tenantId, conversationId);
        data.put("type", EVENT_MESSAGE_STATUS);
        data.put("messageId", messageId);
        data.put("status", status);
        if (errorCode != null)
        {
            data.put("errorCode", errorCode);
        }
        if (errorMessage != null)
        {
            data.put("errorMessage", errorMessage);
        }
        return data;
    }

    public static Map<String, Object> conversationUpdated(String tenantId, Map<String, Object> conversationPartial)
    {
        Long conversationId = null;
        if (conversationPartial != null && conversationPartial.get("id") != null)
        {
            Object id = conversationPartial.get("id");
            if (id instanceof Number)
            {
                conversationId = ((Number) id).longValue();
            }
        }
        Map<String, Object> data = baseEnvelope(tenantId, conversationId);
        data.put("type", EVENT_CONVERSATION_UPDATED);
        data.put("conversation", conversationPartial != null ? conversationPartial : new LinkedHashMap<>());
        return data;
    }

    public static Map<String, Object> toMessageRecord(CrmWhatsappMessage msg)
    {
        Map<String, Object> m = new LinkedHashMap<>();
        if (msg == null)
        {
            return m;
        }
        m.put("id", msg.getId());
        m.put("conversationId", msg.getConversationId());
        m.put("waId", msg.getWaId());
        m.put("messageId", msg.getMessageId());
        m.put("messageType", msg.getMessageType());
        m.put("content", msg.getContent());
        m.put("rawJson", msg.getRawJson());
        m.put("direction", msg.getDirection());
        m.put("sendTime", msg.getSendTime());
        m.put("status", msg.getStatus());
        m.put("mediaId", msg.getMediaId());
        m.put("mediaUrl", CrmWhatsappConversationServiceImpl.resolveDisplayMediaUrl(msg));
        m.put("mediaMimeType", msg.getMediaMimeType());
        m.put("filename", msg.getFilename());
        m.put("source", msg.getSource());
        m.put("createdAt", msg.getCreateTime());
        return m;
    }

    public static Map<String, Object> toConversationPartial(CrmWhatsappConversation conv)
    {
        Map<String, Object> c = new LinkedHashMap<>();
        if (conv == null)
        {
            return c;
        }
        c.put("id", conv.getId());
        if (StringUtils.hasText(conv.getWaId()))
        {
            c.put("waId", conv.getWaId());
        }
        if (conv.getLastMessagePreview() != null)
        {
            c.put("lastMessagePreview", conv.getLastMessagePreview());
        }
        if (conv.getLastMessageAt() != null)
        {
            c.put("lastMessageAt", conv.getLastMessageAt());
        }
        if (conv.getUnreadCount() != null)
        {
            c.put("unreadCount", conv.getUnreadCount());
        }
        if (StringUtils.hasText(conv.getStatus()))
        {
            c.put("status", conv.getStatus());
        }
        if (conv.getLastOutboundAt() != null)
        {
            c.put("lastOutboundAt", conv.getLastOutboundAt());
        }
        if (conv.getLastCustomerMessageAt() != null)
        {
            c.put("lastCustomerMessageAt", conv.getLastCustomerMessageAt());
        }
        c.put("userId", conv.getUserId());
        c.put("owner", conv.getOwner());
        c.put("assignedAt", conv.getAssignedAt());
        c.put("hostedByAi", conv.getHostedByAi());
        return c;
    }

    public static Map<String, Object> conversationPartialFrom(Long id, Integer unreadCount, String status)
    {
        return conversationPartialFrom(id, unreadCount, status, null, null, null, null);
    }

    public static Map<String, Object> conversationPartialFrom(Long id, Integer unreadCount, String status,
            Long userId, String owner, Date assignedAt)
    {
        return conversationPartialFrom(id, unreadCount, status, userId, owner, assignedAt, null);
    }

    public static Map<String, Object> conversationPartialFrom(Long id, Integer unreadCount, String status,
            Long userId, String owner, Date assignedAt, Boolean hostedByAi)
    {
        Map<String, Object> c = new LinkedHashMap<>();
        c.put("id", id);
        if (unreadCount != null)
        {
            c.put("unreadCount", unreadCount);
        }
        if (status != null)
        {
            c.put("status", status);
        }
        c.put("userId", userId);
        c.put("owner", owner);
        c.put("assignedAt", assignedAt);
        c.put("hostedByAi", hostedByAi);
        return c;
    }

    private static Map<String, Object> baseEnvelope(String tenantId, Long conversationId)
    {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("tenantId", tenantId);
        data.put("ts", System.currentTimeMillis());
        data.put("conversationId", conversationId);
        return data;
    }

    /** Format dates consistently for JSON (ISO-like via Fastjson Date default is fine; leave as Date). */
    @SuppressWarnings("unused")
    private static Object dateOrNull(Date d)
    {
        return d;
    }
}
