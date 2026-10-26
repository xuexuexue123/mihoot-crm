package com.ruoyi.crm.domain;

import java.util.Date;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 入站 MCP API Key crm_mcp_api_key
 */
public class CrmMcpApiKey extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String tenantId;
    private String name;
    private String keyPrefix;
    private String keyHash;
    private String boundUserId;
    private String allowedTools;
    private Integer enabled;
    private Date expiresAt;
    private String serviceTokenEnc;

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

    public String getKeyPrefix()
    {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix)
    {
        this.keyPrefix = keyPrefix;
    }

    public String getKeyHash()
    {
        return keyHash;
    }

    public void setKeyHash(String keyHash)
    {
        this.keyHash = keyHash;
    }

    public String getBoundUserId()
    {
        return boundUserId;
    }

    public void setBoundUserId(String boundUserId)
    {
        this.boundUserId = boundUserId;
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

    public Date getExpiresAt()
    {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt)
    {
        this.expiresAt = expiresAt;
    }

    public String getServiceTokenEnc()
    {
        return serviceTokenEnc;
    }

    public void setServiceTokenEnc(String serviceTokenEnc)
    {
        this.serviceTokenEnc = serviceTokenEnc;
    }
}
