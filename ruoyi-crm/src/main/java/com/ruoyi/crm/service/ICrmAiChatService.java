package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.dto.AiChatMessageBatchRequest;
import com.ruoyi.crm.domain.dto.AiChatSessionCreateRequest;
import com.ruoyi.crm.domain.vo.AiChatMessageVo;
import com.ruoyi.crm.domain.vo.AiChatSessionVo;

public interface ICrmAiChatService
{
    List<AiChatSessionVo> listSessions(String tenantId);

    AiChatSessionVo createOrTouchSession(String tenantId, AiChatSessionCreateRequest request);

    List<AiChatMessageVo> listMessages(String tenantId, String sessionId);

    void appendMessages(String tenantId, String sessionId, AiChatMessageBatchRequest request);

    void deleteSession(String tenantId, String sessionId);
}
