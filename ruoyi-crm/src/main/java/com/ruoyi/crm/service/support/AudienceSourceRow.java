package com.ruoyi.crm.service.support;

import java.util.Date;

/**
 * 活动受众单渠道源行（合并前）
 */
public class AudienceSourceRow
{
    private String channel;
    private String sourceId;
    private String displayName;
    private String mobilePhone;
    private String contactId;
    private String leadId;
    private String leadStatus;
    private String leadStage;
    private String registrationStatus;
    private String wecomExternalUserid;
    private String wechatMpOpenid;
    private String wechatMpAppId;
    private String wecomName;
    private String mpNickname;
    private String owner;
    private String followUserid;
    private String rawJson;
    private Date touchedAt;
    private Date lastActivityAt;

    public String getChannel()
    {
        return channel;
    }

    public void setChannel(String channel)
    {
        this.channel = channel;
    }

    public String getSourceId()
    {
        return sourceId;
    }

    public void setSourceId(String sourceId)
    {
        this.sourceId = sourceId;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public String getMobilePhone()
    {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone)
    {
        this.mobilePhone = mobilePhone;
    }

    public String getContactId()
    {
        return contactId;
    }

    public void setContactId(String contactId)
    {
        this.contactId = contactId;
    }

    public String getLeadId()
    {
        return leadId;
    }

    public void setLeadId(String leadId)
    {
        this.leadId = leadId;
    }

    public String getLeadStatus()
    {
        return leadStatus;
    }

    public void setLeadStatus(String leadStatus)
    {
        this.leadStatus = leadStatus;
    }

    public String getLeadStage()
    {
        return leadStage;
    }

    public void setLeadStage(String leadStage)
    {
        this.leadStage = leadStage;
    }

    public String getRegistrationStatus()
    {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus)
    {
        this.registrationStatus = registrationStatus;
    }

    public String getWecomExternalUserid()
    {
        return wecomExternalUserid;
    }

    public void setWecomExternalUserid(String wecomExternalUserid)
    {
        this.wecomExternalUserid = wecomExternalUserid;
    }

    public String getWechatMpOpenid()
    {
        return wechatMpOpenid;
    }

    public void setWechatMpOpenid(String wechatMpOpenid)
    {
        this.wechatMpOpenid = wechatMpOpenid;
    }

    public String getWechatMpAppId()
    {
        return wechatMpAppId;
    }

    public void setWechatMpAppId(String wechatMpAppId)
    {
        this.wechatMpAppId = wechatMpAppId;
    }

    public String getWecomName()
    {
        return wecomName;
    }

    public void setWecomName(String wecomName)
    {
        this.wecomName = wecomName;
    }

    public String getMpNickname()
    {
        return mpNickname;
    }

    public void setMpNickname(String mpNickname)
    {
        this.mpNickname = mpNickname;
    }

    public String getOwner()
    {
        return owner;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    public String getFollowUserid()
    {
        return followUserid;
    }

    public void setFollowUserid(String followUserid)
    {
        this.followUserid = followUserid;
    }

    public String getRawJson()
    {
        return rawJson;
    }

    public void setRawJson(String rawJson)
    {
        this.rawJson = rawJson;
    }

    public Date getTouchedAt()
    {
        return touchedAt;
    }

    public void setTouchedAt(Date touchedAt)
    {
        this.touchedAt = touchedAt;
    }

    public Date getLastActivityAt()
    {
        return lastActivityAt;
    }

    public void setLastActivityAt(Date lastActivityAt)
    {
        this.lastActivityAt = lastActivityAt;
    }
}
