package com.ruoyi.crm.service.support;

/** 活动受众：audience_key → 最优报名记录 */
public class EventRegistrationBestRow
{
    private String audienceKey;
    private String registrationStatus;
    private String sessionName;

    public String getAudienceKey() { return audienceKey; }
    public void setAudienceKey(String audienceKey) { this.audienceKey = audienceKey; }
    public String getRegistrationStatus() { return registrationStatus; }
    public void setRegistrationStatus(String registrationStatus) { this.registrationStatus = registrationStatus; }
    public String getSessionName() { return sessionName; }
    public void setSessionName(String sessionName) { this.sessionName = sessionName; }
}
