package com.ruoyi.crm.service.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.tenant.config.TenantWebsiteProperties;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.service.IMasterTenantService;

/**
 * 按租户解析 H5 公开站 origin（子域名优先，回退全局 SEVENCRM_PUBLIC_APP_URL）。
 */
@Component
public class TenantPublicAppUrlResolver
{
    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private TenantWebsiteProperties tenantWebsiteProperties;

    @Value("${outreach.registration.public-app-url:}")
    private String fallbackPublicAppUrl;

    /**
     * 解析租户 H5 基址，无尾斜杠。
     */
    public String resolve(String tenantId)
    {
        String tenantOrigin = buildTenantOrigin(resolveWebsiteSlug(tenantId));
        if (StringUtils.isNotBlank(tenantOrigin))
        {
            return tenantOrigin;
        }
        return normalizeOrigin(fallbackPublicAppUrl);
    }

    public boolean hasResolvableBase(String tenantId)
    {
        return StringUtils.isNotBlank(resolve(tenantId));
    }

    private String buildTenantOrigin(String websiteSlug)
    {
        if (StringUtils.isBlank(websiteSlug))
        {
            return "";
        }
        String protocol = tenantWebsiteProperties.getPublicBaseProtocol();
        if (StringUtils.isBlank(protocol))
        {
            protocol = "https";
        }
        String domain = tenantWebsiteProperties.getEffectivePublicBaseDomain();
        if (StringUtils.isBlank(domain))
        {
            return "";
        }
        return protocol + "://" + websiteSlug.trim() + "." + domain.trim();
    }

    private String resolveWebsiteSlug(String tenantId)
    {
        if (StringUtils.isBlank(tenantId))
        {
            return null;
        }
        String previousKey = MasterDataSourceSupport.currentKey();
        try
        {
            MasterDataSourceSupport.bindMasterKey();
            MasterTenant tenant = masterTenantService.selectMasterTenant(tenantId);
            return tenant != null ? tenant.getWebsiteSlug() : null;
        }
        finally
        {
            MasterDataSourceSupport.restoreKey(previousKey);
        }
    }

    static String normalizeOrigin(String url)
    {
        if (StringUtils.isBlank(url))
        {
            return "";
        }
        return url.trim().replaceAll("/+$", "");
    }
}
