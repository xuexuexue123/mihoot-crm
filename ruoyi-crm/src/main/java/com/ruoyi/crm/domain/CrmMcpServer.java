package com.ruoyi.crm.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * MCP Server 配置 crm_mcp_server
 */
public class CrmMcpServer extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String tenantId;
    private String name;
    private String url;
    private String transport;
    private String authType;
    private String authSecretEnc;
    private String allowedTools;
    private Integer enabled;

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

    public String getAuthSecretEnc()
    {
        return authSecretEnc;
    }

    public void setAuthSecretEnc(String authSecretEnc)
    {
        this.authSecretEnc = authSecretEnc;
    }

    public String getAllowedTools()
    {
        return allowedTools;
    }

    public void setAllowedTools(String allowedTools)
    {
        this.allowedTools = allowedTools;
    }

    public Integer getEnabled()
    {
        return enabled;
    }

    public void setEnabled(Integer enabled)
    {
        this.enabled = enabled;
    }
}
