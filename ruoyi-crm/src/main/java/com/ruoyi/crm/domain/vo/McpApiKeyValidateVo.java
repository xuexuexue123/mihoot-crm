package com.ruoyi.crm.domain.vo;

import java.util.List;

/**
 * MCP API Key validate 成功响应
 */
public class McpApiKeyValidateVo
{
    private Long apiKeyId;
    private String tenant;
    private String userId;
    private String serviceToken;
    private List<String> allowedTools;

    public Long getApiKeyId()
    {
        return apiKeyId;
    }

    public void setApiKeyId(Long apiKeyId)
    {
        this.apiKeyId = apiKeyId;
    }

    public String getTenant()
    {
        return tenant;
    }

    public void setTenant(String tenant)
    {
        this.tenant = tenant;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getServiceToken()
    {
        return serviceToken;
    }

    public void setServiceToken(String serviceToken)
    {
        this.serviceToken = serviceToken;
    }

    public List<String> getAllowedTools()
    {
        return allowedTools;
    }

    public void setAllowedTools(List<String> allowedTools)
    {
        this.allowedTools = allowedTools;
    }
}
