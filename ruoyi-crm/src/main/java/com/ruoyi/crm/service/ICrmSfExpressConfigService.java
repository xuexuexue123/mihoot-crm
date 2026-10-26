package com.ruoyi.crm.service;

import java.util.Map;

import com.ruoyi.crm.domain.dto.sfexpress.SfExpressConfigSaveRequest;

public interface ICrmSfExpressConfigService
{
    Map<String, Object> getConfigMasked();

    void saveOrUpdate(SfExpressConfigSaveRequest body, String tenantId);
}
