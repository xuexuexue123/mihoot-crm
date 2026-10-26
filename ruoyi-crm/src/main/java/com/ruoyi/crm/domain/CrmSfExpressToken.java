package com.ruoyi.crm.domain;

import java.util.Date;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 顺丰 OAuth2 accessToken（每租户库单行）
 */
public class CrmSfExpressToken extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String accessTokenEnc;
    private Date expiresAt;
    private Date lastRefreshAt;
    private String lastError;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getAccessTokenEnc()
    {
        return accessTokenEnc;
    }

    public void setAccessTokenEnc(String accessTokenEnc)
    {
        this.accessTokenEnc = accessTokenEnc;
    }

    public Date getExpiresAt()
    {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt)
    {
        this.expiresAt = expiresAt;
    }

    public Date getLastRefreshAt()
    {
        return lastRefreshAt;
    }

    public void setLastRefreshAt(Date lastRefreshAt)
    {
        this.lastRefreshAt = lastRefreshAt;
    }

    public String getLastError()
    {
        return lastError;
    }

    public void setLastError(String lastError)
    {
        this.lastError = lastError;
    }
}
