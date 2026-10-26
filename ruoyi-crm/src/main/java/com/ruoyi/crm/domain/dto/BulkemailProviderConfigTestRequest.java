package com.ruoyi.crm.domain.dto;

/**
 * 租户发信配置连通性测试
 */
public class BulkemailProviderConfigTestRequest
{
    private String toEmail;

    public String getToEmail()
    {
        return toEmail;
    }

    public void setToEmail(String toEmail)
    {
        this.toEmail = toEmail;
    }
}
