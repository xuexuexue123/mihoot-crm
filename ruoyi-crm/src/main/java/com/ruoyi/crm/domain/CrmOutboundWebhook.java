package com.ruoyi.crm.domain;

import com.ruoyi.common.core.domain.BaseEntity;

public class CrmOutboundWebhook extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String webhookId;
    private String tenantId;
    private String name;
    private String url;
    private String secretEnc;
    private String eventType;
    private String payloadTemplate;
    private Integer retryCount;
    private Integer timeoutMs;
    private String enabled;
    private String delFlag;

    public String getWebhookId()
    {
        return webhookId;
    }

    public void setWebhookId(String webhookId)
    {
        this.webhookId = webhookId;
    }

    public String getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(String tenantId)
    {
        this.tenantId = tenantId;
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

    public String getSecretEnc()
    {
        return secretEnc;
    }

    public void setSecretEnc(String secretEnc)
    {
        this.secretEnc = secretEnc;
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

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }
}
