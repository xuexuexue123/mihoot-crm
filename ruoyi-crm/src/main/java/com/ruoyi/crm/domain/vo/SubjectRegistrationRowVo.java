package com.ruoyi.crm.domain.vo;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Lead / Contact 详情下的报名列表行（含全部 registration_source）
 */
public class SubjectRegistrationRowVo
{
    private String registrationId;
    private String registrationSource;
    private String eventId;
    private String eventTitle;
    private String sessionId;
    private String sessionName;
    private String leadId;
    private String contactId;
    private String registrationStatus;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date registeredAt;

    private String checkInStatus;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date checkedInAt;

    private Map<String, Object> formData;
    private String customField;
    private String displayName;
    private String mobilePhone;
    private String workEmail;

    /** MyBatis form_data::text */
    @JsonIgnore
    private String formDataJson;

    public String getRegistrationId()
    {
        return registrationId;
    }

    public void setRegistrationId(String registrationId)
    {
        this.registrationId = registrationId;
    }

    public String getRegistrationSource()
    {
        return registrationSource;
    }

    public void setRegistrationSource(String registrationSource)
    {
        this.registrationSource = registrationSource;
    }

    public String getEventId()
    {
        return eventId;
    }

    public void setEventId(String eventId)
    {
        this.eventId = eventId;
    }

    public String getEventTitle()
    {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle)
    {
        this.eventTitle = eventTitle;
    }

    public String getSessionId()
    {
        return sessionId;
    }

    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }

    public String getSessionName()
    {
        return sessionName;
    }

    public void setSessionName(String sessionName)
    {
        this.sessionName = sessionName;
    }

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

    public String getRegistrationStatus()
    {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus)
    {
        this.registrationStatus = registrationStatus;
    }

    public Date getRegisteredAt()
    {
        return registeredAt;
    }

    public void setRegisteredAt(Date registeredAt)
    {
        this.registeredAt = registeredAt;
    }

    public String getCheckInStatus()
    {
        return checkInStatus;
    }

    public void setCheckInStatus(String checkInStatus)
    {
        this.checkInStatus = checkInStatus;
    }

    public Date getCheckedInAt()
    {
        return checkedInAt;
    }

    public void setCheckedInAt(Date checkedInAt)
    {
        this.checkedInAt = checkedInAt;
    }

    public Map<String, Object> getFormData()
    {
        return formData;
    }

    public void setFormData(Map<String, Object> formData)
    {
        this.formData = formData;
    }

    public String getCustomField()
    {
        return customField;
    }

    public void setCustomField(String customField)
    {
        this.customField = customField;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public String getMobilePhone()
    {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone)
    {
        this.mobilePhone = mobilePhone;
    }

    public String getWorkEmail()
    {
        return workEmail;
    }

    public void setWorkEmail(String workEmail)
    {
        this.workEmail = workEmail;
    }

    public String getFormDataJson()
    {
        return formDataJson;
    }

    public void setFormDataJson(String formDataJson)
    {
        this.formDataJson = formDataJson;
    }
}
