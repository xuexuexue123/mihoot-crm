package com.ruoyi.crm.domain.vo;

import java.math.BigDecimal;

public class EventCheckInSummaryVo
{
    private String eventId;
    private String sessionId;
    private String sessionName;
    private Integer capacity;
    private int confirmedCount;
    private int waitlistCount;
    private int checkedInCount;
    private int walkInCheckInCount;
    private int audienceOnlyCheckInCount;
    private BigDecimal attendanceRate;

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getSessionName() { return sessionName; }
    public void setSessionName(String sessionName) { this.sessionName = sessionName; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public int getConfirmedCount() { return confirmedCount; }
    public void setConfirmedCount(int confirmedCount) { this.confirmedCount = confirmedCount; }
    public int getWaitlistCount() { return waitlistCount; }
    public void setWaitlistCount(int waitlistCount) { this.waitlistCount = waitlistCount; }
    public int getCheckedInCount() { return checkedInCount; }
    public void setCheckedInCount(int checkedInCount) { this.checkedInCount = checkedInCount; }
    public int getWalkInCheckInCount() { return walkInCheckInCount; }
    public void setWalkInCheckInCount(int walkInCheckInCount) { this.walkInCheckInCount = walkInCheckInCount; }
    public int getAudienceOnlyCheckInCount() { return audienceOnlyCheckInCount; }
    public void setAudienceOnlyCheckInCount(int audienceOnlyCheckInCount) { this.audienceOnlyCheckInCount = audienceOnlyCheckInCount; }
    public BigDecimal getAttendanceRate() { return attendanceRate; }
    public void setAttendanceRate(BigDecimal attendanceRate) { this.attendanceRate = attendanceRate; }
}
