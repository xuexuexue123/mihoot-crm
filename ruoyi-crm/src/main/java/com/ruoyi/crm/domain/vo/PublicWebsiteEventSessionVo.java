package com.ruoyi.crm.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 官网公开活动场次
 */
public class PublicWebsiteEventSessionVo
{
    private String sessionId;
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date endTime;

    private String location;
    private Integer capacity;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date registrationDeadline;

    private Integer sortOrder;
    private String status;
    private int confirmedCount;
    private int waitlistCount;
    private boolean isFull;

    public String getSessionId()
    {
        return sessionId;
    }

    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public Integer getCapacity()
    {
        return capacity;
    }

    public void setCapacity(Integer capacity)
    {
        this.capacity = capacity;
    }

    public Date getRegistrationDeadline()
    {
        return registrationDeadline;
    }

    public void setRegistrationDeadline(Date registrationDeadline)
    {
        this.registrationDeadline = registrationDeadline;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public int getConfirmedCount()
    {
        return confirmedCount;
    }

    public void setConfirmedCount(int confirmedCount)
    {
        this.confirmedCount = confirmedCount;
    }

    public int getWaitlistCount()
    {
        return waitlistCount;
    }

    public void setWaitlistCount(int waitlistCount)
    {
        this.waitlistCount = waitlistCount;
    }

    public boolean isFull()
    {
        return isFull;
    }

    public void setFull(boolean full)
    {
        isFull = full;
    }
}
