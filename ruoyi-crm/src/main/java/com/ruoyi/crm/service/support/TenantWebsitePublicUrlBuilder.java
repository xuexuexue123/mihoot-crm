package com.ruoyi.crm.service.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.tenant.config.TenantWebsiteProperties;

/**
 * 拼接网站素材公开 URL
 */
@Component
public class TenantWebsitePublicUrlBuilder
{
    @Autowired
    private TenantWebsiteProperties tenantWebsiteProperties;

    public String build(String websiteSlug, String assetId)
    {
        if (StringUtils.isBlank(websiteSlug) || StringUtils.isBlank(assetId))
        {
            return null;
        }
        String protocol = tenantWebsiteProperties.getPublicBaseProtocol();
        if (StringUtils.isBlank(protocol))
        {
            protocol = "https";
        }
        String domain = tenantWebsiteProperties.getEffectivePublicBaseDomain();
        return protocol + "://" + websiteSlug.trim() + "." + domain + "/_assets/" + assetId;
    }

    public String buildPublicPath(String assetId)
    {
        return "/_assets/" + assetId;
    }
}
