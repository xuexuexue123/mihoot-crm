package com.ruoyi.crm.domain.vo;

/**
 * 创建 / 轮换 MCP API Key 响应（含一次性明文 apiKey）
 */
public class McpApiKeyCreateResultVo extends McpApiKeyVo
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
