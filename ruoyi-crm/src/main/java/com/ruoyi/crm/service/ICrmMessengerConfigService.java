package com.ruoyi.crm.service;

import java.util.Map;

public interface ICrmMessengerConfigService
{
    Map<String, Object> getConfigMasked(String tenantId);

    /**
     * @param pageAccessToken 新建必填；更新时空串表示不覆盖
     * @param webhookVerifyToken 空串表示不覆盖已有值
     * @param appSecret 空串表示不覆盖已有值
     */
    void saveOrUpdate(String tenantId, String pageId, String pageAccessToken,
                      String webhookVerifyToken, String appSecret, String pageName);
}
