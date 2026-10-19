package com.ruoyi.crm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * WWaitResume对象 w_wait_resume
 *
 * @author luohd
 * @date 2026-05-19
 */
public class WWaitResume extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String id;

    @Excel(name = "恢复Key")
    private String resumeKey;

    @Excel(name = "工作流ID")
    private String workflowId;

    @Excel(name = "执行ID")
    private String executionId;

    @Excel(name = "节点ID")
    private String nodeId;

    @Excel(name = "节点名称")
    private String nodeName;

    @Excel(name = "模式")
    private String mode;

    @Excel(name = "HTTP方法")
    private String httpMethod;

    private String authConfig;

    private String responseConfig;

    private String formSchema;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "过期时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date expiresAt;

    @Excel(name = "Inngest事件ID")
    private String inngestEventId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "恢复时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date resumedAt;

    private String payload;

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    public void setResumeKey(String resumeKey)
    {
        this.resumeKey = resumeKey;
    }

    public String getResumeKey()
    {
        return resumeKey;
    }

    public void setWorkflowId(String workflowId)
    {
        this.workflowId = workflowId;
    }

    public String getWorkflowId()
    {
        return workflowId;
    }

    public void setExecutionId(String executionId)
    {
        this.executionId = executionId;
    }

    public String getExecutionId()
    {
        return executionId;
    }

    public void setNodeId(String nodeId)
    {
        this.nodeId = nodeId;
    }

    public String getNodeId()
    {
        return nodeId;
    }

    public void setNodeName(String nodeName)
    {
        this.nodeName = nodeName;
    }

    public String getNodeName()
    {
        return nodeName;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    public String getMode()
    {
        return mode;
    }

    public void setHttpMethod(String httpMethod)
    {
        this.httpMethod = httpMethod;
    }

    public String getHttpMethod()
    {
        return httpMethod;
    }

    public void setAuthConfig(String authConfig)
    {
        this.authConfig = authConfig;
    }

    public String getAuthConfig()
    {
        return authConfig;
    }

    public void setResponseConfig(String responseConfig)
    {
        this.responseConfig = responseConfig;
    }

    public String getResponseConfig()
    {
        return responseConfig;
    }

    public void setFormSchema(String formSchema)
    {
        this.formSchema = formSchema;
    }

    public String getFormSchema()
    {
        return formSchema;
    }

    public void setExpiresAt(Date expiresAt)
    {
        this.expiresAt = expiresAt;
    }

    public Date getExpiresAt()
    {
        return expiresAt;
    }

    public void setInngestEventId(String inngestEventId)
    {
        this.inngestEventId = inngestEventId;
    }

    public String getInngestEventId()
    {
        return inngestEventId;
    }

    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }

    public void setUpdatedAt(Date updatedAt)
    {
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt()
    {
        return updatedAt;
    }

    public void setResumedAt(Date resumedAt)
    {
        this.resumedAt = resumedAt;
    }

    public Date getResumedAt()
    {
        return resumedAt;
    }

    public void setPayload(String payload)
    {
        this.payload = payload;
    }

    public String getPayload()
    {
        return payload;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("resumeKey", getResumeKey())
            .append("workflowId", getWorkflowId())
            .append("executionId", getExecutionId())
            .append("nodeId", getNodeId())
            .append("nodeName", getNodeName())
            .append("mode", getMode())
            .append("httpMethod", getHttpMethod())
            .append("authConfig", getAuthConfig())
            .append("responseConfig", getResponseConfig())
            .append("formSchema", getFormSchema())
            .append("expiresAt", getExpiresAt())
            .append("inngestEventId", getInngestEventId())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .append("resumedAt", getResumedAt())
            .append("payload", getPayload())
            .toString();
    }
}
