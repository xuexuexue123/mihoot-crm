package com.ruoyi.ems.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 任务对象 sys_re_worktask
 *
 * @author luohd
 * @date 2023-05-19
 */
public class SysReWorktask extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编码 */
    private String taskId;

    /** 项目编号 */
    @Excel(name = "项目编号")
    private String projectId;

    /** boardId */
    @Excel(name = "boardId")
    private String boardId;

    /** 中文名 */
    @Excel(name = "中文名")
    private String taskLabel;

    /** 优先级 */
    @Excel(name = "优先级")
    private String priority;

    /** 责任人 */
    @Excel(name = "责任人")
    private String assignee;

    /** 截止日 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "截止日", width = 30, dateFormat = "yyyy-MM-dd")
    private Date dueDate;

    /** 类别 */
    @Excel(name = "类别")
    private String category;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    /** 进度 */
    @Excel(name = "进度")
    private Long percentage;

    /** 计划开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "计划开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date planStartDate;

    /** 计划结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "计划结束日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date planEndDate;

    /** 实际开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "实际开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date realStartDate;

    /** 实际结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "实际结束日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date realEndDate;

    /** 工时估算 */
    @Excel(name = "工时估算")
    private BigDecimal planDuration;

    /** 人力资源 */
    @Excel(name = "人力资源")
    private Long humanResource;

    /** 费用估算 */
    @Excel(name = "费用估算")
    private BigDecimal costEstimation;

    /** 实际工时 */
    @Excel(name = "实际工时")
    private BigDecimal realDuration;

    /** 排序 */
    @Excel(name = "排序")
    private Long workOrder;

    /** 项目名 */
    @Excel(name = "项目名")
    private String projectName;

    public void setTaskId(String taskId)
    {
        this.taskId = taskId;
    }

    public String getTaskId()
    {
        return taskId;
    }
    public void setProjectId(String projectId)
    {
        this.projectId = projectId;
    }

    public String getProjectId()
    {
        return projectId;
    }
    public void setBoardId(String boardId)
    {
        this.boardId = boardId;
    }

    public String getBoardId()
    {
        return boardId;
    }
    public void setTaskLabel(String taskLabel)
    {
        this.taskLabel = taskLabel;
    }

    public String getTaskLabel()
    {
        return taskLabel;
    }
    public void setPriority(String priority)
    {
        this.priority = priority;
    }

    public String getPriority()
    {
        return priority;
    }
    public void setAssignee(String assignee)
    {
        this.assignee = assignee;
    }

    public String getAssignee()
    {
        return assignee;
    }
    public void setDueDate(Date dueDate)
    {
        this.dueDate = dueDate;
    }

    public Date getDueDate()
    {
        return dueDate;
    }
    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getCategory()
    {
        return category;
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
    public void setPercentage(Long percentage)
    {
        this.percentage = percentage;
    }

    public Long getPercentage()
    {
        return percentage;
    }
    public void setPlanStartDate(Date planStartDate)
    {
        this.planStartDate = planStartDate;
    }

    public Date getPlanStartDate()
    {
        return planStartDate;
    }
    public void setPlanEndDate(Date planEndDate)
    {
        this.planEndDate = planEndDate;
    }

    public Date getPlanEndDate()
    {
        return planEndDate;
    }
    public void setRealStartDate(Date realStartDate)
    {
        this.realStartDate = realStartDate;
    }

    public Date getRealStartDate()
    {
        return realStartDate;
    }
    public void setRealEndDate(Date realEndDate)
    {
        this.realEndDate = realEndDate;
    }

    public Date getRealEndDate()
    {
        return realEndDate;
    }
    public void setPlanDuration(BigDecimal planDuration)
    {
        this.planDuration = planDuration;
    }

    public BigDecimal getPlanDuration()
    {
        return planDuration;
    }
    public void setHumanResource(Long humanResource)
    {
        this.humanResource = humanResource;
    }

    public Long getHumanResource()
    {
        return humanResource;
    }
    public void setCostEstimation(BigDecimal costEstimation)
    {
        this.costEstimation = costEstimation;
    }

    public BigDecimal getCostEstimation()
    {
        return costEstimation;
    }
    public void setRealDuration(BigDecimal realDuration)
    {
        this.realDuration = realDuration;
    }

    public BigDecimal getRealDuration()
    {
        return realDuration;
    }
    public void setWorkOrder(Long workOrder)
    {
        this.workOrder = workOrder;
    }

    public Long getWorkOrder()
    {
        return workOrder;
    }
    public void setProjectName(String projectName)
    {
        this.projectName = projectName;
    }

    public String getProjectName()
    {
        return projectName;
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("taskId", getTaskId())
                .append("projectId", getProjectId())
                .append("boardId", getBoardId())
                .append("taskLabel", getTaskLabel())
                .append("priority", getPriority())
                .append("assignee", getAssignee())
                .append("dueDate", getDueDate())
                .append("category", getCategory())
                .append("status", getStatus())
                .append("description", getDescription())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("percentage", getPercentage())
                .append("planStartDate", getPlanStartDate())
                .append("planEndDate", getPlanEndDate())
                .append("realStartDate", getRealStartDate())
                .append("realEndDate", getRealEndDate())
                .append("planDuration", getPlanDuration())
                .append("humanResource", getHumanResource())
                .append("costEstimation", getCostEstimation())
                .append("realDuration", getRealDuration())
                .append("workOrder", getWorkOrder())
                .toString();
    }
}
