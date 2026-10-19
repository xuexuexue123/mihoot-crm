package com.ruoyi.crm.service;

import java.util.Map;

public interface ICrmMessengerWebhookService
{
    void processWebhook(String tenantId, Map<String, Object> body);
}
