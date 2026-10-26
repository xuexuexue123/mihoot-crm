package com.ruoyi.web.controller.crm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.crm.domain.dto.wecom.ResolveUserResult;
import com.ruoyi.crm.domain.dto.wecom.WecomAiBotConfigSaveRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomAiBotStatusReportRequest;
import com.ruoyi.crm.domain.dto.wecom.TenantBotCommand;
import com.ruoyi.crm.service.ICrmWecomAiBotService;

@RestController
@RequestMapping("/crm/wecom/aibot")
public class CrmWecomAiBotController extends BaseController
{
    @Autowired
    private ICrmWecomAiBotService crmWecomAiBotService;

    // ---- 租户端点 (JWT auth) ----

    @PreAuthorize("@ss.hasPermi('crm:wecom:config')")
    @GetMapping("/config")
    public AjaxResult getConfig(
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error("缺少租户头 tenant");
        }
        return AjaxResult.success(crmWecomAiBotService.getConfigMasked(tenant));
    }

    @PreAuthorize("@ss.hasPermi('crm:wecom:config')")
    @PostMapping("/config")
    public AjaxResult saveConfig(@RequestBody WecomAiBotConfigSaveRequest body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error("缺少租户头 tenant");
        }
        return AjaxResult.success(crmWecomAiBotService.saveOrUpdateConfig(body, tenant));
    }

    @GetMapping("/status")
    public AjaxResult getStatus(
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error("缺少租户头 tenant");
        }
        return AjaxResult.success(crmWecomAiBotService.getStatus(tenant));
    }

    @PostMapping("/connect")
    public AjaxResult connect(
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error("缺少租户头 tenant");
        }
        return AjaxResult.success("连接请求已提交", crmWecomAiBotService.requestConnect(tenant));
    }

    @PostMapping("/disconnect")
    public AjaxResult disconnect(
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error("缺少租户头 tenant");
        }
        return AjaxResult.success("断开请求已提交", crmWecomAiBotService.requestDisconnect(tenant));
    }

    // ---- 内部端点 (X-Internal-Secret auth) ----

    @GetMapping("/resolve-user")
    public AjaxResult resolveUser(
            @RequestParam String userid,
            @RequestParam(required = false) String tenant)
    {
        ResolveUserResult result = crmWecomAiBotService.resolveUser(userid, tenant);
        if (result == null)
        {
            return AjaxResult.error("未找到关联的CRM用户，请联系管理员将企业微信账号与CRM账号关联");
        }
        return AjaxResult.success(result);
    }

    @GetMapping("/pending")
    public AjaxResult listPending()
    {
        List<TenantBotCommand> commands = crmWecomAiBotService.getPendingCommands();
        return AjaxResult.success(commands);
    }

    @PostMapping("/status")
    public AjaxResult reportStatus(@RequestBody WecomAiBotStatusReportRequest body)
    {
        crmWecomAiBotService.reportStatus(body);
        return AjaxResult.success();
    }
}
