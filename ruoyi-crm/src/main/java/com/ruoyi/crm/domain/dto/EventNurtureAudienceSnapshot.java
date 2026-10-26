package com.ruoyi.crm.domain.dto;

/**
 * 培育入组受众快照（钩子传入）
 */
public class EventNurtureAudienceSnapshot
{
    private String audienceKey;
    private String leadId;
    private String contactId;
    private String wechatMpOpenid;
    private String wechatMpAppId;
    private String wecomExternalUserid;
    private String displayName;
    private String mobilePhone;
    private String audienceStatus;

    public String getAudienceKey() { return audienceKey; }
    public void setAudienceKey(String audienceKey) { this.audienceKey = audienceKey; }
    public String getLeadId() { return leadId; }
    public void setLeadId(String leadId) { this.leadId = leadId; }
    public String getContactId() { return contactId; }
    public void setContactId(String contactId) { this.contactId = contactId; }
    public String getWechatMpOpenid() { return wechatMpOpenid; }
    public void setWechatMpOpenid(String wechatMpOpenid) { this.wechatMpOpenid = wechatMpOpenid; }
    public String getWechatMpAppId() { return wechatMpAppId; }
    public void setWechatMpAppId(String wechatMpAppId) { this.wechatMpAppId = wechatMpAppId; }
    public String getWecomExternalUserid() { return wecomExternalUserid; }
    public void setWecomExternalUserid(String wecomExternalUserid) { this.wecomExternalUserid = wecomExternalUserid; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getMobilePhone() { return mobilePhone; }
    public void setMobilePhone(String mobilePhone) { this.mobilePhone = mobilePhone; }
    public String getAudienceStatus() { return audienceStatus; }
    public void setAudienceStatus(String audienceStatus) { this.audienceStatus = audienceStatus; }
}
