package com.ruoyi.crm.service;

import java.util.Map;

import com.ruoyi.crm.domain.dto.whatsapp.WhatsAppSendResult;

public interface ICrmWhatsappMessageService
{
    WhatsAppSendResult sendMessage(String tenantId, String waId, String phoneNumberId,
                                   String messageType, String content, String mediaId,
                                   String mediaUrl, String caption, Map<String, Object> template);

    /**
     * Inbox AI 出站：跳过坐席 owner 校验，消息 source=ai_inbox_agent。
     */
    WhatsAppSendResult sendMessageAsAi(String tenantId, String waId, String phoneNumberId,
                                       String messageType, String content, String mediaId,
                                       String mediaUrl, String caption, Map<String, Object> template);
}
