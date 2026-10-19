package com.ruoyi.crm.service.support;

import com.ruoyi.crm.domain.CrmLead;

/**
 * H5 活动报名身份解析结果
 */
public final class OutreachRegistrationIdentityResolution
{
    public enum Kind
    {
        CONTACT_KNOWN,
        LEAD_UPSERT,
        CREATE_NEW_LEAD
    }

    private final Kind kind;
    private final String contactId;
    private final String reason;
    private final CrmLead existingLead;

    private OutreachRegistrationIdentityResolution(Kind kind, String contactId, String reason, CrmLead existingLead)
    {
        this.kind = kind;
        this.contactId = contactId;
        this.reason = reason;
        this.existingLead = existingLead;
    }

    public static OutreachRegistrationIdentityResolution contactKnown(String contactId, String reason)
    {
        return new OutreachRegistrationIdentityResolution(Kind.CONTACT_KNOWN, contactId, reason, null);
    }

    public static OutreachRegistrationIdentityResolution leadUpsert(CrmLead existingLead)
    {
        return new OutreachRegistrationIdentityResolution(Kind.LEAD_UPSERT, null, null, existingLead);
    }

    public static OutreachRegistrationIdentityResolution createNewLead()
    {
        return new OutreachRegistrationIdentityResolution(Kind.CREATE_NEW_LEAD, null, null, null);
    }

    public Kind getKind()
    {
        return kind;
    }

    public String getContactId()
    {
        return contactId;
    }

    public String getReason()
    {
        return reason;
    }

    public CrmLead getExistingLead()
    {
        return existingLead;
    }

    public boolean isContactKnown()
    {
        return kind == Kind.CONTACT_KNOWN;
    }
}
