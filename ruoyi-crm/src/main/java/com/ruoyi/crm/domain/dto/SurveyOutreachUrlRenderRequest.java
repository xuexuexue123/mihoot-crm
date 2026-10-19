package com.ruoyi.crm.domain.dto;

/**
 * 问卷个性化 H5 链接签发请求
 */
public class SurveyOutreachUrlRenderRequest
{
    private String openId;
    private String appId;
    private String contactId;
    private String channel;

    public String getOpenId()
    {
        return openId;
    }

    public void setOpenId(String openId)
    {
        this.openId = openId;
    }

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
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
