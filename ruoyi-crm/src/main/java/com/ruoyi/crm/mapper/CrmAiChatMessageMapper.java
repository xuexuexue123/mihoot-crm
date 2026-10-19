package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmAiChatMessage;
import com.ruoyi.crm.domain.vo.AiChatMessageVo;

public interface CrmAiChatMessageMapper
{
    List<AiChatMessageVo> selectMessagesBySession(@Param("tenantId") String tenantId,
            @Param("userId") String userId, @Param("sessionId") String sessionId);

    CrmAiChatMessage selectMessageById(@Param("messageId") String messageId);

    int insertMessage(CrmAiChatMessage message);

    int updateMessage(CrmAiChatMessage message);
}
