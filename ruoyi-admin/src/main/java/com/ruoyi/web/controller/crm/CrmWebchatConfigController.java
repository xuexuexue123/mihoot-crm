package com.ruoyi.web.controller.crm;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.service.ICrmWebchatConfigService;
import com.ruoyi.tenant.service.ISysWebchatTokenTenantService;

@RestController
@RequestMapping("/crm/webchat")
public class CrmWebchatConfigController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(CrmWebchatConfigController.class);

    @Autowired
    private ICrmWebchatConfigService webchatConfigService;

    @Autowired
    private ISysWebchatTokenTenantService webchatTokenTenantService;

    @PreAuthorize("@ss.hasPermi('crm:webchat:config')")
    @GetMapping("/config")
    public AjaxResult getConfig(@RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(400, "Missing tenant header");
        }
        Map<String, Object> config = webchatConfigService.getConfigForAdmin(tenant);
        return AjaxResult.success(config);
    }

    @PreAuthorize("@ss.hasPermi('crm:webchat:config')")
    @Log(title = "Web Chat配置", businessType = BusinessType.UPDATE)
    @PostMapping("/config")
    public AjaxResult save(@RequestBody Map<String, Object> body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(400, "Missing tenant header");
        }
        try
        {
            Map<String, Object> saved = webchatConfigService.saveOrUpdate(tenant, body);
            syncMasterMapping(tenant, saved);
            return AjaxResult.success(saved);
        }
        catch (ServiceException e)
        {
            int code = e.getCode() != null ? e.getCode() : 400;
            return AjaxResult.error(code, e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:webchat:config')")
    @Log(title = "Web Chat轮换Token", businessType = BusinessType.UPDATE)
    @PostMapping("/config/rotate-token")
    public AjaxResult rotateToken(@RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(400, "Missing tenant header");
        }
        try
        {
            Map<String, Object> saved = webchatConfigService.rotateToken(tenant);
            syncMasterMapping(tenant, saved);
            return AjaxResult.success(saved);
        }
        catch (ServiceException e)
        {
            int code = e.getCode() != null ? e.getCode() : 400;
            return AjaxResult.error(code, e.getMessage());
        }
    }

    private void syncMasterMapping(String tenant, Map<String, Object> saved)
    {
        if (saved == null || saved.get("websiteToken") == null)
        {
            return;
        }
        String token = String.valueOf(saved.get("websiteToken")).trim();
        if (token.isEmpty())
        {
            return;
        }
        try
        {
            webchatTokenTenantService.replaceMappingForTenant(tenant, token);
        }
        catch (Exception e)
        {
            log.warn("Failed to sync master webchat token->tenant mapping for tenant={} token={}: {}",
                    tenant, token, e.getMessage());
        }
    }
}
