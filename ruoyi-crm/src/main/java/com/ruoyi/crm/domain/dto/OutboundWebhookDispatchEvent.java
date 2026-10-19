package com.ruoyi.crm.domain.dto;

import java.time.Instant;
import com.alibaba.fastjson2.JSONObject;

public class OutboundWebhookDispatchEvent
{
    private String tenant;
    private String eventType;
    private Instant occurredAt;
    private String sourceRef;
    private JSONObject payload;

    public String getTenant()
    {
        return tenant;
    }

    public void setTenant(String tenant)
    {
        this.tenant = tenant;
    }

    public String getEventType()
    {
        return eventType;
    }

    public void setEventType(String eventType)
    {
        this.eventType = eventType;
    }

    public Instant getOccurredAt()
    {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt)
    {
        this.occurredAt = occurredAt;
    }

    public String getSourceRef()
    {
        return sourceRef;
    }

    public void setSourceRef(String sourceRef)
    {
        this.sourceRef = sourceRef;
    }

    public JSONObject getPayload()
    {
        return payload;
    }

    public void setPayload(JSONObject payload)
    {
        this.payload = payload;
    }
}
