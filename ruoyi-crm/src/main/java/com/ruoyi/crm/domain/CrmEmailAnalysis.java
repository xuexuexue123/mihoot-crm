package com.ruoyi.crm.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 邮件 AI 分析结果 crm_email_analysis
 */
public class CrmEmailAnalysis extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String analysisId;
    private String tenantId;
    private String userId;
    private String messageExternalId;
    private String provider;
    private String externalName;
    private String locale;
    private String subject;
    private String sender;
    private String bodyHash;
    private String executiveSummary;
    private String category;
    private String urgency;
    private String sentimentLabel;
    private Integer sentimentScore;
    private String sentimentDescription;
    private String detectedIntent;
    private String suggestedAction;
    private String entitiesJson;
    private String suggestedTicketJson;
    private String suggestedLeadJson;
    private String analysisJson;
    private String model;
    private String delFlag;

    public String getAnalysisId()
    {
        return analysisId;
    }

    public void setAnalysisId(String analysisId)
    {
        this.analysisId = analysisId;
    }

    public String getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(String tenantId)
    {
        this.tenantId = tenantId;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getMessageExternalId()
    {
        return messageExternalId;
    }

    public void setMessageExternalId(String messageExternalId)
    {
        this.messageExternalId = messageExternalId;
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

    public String getLocale()
    {
        return locale;
    }

    public void setLocale(String locale)
    {
        this.locale = locale;
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

    public String getExecutiveSummary()
    {
        return executiveSummary;
    }

    public void setExecutiveSummary(String executiveSummary)
    {
        this.executiveSummary = executiveSummary;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getUrgency()
    {
        return urgency;
    }

    public void setUrgency(String urgency)
    {
        this.urgency = urgency;
    }

    public String getSentimentLabel()
    {
        return sentimentLabel;
    }

    public void setSentimentLabel(String sentimentLabel)
    {
        this.sentimentLabel = sentimentLabel;
    }

    public Integer getSentimentScore()
    {
        return sentimentScore;
    }

    public void setSentimentScore(Integer sentimentScore)
    {
        this.sentimentScore = sentimentScore;
    }

    public String getSentimentDescription()
    {
        return sentimentDescription;
    }

    public void setSentimentDescription(String sentimentDescription)
    {
        this.sentimentDescription = sentimentDescription;
    }

    public String getDetectedIntent()
    {
        return detectedIntent;
    }

    public void setDetectedIntent(String detectedIntent)
    {
        this.detectedIntent = detectedIntent;
    }

    public String getSuggestedAction()
    {
        return suggestedAction;
    }

    public void setSuggestedAction(String suggestedAction)
    {
        this.suggestedAction = suggestedAction;
    }

    public String getEntitiesJson()
    {
        return entitiesJson;
    }

    public void setEntitiesJson(String entitiesJson)
    {
        this.entitiesJson = entitiesJson;
    }

    public String getSuggestedTicketJson()
    {
        return suggestedTicketJson;
    }

    public void setSuggestedTicketJson(String suggestedTicketJson)
    {
        this.suggestedTicketJson = suggestedTicketJson;
    }

    public String getSuggestedLeadJson()
    {
        return suggestedLeadJson;
    }

    public void setSuggestedLeadJson(String suggestedLeadJson)
    {
        this.suggestedLeadJson = suggestedLeadJson;
    }

    public String getAnalysisJson()
    {
        return analysisJson;
    }

    public void setAnalysisJson(String analysisJson)
    {
        this.analysisJson = analysisJson;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel(String model)
    {
        this.model = model;
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
