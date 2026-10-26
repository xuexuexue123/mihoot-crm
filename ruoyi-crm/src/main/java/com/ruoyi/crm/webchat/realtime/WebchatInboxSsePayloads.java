package com.ruoyi.crm.webchat.realtime;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.ruoyi.crm.domain.CrmWebchatConversation;
import com.ruoyi.crm.domain.CrmWebchatMessage;

/**
 * Builds SSE data maps for Web Chat inbox / widget.
 */
public final class WebchatInboxSsePayloads
{
    public static final String EVENT_MESSAGE_CREATED = "message.created";
    public static final String EVENT_MESSAGE_STATUS = "message.status";
    public static final String EVENT_CONVERSATION_UPDATED = "conversation.updated";
    public static final String EVENT_PING = "ping";
    public static final String EVENT_ERROR = "error";

    private WebchatInboxSsePayloads()
    {
    }

    public static Map<String, Object> ping()
    {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("type", EVENT_PING);
        data.put("ts", System.currentTimeMillis());
        return data;
    }

    public static Map<String, Object> messageCreated(String tenantId, CrmWebchatMessage msg,
            CrmWebchatConversation conversationPreview, boolean includeTenantId)
    {
        Map<String, Object> data = baseEnvelope(tenantId, msg != null ? msg.getConversationId() : null,
                includeTenantId);
        data.put("type", EVENT_MESSAGE_CREATED);
        data.put("message", toMessageRecord(msg));
        if (conversationPreview != null && conversationPreview.getId() != null)
        {
            data.put("conversation", toConversationPartial(conversationPreview));
        }
        return data;
    }

    public static Map<String, Object> conversationUpdated(String tenantId, Map<String, Object> conversationPartial,
            boolean includeTenantId)
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
        Map<String, Object> data = baseEnvelope(tenantId, conversationId, includeTenantId);
        data.put("type", EVENT_CONVERSATION_UPDATED);
        data.put("conversation", conversationPartial != null ? conversationPartial : new LinkedHashMap<>());
        return data;
    }

    public static Map<String, Object> toMessageRecord(CrmWebchatMessage msg)
    {
        Map<String, Object> m = new LinkedHashMap<>();
        if (msg == null)
        {
            return m;
        }
        m.put("id", msg.getId());
        m.put("conversationId", msg.getConversationId());
        m.put("visitorId", msg.getVisitorId());
        m.put("clientMessageId", msg.getClientMessageId());
        m.put("messageType", msg.getMessageType());
        m.put("content", msg.getContent());
        m.put("direction", msg.getDirection());
        m.put("senderType", msg.getSenderType());
        m.put("status", msg.getStatus());
        m.put("errorMessage", msg.getErrorMessage());
        m.put("agentUserId", msg.getAgentUserId());
        m.put("source", msg.getSource());
        m.put("createdAt", msg.getCreateTime());
        return m;
    }

    public static Map<String, Object> toConversationPartial(CrmWebchatConversation conv)
    {
        Map<String, Object> c = new LinkedHashMap<>();
        if (conv == null)
        {
            return c;
        }
        c.put("id", conv.getId());
        if (StringUtils.hasText(conv.getVisitorId()))
        {
            c.put("visitorId", conv.getVisitorId());
        }
        if (StringUtils.hasText(conv.getProfileName()))
        {
            c.put("profileName", conv.getProfileName());
        }
        if (StringUtils.hasText(conv.getContactId()))
        {
            c.put("contactId", conv.getContactId());
        }
        if (StringUtils.hasText(conv.getLeadId()))
        {
            c.put("leadId", conv.getLeadId());
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

    private static Map<String, Object> baseEnvelope(String tenantId, Long conversationId, boolean includeTenantId)
    {
        Map<String, Object> data = new LinkedHashMap<>();
        if (includeTenantId)
        {
            data.put("tenantId", tenantId);
        }
        data.put("ts", System.currentTimeMillis());
        data.put("conversationId", conversationId);
        return data;
    }
}
