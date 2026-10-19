package com.ruoyi.crm.domain.dto;

import java.util.Map;

/**
 * 小程序 BFF：七库市场活动报名
 */
public class MiniappMarketEventRegisterRequest
{
    private String idempotencyKey;
    private String marketEventId;
    private String eventTitle;
    private String registrationType;
    private String typeLabel;
    private String contactName;
    private String leadName;
    private String gender;
    private String mobilePhone;
    private String email;
    private String companyName;
    private String businessField;
    private String website;
    private String address;
    private String appointmentDate;
    /** 权威 form_data 快照 */
    private Map<String, Object> slots;

    public String getIdempotencyKey()
    {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey)
    {
        this.idempotencyKey = idempotencyKey;
    }

    public String getMarketEventId()
    {
        return marketEventId;
    }

    public void setMarketEventId(String marketEventId)
    {
        this.marketEventId = marketEventId;
    }

    public String getEventTitle()
    {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle)
    {
        this.eventTitle = eventTitle;
    }

    public String getRegistrationType()
    {
        return registrationType;
    }

    public void setRegistrationType(String registrationType)
    {
        this.registrationType = registrationType;
    }

    public String getTypeLabel()
    {
        return typeLabel;
    }

    public void setTypeLabel(String typeLabel)
    {
        this.typeLabel = typeLabel;
    }

    public String getContactName()
    {
        return contactName;
    }

    public void setContactName(String contactName)
    {
        this.contactName = contactName;
    }

    public String getLeadName()
    {
        return leadName;
    }

    public void setLeadName(String leadName)
    {
        this.leadName = leadName;
    }

    public String getGender()
    {
        return gender;
    }

    public void setGender(String gender)
    {
        this.gender = gender;
    }

    public String getMobilePhone()
    {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone)
    {
        this.mobilePhone = mobilePhone;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getCompanyName()
    {
        return companyName;
    }

    public void setCompanyName(String companyName)
    {
        this.companyName = companyName;
    }

    public String getBusinessField()
    {
        return businessField;
    }

    public void setBusinessField(String businessField)
    {
        this.businessField = businessField;
    }

    public String getWebsite()
    {
        return website;
    }

    public void setWebsite(String website)
    {
        this.website = website;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getAppointmentDate()
    {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate)
    {
        this.appointmentDate = appointmentDate;
    }

    public Map<String, Object> getSlots()
    {
        return slots;
    }

    public void setSlots(Map<String, Object> slots)
    {
        this.slots = slots;
    }
}
