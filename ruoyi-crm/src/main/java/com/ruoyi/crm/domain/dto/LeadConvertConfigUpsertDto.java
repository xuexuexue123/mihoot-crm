package com.ruoyi.crm.domain.dto;

/**
 * PUT /crm/lead/convert/config 请求体
 */
public class LeadConvertConfigUpsertDto
{
    private Boolean autoConvertOnFirstVisit;
    private Boolean autoConvertOnCheckIn;
    private Boolean autoConvertOnRegistration;
    private Boolean mergeOnDuplicatePhone;
    private String defaultOrgId;
    private String contactOwnerMode;
    private Long contactOwnerUserId;

    public Boolean getAutoConvertOnFirstVisit()
    {
        return autoConvertOnFirstVisit;
    }

    public void setAutoConvertOnFirstVisit(Boolean autoConvertOnFirstVisit)
    {
        this.autoConvertOnFirstVisit = autoConvertOnFirstVisit;
    }

    public Boolean getAutoConvertOnCheckIn()
    {
        return autoConvertOnCheckIn;
    }

    public void setAutoConvertOnCheckIn(Boolean autoConvertOnCheckIn)
    {
        this.autoConvertOnCheckIn = autoConvertOnCheckIn;
    }

    public Boolean getAutoConvertOnRegistration()
    {
        return autoConvertOnRegistration;
    }

    public void setAutoConvertOnRegistration(Boolean autoConvertOnRegistration)
    {
        this.autoConvertOnRegistration = autoConvertOnRegistration;
    }

    public Boolean getMergeOnDuplicatePhone()
    {
        return mergeOnDuplicatePhone;
    }

    public void setMergeOnDuplicatePhone(Boolean mergeOnDuplicatePhone)
    {
        this.mergeOnDuplicatePhone = mergeOnDuplicatePhone;
    }

    public String getDefaultOrgId()
    {
        return defaultOrgId;
    }

    public void setDefaultOrgId(String defaultOrgId)
    {
        this.defaultOrgId = defaultOrgId;
    }

    public String getContactOwnerMode()
    {
        return contactOwnerMode;
    }

    public void setContactOwnerMode(String contactOwnerMode)
    {
        this.contactOwnerMode = contactOwnerMode;
    }

    public Long getContactOwnerUserId()
    {
        return contactOwnerUserId;
    }

    public void setContactOwnerUserId(Long contactOwnerUserId)
    {
        this.contactOwnerUserId = contactOwnerUserId;
    }
}
