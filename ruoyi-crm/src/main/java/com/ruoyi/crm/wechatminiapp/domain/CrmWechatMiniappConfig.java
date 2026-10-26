package com.ruoyi.crm.wechatminiapp.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 微信小程序租户配置（每租户库至多一行）
 */
public class CrmWechatMiniappConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String appId;
    /** AES 密文，不落日志、不回显 */
    private String appSecretEnc;
    private String name;
    private Boolean connected;
    private String lastError;

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

    public String getAppSecretEnc()
    {
        return appSecretEnc;
    }

    public void setAppSecretEnc(String appSecretEnc)
    {
        this.appSecretEnc = appSecretEnc;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
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
}
