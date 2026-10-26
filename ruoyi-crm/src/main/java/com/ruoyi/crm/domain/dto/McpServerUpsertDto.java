package com.ruoyi.crm.domain.dto;

import java.util.List;

/**
 * MCP Server Upsert 请求体
 */
public class McpServerUpsertDto
{
    private Long id;
    private String name;
    private String url;
    private String transport;
    private String authType;
    private String authSecret;
    private List<String> allowedTools;
    private Boolean enabled;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getTransport()
    {
        return transport;
    }

    public void setTransport(String transport)
    {
        this.transport = transport;
    }

    public String getAuthType()
    {
        return authType;
    }

    public void setAuthType(String authType)
    {
        this.authType = authType;
    }

    public String getAuthSecret()
    {
        return authSecret;
    }

    public void setAuthSecret(String authSecret)
    {
        this.authSecret = authSecret;
    }

    public List<String> getAllowedTools()
    {
        return allowedTools;
    }

    public void setAllowedTools(List<String> allowedTools)
    {
        this.allowedTools = allowedTools;
    }

    public Boolean getEnabled()
    {
        return enabled;
    }

    public void setEnabled(Boolean enabled)
    {
        this.enabled = enabled;
    }
}
