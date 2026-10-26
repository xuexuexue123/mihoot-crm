package com.ruoyi.crm.domain.vo;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 官网公开活动详情（安全子集）
 */
public class PublicWebsiteEventDetailVo
{
    private String eventId;
    private String subject;
    private String avatar;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date endTime;

    private String location;
    private String description;
    private String agenda;
    private List<PublicWebsiteEventSpeakerVo> speakers;
    private String registrationFormJson;
    private PublicWebsiteEventRegistrationSettingsVo registrationSettings;
    private List<PublicWebsiteEventSessionVo> sessions;
    private String defaultSessionId;

    public String getEventId()
    {
        return eventId;
    }

    public void setEventId(String eventId)
    {
        this.eventId = eventId;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getAvatar()
    {
        return avatar;
    }

    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getAgenda()
    {
        return agenda;
    }

    public void setAgenda(String agenda)
    {
        this.agenda = agenda;
    }

    public List<PublicWebsiteEventSpeakerVo> getSpeakers()
    {
        return speakers;
    }

    public void setSpeakers(List<PublicWebsiteEventSpeakerVo> speakers)
    {
        this.speakers = speakers;
    }

    public String getRegistrationFormJson()
    {
        return registrationFormJson;
    }

    public void setRegistrationFormJson(String registrationFormJson)
    {
        this.registrationFormJson = registrationFormJson;
    }

    public PublicWebsiteEventRegistrationSettingsVo getRegistrationSettings()
    {
        return registrationSettings;
    }

    public void setRegistrationSettings(PublicWebsiteEventRegistrationSettingsVo registrationSettings)
    {
        this.registrationSettings = registrationSettings;
    }

    public List<PublicWebsiteEventSessionVo> getSessions()
    {
        return sessions;
    }

    public void setSessions(List<PublicWebsiteEventSessionVo> sessions)
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
