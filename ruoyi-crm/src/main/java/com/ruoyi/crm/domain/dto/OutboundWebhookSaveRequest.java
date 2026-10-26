package com.ruoyi.crm.domain.dto;

public class OutboundWebhookSaveRequest
{
    private String webhookId;
    private String name;
    private String url;
    private String secret;
    private String eventType;
    private String payloadTemplate;
    private Integer retryCount;
    private Integer timeoutMs;
    private String enabled;

    public String getWebhookId()
    {
        return webhookId;
    }

    public void setWebhookId(String webhookId)
    {
        this.webhookId = webhookId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getSecret()
    {
        return secret;
    }

    public void setSecret(String secret)
    {
        this.secret = secret;
    }

    public String getEventType()
    {
        return eventType;
    }

    public void setEventType(String eventType)
    {
        this.eventType = eventType;
    }

    public String getPayloadTemplate()
    {
        return payloadTemplate;
    }

    public void setPayloadTemplate(String payloadTemplate)
    {
        this.payloadTemplate = payloadTemplate;
    }

    public Integer getRetryCount()
    {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount)
    {
        this.retryCount = retryCount;
    }

    public Integer getTimeoutMs()
    {
        return timeoutMs;
    }

    public void setTimeoutMs(Integer timeoutMs)
    {
        this.timeoutMs = timeoutMs;
    }

    public String getEnabled()
    {
        return enabled;
    }

    public void setEnabled(String enabled)
    {
        this.enabled = enabled;
    }
}
