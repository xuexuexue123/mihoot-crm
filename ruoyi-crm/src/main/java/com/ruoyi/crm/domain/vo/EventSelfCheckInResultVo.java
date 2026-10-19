package com.ruoyi.crm.domain.vo;

public class EventSelfCheckInResultVo extends EventCheckInResultVo
{
    private String registrationId;
    private String registrationStatus;

    public String getRegistrationId() { return registrationId; }
    public void setRegistrationId(String registrationId) { this.registrationId = registrationId; }
    public String getRegistrationStatus() { return registrationStatus; }
    public void setRegistrationStatus(String registrationStatus) { this.registrationStatus = registrationStatus; }
}
