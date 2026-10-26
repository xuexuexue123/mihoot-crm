package com.ruoyi.crm.domain.dto;

/**
 * 线索指派请求
 */
public class LeadAssignRequest
{
    private Long userId;
    private String snUserid;
    private String trigger = "manager";

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getSnUserid()
    {
        return snUserid;
    }

    public void setSnUserid(String snUserid)
    {
        this.snUserid = snUserid;
    }

    public String getTrigger()
    {
        return trigger;
    }

    public void setTrigger(String trigger)
    {
        this.trigger = trigger;
    }
}
