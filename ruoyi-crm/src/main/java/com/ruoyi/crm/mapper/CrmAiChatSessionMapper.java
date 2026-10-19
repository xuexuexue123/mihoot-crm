package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmAiChatSession;
import com.ruoyi.crm.domain.vo.AiChatSessionVo;

public interface CrmAiChatSessionMapper
{
    List<AiChatSessionVo> selectSessionList(@Param("tenantId") String tenantId, @Param("userId") String userId);

    CrmAiChatSession selectSessionById(@Param("tenantId") String tenantId, @Param("userId") String userId,
            @Param("sessionId") String sessionId);

    int insertSession(CrmAiChatSession session);

    int touchSession(@Param("tenantId") String tenantId, @Param("userId") String userId,
            @Param("sessionId") String sessionId, @Param("title") String title);

    int logicalDeleteSession(@Param("tenantId") String tenantId, @Param("userId") String userId,
            @Param("sessionId") String sessionId);
}
