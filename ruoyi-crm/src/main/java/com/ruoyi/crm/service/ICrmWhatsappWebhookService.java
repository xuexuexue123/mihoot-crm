package com.ruoyi.crm.service;

import java.util.Map;

public interface ICrmWhatsappWebhookService
{
    void processWebhook(String tenantId, Map<String, Object> body);
}
