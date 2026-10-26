package com.ruoyi.crm.wechatmp.realtime;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConversation;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessage;
import com.ruoyi.crm.wechatmp.service.impl.WechatMpConversationServiceImpl;

/**
 * Builds SSE {@code data} JSON maps for WeChat MP inbox (appId + conversationId required).
 */
public final class WechatMpInboxSsePayloads
{
    public static final String EVENT_MESSAGE_CREATED = "message.created";
    public static final String EVENT_MESSAGE_STATUS = "message.status";
    public static final String EVENT_CONVERSATION_UPDATED = "conversation.updated";
    public static final String EVENT_PING = "ping";

    private WechatMpInboxSsePayloads()
    {
    }

    public static Map<String, Object> ping()
    {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("type", EVENT_PING);
        data.put("ts", System.currentTimeMillis());
        return data;
    }

    public static Map<String, Object> messageCreated(CrmWechatMpMessage msg, CrmWechatMpConversation conv)
    {
        String appId = msg != null ? msg.getAppId() : (conv != null ? conv.getAppId() : null);
        Long conversationId = conv != null ? conv.getId() : null;
        Map<String, Object> data = baseEnvelope(appId, conversationId);
        data.put("type", EVENT_MESSAGE_CREATED);
        data.put("message", toMessageRecord(msg, conversationId));
        if (conv != null && conv.getId() != null)
        {
            data.put("conversation", toConversationPartial(conv));
        }
        return data;
    }

    public static Map<String, Object> messageStatus(String appId, Long conversationId, String messageId,
            String status, String errorCode, String errorMessage)
    {
        Map<String, Object> data = baseEnvelope(appId, conversationId);
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

    public static Map<String, Object> conversationUpdated(CrmWechatMpConversation conv)
    {
        Map<String, Object> partial = toConversationPartial(conv);
        return conversationUpdated(conv != null ? conv.getAppId() : null, partial);
    }

    public static Map<String, Object> conversationUpdated(String appId, Map<String, Object> conversationPartial)
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
        if (!StringUtils.hasText(appId) && conversationPartial != null
                && conversationPartial.get("appId") != null)
        {
            appId = String.valueOf(conversationPartial.get("appId"));
        }
        Map<String, Object> data = baseEnvelope(appId, conversationId);
        data.put("type", EVENT_CONVERSATION_UPDATED);
        data.put("conversation", conversationPartial != null ? conversationPartial : new LinkedHashMap<>());
        return data;
    }

    public static Map<String, Object> toMessageRecord(CrmWechatMpMessage msg, Long conversationId)
    {
        Map<String, Object> m = new LinkedHashMap<>();
        if (msg == null)
        {
            return m;
        }
        m.put("id", msg.getId());
        m.put("conversationId", conversationId);
        m.put("appId", msg.getAppId());
        m.put("openId", msg.getOpenId());
        m.put("direction", msg.getDirection());
        m.put("msgType", msg.getMsgType());
        m.put("content", msg.getContent());
        m.put("mediaId", msg.getMediaId());
        m.put("picUrl", msg.getPicUrl());
        m.put("templateId", msg.getTemplateId());
        m.put("wxMsgId", msg.getWxMsgId());
        m.put("source", msg.getSource());
        m.put("createTime", msg.getCreateTime());
        if (StringUtils.hasText(msg.getMediaId()) && StringUtils.hasText(msg.getAppId()))
        {
            m.put("mediaUrl", "/crm/wechat_mp/messages/media?appId=" + msg.getAppId()
                    + "&mediaId=" + msg.getMediaId());
        }
        return m;
    }

    public static Map<String, Object> toConversationPartial(CrmWechatMpConversation conv)
    {
        Map<String, Object> c = new LinkedHashMap<>();
        if (conv == null)
        {
            return c;
        }
        c.put("id", conv.getId());
        c.put("appId", conv.getAppId());
        if (StringUtils.hasText(conv.getOpenId()))
        {
            c.put("openId", conv.getOpenId());
        }
        if (conv.getLastMessage() != null)
        {
            c.put("lastMessage", conv.getLastMessage());
        }
        if (conv.getLastMessageAt() != null)
        {
            c.put("lastMessageAt", conv.getLastMessageAt());
        }
        if (conv.getUnreadCount() != null)
        {
            c.put("unreadCount", conv.getUnreadCount());
        }
        if (conv.getMessageCount() != null)
        {
            c.put("messageCount", conv.getMessageCount());
        }
        if (conv.getLastCustomerMessageAt() != null)
        {
            c.put("lastCustomerMessageAt", conv.getLastCustomerMessageAt());
            c.put("messagingWindowRemainingSec",
                    WechatMpConversationServiceImpl.calcMessagingWindowRemaining(conv.getLastCustomerMessageAt()));
        }
        c.put("userId", conv.getUserId() != null ? String.valueOf(conv.getUserId()) : null);
        c.put("owner", conv.getOwner());
        c.put("assignedAt", conv.getAssignedAt());
        c.put("hostedByAi", conv.getHostedByAi());
        if (conv.getContactId() != null)
        {
            c.put("contactId", conv.getContactId());
        }
        if (conv.getLeadId() != null)
        {
            c.put("leadId", conv.getLeadId());
        }
        return c;
    }

    public static Map<String, Object> conversationPartialFrom(Long id, String appId, Integer unreadCount,
            Long userId, String owner, Date assignedAt)
    {
        return conversationPartialFrom(id, appId, unreadCount, userId, owner, assignedAt, null);
    }

    public static Map<String, Object> conversationPartialFrom(Long id, String appId, Integer unreadCount,
            Long userId, String owner, Date assignedAt, Boolean hostedByAi)
    {
        Map<String, Object> c = new LinkedHashMap<>();
        c.put("id", id);
        c.put("appId", appId);
        if (unreadCount != null)
        {
            c.put("unreadCount", unreadCount);
        }
        c.put("userId", userId != null ? String.valueOf(userId) : null);
        c.put("owner", owner);
        c.put("assignedAt", assignedAt);
        c.put("hostedByAi", hostedByAi);
        return c;
    }

    /** Extract appId from payload for hub visibility filter. */
    public static String extractAppId(Object data)
    {
        if (!(data instanceof Map))
        {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) data;
        Object appId = map.get("appId");
        if (appId != null && StringUtils.hasText(String.valueOf(appId)))
        {
            return String.valueOf(appId);
        }
        Object conv = map.get("conversation");
        if (conv instanceof Map)
        {
            Object a = ((Map<?, ?>) conv).get("appId");
            if (a != null)
            {
                return String.valueOf(a);
            }
        }
        Object msg = map.get("message");
        if (msg instanceof Map)
        {
            Object a = ((Map<?, ?>) msg).get("appId");
            if (a != null)
            {
                return String.valueOf(a);
            }
        }
        return null;
    }

    private static Map<String, Object> baseEnvelope(String appId, Long conversationId)
    {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("appId", appId);
        data.put("conversationId", conversationId);
        data.put("ts", System.currentTimeMillis());
        return data;
    }
}
