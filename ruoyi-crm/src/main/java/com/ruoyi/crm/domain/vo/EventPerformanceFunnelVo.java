package com.ruoyi.crm.domain.vo;

/**
 * 活动效果漏斗各阶段人数
 */
public class EventPerformanceFunnelVo
{
    private int touchCount;
    private int h5RegistrationCount;
    private int registrationPendingCount;
    private int leadReleasedCount;
    private int leadActiveCount;
    private int contactConvertedCount;
    private int contactKnownCount;
    private int registrationConfirmedCount;
    private int waitlistCount;
    private int checkedInCount;

    public int getTouchCount() { return touchCount; }
    public void setTouchCount(int touchCount) { this.touchCount = touchCount; }
    public int getH5RegistrationCount() { return h5RegistrationCount; }
    public void setH5RegistrationCount(int h5RegistrationCount) { this.h5RegistrationCount = h5RegistrationCount; }
    public int getRegistrationPendingCount() { return registrationPendingCount; }
    public void setRegistrationPendingCount(int registrationPendingCount) { this.registrationPendingCount = registrationPendingCount; }
    public int getLeadReleasedCount() { return leadReleasedCount; }
    public void setLeadReleasedCount(int leadReleasedCount) { this.leadReleasedCount = leadReleasedCount; }
    public int getLeadActiveCount() { return leadActiveCount; }
    public void setLeadActiveCount(int leadActiveCount) { this.leadActiveCount = leadActiveCount; }
    public int getContactConvertedCount() { return contactConvertedCount; }
    public void setContactConvertedCount(int contactConvertedCount) { this.contactConvertedCount = contactConvertedCount; }
    public int getContactKnownCount() { return contactKnownCount; }
    public void setContactKnownCount(int contactKnownCount) { this.contactKnownCount = contactKnownCount; }
    public int getRegistrationConfirmedCount() { return registrationConfirmedCount; }
    public void setRegistrationConfirmedCount(int registrationConfirmedCount) { this.registrationConfirmedCount = registrationConfirmedCount; }
    public int getWaitlistCount() { return waitlistCount; }
    public void setWaitlistCount(int waitlistCount) { this.waitlistCount = waitlistCount; }
    public int getCheckedInCount() { return checkedInCount; }
    public void setCheckedInCount(int checkedInCount) { this.checkedInCount = checkedInCount; }
}
