package com.ruoyi.crm.domain.dto;

/**
 * 邮件 AI 分析 Upsert 请求体
 */
public class EmailAnalysisUpsertDto
{
    private String messageExternalId;
    private String locale;
    private String provider;
    private String externalName;
    private String userId;
    private String subject;
    private String sender;
    private String bodyHash;
    private EmailAnalysisResultDto analysis;
    private String model;

    public String getMessageExternalId()
    {
        return messageExternalId;
    }

    public void setMessageExternalId(String messageExternalId)
    {
        this.messageExternalId = messageExternalId;
    }

    public String getLocale()
    {
        return locale;
    }

    public void setLocale(String locale)
    {
        this.locale = locale;
    }

    public String getProvider()
    {
        return provider;
    }

    public void setProvider(String provider)
    {
        this.provider = provider;
    }

    public String getExternalName()
    {
        return externalName;
    }

    public void setExternalName(String externalName)
    {
        this.externalName = externalName;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getSender()
    {
        return sender;
    }

    public void setSender(String sender)
    {
        this.sender = sender;
    }

    public String getBodyHash()
    {
        return bodyHash;
    }

    public void setBodyHash(String bodyHash)
    {
        this.bodyHash = bodyHash;
    }

    public EmailAnalysisResultDto getAnalysis()
    {
        return analysis;
    }

    public void setAnalysis(EmailAnalysisResultDto analysis)
    {
        this.analysis = analysis;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel(String model)
    {
        this.model = model;
    }
}
