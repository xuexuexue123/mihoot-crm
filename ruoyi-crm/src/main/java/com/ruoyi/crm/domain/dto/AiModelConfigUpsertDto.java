package com.ruoyi.crm.domain.dto;

import java.math.BigDecimal;

/**
 * AI 模型配置 Upsert 请求体
 */
public class AiModelConfigUpsertDto
{
    private String provider;
    private String credentialId;
    private String model;
    private BigDecimal temperature;
    private String defaultTranslationLanguage;

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
}
