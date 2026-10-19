package com.ruoyi.crm.domain.vo;

/**
 * resolve 返回的 crmAuth，仅供 BFF / Inngest 使用
 */
public class WebhookCrmAuthVO
{
    private String token;

    private String tenant;

    public WebhookCrmAuthVO()
    {
    }

    public WebhookCrmAuthVO(String token, String tenant)
    {
        this.token = token;
        this.tenant = tenant;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public String getTenant()
    {
        return tenant;
    }

    public void setTenant(String tenant)
    {
        this.tenant = tenant;
    }
}
