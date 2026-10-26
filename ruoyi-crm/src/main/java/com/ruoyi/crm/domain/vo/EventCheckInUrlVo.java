package com.ruoyi.crm.domain.vo;

/**
 * 现场自助签到码 URL 签发响应
 */
public class EventCheckInUrlVo
{
    private String eventId;
    private String sessionId;
    private String sessionName;
    private String checkInUrl;
    private String expiresAt;

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getSessionName() { return sessionName; }
    public void setSessionName(String sessionName) { this.sessionName = sessionName; }
    public String getCheckInUrl() { return checkInUrl; }
    public void setCheckInUrl(String checkInUrl) { this.checkInUrl = checkInUrl; }
    public String getExpiresAt() { return expiresAt; }
    public void setExpiresAt(String expiresAt) { this.expiresAt = expiresAt; }
}
