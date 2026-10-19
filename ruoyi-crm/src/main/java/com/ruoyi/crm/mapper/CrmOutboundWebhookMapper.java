package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmOutboundWebhook;
import com.ruoyi.crm.domain.vo.OutboundWebhookVo;

public interface CrmOutboundWebhookMapper
{
    List<OutboundWebhookVo> selectList(@Param("tenantId") String tenantId,
            @Param("eventType") String eventType,
            @Param("enabled") String enabled);

    OutboundWebhookVo selectById(@Param("tenantId") String tenantId, @Param("webhookId") String webhookId);

    CrmOutboundWebhook selectEntityById(@Param("tenantId") String tenantId, @Param("webhookId") String webhookId);

    List<CrmOutboundWebhook> selectEnabledByEvent(@Param("tenantId") String tenantId,
            @Param("eventType") String eventType);

    int countActive(@Param("tenantId") String tenantId);

    int insertCrmOutboundWebhook(CrmOutboundWebhook row);

    int updateCrmOutboundWebhook(CrmOutboundWebhook row);

    int logicalDeleteById(@Param("webhookId") String webhookId,
            @Param("tenantId") String tenantId,
            @Param("updateBy") String updateBy);
}
