package com.ruoyi.crm.domain.vo;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 认定潜客外投拉卷响应
 */
public class LeadQualifySurveyVo
{
    private String leadId;

    private String displayName;

    private String templateId;

    private String templateName;

    /** EMS Template content（SurveyJS 定义），非答卷 */
    private String surveyJson;

    /** pending | completed */
    private String status;

    /** completed 时为答卷对象；pending 时为 null */
    private JsonNode answers;

    private String channel;

    private String expiresAt;

    public String getLeadId()
    {
        return leadId;
    }

    public void setLeadId(String leadId)
    {
        this.leadId = leadId;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public String getTemplateId()
    {
        return templateId;
    }

    public void setTemplateId(String templateId)
    {
        this.templateId = templateId;
    }

    public String getTemplateName()
    {
        return templateName;
    }

    public void setTemplateName(String templateName)
    {
        this.templateName = templateName;
    }

    public String getSurveyJson()
    {
        return surveyJson;
    }

    public void setSurveyJson(String surveyJson)
    {
        this.surveyJson = surveyJson;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public JsonNode getAnswers()
    {
        return answers;
    }

    public void setAnswers(JsonNode answers)
    {
        this.answers = answers;
    }

    public String getChannel()
    {
        return channel;
    }

    public void setChannel(String channel)
    {
        this.channel = channel;
    }

    public String getExpiresAt()
    {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt)
    {
        this.expiresAt = expiresAt;
    }
}
