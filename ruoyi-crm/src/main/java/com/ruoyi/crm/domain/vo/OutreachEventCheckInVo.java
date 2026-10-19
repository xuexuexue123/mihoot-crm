package com.ruoyi.crm.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

public class OutreachEventCheckInVo
{
    private String eventId;
    private String eventSubject;
    private String sessionId;
    private String sessionName;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date endTime;

    private String location;
    private Boolean checkInEnabled;
    private Boolean allowWalkIn;
    private Boolean registrationEnabled;
    private String registrationFormJson;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date checkInOpenAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date checkInCloseAt;

    private Boolean oauthRequired;
    private String oauthAuthorizeUrl;
    private IdentityState identityState;

    public static class IdentityState
    {
        private boolean hasOpenId;
        private String maskedOpenId;

        public boolean isHasOpenId() { return hasOpenId; }
        public void setHasOpenId(boolean hasOpenId) { this.hasOpenId = hasOpenId; }
        public String getMaskedOpenId() { return maskedOpenId; }
        public void setMaskedOpenId(String maskedOpenId) { this.maskedOpenId = maskedOpenId; }
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getEventSubject() { return eventSubject; }
    public void setEventSubject(String eventSubject) { this.eventSubject = eventSubject; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getSessionName() { return sessionName; }
    public void setSessionName(String sessionName) { this.sessionName = sessionName; }
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Boolean getCheckInEnabled() { return checkInEnabled; }
    public void setCheckInEnabled(Boolean checkInEnabled) { this.checkInEnabled = checkInEnabled; }
    public Boolean getAllowWalkIn() { return allowWalkIn; }
    public void setAllowWalkIn(Boolean allowWalkIn) { this.allowWalkIn = allowWalkIn; }
    public Boolean getRegistrationEnabled() { return registrationEnabled; }
    public void setRegistrationEnabled(Boolean registrationEnabled) { this.registrationEnabled = registrationEnabled; }
    public String getRegistrationFormJson() { return registrationFormJson; }
    public void setRegistrationFormJson(String registrationFormJson) { this.registrationFormJson = registrationFormJson; }
    public Date getCheckInOpenAt() { return checkInOpenAt; }
    public void setCheckInOpenAt(Date checkInOpenAt) { this.checkInOpenAt = checkInOpenAt; }
    public Date getCheckInCloseAt() { return checkInCloseAt; }
    public void setCheckInCloseAt(Date checkInCloseAt) { this.checkInCloseAt = checkInCloseAt; }
    public Boolean getOauthRequired() { return oauthRequired; }
    public void setOauthRequired(Boolean oauthRequired) { this.oauthRequired = oauthRequired; }
    public String getOauthAuthorizeUrl() { return oauthAuthorizeUrl; }
    public void setOauthAuthorizeUrl(String oauthAuthorizeUrl) { this.oauthAuthorizeUrl = oauthAuthorizeUrl; }
    public IdentityState getIdentityState() { return identityState; }
    public void setIdentityState(IdentityState identityState) { this.identityState = identityState; }
}
