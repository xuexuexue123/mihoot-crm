package com.ruoyi.crm.domain;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 诊所上门预约 crm_booking（与会议 crm_appointment 分离）
 */
public class CrmBooking extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String bookingId;

    private String leadId;

    private String contactId;

    private String orgId;

    @Excel(name = "预约名称")
    private String bookingName;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Asia/Shanghai")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Asia/Shanghai")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /** scheduled | confirmed | arrived | completed | noshow | cancelled */
    private String status;

    /** website | assistant | inbox | manual | miniapp | migration */
    private String source;

    private String sourceRef;

    private String mobilePhone;

    private String notes;

    private String owner;

    private Long userId;

    private Long deptId;

    /** 列表过滤：开始时间下界（非表字段） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date startTimeFrom;

    /** 列表过滤：开始时间上界（非表字段） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date startTimeTo;

    /** listbyday 日界起点（非表字段） */
    private Date dayStart;

    /** listbyday 日界终点（非表字段） */
    private Date dayEnd;

    /** 逗号多值 status 展开后的列表（非表字段） */
    private List<String> statusList;

    /** leadIds 逗号分隔展开（非表字段） */
    private List<String> leadIdList;

    /** listbyday：是否包含 cancelled，默认 false（非表字段） */
    private Boolean includeCancelled;

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

    public String getMobilePhone()
    {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone)
    {
        this.mobilePhone = mobilePhone;
    }

    public String getNotes()
    {
        return notes;
    }

    public void setNotes(String notes)
    {
        this.notes = notes;
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

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public Date getStartTimeFrom()
    {
        return startTimeFrom;
    }

    public void setStartTimeFrom(Date startTimeFrom)
    {
        this.startTimeFrom = startTimeFrom;
    }

    public Date getStartTimeTo()
    {
        return startTimeTo;
    }

    public void setStartTimeTo(Date startTimeTo)
    {
        this.startTimeTo = startTimeTo;
    }

    public Date getDayStart()
    {
        return dayStart;
    }

    public void setDayStart(Date dayStart)
    {
        this.dayStart = dayStart;
    }

    public Date getDayEnd()
    {
        return dayEnd;
    }

    public void setDayEnd(Date dayEnd)
    {
        this.dayEnd = dayEnd;
    }

    public List<String> getStatusList()
    {
        return statusList;
    }

    public void setStatusList(List<String> statusList)
    {
        this.statusList = statusList;
    }

    public List<String> getLeadIdList()
    {
        return leadIdList;
    }

    public void setLeadIdList(List<String> leadIdList)
    {
        this.leadIdList = leadIdList;
    }

    public Boolean getIncludeCancelled()
    {
        return includeCancelled;
    }

    public void setIncludeCancelled(Boolean includeCancelled)
    {
        this.includeCancelled = includeCancelled;
    }
}
