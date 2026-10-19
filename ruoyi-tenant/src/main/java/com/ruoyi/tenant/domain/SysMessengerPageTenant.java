package com.ruoyi.tenant.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 主库：Messenger page_id 与租户映射（Webhook 回调路由）
 */
public class SysMessengerPageTenant
{
    private String pageId;
    private String tenant;

    public String getPageId()
    {
        return pageId;
    }

    public void setPageId(String pageId)
    {
        this.pageId = pageId;
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
                .append("pageId", getPageId())
                .append("tenant", getTenant())
                .toString();
    }
}
