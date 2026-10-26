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
import com.ruoyi.crm.service.ICrmMessengerConfigService;
import com.ruoyi.tenant.service.ISysMessengerPageTenantService;

@RestController
@RequestMapping("/crm/messenger")
public class CrmMessengerConfigController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(CrmMessengerConfigController.class);

    @Autowired
    private ICrmMessengerConfigService messengerConfigService;

    @Autowired
    private ISysMessengerPageTenantService messengerPageTenantService;

    @PreAuthorize("@ss.hasPermi('crm:messenger:config')")
    @GetMapping("/config")
    public AjaxResult getConfig(@RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(400, "Missing tenant header");
        }
        Map<String, Object> config = messengerConfigService.getConfigMasked(tenant);
        return AjaxResult.success(config);
    }

    @PreAuthorize("@ss.hasPermi('crm:messenger:config')")
    @Log(title = "Messenger配置", businessType = BusinessType.UPDATE)
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
            String pageId = (String) body.get("pageId");
            String pageAccessToken = (String) body.get("pageAccessToken");
            String webhookVerifyToken = (String) body.get("webhookVerifyToken");
            String appSecret = (String) body.get("appSecret");
            String pageName = (String) body.get("pageName");

            messengerConfigService.saveOrUpdate(tenant, pageId, pageAccessToken,
                    webhookVerifyToken, appSecret, pageName);

            if (pageId != null && !pageId.trim().isEmpty())
            {
                try
                {
                    messengerPageTenantService.replaceMappingForTenant(tenant, pageId.trim());
                }
                catch (Exception e)
                {
                    log.warn("Failed to sync master messenger page->tenant mapping for tenant={} page={}: {}",
                            tenant, pageId, e.getMessage());
                }
            }

            return AjaxResult.success("Messenger configuration saved");
        }
        catch (ServiceException e)
        {
            int code = e.getCode() != null ? e.getCode() : 400;
            return AjaxResult.error(code, e.getMessage());
        }
    }
}
