package com.ruoyi.crm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * WExecution对象 w_execution
 * 
 * @author luohd
 * @date 2026-05-19
 */
public class WExecution extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String id;

    @Excel(name = "工作流ID")
    private String workflowId;

    @Excel(name = "状态")
    private String status;

    @Excel(name = "错误信息")
    private String error;

    private String errorStack;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date startedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "完成时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date completedAt;

    @Excel(name = "Inngest事件ID")
    private String inngestEventId;

    private String output;

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    public void setWorkflowId(String workflowId)
    {
        this.workflowId = workflowId;
    }

    public String getWorkflowId()
    {
        return workflowId;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public void setError(String error)
    {
        this.error = error;
    }

    public String getError()
    {
        return error;
    }

    public void setErrorStack(String errorStack)
    {
        this.errorStack = errorStack;
    }

    public String getErrorStack()
    {
        return errorStack;
    }

    public void setStartedAt(Date startedAt)
    {
        this.startedAt = startedAt;
    }

    public Date getStartedAt()
    {
        return startedAt;
    }

    public void setCompletedAt(Date completedAt)
    {
        this.completedAt = completedAt;
    }

    public Date getCompletedAt()
    {
        return completedAt;
    }

    public void setInngestEventId(String inngestEventId)
    {
        this.inngestEventId = inngestEventId;
    }

    public String getInngestEventId()
    {
        return inngestEventId;
    }

    public void setOutput(String output)
    {
        this.output = output;
    }

    public String getOutput()
    {
        return output;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("workflowId", getWorkflowId())
            .append("status", getStatus())
            .append("error", getError())
            .append("errorStack", getErrorStack())
            .append("startedAt", getStartedAt())
            .append("completedAt", getCompletedAt())
            .append("inngestEventId", getInngestEventId())
            .append("output", getOutput())
            .toString();
    }
}
