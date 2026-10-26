package com.ruoyi.crm.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

public class EventRegistrationRowVo
{
    private String registrationId;
    private String eventId;
    private String sessionId;
    private String sessionName;
    private String leadId;
    private String contactId;
    private String audienceKey;
    private String displayName;
    private String mobilePhone;
    private String registrationStatus;
    private String registrationSource;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date registeredAt;

    private String checkInStatus;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date checkedInAt;

    public String getRegistrationId() { return registrationId; }
    public void setRegistrationId(String registrationId) { this.registrationId = registrationId; }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getSessionName() { return sessionName; }
    public void setSessionName(String sessionName) { this.sessionName = sessionName; }
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
    public Date getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(Date registeredAt) { this.registeredAt = registeredAt; }
    public String getCheckInStatus() { return checkInStatus; }
    public void setCheckInStatus(String checkInStatus) { this.checkInStatus = checkInStatus; }
    public Date getCheckedInAt() { return checkedInAt; }
    public void setCheckedInAt(Date checkedInAt) { this.checkedInAt = checkedInAt; }
}
