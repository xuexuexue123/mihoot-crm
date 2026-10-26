package com.ruoyi.crm.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

public class OutboundWebhookVo
{
    private String webhookId;
    private String name;
    private String url;
    private boolean secretConfigured;
    private String eventType;
    private String payloadTemplate;
    private Integer retryCount;
    private Integer timeoutMs;
    private String enabled;
    private String lastDeliveryStatus;
    private Integer lastHttpStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastDeliveryTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

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

    public boolean isSecretConfigured()
    {
        return secretConfigured;
    }

    public void setSecretConfigured(boolean secretConfigured)
    {
        this.secretConfigured = secretConfigured;
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

    public String getLastDeliveryStatus()
    {
        return lastDeliveryStatus;
    }

    public void setLastDeliveryStatus(String lastDeliveryStatus)
    {
        this.lastDeliveryStatus = lastDeliveryStatus;
    }

    public Integer getLastHttpStatus()
    {
        return lastHttpStatus;
    }

    public void setLastHttpStatus(Integer lastHttpStatus)
    {
        this.lastHttpStatus = lastHttpStatus;
    }

    public Date getLastDeliveryTime()
    {
        return lastDeliveryTime;
    }

    public void setLastDeliveryTime(Date lastDeliveryTime)
    {
        this.lastDeliveryTime = lastDeliveryTime;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }
}
