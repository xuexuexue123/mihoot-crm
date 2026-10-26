package com.ruoyi.crm.domain.dto;

public class EventCheckInRequest
{
    private String sessionId;
    private String ticketToken;
    private String registrationId;
    private String audienceKey;
    private String method;
    private String notes;

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getTicketToken() { return ticketToken; }
    public void setTicketToken(String ticketToken) { this.ticketToken = ticketToken; }
    public String getRegistrationId() { return registrationId; }
    public void setRegistrationId(String registrationId) { this.registrationId = registrationId; }
    public String getAudienceKey() { return audienceKey; }
    public void setAudienceKey(String audienceKey) { this.audienceKey = audienceKey; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
