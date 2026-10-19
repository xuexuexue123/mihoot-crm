package com.ruoyi.web.controller.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.dto.BulkemailProviderConfigSaveRequest;
import com.ruoyi.crm.domain.dto.BulkemailProviderConfigTestRequest;
import com.ruoyi.crm.service.bulkemail.IBulkemailProviderConfigService;

@RestController
@RequestMapping("/crm/bulkemail/provider-config")
public class CrmBulkemailProviderConfigController extends BaseController
{
    @Autowired
    private IBulkemailProviderConfigService bulkemailProviderConfigService;

    @PreAuthorize("@ss.hasPermi('crm:bulkemail:query')")
    @GetMapping
    public AjaxResult getConfig()
    {
        return AjaxResult.success(bulkemailProviderConfigService.getMasked());
    }

    @PreAuthorize("@ss.hasPermi('crm:bulkemail:config')")
    @Log(title = "批量邮件发信配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult save(@RequestBody BulkemailProviderConfigSaveRequest body)
    {
        bulkemailProviderConfigService.saveOrUpdate(body, getUsername());
        return AjaxResult.success("success");
    }

    @PreAuthorize("@ss.hasPermi('crm:bulkemail:send')")
    @Log(title = "批量邮件发信配置测试", businessType = BusinessType.OTHER)
    @PostMapping("/test")
    public AjaxResult test(@RequestBody BulkemailProviderConfigTestRequest body)
    {
        bulkemailProviderConfigService.testSend(body.getToEmail(), getUsername());
        return AjaxResult.success("success");
    }
}
