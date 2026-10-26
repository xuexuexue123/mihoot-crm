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
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.dto.wecom.WecomConfigSaveRequest;
import com.ruoyi.crm.service.ICrmWecomConfigService;

@RestController
@RequestMapping("/crm/wecom")
public class CrmWecomConfigController extends BaseController
{
    @Autowired
    private ICrmWecomConfigService crmWecomConfigService;

    @PreAuthorize("@ss.hasPermi('crm:wecom:config')")
    @GetMapping("/config")
    public AjaxResult getConfig()
    {
        return AjaxResult.success(crmWecomConfigService.getConfigMasked());
    }

    @PreAuthorize("@ss.hasPermi('crm:wecom:config')")
    @Log(title = "企业微信配置", businessType = BusinessType.UPDATE)
    @PostMapping("/config")
    public AjaxResult save(@RequestBody WecomConfigSaveRequest body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "缺少租户头 tenant");
        }
        try
        {
            crmWecomConfigService.saveOrUpdate(body, tenant);
            return AjaxResult.success(crmWecomConfigService.getConfigMasked());
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
