package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmWhatsappWebhookEvent;

public interface CrmWhatsappWebhookEventMapper
{
    int insert(CrmWhatsappWebhookEvent row);

    int updateStatus(@Param("id") Long id, @Param("status") String status,
                     @Param("processingError") String processingError);

    CrmWhatsappWebhookEvent selectByMessageIdAndEventType(
            @Param("tenantId") String tenantId,
            @Param("messageId") String messageId,
            @Param("eventType") String eventType);

    List<CrmWhatsappWebhookEvent> selectPage(
            @Param("tenantId") String tenantId,
            @Param("waId") String waId,
            @Param("status") String status,
            @Param("eventType") String eventType,
            @Param("phoneNumberId") String phoneNumberId,
            @Param("startTime") Long startTime,
            @Param("endTime") Long endTime
    );
}
