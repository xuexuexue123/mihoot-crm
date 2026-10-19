package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmMessengerMessage;

public interface CrmMessengerMessageMapper
{
    int insert(CrmMessengerMessage row);

    CrmMessengerMessage selectByMessageId(
            @Param("tenantId") String tenantId,
            @Param("messageId") String messageId);

    CrmMessengerMessage selectById(
            @Param("tenantId") String tenantId,
            @Param("id") Long id);

    CrmMessengerMessage selectByMediaUrl(
            @Param("tenantId") String tenantId,
            @Param("mediaUrl") String mediaUrl);

    int updateStatusByMessageId(
            @Param("tenantId") String tenantId,
            @Param("messageId") String messageId,
            @Param("status") String status);

    /**
     * 按 watermark（毫秒）将会话中出站消息标记为已读（send_time &lt;= watermark）。
     */
    int updateStatusReadByPsidWatermark(
            @Param("tenantId") String tenantId,
            @Param("psid") String psid,
            @Param("watermark") Long watermark);

    List<CrmMessengerMessage> selectByConversationId(
            @Param("conversationId") Long conversationId,
            @Param("beforeMsgId") Long beforeMsgId,
            @Param("pageSize") Integer pageSize
    );

    long countByDirection(
            @Param("tenantId") String tenantId,
            @Param("direction") Integer direction,
            @Param("startTime") Long startTime,
            @Param("endTime") Long endTime);
}
