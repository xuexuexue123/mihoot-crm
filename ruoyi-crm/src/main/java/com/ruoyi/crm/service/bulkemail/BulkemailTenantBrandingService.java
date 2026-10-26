package com.ruoyi.crm.service.bulkemail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmCampaignContacts;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.service.IMasterTenantService;

/**
 * 租户品牌名（clinicName merge tag）
 */
@Component
public class BulkemailTenantBrandingService
{
    private static final String CONFIG_KEY = "tenant.display_name";

    @Autowired
    private ISysConfigService sysConfigService;

    @Autowired
    private IMasterTenantService masterTenantService;

    public String getClinicName(String tenantId)
    {
        String fromConfig = sysConfigService.selectConfigByKey(CONFIG_KEY);
        if (StringUtils.isNotBlank(fromConfig))
        {
            return fromConfig.trim();
        }
        if (StringUtils.isNotBlank(tenantId))
        {
            MasterTenant tenant = masterTenantService.selectTenantByTenantId(tenantId);
            if (tenant != null && StringUtils.isNotBlank(tenant.getTenant()))
            {
                return tenant.getTenant();
            }
        }
        return "";
    }
}
