package com.ruoyi.web.controller.crm.wechatmp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.ruoyi.crm.wechatmp.dto.WechatMpConfigSaveRequest;
import com.ruoyi.crm.wechatmp.service.ICrmWechatMpConfigService;

@RestController
@RequestMapping({ "/crm/wechat_mp", "/wechat_mp" })
public class CrmWechatMpConfigController extends BaseController
{
    @Autowired
    private ICrmWechatMpConfigService wechatMpConfigService;

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:config')")
    @GetMapping("/config")
    public AjaxResult listConfigs()
    {
        return AjaxResult.success(wechatMpConfigService.listConfigs());
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:config')")
    @GetMapping("/config/{id}")
    public AjaxResult getConfig(@PathVariable Long id)
    {
        try
        {
            return AjaxResult.success(wechatMpConfigService.getConfigMasked(id));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:config')")
    @Log(title = "微信公众号配置", businessType = BusinessType.UPDATE)
    @PostMapping("/config")
    public AjaxResult save(@RequestBody WechatMpConfigSaveRequest body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "缺少租户头 tenant");
        }
        try
        {
            wechatMpConfigService.saveOrUpdate(body, tenant);
            return AjaxResult.success(wechatMpConfigService.listConfigs());
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:config')")
    @Log(title = "微信公众号配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/config/{id}")
    public AjaxResult delete(@PathVariable Long id,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "缺少租户头 tenant");
        }
        try
        {
            wechatMpConfigService.delete(id, tenant);
            return AjaxResult.success();
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
