package com.ruoyi.crm.domain.vo;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 活动受众列表行
 */
public class EventAudienceRowVo
{
    private String audienceKey;
    private String displayName;
    private String mobilePhone;
    private List<String> channels;
    private String audienceStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date firstTouchAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastActivityAt;

    private String leadId;
    private String leadStatus;
    private String contactId;
    private String contactName;
    private String wecomExternalUserid;
    private String wechatMpOpenid;
    private String wechatMpAppId;
    private String owner;
    private List<EventAudienceChannelDetailVo> channelDetails;

    /** 公众号报名自动化模板消息发送时间（status=sent） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date mpOutreachSentAt;

    private String registrationStatus;
    private String registrationSessionName;
    private String checkInStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkedInAt;

    public String getAudienceKey()
    {
        return audienceKey;
    }

    public void setAudienceKey(String audienceKey)
    {
        this.audienceKey = audienceKey;
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

    public List<String> getChannels()
    {
        return channels;
    }

    public void setChannels(List<String> channels)
    {
        this.channels = channels;
    }

    public String getAudienceStatus()
    {
        return audienceStatus;
    }

    public void setAudienceStatus(String audienceStatus)
    {
        this.audienceStatus = audienceStatus;
    }

    public Date getFirstTouchAt()
    {
        return firstTouchAt;
    }

    public void setFirstTouchAt(Date firstTouchAt)
    {
        this.firstTouchAt = firstTouchAt;
    }

    public Date getLastActivityAt()
    {
        return lastActivityAt;
    }

    public void setLastActivityAt(Date lastActivityAt)
    {
        this.lastActivityAt = lastActivityAt;
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

    public String getContactId()
    {
        return contactId;
    }

    public void setContactId(String contactId)
    {
        this.contactId = contactId;
    }

    public String getContactName()
    {
        return contactName;
    }

    public void setContactName(String contactName)
    {
        this.contactName = contactName;
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

    public String getOwner()
    {
        return owner;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    public List<EventAudienceChannelDetailVo> getChannelDetails()
    {
        return channelDetails;
    }

    public void setChannelDetails(List<EventAudienceChannelDetailVo> channelDetails)
    {
        this.channelDetails = channelDetails;
    }

    public Date getMpOutreachSentAt()
    {
        return mpOutreachSentAt;
    }

    public void setMpOutreachSentAt(Date mpOutreachSentAt)
    {
        this.mpOutreachSentAt = mpOutreachSentAt;
    }

    public String getRegistrationStatus()
    {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus)
    {
        this.registrationStatus = registrationStatus;
    }

    public String getRegistrationSessionName()
    {
        return registrationSessionName;
    }

    public void setRegistrationSessionName(String registrationSessionName)
    {
        this.registrationSessionName = registrationSessionName;
    }

    public String getCheckInStatus()
    {
        return checkInStatus;
    }

    public void setCheckInStatus(String checkInStatus)
    {
        this.checkInStatus = checkInStatus;
    }

    public Date getCheckedInAt()
    {
        return checkedInAt;
    }

    public void setCheckedInAt(Date checkedInAt)
    {
        this.checkedInAt = checkedInAt;
    }
}
