package com.ruoyi.crm.domain;

import java.math.BigDecimal;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 租户 AI 模型配置 crm_ai_model_config
 */
public class CrmAiModelConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String configId;
    private String tenantId;
    private String provider;
    private String credentialId;
    private String model;
    private BigDecimal temperature;
    private String defaultTranslationLanguage;
    private String delFlag;

    public String getConfigId()
    {
        return configId;
    }

    public void setConfigId(String configId)
    {
        this.configId = configId;
    }

    public String getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(String tenantId)
    {
        this.tenantId = tenantId;
    }

    public String getProvider()
    {
        return provider;
    }

    public void setProvider(String provider)
    {
        this.provider = provider;
    }

    public String getCredentialId()
    {
        return credentialId;
    }

    public void setCredentialId(String credentialId)
    {
        this.credentialId = credentialId;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public BigDecimal getTemperature()
    {
        return temperature;
    }

    public void setTemperature(BigDecimal temperature)
    {
        this.temperature = temperature;
    }

    public String getDefaultTranslationLanguage()
    {
        return defaultTranslationLanguage;
    }

    public void setDefaultTranslationLanguage(String defaultTranslationLanguage)
    {
        this.defaultTranslationLanguage = defaultTranslationLanguage;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }
}
