package com.ruoyi.crm.domain.dto;

/**
 * POST /crm/lead/{leadId}/convert 请求体
 */
public class LeadConvertRequest
{
    private String contactId;
    private String orgId;
    private String name;
    private String mobilePhone;
    /** contactOnly | withOpportunity | mergeOnly */
    private String conversionType;
    private LeadConvertCopyOptions copyOptions;
    private String opportunityName;
    /** manual | stage_arrived | event_check_in | workflow */
    private String trigger;
    private String eventId;

    public String getContactId()
    {
        return contactId;
    }

    public void setContactId(String contactId)
    {
        this.contactId = contactId;
    }

    public String getOrgId()
    {
        return orgId;
    }

    public void setOrgId(String orgId)
    {
        this.orgId = orgId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getMobilePhone()
    {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone)
    {
        this.mobilePhone = mobilePhone;
    }

    public String getConversionType()
    {
        return conversionType;
    }

    public void setConversionType(String conversionType)
    {
        this.conversionType = conversionType;
    }

    public LeadConvertCopyOptions getCopyOptions()
    {
        return copyOptions;
    }

    public void setCopyOptions(LeadConvertCopyOptions copyOptions)
    {
        this.copyOptions = copyOptions;
    }

    public String getOpportunityName()
    {
        return opportunityName;
    }

    public void setOpportunityName(String opportunityName)
    {
        this.opportunityName = opportunityName;
    }

    public String getTrigger()
    {
        return trigger;
    }

    public void setTrigger(String trigger)
    {
        this.trigger = trigger;
    }

    public String getEventId()
    {
        return eventId;
    }

    public void setEventId(String eventId)
    {
        this.eventId = eventId;
    }

    public LeadConvertCopyOptions effectiveCopyOptions()
    {
        return copyOptions != null ? copyOptions : LeadConvertCopyOptions.defaults();
    }

    public String effectiveConversionType()
    {
        if (conversionType == null || conversionType.trim().isEmpty())
        {
            return "contactOnly";
        }
        return conversionType.trim();
    }
}
