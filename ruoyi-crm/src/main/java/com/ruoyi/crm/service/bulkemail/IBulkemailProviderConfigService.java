package com.ruoyi.crm.service.bulkemail;

import java.util.Map;

import com.ruoyi.crm.domain.dto.BulkemailProviderConfigSaveRequest;

public interface IBulkemailProviderConfigService
{
    Map<String, Object> getMasked();

    void saveOrUpdate(BulkemailProviderConfigSaveRequest body, String operator);

    void requireActive();

    BulkemailProviderRuntimeConfig resolveForSend();

    void testSend(String toEmail, String operator);
}
