package com.ruoyi.crm.domain.dto;

import java.util.Date;

/**
 * Standup agenda booking 原始行（含主体名）。
 */
public class StandupBookingAgendaRow
{
    private String bookingId;
    private String leadId;
    private String contactId;
    private String orgId;
    private String orgName;
    private String bookingName;
    private Date startTime;
    private Date endTime;
    private String status;
    private String mobilePhone;
    private String owner;
    private Long userId;
    private String subjectName;

    public String getBookingId()
    {
        return bookingId;
    }

    public void setBookingId(String bookingId)
    {
        this.bookingId = bookingId;
    }

    public String getLeadId()
    {
        return leadId;
    }

    public void setLeadId(String leadId)
    {
        this.leadId = leadId;
    }

    public String getContactId()
    {
        return contactId;
    }

    public void setContactId(String contactId)
    {
        this.contactId = contactId;
    }

    public String getOrgId()
    {
        return orgId;
    }

    public void setOrgId(String orgId)
    {
        this.orgId = orgId;
    }

    public String getOrgName()
    {
        return orgName;
    }

    public void setOrgName(String orgName)
    {
        this.orgName = orgName;
    }

    public String getBookingName()
    {
        return bookingName;
    }

    public void setBookingName(String bookingName)
    {
        this.bookingName = bookingName;
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

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getMobilePhone()
    {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone)
    {
        this.mobilePhone = mobilePhone;
    }

    public String getOwner()
    {
        return owner;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getSubjectName()
    {
        return subjectName;
    }

    public void setSubjectName(String subjectName)
    {
        this.subjectName = subjectName;
    }
}
