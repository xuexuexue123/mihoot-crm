package com.ruoyi.crm.domain.dto;

/**
 * POST /crm/lead/{leadId}/convert 响应 data
 */
public class LeadConvertResult
{
    private String leadId;
    private String contactId;
    private boolean merged;
    private boolean skipped;
    private String opportunityId;
    private String reason;

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

    public boolean isMerged()
    {
        return merged;
    }

    public void setMerged(boolean merged)
    {
        this.merged = merged;
    }

    public boolean isSkipped()
    {
        return skipped;
    }

    public void setSkipped(boolean skipped)
    {
        this.skipped = skipped;
    }

    public String getOpportunityId()
    {
        return opportunityId;
    }

    public void setOpportunityId(String opportunityId)
    {
        this.opportunityId = opportunityId;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }
}
