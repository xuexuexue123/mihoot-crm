package com.ruoyi.crm.wechatmp.dto;

/**
 * 异步 Identity Enrich 任务上下文（subscribe / SCAN）。
 */
public class WechatMpIdentityEnrichContext
{
    private String tenantId;
    private String appId;
    private String openId;
    private String channelEventId;
    private String eventType;
    private String rawXml;
    private String xmlUnionId;
    private String syncContactId;
    private boolean syncWroteUnmatched;
    private Long unmatchedId;

    public String getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(String tenantId)
    {
        this.tenantId = tenantId;
    }

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    public String getOpenId()
    {
        return openId;
    }

    public void setOpenId(String openId)
    {
        this.openId = openId;
    }

    public String getChannelEventId()
    {
        return channelEventId;
    }

    public void setChannelEventId(String channelEventId)
    {
        this.channelEventId = channelEventId;
    }

    public String getEventType()
    {
        return eventType;
    }

    public void setEventType(String eventType)
    {
        this.eventType = eventType;
    }

    public String getRawXml()
    {
        return rawXml;
    }

    public void setRawXml(String rawXml)
    {
        this.rawXml = rawXml;
    }

    public String getXmlUnionId()
    {
        return xmlUnionId;
    }

    public void setXmlUnionId(String xmlUnionId)
    {
        this.xmlUnionId = xmlUnionId;
    }

    public String getSyncContactId()
    {
        return syncContactId;
    }

    public void setSyncContactId(String syncContactId)
    {
        this.syncContactId = syncContactId;
    }

    public boolean isSyncWroteUnmatched()
    {
        return syncWroteUnmatched;
    }

    public void setSyncWroteUnmatched(boolean syncWroteUnmatched)
    {
        this.syncWroteUnmatched = syncWroteUnmatched;
    }

    public Long getUnmatchedId()
    {
        return unmatchedId;
    }

    public void setUnmatchedId(Long unmatchedId)
    {
        this.unmatchedId = unmatchedId;
    }
}
