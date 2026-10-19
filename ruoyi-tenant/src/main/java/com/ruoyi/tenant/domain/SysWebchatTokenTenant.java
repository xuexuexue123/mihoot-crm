package com.ruoyi.tenant.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 主库：Web Chat website_token 与租户映射（公开 API 路由）
 */
public class SysWebchatTokenTenant
{
    private String websiteToken;
    private String tenant;

    public String getWebsiteToken()
    {
        return websiteToken;
    }

    public void setWebsiteToken(String websiteToken)
    {
        this.websiteToken = websiteToken;
    }

    public String getTenant()
    {
        return tenant;
    }

    public void setTenant(String tenant)
    {
        this.tenant = tenant;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("websiteToken", getWebsiteToken())
                .append("tenant", getTenant())
                .toString();
    }
}
