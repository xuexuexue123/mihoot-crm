package com.ruoyi.crm.domain.vo;

/**
 * 昨日活动摘要（成员下钻，上限 100）。
 */
public class StandupActivityBrief
{
    private String pulseId;
    private String eventType;
    private String eventCategory;
    private String startedDate;
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

    public String getStartedDate()
    {
        return startedDate;
    }

    public void setStartedDate(String startedDate)
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
