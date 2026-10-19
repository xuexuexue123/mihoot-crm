package com.ruoyi.crm.domain.vo;

/**
 * 小程序七库市场活动报名响应
 */
public class MiniappMarketEventRegisterResultVo
{
    private String leadId;
    private String contactId;
    private String registrationId;
    /** lead_created | lead_updated | contact_known | registration_updated */
    private String resolution;
    private String audienceKey;
    private String eventId;
    private String registrationSource;

    public static MiniappMarketEventRegisterResultVo of(String leadId, String contactId, String registrationId,
            String resolution, String audienceKey, String eventId, String registrationSource)
    {
        MiniappMarketEventRegisterResultVo vo = new MiniappMarketEventRegisterResultVo();
        vo.setLeadId(leadId);
        vo.setContactId(contactId);
        vo.setRegistrationId(registrationId);
        vo.setResolution(resolution);
        vo.setAudienceKey(audienceKey);
        vo.setEventId(eventId);
        vo.setRegistrationSource(registrationSource);
        return vo;
    }

    public String getLeadId()
    {
        return leadId;
    }

    public void setLeadId(String leadId)
    {
        this.leadId = leadId;
    }

    public String getContactId()
    {
        return contactId;
    }

    public void setContactId(String contactId)
    {
        this.contactId = contactId;
    }

    public String getRegistrationId()
    {
        return registrationId;
    }

    public void setRegistrationId(String registrationId)
    {
        this.registrationId = registrationId;
    }

    public String getResolution()
    {
        return resolution;
    }

    public void setResolution(String resolution)
    {
        this.resolution = resolution;
    }

    public String getAudienceKey()
    {
        return audienceKey;
    }

    public void setAudienceKey(String audienceKey)
    {
        this.audienceKey = audienceKey;
    }

    public String getEventId()
    {
        return eventId;
    }

    public void setEventId(String eventId)
    {
        this.eventId = eventId;
    }

    public String getRegistrationSource()
    {
        return registrationSource;
    }

    public void setRegistrationSource(String registrationSource)
    {
        this.registrationSource = registrationSource;
    }
}
