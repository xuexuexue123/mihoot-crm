package com.ruoyi.crm.domain.dto;

/**
 * MCP tools/call 审计上报（Next BFF）
 */
public class McpApiKeyAuditDto
{
    private String tenant;
    private Long apiKeyId;
    private String userId;
    private String toolName;
    private Boolean ok;
    private String errorCode;
    private Integer latencyMs;

    public String getTenant()
    {
        return tenant;
    }

    public void setTenant(String tenant)
    {
        this.tenant = tenant;
    }

    public Long getApiKeyId()
    {
        return apiKeyId;
    }

    public void setApiKeyId(Long apiKeyId)
    {
        this.apiKeyId = apiKeyId;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getToolName()
    {
        return toolName;
    }

    public void setToolName(String toolName)
    {
        this.toolName = toolName;
    }

    public Boolean getOk()
    {
        return ok;
    }

    public void setOk(Boolean ok)
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
}
