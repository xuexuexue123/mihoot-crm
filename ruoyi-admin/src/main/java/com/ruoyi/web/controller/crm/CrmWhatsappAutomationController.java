package com.ruoyi.web.controller.crm;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmWhatsappAutomationRule;
import com.ruoyi.crm.service.ICrmWhatsappAutomationRuleService;

@RestController
@RequestMapping("/crm/whatsapp/automation/rules")
public class CrmWhatsappAutomationController extends BaseController
{
    @Autowired
    private ICrmWhatsappAutomationRuleService ruleService;

    @PreAuthorize("@ss.hasPermi('crm:whatsapp:automation') or @ss.hasPermi('crm:whatsapp:marketing') or @ss.hasPermi('crm:whatsapp:templates')")
    @GetMapping
    public AjaxResult list(@RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(400, "Missing tenant header");
        }
        List<CrmWhatsappAutomationRule> list = ruleService.list(tenant);
        return AjaxResult.success(list);
    }

    @PreAuthorize("@ss.hasPermi('crm:whatsapp:automation') or @ss.hasPermi('crm:whatsapp:templates')")
    @PostMapping
    public AjaxResult create(@RequestHeader(value = "tenant", required = false) String tenant,
                             @RequestBody Map<String, Object> body)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(400, "Missing tenant header");
        }
        try
        {
            CrmWhatsappAutomationRule rule = fromBody(body);
            return AjaxResult.success(ruleService.create(tenant, rule));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : 400, e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:whatsapp:automation') or @ss.hasPermi('crm:whatsapp:templates')")
    @PutMapping("/{id}")
    public AjaxResult update(@PathVariable("id") Long id,
                             @RequestHeader(value = "tenant", required = false) String tenant,
                             @RequestBody Map<String, Object> body)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(400, "Missing tenant header");
        }
        try
        {
            CrmWhatsappAutomationRule rule = fromBody(body);
            return AjaxResult.success(ruleService.update(tenant, id, rule));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : 400, e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:whatsapp:automation') or @ss.hasPermi('crm:whatsapp:templates')")
    @DeleteMapping("/{id}")
    public AjaxResult delete(@PathVariable("id") Long id,
                             @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(400, "Missing tenant header");
        }
        try
        {
            ruleService.delete(tenant, id);
            return AjaxResult.success();
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : 400, e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static CrmWhatsappAutomationRule fromBody(Map<String, Object> body)
    {
        CrmWhatsappAutomationRule rule = new CrmWhatsappAutomationRule();
        rule.setRuleName((String) body.get("ruleName"));
        rule.setTriggerType((String) body.get("triggerType"));
        Object triggerConfig = body.get("triggerConfig");
        if (triggerConfig != null)
        {
            rule.setTriggerConfig(triggerConfig instanceof String
                    ? (String) triggerConfig : JSON.toJSONString(triggerConfig));
        }
        rule.setTemplateName((String) body.get("templateName"));
        rule.setTemplateLanguage((String) body.get("templateLanguage"));
        Object templateParams = body.get("templateParams");
        if (templateParams == null)
        {
            templateParams = body.get("components");
        }
        if (templateParams != null)
        {
            rule.setTemplateParams(templateParams instanceof String
                    ? (String) templateParams : JSON.toJSONString(templateParams));
        }
        Object delay = body.get("delaySeconds");
        if (delay instanceof Number)
        {
            rule.setDelaySeconds(((Number) delay).intValue());
        }
        Object enabled = body.get("enabled");
        if (enabled instanceof Boolean)
        {
            rule.setEnabled((Boolean) enabled);
        }
        return rule;
    }
}
