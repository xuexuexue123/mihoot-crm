package com.ruoyi.crm.service;

import java.util.Map;

import com.ruoyi.crm.domain.dto.wecom.WecomKfConfigSaveRequest;

public interface IWecomKfConfigService
{
    Map<String, Object> getConfigMasked();

    void saveOrUpdate(WecomKfConfigSaveRequest body, String tenantId);
}
