package com.ruoyi.crm.domain.vo;

/**
 * GET /public/captures/{captureId} 响应 data
 */
public class PublicCaptureDefinitionVo
{
    private String captureId;
    private String title;
    private String surveyJson;
    private String successMessage;

    public String getCaptureId()
    {
        return captureId;
    }

    public void setCaptureId(String captureId)
    {
        this.captureId = captureId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getSurveyJson()
    {
        return surveyJson;
    }

    public void setSurveyJson(String surveyJson)
    {
        this.surveyJson = surveyJson;
    }

    public String getSuccessMessage()
    {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage)
    {
        this.successMessage = successMessage;
    }
}
