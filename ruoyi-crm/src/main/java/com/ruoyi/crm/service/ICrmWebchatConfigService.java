package com.ruoyi.crm.service;

import java.util.List;
import java.util.Map;

public interface ICrmWebchatConfigService
{
    Map<String, Object> getConfigForAdmin(String tenantId);

    Map<String, Object> getPublicConfig(String tenantId);

    Map<String, Object> saveOrUpdate(String tenantId, Map<String, Object> fields);

    Map<String, Object> rotateToken(String tenantId);
}
