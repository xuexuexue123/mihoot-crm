package com.ruoyi.crm.service.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmWhatsappAutomationRule;
import com.ruoyi.crm.mapper.CrmWhatsappAutomationRuleMapper;
import com.ruoyi.crm.service.ICrmWhatsappAutomationRuleService;

@Service
public class CrmWhatsappAutomationRuleServiceImpl implements ICrmWhatsappAutomationRuleService
{
    private static final Set<String> ALLOWED_TRIGGERS = new HashSet<>(Arrays.asList(
            "appointment_reminder",
            "lead_no_reply",
            "lead_created",
            "post_treatment",
            "revisit_reminder",
            "birthday",
            "holiday",
            "campaign"
    ));

    @Autowired
    private CrmWhatsappAutomationRuleMapper ruleMapper;

    @Override
    public List<CrmWhatsappAutomationRule> list(String tenantId)
    {
        return ruleMapper.selectByTenant(tenantId);
    }

    @Override
    public CrmWhatsappAutomationRule get(String tenantId, Long id)
    {
        return ruleMapper.selectById(id, tenantId);
    }

    @Override
    public CrmWhatsappAutomationRule create(String tenantId, CrmWhatsappAutomationRule rule)
    {
        validate(rule);
        rule.setTenantId(tenantId);
        if (rule.getDelaySeconds() == null)
        {
            rule.setDelaySeconds(0);
        }
        if (rule.getEnabled() == null)
        {
            rule.setEnabled(Boolean.TRUE);
        }
        ruleMapper.insert(rule);
        return ruleMapper.selectById(rule.getId(), tenantId);
    }

    @Override
    public CrmWhatsappAutomationRule update(String tenantId, Long id, CrmWhatsappAutomationRule rule)
    {
        CrmWhatsappAutomationRule existing = ruleMapper.selectById(id, tenantId);
        if (existing == null)
        {
            throw new ServiceException("Automation rule not found", 404);
        }
        validate(rule);
        rule.setId(id);
        rule.setTenantId(tenantId);
        if (rule.getDelaySeconds() == null)
        {
            rule.setDelaySeconds(existing.getDelaySeconds());
        }
        if (rule.getEnabled() == null)
        {
            rule.setEnabled(existing.getEnabled());
        }
        ruleMapper.update(rule);
        return ruleMapper.selectById(id, tenantId);
    }

    @Override
    public void delete(String tenantId, Long id)
    {
        CrmWhatsappAutomationRule existing = ruleMapper.selectById(id, tenantId);
        if (existing == null)
        {
            throw new ServiceException("Automation rule not found", 404);
        }
        ruleMapper.deleteById(id, tenantId);
    }

    private void validate(CrmWhatsappAutomationRule rule)
    {
        if (rule == null)
        {
            throw new ServiceException("body is required", 400);
        }
        if (StringUtils.isEmpty(rule.getRuleName()))
        {
            throw new ServiceException("ruleName is required", 400);
        }
        if (StringUtils.isEmpty(rule.getTriggerType()) || !ALLOWED_TRIGGERS.contains(rule.getTriggerType()))
        {
            throw new ServiceException("Invalid triggerType", 400);
        }
        if (StringUtils.isEmpty(rule.getTemplateName()) || StringUtils.isEmpty(rule.getTemplateLanguage()))
        {
            throw new ServiceException("templateName and templateLanguage are required", 400);
        }
    }
}
