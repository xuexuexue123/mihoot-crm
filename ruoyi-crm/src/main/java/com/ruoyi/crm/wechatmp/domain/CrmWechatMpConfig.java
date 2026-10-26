package com.ruoyi.crm.wechatmp.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 微信公众号租户配置（每租户库可多行）
 */
public class CrmWechatMpConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String appId;
    /** AES 密文，不落日志 */
    private String secretEnc;
    private String token;
    private String encodingAesKey;
    private Boolean verifySignature;
    private Boolean connected;
    private String lastError;
    private String accountType;
    private String accountName;
    private java.util.Date accountSyncedAt;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    public String getSecretEnc()
    {
        return secretEnc;
    }

    public void setSecretEnc(String secretEnc)
    {
        this.secretEnc = secretEnc;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public String getEncodingAesKey()
    {
        return encodingAesKey;
    }

    public void setEncodingAesKey(String encodingAesKey)
    {
        this.encodingAesKey = encodingAesKey;
    }

    public Boolean getVerifySignature()
    {
        return verifySignature;
    }

    public void setVerifySignature(Boolean verifySignature)
    {
        this.verifySignature = verifySignature;
    }

    public Boolean getConnected()
    {
        return connected;
    }

    public void setConnected(Boolean connected)
    {
        this.connected = connected;
    }

    public String getLastError()
    {
        return lastError;
    }

    public void setLastError(String lastError)
    {
        this.lastError = lastError;
    }

    public String getAccountType()
    {
        return accountType;
    }

    public void setAccountType(String accountType)
    {
        this.accountType = accountType;
    }

    public String getAccountName()
    {
        return accountName;
    }

    public void setAccountName(String accountName)
    {
        this.accountName = accountName;
    }

    public java.util.Date getAccountSyncedAt()
    {
        return accountSyncedAt;
    }

    public void setAccountSyncedAt(java.util.Date accountSyncedAt)
    {
        this.accountSyncedAt = accountSyncedAt;
    }
}
