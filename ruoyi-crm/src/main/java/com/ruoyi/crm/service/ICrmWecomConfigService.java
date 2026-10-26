package com.ruoyi.crm.service;

import java.util.Map;

import com.ruoyi.crm.domain.dto.wecom.WecomConfigSaveRequest;

public interface ICrmWecomConfigService
{
    Map<String, Object> getConfigMasked();

    void saveOrUpdate(WecomConfigSaveRequest body, String tenantId);
}
