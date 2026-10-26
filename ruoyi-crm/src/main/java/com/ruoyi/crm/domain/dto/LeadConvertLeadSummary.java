package com.ruoyi.crm.domain.dto;

/**
 * evaluate 响应中的 Lead 摘要
 */
public class LeadConvertLeadSummary
{
    private String leadId;
    private String leadName;
    private String mobilePhone;
    private String stage;

    public String getLeadId()
    {
        return leadId;
    }

    public void setLeadId(String leadId)
    {
        this.leadId = leadId;
    }

    public String getLeadName()
    {
        return leadName;
    }

    public void setLeadName(String leadName)
    {
        this.leadName = leadName;
    }

    public String getMobilePhone()
    {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone)
    {
        this.mobilePhone = mobilePhone;
    }

    public String getStage()
    {
        return stage;
    }

    public void setStage(String stage)
    {
        this.stage = stage;
    }
}
