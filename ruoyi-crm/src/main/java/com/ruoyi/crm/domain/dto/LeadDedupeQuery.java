package com.ruoyi.crm.domain.dto;

public class LeadDedupeQuery
{
    private String excludeLeadId;
    private String mobilePhone;
    private String wecomExternalUserid;
    private String workPhone;
    private String workEmail;
    private String wechat;

    public String getExcludeLeadId()
    {
        return excludeLeadId;
    }

    public void setExcludeLeadId(String excludeLeadId)
    {
        this.excludeLeadId = excludeLeadId;
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
}
