package com.ruoyi.crm.wecom;

/**
 * 企微活码 state 解析结果（活动归因 + 子渠道）。
 */
public class WecomStateParseResult
{
    private String crmEventId;
    private String leadSource;
    private String channel;
    private String eventSuffix;

    public static WecomStateParseResult empty()
    {
        return new WecomStateParseResult();
    }

    public String getCrmEventId()
    {
        return crmEventId;
    }

    public void setCrmEventId(String crmEventId)
    {
        this.crmEventId = crmEventId;
    }

    public String getLeadSource()
    {
        return leadSource;
    }

    public void setLeadSource(String leadSource)
    {
        this.leadSource = leadSource;
    }

    public String getChannel()
    {
        return channel;
    }

    public void setChannel(String channel)
    {
        this.channel = channel;
    }

    public String getEventSuffix()
    {
        return eventSuffix;
    }

    public void setEventSuffix(String eventSuffix)
    {
        this.eventSuffix = eventSuffix;
    }
}
