package com.ruoyi.crm.mobile.push;

/**
 * 入站消息触发移动推送的上下文（与 SSE message.created 同钩子）。
 * <p>
 * {@code conversationKey} 约定（Unified Inbox）：
 * <ul>
 *   <li>whatsapp / messenger / webchat: {@code {channel}:{conversationId}}</li>
 *   <li>wechat_mp: 有数字 id 时 {@code wechat_mp:{id}}；否则 {@code wechat_mp:{appId}:{openId}}</li>
 * </ul>
 */
public class InboxMobilePushRequest
{
    public static final String CHANNEL_WHATSAPP = "whatsapp";
    public static final String CHANNEL_MESSENGER = "messenger";
    public static final String CHANNEL_WECHAT_MP = "wechat_mp";
    public static final String CHANNEL_WEBCHAT = "webchat";

    private String tenantId;
    private String channel;
    private Long conversationId;
    private String conversationKey;
    private Long ownerUserId;
    private String messageType;
    private String messageContent;
    /** 对方展示名（已脱敏或安全昵称） */
    private String peerDisplayName;
    /** 当前会话 unread，badge 查库失败时的 fallback */
    private Integer conversationUnread;

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

    public String getConversationKey() { return conversationKey; }
    public void setConversationKey(String conversationKey) { this.conversationKey = conversationKey; }

    public Long getOwnerUserId() { return ownerUserId; }
    public void setOwnerUserId(Long ownerUserId) { this.ownerUserId = ownerUserId; }

    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }

    public String getMessageContent() { return messageContent; }
    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }

    public String getPeerDisplayName() { return peerDisplayName; }
    public void setPeerDisplayName(String peerDisplayName) { this.peerDisplayName = peerDisplayName; }

    public Integer getConversationUnread() { return conversationUnread; }
    public void setConversationUnread(Integer conversationUnread) { this.conversationUnread = conversationUnread; }
}
