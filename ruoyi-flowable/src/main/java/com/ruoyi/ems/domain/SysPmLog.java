package com.ruoyi.ems.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 项目日志记录对象 sys_pm_log
 * 
 * @author luohd
 * @date 2023-05-17
 */
public class SysPmLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 日志主键 */
    private Long logId;

    /** 项目编号 */
    @Excel(name = "项目编号")
    private String projectId;

    /** 任务编号 */
    @Excel(name = "任务编号")
    private String taskId;

    /** 标题 */
    @Excel(name = "标题")
    private String title;

    /** 消息类型 */
    @Excel(name = "消息类型")
    private String messageType;

    /** 消息 */
    @Excel(name = "消息")
    private String message;

    /** 操作人员 */
    @Excel(name = "操作人员")
    private String operName;

    /** 操作状态 */
    @Excel(name = "操作状态")
    private Long status;

    public void setLogId(Long logId) 
    {
        this.logId = logId;
    }

    public Long getLogId() 
    {
        return logId;
    }
    public void setProjectId(String projectId) 
    {
        this.projectId = projectId;
    }

    public String getProjectId() 
    {
        return projectId;
    }
    public void setTaskId(String taskId) 
    {
        this.taskId = taskId;
    }

    public String getTaskId() 
    {
        return taskId;
    }
    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }
    public void setMessageType(String messageType) 
    {
        this.messageType = messageType;
    }

    public String getMessageType() 
    {
        return messageType;
    }
    public void setMessage(String message) 
    {
        this.message = message;
    }

    public String getMessage() 
    {
        return message;
    }
    public void setOperName(String operName) 
    {
        this.operName = operName;
    }

    public String getOperName() 
    {
        return operName;
    }
    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("logId", getLogId())
            .append("projectId", getProjectId())
            .append("taskId", getTaskId())
            .append("title", getTitle())
            .append("messageType", getMessageType())
            .append("message", getMessage())
            .append("operName", getOperName())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .toString();
    }
}
