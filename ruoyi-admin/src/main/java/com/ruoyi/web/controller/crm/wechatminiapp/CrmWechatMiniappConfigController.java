package com.ruoyi.web.controller.crm.wechatminiapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.annotation.RateLimiter;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.enums.LimitType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.wechatminiapp.dto.WechatMiniappConfigSaveRequest;
import com.ruoyi.crm.wechatminiapp.service.ICrmWechatMiniappConfigService;

@RestController
@RequestMapping("/crm/wechat_miniapp")
public class CrmWechatMiniappConfigController extends BaseController
{
    @Autowired
    private ICrmWechatMiniappConfigService wechatMiniappConfigService;

    @PreAuthorize("@ss.hasPermi('crm:wechat_miniapp:config')")
    @GetMapping("/config")
    public AjaxResult getConfig(@RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "缺少租户头 tenant");
        }
        return AjaxResult.success(wechatMiniappConfigService.getConfigMasked());
    }

    /**
     * 服务账号专用：返回 AppSecret 明文供 Next BFF 微信换号。顾问 JWT 无此权限 → 403。
     * 审计日志不记录 Secret 明文。
     */
    @PreAuthorize("@ss.hasPermi('crm:wechat_miniapp:runtime')")
    @Log(title = "微信小程序Runtime密钥", businessType = BusinessType.OTHER)
    @RateLimiter(time = 60, count = 30, limitType = LimitType.DEFAULT)
    @GetMapping("/config/runtime")
    public AjaxResult getConfigRuntime(@RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "缺少租户头 tenant");
        }
        try
        {
            return AjaxResult.success(wechatMiniappConfigService.getConfigRuntime());
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_miniapp:config')")
    @Log(title = "微信小程序配置", businessType = BusinessType.UPDATE)
    @PostMapping("/config")
    public AjaxResult save(@RequestBody WechatMiniappConfigSaveRequest body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "缺少租户头 tenant");
        }
        try
        {
            wechatMiniappConfigService.saveOrUpdate(body);
            return AjaxResult.success(wechatMiniappConfigService.getConfigMasked());
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_miniapp:config')")
    @Log(title = "微信小程序配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/config")
    public AjaxResult delete(@RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "缺少租户头 tenant");
        }
        try
        {
            wechatMiniappConfigService.delete();
            return AjaxResult.success();
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
