package com.ruoyi.crm.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 企业微信智能机器人租户配置（每租户库单行）
 */
public class CrmWecomAiBotConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String tenant;
    private String botId;
    /** AES 密文，不落日志 */
    private String botSecretEnc;
    private Boolean enabled;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getTenant()
    {
        return tenant;
    }

    public void setTenant(String tenant)
    {
        this.tenant = tenant;
    }

    public String getBotId()
    {
        return botId;
    }

    public void setBotId(String botId)
    {
        this.botId = botId;
    }

    public String getBotSecretEnc()
    {
        return botSecretEnc;
    }

    public void setBotSecretEnc(String botSecretEnc)
    {
        this.botSecretEnc = botSecretEnc;
    }

    public Boolean getEnabled()
    {
        return enabled;
    }

    public void setEnabled(Boolean enabled)
    {
        this.enabled = enabled;
    }

    public java.time.LocalDateTime getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(java.time.LocalDateTime createdAt)
    {
        this.createdAt = createdAt;
    }

    public java.time.LocalDateTime getUpdatedAt()
    {
        return updatedAt;
    }

    public void setUpdatedAt(java.time.LocalDateTime updatedAt)
    {
        this.updatedAt = updatedAt;
    }
}
