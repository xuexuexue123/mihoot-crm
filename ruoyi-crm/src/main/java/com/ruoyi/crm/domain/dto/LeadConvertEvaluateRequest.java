package com.ruoyi.crm.domain.dto;

/**
 * POST /crm/lead/{leadId}/convert/evaluate 请求体
 */
public class LeadConvertEvaluateRequest
{
    /** stage_arrived | event_check_in */
    private String trigger;
    private String eventId;
    private String audienceKey;
    private String registrationId;

    public String getTrigger()
    {
        return trigger;
    }

    public void setTrigger(String trigger)
    {
        this.trigger = trigger;
    }

    public String getEventId()
    {
        return eventId;
    }

    public void setEventId(String eventId)
    {
        this.eventId = eventId;
    }

    public String getAudienceKey()
    {
        return audienceKey;
    }

    public void setAudienceKey(String audienceKey)
    {
        this.audienceKey = audienceKey;
    }

    public String getRegistrationId()
    {
        return registrationId;
    }

    public void setRegistrationId(String registrationId)
    {
        this.registrationId = registrationId;
    }
}
