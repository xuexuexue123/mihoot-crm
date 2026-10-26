package com.ruoyi.crm.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 工作流定时任务 w_scheduled_task
 */
public class WScheduledTask extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String id;

    @Excel(name = "任务名称")
    private String name;

    @Excel(name = "工作流ID")
    private String workflowId;

    @Excel(name = "工作流名称")
    private String workflowName;

    /** daily | weekly */
    @Excel(name = "调度类型")
    private String scheduleType;

    /** HH:mm，分钟仅 00 或 30 */
    @Excel(name = "触发时刻")
    private String runTime;

    /** weekly: 逗号分隔 1-7（ISO） */
    @Excel(name = "星期")
    private String daysOfWeek;

    /** 1 启用 / 0 停用 */
    @Excel(name = "启用")
    private String enabled;

    /** 上次成功抢占的 slot */
    private String lastFiredSlot;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getWorkflowId()
    {
        return workflowId;
    }

    public void setWorkflowId(String workflowId)
    {
        this.workflowId = workflowId;
    }

    public String getWorkflowName()
    {
        return workflowName;
    }

    public void setWorkflowName(String workflowName)
    {
        this.workflowName = workflowName;
    }

    public String getScheduleType()
    {
        return scheduleType;
    }

    public void setScheduleType(String scheduleType)
    {
        this.scheduleType = scheduleType;
    }

    public String getRunTime()
    {
        return runTime;
    }

    public void setRunTime(String runTime)
    {
        this.runTime = runTime;
    }

    public String getDaysOfWeek()
    {
        return daysOfWeek;
    }

    public void setDaysOfWeek(String daysOfWeek)
    {
        this.daysOfWeek = daysOfWeek;
    }

    public String getEnabled()
    {
        return enabled;
    }

    public void setEnabled(String enabled)
    {
        this.enabled = enabled;
    }

    public String getLastFiredSlot()
    {
        return lastFiredSlot;
    }

    public void setLastFiredSlot(String lastFiredSlot)
    {
        this.lastFiredSlot = lastFiredSlot;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("workflowId", getWorkflowId())
            .append("workflowName", getWorkflowName())
            .append("scheduleType", getScheduleType())
            .append("runTime", getRunTime())
            .append("daysOfWeek", getDaysOfWeek())
            .append("enabled", getEnabled())
            .append("lastFiredSlot", getLastFiredSlot())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
