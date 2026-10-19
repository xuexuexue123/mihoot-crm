package com.ruoyi.crm.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * Scope carrier for context summary access checks and counts.
 */
public class ContextSummaryScope extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String entityId;

    private String orgId;

    private String accountId;

    private String contactId;

    private String optyId;

    private String leadId;

    private String pulseRelateType;

    private Integer pulseLimit;

    public String getEntityId()
    {
        return entityId;
    }

    public void setEntityId(String entityId)
    {
        this.entityId = entityId;
    }

    public String getOrgId()
    {
        return orgId;
    }

    public void setOrgId(String orgId)
    {
        this.orgId = orgId;
    }

    public String getAccountId()
    {
        return accountId;
    }

    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }

    public String getContactId()
    {
        return contactId;
    }

    public void setContactId(String contactId)
    {
        this.contactId = contactId;
    }

    public String getOptyId()
    {
        return optyId;
    }

    public void setOptyId(String optyId)
    {
        this.optyId = optyId;
    }

    public String getLeadId()
    {
        return leadId;
    }

    public void setLeadId(String leadId)
    {
        this.leadId = leadId;
    }

    public String getPulseRelateType()
    {
        return pulseRelateType;
    }

    public void setPulseRelateType(String pulseRelateType)
    {
        this.pulseRelateType = pulseRelateType;
    }

    public Integer getPulseLimit()
    {
        return pulseLimit;
    }

    public void setPulseLimit(Integer pulseLimit)
    {
        this.pulseLimit = pulseLimit;
    }
}
