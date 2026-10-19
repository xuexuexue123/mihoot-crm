package com.ruoyi.crm.domain.dto;

/**
 * MCP API Key validate 请求（BFF / MCP Server）
 */
public class McpApiKeyValidateDto
{
    private String apiKey;

    public String getApiKey()
    {
        return apiKey;
    }

    public void setApiKey(String apiKey)
    {
        this.apiKey = apiKey;
    }
}
