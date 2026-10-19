package com.ruoyi.crm.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 差旅对象 crm_travel
 * 
 * @author luohd
 * @date 2024-03-13
 */
public class CrmTravel extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String travelId;

    /** 类型 */
//    @Excel(name = "类型")
    private String travelType;

    /** 编号 */
//    @Excel(name = "编号")
    private String projectId;

    /** 项目名 */
    @Excel(name = "项目名")
    private String projectName;

    /** 申请人 */
    @Excel(name = "申请人")
    private String applicant;

    /** 地址 */
    @Excel(name = "地址")
    private String location;

    /** 开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startDate;

    /** 结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endDate;

    /** 时长 */
    @Excel(name = "时长")
    private Long duration;

    /** 出差目的 */
    @Excel(name = "出差目的")
    private String purpose;

    /** 工作安排 */
    @Excel(name = "工作安排")
    private String workSchedule;

    /** 费用估算 */
    @Excel(name = "费用估算")
    private BigDecimal assumedCost;

    /** 需要审批 */
    @Excel(name = "需要审批")
    private Long requireApproval;

    /** 审批人 */
    @Excel(name = "审批人")
    private String approver;

    /** 审批时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "审批时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date approverTime;

    /** 是否核销 */
//    @Excel(name = "是否核销")
    private Long isReply;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    /** 描述 */
    @Excel(name = "核销描述")
    private String replyDescription;

    /** 用户ID */
//    @Excel(name = "用户ID")
    private Long userId;

    /** 部门ID */
//    @Excel(name = "部门ID")
    private Long deptId;

    /** 关联任务 */
    @Excel(name = "关联任务")
    private String relateTask;

    /** 关联任务ID */
//    @Excel(name = "关联任务ID")
    private String relateTaskid;

    /** 行程信息 */
    private List<CrmItinerary> crmItineraryList;

    public void setTravelId(String travelId) 
    {
        this.travelId = travelId;
    }

    public String getTravelId() 
    {
        return travelId;
    }
    public void setTravelType(String travelType) 
    {
        this.travelType = travelType;
    }

    public String getTravelType() 
    {
        return travelType;
    }
    public void setProjectId(String projectId) 
    {
        this.projectId = projectId;
    }

    public String getProjectId() 
    {
        return projectId;
    }
    public void setProjectName(String projectName) 
    {
        this.projectName = projectName;
    }

    public String getProjectName() 
    {
        return projectName;
    }
    public void setApplicant(String applicant) 
    {
        this.applicant = applicant;
    }

    public String getApplicant() 
    {
        return applicant;
    }
    public void setLocation(String location) 
    {
        this.location = location;
    }

    public String getLocation() 
    {
        return location;
    }
    public void setStartDate(Date startDate) 
    {
        this.startDate = startDate;
    }

    public Date getStartDate() 
    {
        return startDate;
    }
    public void setEndDate(Date endDate) 
    {
        this.endDate = endDate;
    }

    public Date getEndDate() 
    {
        return endDate;
    }
    public void setDuration(Long duration) 
    {
        this.duration = duration;
    }

    public Long getDuration() 
    {
        return duration;
    }
    public void setPurpose(String purpose) 
    {
        this.purpose = purpose;
    }

    public String getPurpose() 
    {
        return purpose;
    }
    public void setWorkSchedule(String workSchedule) 
    {
        this.workSchedule = workSchedule;
    }

    public String getWorkSchedule() 
    {
        return workSchedule;
    }
    public void setAssumedCost(BigDecimal assumedCost) 
    {
        this.assumedCost = assumedCost;
    }

    public BigDecimal getAssumedCost() 
    {
        return assumedCost;
    }
    public void setRequireApproval(Long requireApproval) 
    {
        this.requireApproval = requireApproval;
    }

    public Long getRequireApproval() 
    {
        return requireApproval;
    }
    public void setApprover(String approver) 
    {
        this.approver = approver;
    }

    public String getApprover() 
    {
        return approver;
    }
    public void setApproverTime(Date approverTime) 
    {
        this.approverTime = approverTime;
    }

    public Date getApproverTime() 
    {
        return approverTime;
    }
    public void setIsReply(Long isReply) 
    {
        this.isReply = isReply;
    }

    public Long getIsReply() 
    {
        return isReply;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }
    public void setReplyDescription(String replyDescription) 
    {
        this.replyDescription = replyDescription;
    }

    public String getReplyDescription() 
    {
        return replyDescription;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }
    public void setRelateTask(String relateTask) 
    {
        this.relateTask = relateTask;
    }

    public String getRelateTask() 
    {
        return relateTask;
    }
    public void setRelateTaskid(String relateTaskid) 
    {
        this.relateTaskid = relateTaskid;
    }

    public String getRelateTaskid() 
    {
        return relateTaskid;
    }

    public List<CrmItinerary> getCrmItineraryList()
    {
        return crmItineraryList;
    }

    public void setCrmItineraryList(List<CrmItinerary> crmItineraryList)
    {
        this.crmItineraryList = crmItineraryList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("travelId", getTravelId())
            .append("travelType", getTravelType())
            .append("projectId", getProjectId())
            .append("projectName", getProjectName())
            .append("applicant", getApplicant())
            .append("location", getLocation())
            .append("startDate", getStartDate())
            .append("endDate", getEndDate())
            .append("duration", getDuration())
            .append("purpose", getPurpose())
            .append("workSchedule", getWorkSchedule())
            .append("assumedCost", getAssumedCost())
            .append("requireApproval", getRequireApproval())
            .append("approver", getApprover())
            .append("approverTime", getApproverTime())
            .append("isReply", getIsReply())
            .append("status", getStatus())
            .append("description", getDescription())
            .append("replyDescription", getReplyDescription())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("userId", getUserId())
            .append("deptId", getDeptId())
            .append("relateTask", getRelateTask())
            .append("relateTaskid", getRelateTaskid())
            .append("crmItineraryList", getCrmItineraryList())
            .toString();
    }
}
