package com.ruoyi.crm.domain.vo;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 匿名 H5 活动报名表单响应
 */
public class OutreachEventRegistrationVo
{
  private String eventId;

  private String eventSubject;

  private String registrationFormJson;

  private OutreachRegistrationSettingsBrief registrationSettings;

  private List<OutreachSessionBrief> sessions;

  private String defaultSessionId;

  public static class OutreachRegistrationSettingsBrief
  {
    private Boolean registrationEnabled;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date registrationDeadline;

    private Boolean waitlistEnabled;

    public Boolean getRegistrationEnabled() { return registrationEnabled; }
    public void setRegistrationEnabled(Boolean registrationEnabled) { this.registrationEnabled = registrationEnabled; }
    public Date getRegistrationDeadline() { return registrationDeadline; }
    public void setRegistrationDeadline(Date registrationDeadline) { this.registrationDeadline = registrationDeadline; }
    public Boolean getWaitlistEnabled() { return waitlistEnabled; }
    public void setWaitlistEnabled(Boolean waitlistEnabled) { this.waitlistEnabled = waitlistEnabled; }
  }

  public static class OutreachSessionBrief
  {
    private String sessionId;
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date endTime;

    private String location;
    private Integer capacity;
    private int confirmedCount;
    private boolean isFull;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date registrationDeadline;

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public int getConfirmedCount() { return confirmedCount; }
    public void setConfirmedCount(int confirmedCount) { this.confirmedCount = confirmedCount; }
    public boolean isFull() { return isFull; }
    public void setFull(boolean full) { isFull = full; }
    public Date getRegistrationDeadline() { return registrationDeadline; }
    public void setRegistrationDeadline(Date registrationDeadline) { this.registrationDeadline = registrationDeadline; }
  }

  public String getEventId()
  {
    return eventId;
  }

  public void setEventId(String eventId)
  {
    this.eventId = eventId;
  }

  public String getEventSubject()
  {
    return eventSubject;
  }

  public void setEventSubject(String eventSubject)
  {
    this.eventSubject = eventSubject;
  }

  public String getRegistrationFormJson()
  {
    return registrationFormJson;
  }

  public void setRegistrationFormJson(String registrationFormJson)
  {
    this.registrationFormJson = registrationFormJson;
  }

  public OutreachRegistrationSettingsBrief getRegistrationSettings()
  {
    return registrationSettings;
  }

  public void setRegistrationSettings(OutreachRegistrationSettingsBrief registrationSettings)
  {
    this.registrationSettings = registrationSettings;
  }

  public List<OutreachSessionBrief> getSessions()
  {
    return sessions;
  }

  public void setSessions(List<OutreachSessionBrief> sessions)
  {
    this.sessions = sessions;
  }

  public String getDefaultSessionId()
  {
    return defaultSessionId;
  }

  public void setDefaultSessionId(String defaultSessionId)
  {
    this.defaultSessionId = defaultSessionId;
  }
}
