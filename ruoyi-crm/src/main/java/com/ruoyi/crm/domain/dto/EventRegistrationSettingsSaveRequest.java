package com.ruoyi.crm.domain.dto;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

public class EventRegistrationSettingsSaveRequest
{
    private Boolean registrationEnabled;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date registrationDeadline;

    private Boolean waitlistEnabled;
    private Boolean checkInEnabled;
    private Boolean allowWalkIn;
    private String defaultSessionId;

    public Boolean getRegistrationEnabled() { return registrationEnabled; }
    public void setRegistrationEnabled(Boolean registrationEnabled) { this.registrationEnabled = registrationEnabled; }
    public Date getRegistrationDeadline() { return registrationDeadline; }
    public void setRegistrationDeadline(Date registrationDeadline) { this.registrationDeadline = registrationDeadline; }
    public Boolean getWaitlistEnabled() { return waitlistEnabled; }
    public void setWaitlistEnabled(Boolean waitlistEnabled) { this.waitlistEnabled = waitlistEnabled; }
    public Boolean getCheckInEnabled() { return checkInEnabled; }
    public void setCheckInEnabled(Boolean checkInEnabled) { this.checkInEnabled = checkInEnabled; }
    public Boolean getAllowWalkIn() { return allowWalkIn; }
    public void setAllowWalkIn(Boolean allowWalkIn) { this.allowWalkIn = allowWalkIn; }
    public String getDefaultSessionId() { return defaultSessionId; }
    public void setDefaultSessionId(String defaultSessionId) { this.defaultSessionId = defaultSessionId; }
}
