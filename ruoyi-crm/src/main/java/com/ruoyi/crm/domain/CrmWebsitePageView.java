package com.ruoyi.crm.domain;

import java.util.Date;

/**
 * 官网页面浏览明细（对应 crm_website_page_view）
 */
public class CrmWebsitePageView
{
    private String id;
    private String sessionId;
    private String pageUrl;
    private String pageDescription;
    private String referrer;
    private Date viewedAt;
    private Boolean syncedToActivity;
    private String activityId;
    private Date createTime;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
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

    public Date getViewedAt()
    {
        return viewedAt;
    }

    public void setViewedAt(Date viewedAt)
    {
        this.viewedAt = viewedAt;
    }

    public Boolean getSyncedToActivity()
    {
        return syncedToActivity;
    }

    public void setSyncedToActivity(Boolean syncedToActivity)
    {
        this.syncedToActivity = syncedToActivity;
    }

    public String getActivityId()
    {
        return activityId;
    }

    public void setActivityId(String activityId)
    {
        this.activityId = activityId;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }
}
