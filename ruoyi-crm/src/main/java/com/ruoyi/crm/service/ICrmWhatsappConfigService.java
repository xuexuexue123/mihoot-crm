package com.ruoyi.crm.service;

import java.util.Map;

public interface ICrmWhatsappConfigService
{
    Map<String, Object> getConfigMasked(String tenantId);

    /**
     * @param appSecret Meta App Secret；空串表示不覆盖已有值
     */
    void saveOrUpdate(String tenantId, String phoneNumberId, String wabaId,
                      String accessToken, String webhookVerifyToken, String appSecret);
}
