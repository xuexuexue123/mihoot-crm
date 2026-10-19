package com.ruoyi.crm.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmOutboundWebhookDelivery;
import com.ruoyi.crm.domain.vo.OutboundWebhookDeliveryVo;

public interface CrmOutboundWebhookDeliveryMapper
{
    CrmOutboundWebhookDelivery selectById(@Param("deliveryId") String deliveryId);

    List<OutboundWebhookDeliveryVo> selectList(@Param("tenantId") String tenantId,
            @Param("webhookId") String webhookId,
            @Param("status") String status);

    int insertIgnoreDuplicate(CrmOutboundWebhookDelivery row);

    List<CrmOutboundWebhookDelivery> selectDue(@Param("tenantId") String tenantId,
            @Param("now") Date now,
            @Param("limit") int limit);

    int markAttemptResult(CrmOutboundWebhookDelivery row);
}
