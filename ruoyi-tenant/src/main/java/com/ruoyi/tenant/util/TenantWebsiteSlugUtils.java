package com.ruoyi.tenant.util;

import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.tenant.config.TenantWebsiteProperties;

/**
 * 租户公开子域名 slug 生成与校验
 */
@Component
public class TenantWebsiteSlugUtils
{
    private static final Pattern WEBSITE_SLUG_PATTERN = Pattern.compile("^[a-z][a-z0-9-]{2,62}$");

    @Autowired
    private TenantWebsiteProperties tenantWebsiteProperties;

    public static String fromTenantAlias(String tenantAlias)
    {
        if (tenantAlias == null)
        {
            return "";
        }
        return tenantAlias.trim().toLowerCase().replace('_', '-');
    }

    public static boolean isValidFormat(String slug)
    {
        return slug != null && WEBSITE_SLUG_PATTERN.matcher(slug).matches();
    }

    public boolean isReserved(String slug)
    {
        if (slug == null)
        {
            return false;
        }
        return tenantWebsiteProperties.getReservedSlugSet().contains(slug.trim().toLowerCase());
    }

    public String buildPreviewUrl(String websiteSlug)
    {
        return "https://" + websiteSlug + "." + tenantWebsiteProperties.getPreviewBaseDomain();
    }
}
