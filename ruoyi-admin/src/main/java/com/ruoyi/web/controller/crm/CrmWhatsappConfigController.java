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
import com.ruoyi.crm.service.ICrmWhatsappConfigService;
import com.ruoyi.tenant.service.ISysWhatsappPhoneTenantService;

@RestController
@RequestMapping("/crm/whatsapp")
public class CrmWhatsappConfigController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(CrmWhatsappConfigController.class);

    @Autowired
    private ICrmWhatsappConfigService whatsappConfigService;

    @Autowired
    private ISysWhatsappPhoneTenantService whatsappPhoneTenantService;

    @PreAuthorize("@ss.hasPermi('crm:whatsapp:config')")
    @GetMapping("/config")
    public AjaxResult getConfig(@RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(400, "Missing tenant header");
        }
        Map<String, Object> config = whatsappConfigService.getConfigMasked(tenant);
        return AjaxResult.success(config);
    }

    @PreAuthorize("@ss.hasPermi('crm:whatsapp:config')")
    @Log(title = "WhatsApp配置", businessType = BusinessType.UPDATE)
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
            String phoneNumberId = (String) body.get("phoneNumberId");
            String wabaId = (String) body.get("wabaId");
            String accessToken = (String) body.get("accessToken");
            String webhookVerifyToken = (String) body.get("webhookVerifyToken");
            String appSecret = (String) body.get("appSecret");

            whatsappConfigService.saveOrUpdate(tenant, phoneNumberId, wabaId, accessToken,
                    webhookVerifyToken, appSecret);

            // 同步主库 phone_number_id → tenant 路由映射（用于 Webhook 回调切库）
            try
            {
                whatsappPhoneTenantService.replaceMappingForTenant(tenant, phoneNumberId);
            }
            catch (Exception e)
            {
                log.warn("Failed to sync master whatsapp phone->tenant mapping for tenant={} phone={}: {}",
                        tenant, phoneNumberId, e.getMessage());
                // 不阻塞配置保存，后续可通过重试或手动修复
            }

            return AjaxResult.success("WhatsApp configuration saved");
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(400, e.getMessage());
        }
    }
}
