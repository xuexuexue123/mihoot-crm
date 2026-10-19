package com.ruoyi.crm.domain.dto;

public class AudienceKeyHints
{
    private String contactId;
    private String mobilePhone;
    private String unionid;
    private String leadId;
    private String wecomExternalUserid;
    private String wechatMpOpenid;

    public static AudienceKeyHints create()
    {
        return new AudienceKeyHints();
    }

    public AudienceKeyHints contactId(String contactId) { this.contactId = contactId; return this; }
    public AudienceKeyHints mobilePhone(String mobilePhone) { this.mobilePhone = mobilePhone; return this; }
    public AudienceKeyHints unionid(String unionid) { this.unionid = unionid; return this; }
    public AudienceKeyHints leadId(String leadId) { this.leadId = leadId; return this; }
    public AudienceKeyHints wecomExternalUserid(String wecomExternalUserid) { this.wecomExternalUserid = wecomExternalUserid; return this; }
    public AudienceKeyHints wechatMpOpenid(String wechatMpOpenid) { this.wechatMpOpenid = wechatMpOpenid; return this; }

    public String getContactId() { return contactId; }
    public void setContactId(String contactId) { this.contactId = contactId; }
    public String getMobilePhone() { return mobilePhone; }
    public void setMobilePhone(String mobilePhone) { this.mobilePhone = mobilePhone; }
    public String getUnionid() { return unionid; }
    public void setUnionid(String unionid) { this.unionid = unionid; }
    public String getLeadId() { return leadId; }
    public void setLeadId(String leadId) { this.leadId = leadId; }
    public String getWecomExternalUserid() { return wecomExternalUserid; }
    public void setWecomExternalUserid(String wecomExternalUserid) { this.wecomExternalUserid = wecomExternalUserid; }
    public String getWechatMpOpenid() { return wechatMpOpenid; }
    public void setWechatMpOpenid(String wechatMpOpenid) { this.wechatMpOpenid = wechatMpOpenid; }
}
