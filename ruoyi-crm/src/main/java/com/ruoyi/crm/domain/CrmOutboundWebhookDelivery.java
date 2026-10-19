package com.ruoyi.crm.domain;

import java.util.Date;

public class CrmOutboundWebhookDelivery
{
    private String deliveryId;
    private String tenantId;
    private String webhookId;
    private String eventId;
    private String eventType;
    private String source;
    private String status;
    private Integer attempt;
    private Integer httpStatus;
    private String errorMessage;
    private String requestId;
    private Date nextRetryAt;
    private String payloadJson;
    private Date createTime;
    private Date updateTime;

    public String getDeliveryId()
    {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId)
    {
        this.deliveryId = deliveryId;
    }

    public String getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(String tenantId)
    {
        this.tenantId = tenantId;
    }

    public String getWebhookId()
    {
        return webhookId;
    }

    public void setWebhookId(String webhookId)
    {
        this.webhookId = webhookId;
    }

    public String getEventId()
    {
        return eventId;
    }

    public void setEventId(String eventId)
    {
        this.eventId = eventId;
    }

    public String getEventType()
    {
        return eventType;
    }

    public void setEventType(String eventType)
    {
        this.eventType = eventType;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Integer getAttempt()
    {
        return attempt;
    }

    public void setAttempt(Integer attempt)
    {
        this.attempt = attempt;
    }

    public Integer getHttpStatus()
    {
        return httpStatus;
    }

    public void setHttpStatus(Integer httpStatus)
    {
        this.httpStatus = httpStatus;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getRequestId()
    {
        return requestId;
    }

    public void setRequestId(String requestId)
    {
        this.requestId = requestId;
    }

    public Date getNextRetryAt()
    {
        return nextRetryAt;
    }

    public void setNextRetryAt(Date nextRetryAt)
    {
        this.nextRetryAt = nextRetryAt;
    }

    public String getPayloadJson()
    {
        return payloadJson;
    }

    public void setPayloadJson(String payloadJson)
    {
        this.payloadJson = payloadJson;
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
