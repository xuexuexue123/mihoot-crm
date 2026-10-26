package com.ruoyi.tenant.domain.vo;

/**
 * 租户公开子域名站点元数据（Phase 1，不含 logoUrl）
 */
public class PublicTenantSiteVo
{
    private String tenantName;
    private String websiteSlug;
    private String companyName;
    private String websiteStatus;

    public String getTenantName()
    {
        return tenantName;
    }

    public void setTenantName(String tenantName)
    {
        this.tenantName = tenantName;
    }

    public String getWebsiteSlug()
    {
        return websiteSlug;
    }

    public void setWebsiteSlug(String websiteSlug)
    {
        this.websiteSlug = websiteSlug;
    }

    public String getCompanyName()
    {
        return companyName;
    }

    public void setCompanyName(String companyName)
    {
        this.companyName = companyName;
    }

    public String getWebsiteStatus()
    {
        return websiteStatus;
    }

    public void setWebsiteStatus(String websiteStatus)
    {
        this.websiteStatus = websiteStatus;
    }
}
