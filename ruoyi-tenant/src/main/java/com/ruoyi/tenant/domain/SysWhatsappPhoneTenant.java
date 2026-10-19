package com.ruoyi.tenant.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 主库：WhatsApp phone_number_id 与租户映射（Webhook 回调路由）
 */
public class SysWhatsappPhoneTenant
{
    private String phoneNumberId;
    private String tenant;

    public String getPhoneNumberId()
    {
        return phoneNumberId;
    }

    public void setPhoneNumberId(String phoneNumberId)
    {
        this.phoneNumberId = phoneNumberId;
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
                .append("phoneNumberId", getPhoneNumberId())
                .append("tenant", getTenant())
                .toString();
    }
}
