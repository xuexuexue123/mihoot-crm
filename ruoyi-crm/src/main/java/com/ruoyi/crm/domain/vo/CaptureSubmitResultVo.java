package com.ruoyi.crm.domain.vo;

/**
 * POST /public/captures/{captureId}/submit 响应 data
 */
public class CaptureSubmitResultVo
{
    private String leadId;
    private String resolution;
    private String contactId;
    private String visitorToken;

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

    public String getVisitorToken()
    {
        return visitorToken;
    }

    public void setVisitorToken(String visitorToken)
    {
        this.visitorToken = visitorToken;
    }
}
