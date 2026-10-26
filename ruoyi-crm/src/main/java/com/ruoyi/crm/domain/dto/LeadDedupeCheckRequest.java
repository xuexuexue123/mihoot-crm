package com.ruoyi.crm.domain.dto;

public class LeadDedupeCheckRequest
{
    private String entity;
    private String leadId;
    private String mobilePhone;
    private String wecomExternalUserid;
    private String workPhone;
    private String workEmail;
    private String wechat;
    private String personalEmail;
    private String wechatUnionid;
    private String wechatMpOpenid;

    public String getEntity()
    {
        return entity;
    }

    public void setEntity(String entity)
    {
        this.entity = entity;
    }

    public String getLeadId()
    {
        return leadId;
    }

    public void setLeadId(String leadId)
    {
        this.leadId = leadId;
    }

    public String getMobilePhone()
    {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone)
    {
        this.mobilePhone = mobilePhone;
    }

    public String getWecomExternalUserid()
    {
        return wecomExternalUserid;
    }

    public void setWecomExternalUserid(String wecomExternalUserid)
    {
        this.wecomExternalUserid = wecomExternalUserid;
    }

    public String getWorkPhone()
    {
        return workPhone;
    }

    public void setWorkPhone(String workPhone)
    {
        this.workPhone = workPhone;
    }

    public String getWorkEmail()
    {
        return workEmail;
    }

    public void setWorkEmail(String workEmail)
    {
        this.workEmail = workEmail;
    }

    public String getWechat()
    {
        return wechat;
    }

    public void setWechat(String wechat)
    {
        this.wechat = wechat;
    }

    public String getPersonalEmail()
    {
        return personalEmail;
    }

    public void setPersonalEmail(String personalEmail)
    {
        this.personalEmail = personalEmail;
    }

    public String getWechatUnionid()
    {
        return wechatUnionid;
    }

    public void setWechatUnionid(String wechatUnionid)
    {
        this.wechatUnionid = wechatUnionid;
    }

    public String getWechatMpOpenid()
    {
        return wechatMpOpenid;
    }

    public void setWechatMpOpenid(String wechatMpOpenid)
    {
        this.wechatMpOpenid = wechatMpOpenid;
    }
}
