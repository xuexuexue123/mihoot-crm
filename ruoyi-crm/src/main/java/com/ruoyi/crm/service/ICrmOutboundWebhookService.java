package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.dto.OutboundWebhookSaveRequest;
import com.ruoyi.crm.domain.vo.OutboundWebhookDeliveryVo;
import com.ruoyi.crm.domain.vo.OutboundWebhookTestResultVo;
import com.ruoyi.crm.domain.vo.OutboundWebhookVo;

public interface ICrmOutboundWebhookService
{
    List<OutboundWebhookVo> selectList(String tenantId, String eventType, String enabled);

    OutboundWebhookVo getById(String tenantId, String webhookId);

    void add(String tenantId, OutboundWebhookSaveRequest request);

    void update(String tenantId, OutboundWebhookSaveRequest request);

    void remove(String tenantId, String webhookId);

    List<OutboundWebhookDeliveryVo> selectDeliveries(String tenantId, String webhookId, String status);

    OutboundWebhookTestResultVo test(String tenantId, String webhookId);
}
