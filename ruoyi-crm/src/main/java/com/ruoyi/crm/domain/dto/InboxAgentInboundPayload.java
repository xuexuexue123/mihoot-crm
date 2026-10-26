package com.ruoyi.crm.domain.dto;

/**
 * Java → Next Inbox AI Agent 入站通知载荷。
 */
public class InboxAgentInboundPayload
{
    public static final String CHANNEL_WHATSAPP = "whatsapp";
    public static final String CHANNEL_WECHAT_MP = "wechat_mp";
    public static final String CHANNEL_MESSENGER = "messenger";
    public static final String CHANNEL_WEBCHAT = "webchat";
    public static final String CHANNEL_WECOM_KF = "wecom_kf";

    private String channel;
    private Long conversationId;
    private Long inboundMessageId;
    private String accountId;
    private String peerId;
    private String msgType;
    private String text;
    private boolean keywordAutoReplied;

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

    public Long getInboundMessageId() { return inboundMessageId; }
    public void setInboundMessageId(Long inboundMessageId) { this.inboundMessageId = inboundMessageId; }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public String getPeerId() { return peerId; }
    public void setPeerId(String peerId) { this.peerId = peerId; }

    public String getMsgType() { return msgType; }
    public void setMsgType(String msgType) { this.msgType = msgType; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public boolean isKeywordAutoReplied() { return keywordAutoReplied; }
    public void setKeywordAutoReplied(boolean keywordAutoReplied) { this.keywordAutoReplied = keywordAutoReplied; }
}
