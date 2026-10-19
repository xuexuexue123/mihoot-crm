package com.ruoyi.crm.domain;

import java.util.Date;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 批量邮件租户发信配置（每租户库单行）
 */
public class CrmBulkemailProviderConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String provider;
    private String domain;
    private String fromEmail;
    private String fromName;
    private String region;
    /** AES 密文，不落日志 */
    private String apiKeyEnc;
    private String status;
    private Date lastTestAt;
    private String lastError;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getProvider()
    {
        return provider;
    }

    public void setProvider(String provider)
    {
        this.provider = provider;
    }

    public String getDomain()
    {
        return domain;
    }

    public void setDomain(String domain)
    {
        this.domain = domain;
    }

    public String getFromEmail()
    {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail)
    {
        this.fromEmail = fromEmail;
    }

    public String getFromName()
    {
        return fromName;
    }

    public void setFromName(String fromName)
    {
        this.fromName = fromName;
    }

    public String getRegion()
    {
        return region;
    }

    public void setRegion(String region)
    {
        this.region = region;
    }

    public String getApiKeyEnc()
    {
        return apiKeyEnc;
    }

    public void setApiKeyEnc(String apiKeyEnc)
    {
        this.apiKeyEnc = apiKeyEnc;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Date getLastTestAt()
    {
        return lastTestAt;
    }

    public void setLastTestAt(Date lastTestAt)
    {
        this.lastTestAt = lastTestAt;
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
