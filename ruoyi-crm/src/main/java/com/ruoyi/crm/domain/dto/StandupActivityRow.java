package com.ruoyi.crm.domain.dto;

import java.util.Date;

/**
 * 昨日活动原始行。
 */
public class StandupActivityRow
{
    private String pulseId;
    private String eventType;
    private String eventCategory;
    private Date startedDate;
    private String relateName;
    private String subjectType;
    private String subjectId;

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

    public String getEventCategory()
    {
        return eventCategory;
    }

    public void setEventCategory(String eventCategory)
    {
        this.eventCategory = eventCategory;
    }

    public Date getStartedDate()
    {
        return startedDate;
    }

    public void setStartedDate(Date startedDate)
    {
        this.startedDate = startedDate;
    }

    public String getRelateName()
    {
        return relateName;
    }

    public void setRelateName(String relateName)
    {
        this.relateName = relateName;
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
}
