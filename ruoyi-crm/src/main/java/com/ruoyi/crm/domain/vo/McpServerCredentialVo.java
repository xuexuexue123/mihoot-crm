package com.ruoyi.crm.domain.vo;

import java.util.List;

/**
 * MCP Server runtime credentials 响应（含解密 authSecret）
 */
public class McpServerCredentialVo extends McpServerVo
{
    private String authSecret;

    public String getAuthSecret()
    {
        return authSecret;
    }

    public void setAuthSecret(String authSecret)
    {
        this.authSecret = authSecret;
    }
}
