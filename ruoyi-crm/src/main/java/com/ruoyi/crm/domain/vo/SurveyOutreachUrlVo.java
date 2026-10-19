package com.ruoyi.crm.domain.vo;

/**
 * 问卷 H5 填写链接签发响应
 */
public class SurveyOutreachUrlVo
{
    private String surveyUrl;

    private String expiresAt;

    public String getSurveyUrl()
    {
        return surveyUrl;
    }

    public void setSurveyUrl(String surveyUrl)
    {
        this.surveyUrl = surveyUrl;
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
