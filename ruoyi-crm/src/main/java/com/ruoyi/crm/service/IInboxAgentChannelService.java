package com.ruoyi.crm.service;

import java.util.Map;

/**
 * Inbox AI Agent 内部发送（Bot claim + 渠道出站，source=ai_inbox_agent）。
 */
public interface IInboxAgentChannelService
{
    String SOURCE_AI_INBOX_AGENT = "ai_inbox_agent";

    /**
     * 若会话无人负责则 Bot claim（不改 hostedByAi=false）；再发送文本。
     * 若 hostedByAi==false 则拒绝。
     */
    Map<String, Object> sendWhatsAppText(String tenantId, Long conversationId, String text);

    /**
     * 若会话无人负责则 Bot claim；再发公众号客服文本。
     */
    Map<String, Object> replyWechatMpText(Long conversationId, String text);

    /**
     * 若会话无人负责则 Bot claim；再发 Messenger 文本（asAi=true）。
     * 若 hostedByAi==false 则拒绝。
     */
    Map<String, Object> sendMessengerText(String tenantId, Long conversationId, String text);

    /**
     * 若会话无人负责则 Bot claim；再发 Web Chat 文本（sender_type=ai, source=ai_inbox_agent）。
     * 若 hostedByAi==false 则拒绝。
     */
    Map<String, Object> sendWebchatText(String tenantId, Long conversationId, String text);

    /**
     * 若会话无人负责则 Bot claim；再发微信客服文本（source=ai_inbox_agent）。
     * 若 hostedByAi==false 则拒绝。
     */
    Map<String, Object> sendWecomKfText(String tenantId, Long conversationId, String text);
}
