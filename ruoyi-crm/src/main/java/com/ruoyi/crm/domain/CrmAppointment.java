package com.ruoyi.crm.domain;

import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 预约对象 crm_appointment
 * 
 * @author luohd
 * @date 2024-02-26
 */
public class CrmAppointment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String appointmentId;

    /** 名称 */
    @Excel(name = "名称")
    private String appointmentName;

    /** 指派人 */
    @Excel(name = "指派人")
    private String owner;

    /** 指派人id */
    @Excel(name = "指派人id")
    private String ownerId;

    /** 会议地址 */
    @Excel(name = "会议地址")
    private String location;

    /** 在线会议 （Y是 N否） */
    @Excel(name = "在线会议 ", readConverterExp = "Y=是,N=否")
    private String isVirtual;

    /** 全天会议 （Y是 N否） */
    @Excel(name = "全天会议 ", readConverterExp = "Y=是,N=否")
    private String isAllday;

    /** 重复事件 */
    @Excel(name = "重复事件")
    private String repeatType;

    /** 循环次数 */
    @Excel(name = "循环次数")
    private Long endRepeat;

    /** 时长 */
    @Excel(name = "时长")
    private Long duration;

    /** 提醒 */
    @Excel(name = "提醒")
    private Long reminder;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /** 关联ID */
    @Excel(name = "关联ID")
    private String relateId;

    /** 关联名称 */
    @Excel(name = "关联名称")
    private String relateName;

    /** 关联类型 */
    @Excel(name = "关联类型")
    private String relateType;

    /** 会议状态 */
    @Excel(name = "会议状态")
    private String appointmentStatus;

    /** 日程安排 */
    @Excel(name = "日程安排")
    private String agenda;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 部门ID */
    @Excel(name = "部门ID")
    private Long deptId;

    /** 会议纪要 */
    @Excel(name = "会议纪要")
    private String notes;

    /** 会议结论 */
    @Excel(name = "会议结论")
    private String conclusion;


    /** 参与者信息 */
    private List<CrmAttendee> crmAttendeeList;

    public void setAppointmentId(String appointmentId) 
    {
        this.appointmentId = appointmentId;
    }

    public String getAppointmentId() 
    {
        return appointmentId;
    }
    public void setAppointmentName(String appointmentName) 
    {
        this.appointmentName = appointmentName;
    }

    public String getAppointmentName() 
    {
        return appointmentName;
    }
    public void setOwner(String owner) 
    {
        this.owner = owner;
    }

    public String getOwner() 
    {
        return owner;
    }
    public void setOwnerId(String ownerId) 
    {
        this.ownerId = ownerId;
    }

    public String getOwnerId() 
    {
        return ownerId;
    }
    public void setLocation(String location) 
    {
        this.location = location;
    }

    public String getLocation() 
    {
        return location;
    }
    public void setIsVirtual(String isVirtual) 
    {
        this.isVirtual = isVirtual;
    }

    public String getIsVirtual() 
    {
        return isVirtual;
    }
    public void setIsAllday(String isAllday) 
    {
        this.isAllday = isAllday;
    }

    public String getIsAllday() 
    {
        return isAllday;
    }
    public void setRepeatType(String repeatType) 
    {
        this.repeatType = repeatType;
    }

    public String getRepeatType() 
    {
        return repeatType;
    }
    public void setEndRepeat(Long endRepeat) 
    {
        this.endRepeat = endRepeat;
    }

    public Long getEndRepeat() 
    {
        return endRepeat;
    }
    public void setDuration(Long duration) 
    {
        this.duration = duration;
    }

    public Long getDuration() 
    {
        return duration;
    }
    public void setReminder(Long reminder) 
    {
        this.reminder = reminder;
    }

    public Long getReminder() 
    {
        return reminder;
    }
    public void setStartTime(Date startTime) 
    {
        this.startTime = startTime;
    }

    public Date getStartTime() 
    {
        return startTime;
    }
    public void setEndTime(Date endTime) 
    {
        this.endTime = endTime;
    }

    public Date getEndTime() 
    {
        return endTime;
    }
    public void setRelateId(String relateId) 
    {
        this.relateId = relateId;
    }

    public String getRelateId() 
    {
        return relateId;
    }
    public void setRelateName(String relateName) 
    {
        this.relateName = relateName;
    }

    public String getRelateName() 
    {
        return relateName;
    }
    public void setRelateType(String relateType) 
    {
        this.relateType = relateType;
    }

    public String getRelateType() 
    {
        return relateType;
    }
    public void setAppointmentStatus(String appointmentStatus) 
    {
        this.appointmentStatus = appointmentStatus;
    }

    public String getAppointmentStatus() 
    {
        return appointmentStatus;
    }
    public void setAgenda(String agenda) 
    {
        this.agenda = agenda;
    }

    public String getAgenda() 
    {
        return agenda;
    }

    public List<CrmAttendee> getCrmAttendeeList()
    {
        return crmAttendeeList;
    }

    public void setCrmAttendeeList(List<CrmAttendee> crmAttendeeList)
    {
        this.crmAttendeeList = crmAttendeeList;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    public String getNotes()
    {
        return notes;
    }

    public void setConclusion(String conclusion)
    {
        this.conclusion = conclusion;
    }

    public String getConclusion()
    {
        return conclusion;
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("appointmentId", getAppointmentId())
            .append("appointmentName", getAppointmentName())
            .append("owner", getOwner())
            .append("ownerId", getOwnerId())
            .append("location", getLocation())
            .append("isVirtual", getIsVirtual())
            .append("isAllday", getIsAllday())
            .append("repeatType", getRepeatType())
            .append("endRepeat", getEndRepeat())
            .append("duration", getDuration())
            .append("reminder", getReminder())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("relateId", getRelateId())
            .append("relateName", getRelateName())
            .append("relateType", getRelateType())
            .append("appointmentStatus", getAppointmentStatus())
            .append("agenda", getAgenda())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("crmAttendeeList", getCrmAttendeeList())
            .toString();
    }
}
