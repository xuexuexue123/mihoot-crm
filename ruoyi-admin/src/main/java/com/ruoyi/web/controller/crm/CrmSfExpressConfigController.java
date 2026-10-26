package com.ruoyi.web.controller.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.dto.sfexpress.SfExpressConfigSaveRequest;
import com.ruoyi.crm.service.ICrmSfExpressConfigService;

@RestController
@RequestMapping("/crm/sf-express")
public class CrmSfExpressConfigController extends BaseController
{
    @Autowired
    private ICrmSfExpressConfigService crmSfExpressConfigService;

    @PreAuthorize("@ss.hasPermi('crm:sfexpress:config')")
    @GetMapping("/config")
    public AjaxResult getConfig()
    {
        return AjaxResult.success(crmSfExpressConfigService.getConfigMasked());
    }

    @PreAuthorize("@ss.hasPermi('crm:sfexpress:config')")
    @Log(title = "顺丰配置", businessType = BusinessType.UPDATE)
    @PostMapping("/config")
    public AjaxResult save(@RequestBody SfExpressConfigSaveRequest body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error("缺少租户头 tenant");
        }
        crmSfExpressConfigService.saveOrUpdate(body, tenant);
        return AjaxResult.success(crmSfExpressConfigService.getConfigMasked());
    }
}
