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
import com.ruoyi.crm.domain.dto.wecom.WecomKfConfigSaveRequest;
import com.ruoyi.crm.service.IWecomKfConfigService;

@RestController
@RequestMapping("/crm/wecom/kf")
public class WecomKfConfigController extends BaseController
{
    @Autowired
    private IWecomKfConfigService wecomKfConfigService;

    @PreAuthorize("@ss.hasPermi('crm:wecom:config')")
    @GetMapping("/config")
    public AjaxResult getConfig()
    {
        return AjaxResult.success(wecomKfConfigService.getConfigMasked());
    }

    @PreAuthorize("@ss.hasPermi('crm:wecom:config')")
    @Log(title = "微信客服配置", businessType = BusinessType.UPDATE)
    @PostMapping("/config")
    public AjaxResult save(@RequestBody WecomKfConfigSaveRequest body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "缺少租户头 tenant");
        }
        try
        {
            wecomKfConfigService.saveOrUpdate(body, tenant);
            return AjaxResult.success(wecomKfConfigService.getConfigMasked());
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
