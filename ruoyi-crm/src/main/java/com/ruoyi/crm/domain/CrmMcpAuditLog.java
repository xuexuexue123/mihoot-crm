package com.ruoyi.crm.domain;

import java.util.Date;

/**
 * MCP tools/call 审计 crm_mcp_audit_log
 */
public class CrmMcpAuditLog
{
    private Long id;
    private String tenantId;
    private Long apiKeyId;
    private String boundUserId;
    private String toolName;
    private Integer ok;
    private String errorCode;
    private Integer latencyMs;
    private Date createTime;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(String tenantId)
    {
        this.tenantId = tenantId;
    }

    public Long getApiKeyId()
    {
        return apiKeyId;
    }

    public void setApiKeyId(Long apiKeyId)
    {
        this.apiKeyId = apiKeyId;
    }

    public String getBoundUserId()
    {
        return boundUserId;
    }

    public void setBoundUserId(String boundUserId)
    {
        this.boundUserId = boundUserId;
    }

    public String getToolName()
    {
        return toolName;
    }

    public void setToolName(String toolName)
    {
        this.toolName = toolName;
    }

    public Integer getOk()
    {
        return ok;
    }

    public void setOk(Integer ok)
    {
        this.ok = ok;
    }

    public String getErrorCode()
    {
        return errorCode;
    }

    public void setErrorCode(String errorCode)
    {
        this.errorCode = errorCode;
    }

    public Integer getLatencyMs()
    {
        return latencyMs;
    }

    public void setLatencyMs(Integer latencyMs)
    {
        this.latencyMs = latencyMs;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }
}
