package com.ruoyi.crm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * OAuth2对象 crm_oauth_connections
 * 
 * @author luohd
 * @date 2025-05-14
 */
public class CrmOauthConnections extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String userId;

    /** 类型，如 email, calendar, social, storage */
    @Excel(name = "类型，如 email, calendar, social, storage")
    private String integrationType;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String provider;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String externalId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String externalName;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String accessToken;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String refreshToken;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String tokenType;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String scope;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Date expiresAt;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String metadata;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Date connectedAt;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Date lastRefreshedAt;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Integer revoked;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Date createdAt;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Date updatedAt;
    /** Sync status */
    @Excel(name = "Sync status")
    private String status;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getUserId()
    {
        return userId;
    }
    public void setIntegrationType(String integrationType)
    {
        this.integrationType = integrationType;
    }

    public String getIntegrationType()
    {
        return integrationType;
    }
    public void setProvider(String provider) 
    {
        this.provider = provider;
    }

    public String getProvider() 
    {
        return provider;
    }
    public void setExternalId(String externalId) 
    {
        this.externalId = externalId;
    }

    public String getExternalId() 
    {
        return externalId;
    }
    public void setExternalName(String externalName) 
    {
        this.externalName = externalName;
    }

    public String getExternalName() 
    {
        return externalName;
    }
    public void setAccessToken(String accessToken) 
    {
        this.accessToken = accessToken;
    }

    public String getAccessToken() 
    {
        return accessToken;
    }
    public void setRefreshToken(String refreshToken) 
    {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() 
    {
        return refreshToken;
    }
    public void setTokenType(String tokenType) 
    {
        this.tokenType = tokenType;
    }

    public String getTokenType() 
    {
        return tokenType;
    }
    public void setScope(String scope) 
    {
        this.scope = scope;
    }

    public String getScope() 
    {
        return scope;
    }
    public void setExpiresAt(Date expiresAt) 
    {
        this.expiresAt = expiresAt;
    }

    public Date getExpiresAt() 
    {
        return expiresAt;
    }
    public void setMetadata(String metadata) 
    {
        this.metadata = metadata;
    }

    public String getMetadata() 
    {
        return metadata;
    }
    public void setConnectedAt(Date connectedAt) 
    {
        this.connectedAt = connectedAt;
    }

    public Date getConnectedAt() 
    {
        return connectedAt;
    }
    public void setLastRefreshedAt(Date lastRefreshedAt) 
    {
        this.lastRefreshedAt = lastRefreshedAt;
    }

    public Date getLastRefreshedAt() 
    {
        return lastRefreshedAt;
    }
    public void setRevoked(Integer revoked) 
    {
        this.revoked = revoked;
    }

    public Integer getRevoked() 
    {
        return revoked;
    }
    public void setCreatedAt(Date createdAt) 
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() 
    {
        return createdAt;
    }
    public void setUpdatedAt(Date updatedAt) 
    {
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt() 
    {
        return updatedAt;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("provider", getProvider())
            .append("externalId", getExternalId())
            .append("externalName", getExternalName())
            .append("accessToken", getAccessToken())
            .append("refreshToken", getRefreshToken())
            .append("tokenType", getTokenType())
            .append("scope", getScope())
            .append("expiresAt", getExpiresAt())
            .append("metadata", getMetadata())
            .append("connectedAt", getConnectedAt())
            .append("lastRefreshedAt", getLastRefreshedAt())
            .append("revoked", getRevoked())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .toString();
    }
}
