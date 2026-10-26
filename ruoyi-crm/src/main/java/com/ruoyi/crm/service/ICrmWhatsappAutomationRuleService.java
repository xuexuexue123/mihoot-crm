package com.ruoyi.crm.service;

import java.util.List;

import com.ruoyi.crm.domain.CrmWhatsappAutomationRule;

public interface ICrmWhatsappAutomationRuleService
{
    List<CrmWhatsappAutomationRule> list(String tenantId);

    CrmWhatsappAutomationRule get(String tenantId, Long id);

    CrmWhatsappAutomationRule create(String tenantId, CrmWhatsappAutomationRule rule);

    CrmWhatsappAutomationRule update(String tenantId, Long id, CrmWhatsappAutomationRule rule);

    void delete(String tenantId, Long id);
}
