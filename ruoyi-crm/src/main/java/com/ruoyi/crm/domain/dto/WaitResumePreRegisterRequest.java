package com.ruoyi.crm.domain.dto;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Wait 节点预注册请求
 */
public class WaitResumePreRegisterRequest
{
    private String id;
    private String resumeKey;
    private String workflowId;
    private String executionId;
    private String inngestEventId;
    private String nodeId;
    private String nodeName;
    private String mode;
    private String httpMethod;
    private String authConfig;
    private String responseConfig;
    private String formSchema;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date expiresAt;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getResumeKey()
    {
        return resumeKey;
    }

    public void setResumeKey(String resumeKey)
    {
        this.resumeKey = resumeKey;
    }

    public String getWorkflowId()
    {
        return workflowId;
    }

    public void setWorkflowId(String workflowId)
    {
        this.workflowId = workflowId;
    }

    public String getExecutionId()
    {
        return executionId;
    }

    public void setExecutionId(String executionId)
    {
        this.executionId = executionId;
    }

    public String getInngestEventId()
    {
        return inngestEventId;
    }

    public void setInngestEventId(String inngestEventId)
    {
        this.inngestEventId = inngestEventId;
    }

    public String getNodeId()
    {
        return nodeId;
    }

    public void setNodeId(String nodeId)
    {
        this.nodeId = nodeId;
    }

    public String getNodeName()
    {
        return nodeName;
    }

    public void setNodeName(String nodeName)
    {
        this.nodeName = nodeName;
    }

    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    public String getHttpMethod()
    {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod)
    {
        this.httpMethod = httpMethod;
    }

    public String getAuthConfig()
    {
        return authConfig;
    }

    public void setAuthConfig(String authConfig)
    {
        this.authConfig = authConfig;
    }

    public String getResponseConfig()
    {
        return responseConfig;
    }

    public void setResponseConfig(String responseConfig)
    {
        this.responseConfig = responseConfig;
    }

    public String getFormSchema()
    {
        return formSchema;
    }

    public void setFormSchema(String formSchema)
    {
        this.formSchema = formSchema;
    }

    public Date getExpiresAt()
    {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt)
    {
        this.expiresAt = expiresAt;
    }
}
