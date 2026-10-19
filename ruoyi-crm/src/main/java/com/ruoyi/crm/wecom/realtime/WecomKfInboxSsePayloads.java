package com.ruoyi.crm.wecom.realtime;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.ruoyi.crm.domain.CrmWecomKfConversation;
import com.ruoyi.crm.domain.CrmWecomKfMessage;
import com.ruoyi.crm.service.IInboxAgentChannelService;

/**
 * Builds SSE {@code data} JSON maps for WeCom KF inbox (openKfid + conversationId required).
 */
public final class WecomKfInboxSsePayloads
{
    public static final String EVENT_MESSAGE_CREATED = "message.created";
    public static final String EVENT_MESSAGE_STATUS = "message.status";
    public static final String EVENT_CONVERSATION_UPDATED = "conversation.updated";
    public static final String EVENT_PING = "ping";

    private WecomKfInboxSsePayloads()
    {
    }

    public static Map<String, Object> ping()
    {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("type", EVENT_PING);
        data.put("ts", System.currentTimeMillis());
        return data;
    }

    public static Map<String, Object> messageCreated(CrmWecomKfMessage msg, CrmWecomKfConversation conv)
    {
        String openKfid = msg != null ? msg.getOpenKfid() : (conv != null ? conv.getOpenKfid() : null);
        Long conversationId = conv != null ? conv.getId() : (msg != null ? msg.getConversationId() : null);
        Map<String, Object> data = baseEnvelope(openKfid, conversationId);
        data.put("type", EVENT_MESSAGE_CREATED);
        data.put("message", toMessageRecord(msg, conversationId));
        if (conv != null && conv.getId() != null)
        {
            data.put("conversation", toConversationPartial(conv));
        }
        return data;
    }

    public static Map<String, Object> messageStatus(String openKfid, Long conversationId, String messageId,
            String status, String errorCode, String errorMessage)
    {
        Map<String, Object> data = baseEnvelope(openKfid, conversationId);
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

    public static Map<String, Object> conversationUpdated(CrmWecomKfConversation conv)
    {
        Map<String, Object> partial = toConversationPartial(conv);
        return conversationUpdated(conv != null ? conv.getOpenKfid() : null, partial);
    }

    public static Map<String, Object> conversationUpdated(String openKfid, Map<String, Object> conversationPartial)
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
        if (!StringUtils.hasText(openKfid) && conversationPartial != null
                && conversationPartial.get("openKfid") != null)
        {
            openKfid = String.valueOf(conversationPartial.get("openKfid"));
        }
        Map<String, Object> data = baseEnvelope(openKfid, conversationId);
        data.put("type", EVENT_CONVERSATION_UPDATED);
        data.put("conversation", conversationPartial != null ? conversationPartial : new LinkedHashMap<>());
        return data;
    }

    public static Map<String, Object> toMessageRecord(CrmWecomKfMessage msg, Long conversationId)
    {
        Map<String, Object> m = new LinkedHashMap<>();
        if (msg == null)
        {
            return m;
        }
        m.put("id", msg.getId());
        m.put("conversationId", conversationId != null ? conversationId : msg.getConversationId());
        m.put("openKfid", msg.getOpenKfid());
        m.put("externalUserid", msg.getExternalUserid());
        m.put("direction", msg.getDirection());
        m.put("msgType", msg.getMsgtype());
        m.put("content", msg.getContent());
        m.put("mediaId", msg.getMediaId());
        m.put("picUrl", msg.getPicUrl());
        m.put("msgid", msg.getMsgid());
        m.put("sendTime", msg.getSendTime());
        m.put("createTime", msg.getCreatedAt());
        if (StringUtils.hasText(msg.getSource()))
        {
            m.put("source", msg.getSource());
            if (IInboxAgentChannelService.SOURCE_AI_INBOX_AGENT.equals(msg.getSource()))
            {
                m.put("senderType", "ai");
            }
        }
        if (StringUtils.hasText(msg.getMediaId()) && StringUtils.hasText(msg.getOpenKfid()))
        {
            m.put("mediaUrl", "/crm/wecom/kf/messages/media?openKfid=" + msg.getOpenKfid()
                    + "&mediaId=" + msg.getMediaId());
        }
        return m;
    }

    public static Map<String, Object> toConversationPartial(CrmWecomKfConversation conv)
    {
        Map<String, Object> c = new LinkedHashMap<>();
        if (conv == null)
        {
            return c;
        }
        c.put("id", conv.getId());
        c.put("openKfid", conv.getOpenKfid());
        if (StringUtils.hasText(conv.getExternalUserid()))
        {
            c.put("externalUserid", conv.getExternalUserid());
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
        c.put("canReply", conv.getCanReply() == null || conv.getCanReply() == 1);
        c.put("userId", conv.getUserId() != null ? String.valueOf(conv.getUserId()) : null);
        c.put("owner", conv.getOwner());
        c.put("assignedAt", conv.getAssignedAt());
        c.put("hostedByAi", conv.getHostedByAi());
        if (conv.getContactId() != null)
        {
            c.put("contactId", conv.getContactId());
        }
        if (conv.getContactName() != null)
        {
            c.put("contactName", conv.getContactName());
        }
        return c;
    }

    public static Map<String, Object> conversationPartialFrom(Long id, String openKfid, Integer unreadCount,
            Long userId, String owner, Date assignedAt)
    {
        return conversationPartialFrom(id, openKfid, unreadCount, userId, owner, assignedAt, null);
    }

    public static Map<String, Object> conversationPartialFrom(Long id, String openKfid, Integer unreadCount,
            Long userId, String owner, Date assignedAt, Boolean hostedByAi)
    {
        Map<String, Object> c = new LinkedHashMap<>();
        c.put("id", id);
        c.put("openKfid", openKfid);
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

    /** Extract openKfid from payload for hub visibility filter. */
    public static String extractOpenKfid(Object data)
    {
        if (!(data instanceof Map))
        {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) data;
        Object openKfid = map.get("openKfid");
        if (openKfid != null && StringUtils.hasText(String.valueOf(openKfid)))
        {
            return String.valueOf(openKfid);
        }
        Object conv = map.get("conversation");
        if (conv instanceof Map)
        {
            Object a = ((Map<?, ?>) conv).get("openKfid");
            if (a != null)
            {
                return String.valueOf(a);
            }
        }
        Object msg = map.get("message");
        if (msg instanceof Map)
        {
            Object a = ((Map<?, ?>) msg).get("openKfid");
            if (a != null)
            {
                return String.valueOf(a);
            }
        }
        return null;
    }

    private static Map<String, Object> baseEnvelope(String openKfid, Long conversationId)
    {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("openKfid", openKfid);
        data.put("conversationId", conversationId);
        data.put("ts", System.currentTimeMillis());
        return data;
    }
}
