package com.ruoyi.crm.domain.vo;

/**
 * POST /websitelead 响应 data
 */
public class WebsiteLeadSubmitResultVo
{
    private String leadId;
    private String resolution;
    private String contactId;

    public String getLeadId()
    {
        return leadId;
    }

    public void setLeadId(String leadId)
    {
        this.leadId = leadId;
    }

    public String getResolution()
    {
        return resolution;
    }

    public void setResolution(String resolution)
    {
        this.resolution = resolution;
    }

    public String getContactId()
    {
        return contactId;
    }

    public void setContactId(String contactId)
    {
        this.contactId = contactId;
    }
}
