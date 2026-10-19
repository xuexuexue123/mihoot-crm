package com.ruoyi.ems.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * case历史对象 sys_hi_audittrail
 * 
 * @author luohd
 * @date 2023-01-06
 */
public class SysAudittrail extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private String id;

    /** caseid */
    @Excel(name = "caseid")
    private String pyCaseId;

    /** 类型 */
    @Excel(name = "类型")
    private String type;

    /** 创建人 */
    @Excel(name = "创建人")
    private String userId;

    /** 任务ID */
    @Excel(name = "任务ID")
    private String taskId;

    /** 流程实例id */
    @Excel(name = "流程实例id")
    private String procInstId;

    /** 动作 */
    @Excel(name = "动作")
    private String action;

    /** 消息 */
    @Excel(name = "消息")
    private String message;

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }
    public void setPyCaseId(String pyCaseId) 
    {
        this.pyCaseId = pyCaseId;
    }

    public String getPyCaseId() 
    {
        return pyCaseId;
    }
    public void setType(String type) 
    {
        this.type = type;
    }

    public String getType() 
    {
        return type;
    }
    public void setUserId(String userId) 
    {
        this.userId = userId;
    }

    public String getUserId() 
    {
        return userId;
    }
    public void setTaskId(String taskId) 
    {
        this.taskId = taskId;
    }

    public String getTaskId() 
    {
        return taskId;
    }
    public void setProcInstId(String procInstId) 
    {
        this.procInstId = procInstId;
    }

    public String getProcInstId() 
    {
        return procInstId;
    }
    public void setAction(String action) 
    {
        this.action = action;
    }

    public String getAction() 
    {
        return action;
    }
    public void setMessage(String message) 
    {
        this.message = message;
    }

    public String getMessage() 
    {
        return message;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("pyCaseId", getPyCaseId())
            .append("type", getType())
            .append("createTime", getCreateTime())
            .append("userId", getUserId())
            .append("taskId", getTaskId())
            .append("procInstId", getProcInstId())
            .append("action", getAction())
            .append("message", getMessage())
            .toString();
    }
}
