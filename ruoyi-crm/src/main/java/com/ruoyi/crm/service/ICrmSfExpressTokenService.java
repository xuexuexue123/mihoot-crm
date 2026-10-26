package com.ruoyi.crm.service;

import java.util.Map;

public interface ICrmSfExpressTokenService
{
    Map<String, Object> refreshToken(String tenantId, boolean force);
}
