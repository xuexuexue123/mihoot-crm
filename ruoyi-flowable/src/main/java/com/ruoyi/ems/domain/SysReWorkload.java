package com.ruoyi.ems.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 工作量对象 sys_re_workload
 * 
 * @author luohd
 * @date 2023-05-22
 */
public class SysReWorkload extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编码 */
    private String workloadId;

    /** TASK编码 */
    @Excel(name = "TASK编码")
    private String taskId;

    /** 中文名 */
    @Excel(name = "中文名")
    private String taskLabel;

    /** 开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startDate;

    /** 结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endDate;

    /** 持续时长 */
    @Excel(name = "持续时长")
    private BigDecimal duration;

    /** 责任人 */
    @Excel(name = "责任人")
    private String assignee;

    /** 责任人ID */
    @Excel(name = "责任人ID")
    private String assigneeId;

    public void setWorkloadId(String workloadId) 
    {
        this.workloadId = workloadId;
    }

    public String getWorkloadId() 
    {
        return workloadId;
    }
    public void setTaskId(String taskId) 
    {
        this.taskId = taskId;
    }

    public String getTaskId() 
    {
        return taskId;
    }
    public void setTaskLabel(String taskLabel) 
    {
        this.taskLabel = taskLabel;
    }

    public String getTaskLabel() 
    {
        return taskLabel;
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
    public void setDuration(BigDecimal duration) 
    {
        this.duration = duration;
    }

    public BigDecimal getDuration() 
    {
        return duration;
    }
    public void setAssignee(String assignee) 
    {
        this.assignee = assignee;
    }

    public String getAssignee() 
    {
        return assignee;
    }
    public void setAssigneeId(String assigneeId) 
    {
        this.assigneeId = assigneeId;
    }

    public String getAssigneeId() 
    {
        return assigneeId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("workloadId", getWorkloadId())
            .append("taskId", getTaskId())
            .append("taskLabel", getTaskLabel())
            .append("startDate", getStartDate())
            .append("endDate", getEndDate())
            .append("duration", getDuration())
            .append("assignee", getAssignee())
            .append("assigneeId", getAssigneeId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
