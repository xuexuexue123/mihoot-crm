package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmWebchatMessage;

public interface CrmWebchatMessageMapper
{
    int insert(CrmWebchatMessage row);

    CrmWebchatMessage selectByClientMessageId(
            @Param("tenantId") String tenantId,
            @Param("conversationId") Long conversationId,
            @Param("clientMessageId") String clientMessageId);

    CrmWebchatMessage selectById(
            @Param("tenantId") String tenantId,
            @Param("id") Long id);

    List<CrmWebchatMessage> selectByConversationId(
            @Param("conversationId") Long conversationId,
            @Param("beforeId") Long beforeId,
            @Param("pageSize") Integer pageSize);
}
