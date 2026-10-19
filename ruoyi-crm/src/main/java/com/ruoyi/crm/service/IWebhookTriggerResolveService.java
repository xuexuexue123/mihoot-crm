package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.dto.WebhookTriggerResolveRequest;
import com.ruoyi.crm.domain.vo.WebhookTriggerResolveVO;

public interface IWebhookTriggerResolveService
{
    WebhookTriggerResolveVO resolve(WebhookTriggerResolveRequest request);
}
