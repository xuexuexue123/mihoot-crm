package com.ruoyi.crm.domain.dto;

/**
 * POST /website/pageview 请求体
 */
public class WebsitePageviewRequest
{
    private String tenantSlug;
    private String sessionId;
    private String pageUrl;
    private String pageDescription;
    private String referrer;
    /** 客户端毫秒时间戳或 ISO 字符串 */
    private Object viewedAt;
    private String visitorToken;

    public String getTenantSlug()
    {
        return tenantSlug;
    }

    public void setTenantSlug(String tenantSlug)
    {
        this.tenantSlug = tenantSlug;
    }

    public String getSessionId()
    {
        return sessionId;
    }

    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }

    public String getPageUrl()
    {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl)
    {
        this.pageUrl = pageUrl;
    }

    public String getPageDescription()
    {
        return pageDescription;
    }

    public void setPageDescription(String pageDescription)
    {
        this.pageDescription = pageDescription;
    }

    public String getReferrer()
    {
        return referrer;
    }

    public void setReferrer(String referrer)
    {
        this.referrer = referrer;
    }

    public Object getViewedAt()
    {
        return viewedAt;
    }

    public void setViewedAt(Object viewedAt)
    {
        this.viewedAt = viewedAt;
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
