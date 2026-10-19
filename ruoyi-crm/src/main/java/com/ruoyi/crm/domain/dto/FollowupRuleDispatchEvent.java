package com.ruoyi.crm.domain.dto;

import java.time.Instant;
import com.alibaba.fastjson2.JSONObject;

/**
 * 回访规则领域事件（进程内 dispatch，契约见 followup-rule-event-contract.md）
 */
public class FollowupRuleDispatchEvent
{
    private String tenant;
    private String triggerType;
    private Instant occurredAt;
    private String source;
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

    public String getTriggerType()
    {
        return triggerType;
    }

    public void setTriggerType(String triggerType)
    {
        this.triggerType = triggerType;
    }

    public Instant getOccurredAt()
    {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt)
    {
        this.occurredAt = occurredAt;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
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
