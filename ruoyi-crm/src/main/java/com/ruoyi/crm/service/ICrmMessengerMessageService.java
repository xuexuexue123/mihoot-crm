package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.dto.messenger.MessengerSendResult;

public interface ICrmMessengerMessageService
{
    /**
     * 发送文本 / 图片 / 文件。
     *
     * @param messagingType RESPONSE（窗内默认）或 MESSAGE_TAG（窗外）
     * @param tag           MESSAGE_TAG 时必填；白名单 HUMAN_AGENT
     * @param asAi          true 时跳过负责人校验，且不强制关 AI 托管
     */
    MessengerSendResult sendMessage(String tenantId, String psid, String pageId,
                                    String messageType, String content, String mediaUrl,
                                    String filename, String caption,
                                    String messagingType, String tag, boolean asAi);

    default MessengerSendResult sendTextMessage(String tenantId, String psid, String pageId, String content)
    {
        return sendMessage(tenantId, psid, pageId, "text", content, null, null, null,
                null, null, false);
    }
}
