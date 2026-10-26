package com.ruoyi.crm.service;

import java.util.Map;

import com.ruoyi.crm.domain.CrmWebchatMessage;

public interface ICrmWebchatMessageService
{
    /**
     * CRM / AI 出站文本。
     *
     * @param asAi true → sender_type=ai, source=ai_inbox_agent；不校验负责人
     */
    Map<String, Object> sendText(String tenantId, Long conversationId, String content,
                                 String clientMessageId, boolean asAi);

    /**
     * 访客入站文本（幂等 clientMessageId）。
     */
    CrmWebchatMessage insertInbound(String tenantId, Long conversationId, String visitorId,
                                    String content, String messageType, String clientMessageId);
}
