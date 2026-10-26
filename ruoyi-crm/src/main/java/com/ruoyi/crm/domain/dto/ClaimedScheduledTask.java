package com.ruoyi.crm.domain.dto;

/**
 * claim-due 单条抢占结果
 */
public class ClaimedScheduledTask
{
    private String tenant;
    private String taskId;
    private String taskName;
    private String workflowId;

    public ClaimedScheduledTask()
    {
    }

    public ClaimedScheduledTask(String tenant, String taskId, String taskName, String workflowId)
    {
        this.tenant = tenant;
        this.taskId = taskId;
        this.taskName = taskName;
        this.workflowId = workflowId;
    }

    public String getTenant()
    {
        return tenant;
    }

    public void setTenant(String tenant)
    {
        this.tenant = tenant;
    }

    public String getTaskId()
    {
        return taskId;
    }

    public void setTaskId(String taskId)
    {
        this.taskId = taskId;
    }

    public String getTaskName()
    {
        return taskName;
    }

    public void setTaskName(String taskName)
    {
        this.taskName = taskName;
    }

    public String getWorkflowId()
    {
        return workflowId;
    }

    public void setWorkflowId(String workflowId)
    {
        this.workflowId = workflowId;
    }
}
