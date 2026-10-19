package com.ruoyi.crm.domain.dto;

import java.util.Map;

/**
 * POST /public/captures/{captureId}/submit
 */
public class CaptureSubmitRequest
{
    private String tenantSlug;
    private String leadId;
    private Map<String, Object> surveyData;
    private String pageUrl;
    private String pagePath;
    private String referrer;
    private String _sessionId;
    private String _browseHistory;
    private String visitorToken;

    public String getTenantSlug()
    {
        return tenantSlug;
    }

    public void setTenantSlug(String tenantSlug)
    {
        this.tenantSlug = tenantSlug;
    }

    public String getLeadId()
    {
        return leadId;
    }

    public void setLeadId(String leadId)
    {
        this.leadId = leadId;
    }

    public Map<String, Object> getSurveyData()
    {
        return surveyData;
    }

    public void setSurveyData(Map<String, Object> surveyData)
    {
        this.surveyData = surveyData;
    }

    public String getPageUrl()
    {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl)
    {
        this.pageUrl = pageUrl;
    }

    public String getPagePath()
    {
        return pagePath;
    }

    public void setPagePath(String pagePath)
    {
        this.pagePath = pagePath;
    }

    public String getReferrer()
    {
        return referrer;
    }

    public void setReferrer(String referrer)
    {
        this.referrer = referrer;
    }

    public String get_sessionId()
    {
        return _sessionId;
    }

    public void set_sessionId(String _sessionId)
    {
        this._sessionId = _sessionId;
    }

    public String get_browseHistory()
    {
        return _browseHistory;
    }

    public void set_browseHistory(String _browseHistory)
    {
        this._browseHistory = _browseHistory;
    }

    public String getVisitorToken()
    {
        return visitorToken;
    }

    public void setVisitorToken(String visitorToken)
    {
        this.visitorToken = visitorToken;
    }
}
