package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmMessengerWebhookEvent;

public interface CrmMessengerWebhookEventMapper
{
    int insert(CrmMessengerWebhookEvent row);

    int updateStatus(@Param("id") Long id, @Param("status") String status,
                     @Param("processingError") String processingError);

    CrmMessengerWebhookEvent selectByMessageIdAndEventType(
            @Param("tenantId") String tenantId,
            @Param("messageId") String messageId,
            @Param("eventType") String eventType);

    List<CrmMessengerWebhookEvent> selectPage(
            @Param("tenantId") String tenantId,
            @Param("psid") String psid,
            @Param("status") String status,
            @Param("eventType") String eventType,
            @Param("pageId") String pageId,
            @Param("startTime") Long startTime,
            @Param("endTime") Long endTime
    );

    long countByTenantAndDirection(
            @Param("tenantId") String tenantId,
            @Param("direction") Integer direction,
            @Param("startTime") Long startTime,
            @Param("endTime") Long endTime);
}
