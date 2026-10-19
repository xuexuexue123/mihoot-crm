package com.ruoyi.crm.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 参与者对象 crm_attendee
 * 
 * @author luohd
 * @date 2024-02-16
 */
public class CrmAttendee extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String attendeeId;

    /** 会议ID */
    @Excel(name = "会议ID")
    private String appointmentId;

    /** 姓名 */
    @Excel(name = "姓名")
    private String attendeeName;

    /** 参与者类型 */
    @Excel(name = "参与者类型")
    private String attendeeType;

    /** 参与者ID */
    @Excel(name = "参与者ID")
    private String attendeeRefid;

    /** 机构ID */
    @Excel(name = "机构ID")
    private String orgId;


    /** 机构名称 */
    @Excel(name = "机构名称")
    private String fullName;

    public void setAttendeeId(String attendeeId) 
    {
        this.attendeeId = attendeeId;
    }

    public String getAttendeeId() 
    {
        return attendeeId;
    }
    public void setAppointmentId(String appointmentId) 
    {
        this.appointmentId = appointmentId;
    }

    public String getAppointmentId() 
    {
        return appointmentId;
    }
    public void setAttendeeName(String attendeeName) 
    {
        this.attendeeName = attendeeName;
    }

    public String getAttendeeName() 
    {
        return attendeeName;
    }
    public void setAttendeeType(String attendeeType) 
    {
        this.attendeeType = attendeeType;
    }

    public String getAttendeeType() 
    {
        return attendeeType;
    }
    public void setAttendeeRefid(String attendeeRefid) 
    {
        this.attendeeRefid = attendeeRefid;
    }

    public String getAttendeeRefid() 
    {
        return attendeeRefid;
    }

    public void setOrgId(String orgId)
    {
        this.orgId = orgId;
    }

    public String getOrgId()
    {
        return orgId;
    }
    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public String getFullName()
    {
        return fullName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("attendeeId", getAttendeeId())
            .append("appointmentId", getAppointmentId())
            .append("attendeeName", getAttendeeName())
            .append("attendeeType", getAttendeeType())
            .append("attendeeRefid", getAttendeeRefid())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
