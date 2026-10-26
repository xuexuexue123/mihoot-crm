package com.ruoyi.crm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

public class CrmEventRegistration extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String registrationId;
    private String eventId;
    private String sessionId;
    private String tenantId;
    private String leadId;
    private String contactId;
    private String audienceKey;
    private String displayName;
    private String mobilePhone;
    private String registrationStatus;
    private String registrationSource;
    private String ticketTokenHash;
    private String formData;
    private String customField;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date registeredAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date cancelledAt;

    public String getRegistrationId() { return registrationId; }
    public void setRegistrationId(String registrationId) { this.registrationId = registrationId; }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getLeadId() { return leadId; }
    public void setLeadId(String leadId) { this.leadId = leadId; }
    public String getContactId() { return contactId; }
    public void setContactId(String contactId) { this.contactId = contactId; }
    public String getAudienceKey() { return audienceKey; }
    public void setAudienceKey(String audienceKey) { this.audienceKey = audienceKey; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getMobilePhone() { return mobilePhone; }
    public void setMobilePhone(String mobilePhone) { this.mobilePhone = mobilePhone; }
    public String getRegistrationStatus() { return registrationStatus; }
    public void setRegistrationStatus(String registrationStatus) { this.registrationStatus = registrationStatus; }
    public String getRegistrationSource() { return registrationSource; }
    public void setRegistrationSource(String registrationSource) { this.registrationSource = registrationSource; }
    public String getTicketTokenHash() { return ticketTokenHash; }
    public void setTicketTokenHash(String ticketTokenHash) { this.ticketTokenHash = ticketTokenHash; }
    public String getFormData() { return formData; }
    public void setFormData(String formData) { this.formData = formData; }
    public String getCustomField() { return customField; }
    public void setCustomField(String customField) { this.customField = customField; }
    public Date getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(Date registeredAt) { this.registeredAt = registeredAt; }
    public Date getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(Date cancelledAt) { this.cancelledAt = cancelledAt; }
}
