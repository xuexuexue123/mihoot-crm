package com.ruoyi.crm.domain.vo;

/**
 * 匿名问卷 H5 GET 响应（不含 tenantId）
 */
public class OutreachSurveyVo
{
    private String surveyId;

    private String title;

    private String description;

    private String surveyJson;

    private String contactId;

    private String channel;

    public String getSurveyId()
    {
        return surveyId;
    }

    public void setSurveyId(String surveyId)
    {
        this.surveyId = surveyId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getSurveyJson()
    {
        return surveyJson;
    }

    public void setSurveyJson(String surveyJson)
    {
        this.surveyJson = surveyJson;
    }

    public String getContactId()
    {
        return contactId;
    }

    public void setContactId(String contactId)
    {
        this.contactId = contactId;
    }

    public String getChannel()
    {
        return channel;
    }

    public void setChannel(String channel)
    {
        this.channel = channel;
    }
}
