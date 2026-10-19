package com.ruoyi.crm.domain.vo;

import java.util.List;

public class LeadRecycleDedupeConfigVo
{
    private List<String> leadMatchFields;
    private List<String> contactMatchFields;
    private String matchMode;

    public List<String> getLeadMatchFields()
    {
        return leadMatchFields;
    }

    public void setLeadMatchFields(List<String> leadMatchFields)
    {
        this.leadMatchFields = leadMatchFields;
    }

    public List<String> getContactMatchFields()
    {
        return contactMatchFields;
    }

    public void setContactMatchFields(List<String> contactMatchFields)
    {
        this.contactMatchFields = contactMatchFields;
    }

    public String getMatchMode()
    {
        return matchMode;
    }

    public void setMatchMode(String matchMode)
    {
        this.matchMode = matchMode;
    }
}
