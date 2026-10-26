package com.ruoyi.crm.domain.dto;

/**
 * POST /crm/webhook-trigger/resolve 请求体
 */
public class WebhookTriggerResolveRequest
{
    private String path;

    private String httpMethod;

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getHttpMethod()
    {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod)
    {
        this.httpMethod = httpMethod;
    }
}
