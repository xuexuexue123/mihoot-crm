package com.ruoyi.web.controller.crm.feishu;

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
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.feishu.dto.FeishuConfigSaveRequest;
import com.ruoyi.crm.feishu.service.ICrmFeishuConfigService;

@RestController
@RequestMapping("/crm/feishu")
public class CrmFeishuConfigController extends BaseController
{
    @Autowired
    private ICrmFeishuConfigService feishuConfigService;

    @PreAuthorize("@ss.hasPermi('crm:feishu:config')")
    @GetMapping("/config")
    public AjaxResult getConfig(@RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "缺少租户头 tenant");
        }
        return AjaxResult.success(feishuConfigService.getConfigMasked());
    }

    @PreAuthorize("@ss.hasPermi('crm:feishu:config')")
    @Log(title = "飞书配置", businessType = BusinessType.UPDATE)
    @PostMapping("/config")
    public AjaxResult save(@RequestBody FeishuConfigSaveRequest body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "缺少租户头 tenant");
        }
        try
        {
            return AjaxResult.success(feishuConfigService.saveOrUpdate(body));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:feishu:config')")
    @Log(title = "飞书配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/config")
    public AjaxResult delete(@RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "缺少租户头 tenant");
        }
        try
        {
            feishuConfigService.delete();
            return AjaxResult.success();
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:feishu:config')")
    @Log(title = "飞书配置连通测试", businessType = BusinessType.OTHER)
    @PostMapping("/config/test")
    public AjaxResult test(@RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "缺少租户头 tenant");
        }
        try
        {
            return AjaxResult.success("ok", feishuConfigService.testConnection());
        }
        catch (ServiceException e)
        {
            AjaxResult err = AjaxResult.error(HttpStatus.BAD_REQUEST, e.getMessage());
            err.put("data", java.util.Collections.singletonMap("connected", false));
            return err;
        }
    }
}
