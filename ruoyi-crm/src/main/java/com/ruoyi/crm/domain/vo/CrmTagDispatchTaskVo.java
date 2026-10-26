package com.ruoyi.crm.domain.vo;

/**
 * 下发任务状态
 */
public class CrmTagDispatchTaskVo
{
    private String taskId;
    private String status;
    private int total;
    private int succeeded;
    private int failed;
    private String errorMessage;

    public String getTaskId()
    {
        return taskId;
    }

    public void setTaskId(String taskId)
    {
        this.taskId = taskId;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public int getTotal()
    {
        return total;
    }

    public void setTotal(int total)
    {
        this.total = total;
    }

    public int getSucceeded()
    {
        return succeeded;
    }

    public void setSucceeded(int succeeded)
    {
        this.succeeded = succeeded;
    }

    public int getFailed()
    {
        return failed;
    }

    public void setFailed(int failed)
    {
        this.failed = failed;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }
}
