package com.ruoyi.crm.domain.vo;

import java.math.BigDecimal;

/**
 * 活动效果转化率
 */
public class EventPerformanceRatesVo
{
    private BigDecimal touchToRegistration;
    private BigDecimal registrationToLeadReleased;
    private BigDecimal leadReleasedToContact;
    private BigDecimal touchToContact;
    private BigDecimal registrationToCheckIn;
    private BigDecimal touchToCheckIn;

    public BigDecimal getTouchToRegistration() { return touchToRegistration; }
    public void setTouchToRegistration(BigDecimal touchToRegistration) { this.touchToRegistration = touchToRegistration; }
    public BigDecimal getRegistrationToLeadReleased() { return registrationToLeadReleased; }
    public void setRegistrationToLeadReleased(BigDecimal registrationToLeadReleased) { this.registrationToLeadReleased = registrationToLeadReleased; }
    public BigDecimal getLeadReleasedToContact() { return leadReleasedToContact; }
    public void setLeadReleasedToContact(BigDecimal leadReleasedToContact) { this.leadReleasedToContact = leadReleasedToContact; }
    public BigDecimal getTouchToContact() { return touchToContact; }
    public void setTouchToContact(BigDecimal touchToContact) { this.touchToContact = touchToContact; }
    public BigDecimal getRegistrationToCheckIn() { return registrationToCheckIn; }
    public void setRegistrationToCheckIn(BigDecimal registrationToCheckIn) { this.registrationToCheckIn = registrationToCheckIn; }
    public BigDecimal getTouchToCheckIn() { return touchToCheckIn; }
    public void setTouchToCheckIn(BigDecimal touchToCheckIn) { this.touchToCheckIn = touchToCheckIn; }
}
