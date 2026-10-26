package com.ruoyi.crm.domain.dto;

import java.util.Map;

/**
 * Dispatch 至 Next /internal/event-trigger/dispatch/ 的请求体材料
 */
public class EventTriggerDispatchPayload
{
    private String pulseId;

    private String eventType;

    private String subjectType;

    private String subjectId;

    private String occurredAt;

    private String subjectEntity;

    /** 全量业务对象；查不到时为 null */
    private Map<String, Object> subject;

    public String getPulseId()
    {
        return pulseId;
    }

    public void setPulseId(String pulseId)
    {
        this.pulseId = pulseId;
    }

    public String getEventType()
    {
        return eventType;
    }

    public void setEventType(String eventType)
    {
        this.eventType = eventType;
    }

    public String getSubjectType()
    {
        return subjectType;
    }

    public void setSubjectType(String subjectType)
    {
        this.subjectType = subjectType;
    }

    public String getSubjectId()
    {
        return subjectId;
    }

    public void setSubjectId(String subjectId)
    {
        this.subjectId = subjectId;
    }

    public String getOccurredAt()
    {
        return occurredAt;
    }

    public void setOccurredAt(String occurredAt)
    {
        this.occurredAt = occurredAt;
    }

    public String getSubjectEntity()
    {
        return subjectEntity;
    }

    public void setSubjectEntity(String subjectEntity)
    {
        this.subjectEntity = subjectEntity;
    }

    public Map<String, Object> getSubject()
    {
        return subject;
    }

    public void setSubject(Map<String, Object> subject)
    {
        this.subject = subject;
    }
}
