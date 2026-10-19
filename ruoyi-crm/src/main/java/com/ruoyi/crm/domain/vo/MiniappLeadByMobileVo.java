package com.ruoyi.crm.domain.vo;

/**
 * 小程序按手机号查线索摘要
 */
public class MiniappLeadByMobileVo
{
    private String leadId;
    private String contactId;
    private String mobilePhone;
    private String leadName;
    private String stage;
    private String status;
    private String leadSource;

    public String getLeadId()
    {
        return leadId;
    }

    public void setLeadId(String leadId)
    {
        this.leadId = leadId;
    }

    public String getContactId()
    {
        return contactId;
    }

    public void setContactId(String contactId)
    {
        this.contactId = contactId;
    }

    public String getMobilePhone()
    {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone)
    {
        this.mobilePhone = mobilePhone;
    }

    public String getLeadName()
    {
        return leadName;
    }

    public void setLeadName(String leadName)
    {
        this.leadName = leadName;
    }

    public String getStage()
    {
        return stage;
    }

    public void setStage(String stage)
    {
        this.stage = stage;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getLeadSource()
    {
        return leadSource;
    }

    public void setLeadSource(String leadSource)
    {
        this.leadSource = leadSource;
    }
}
