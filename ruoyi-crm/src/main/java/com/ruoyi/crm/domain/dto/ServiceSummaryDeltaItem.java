package com.ruoyi.crm.domain.dto;

import java.util.Date;

/**
 * Worker / LLM 增量消息记录
 */
public class ServiceSummaryDeltaItem
{
    private String eventId;
    private String channel;
    /** in | out */
    private String direction;
    private Date at;
    private String text;
    /** customer | agent | ai */
    private String actor;

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
    public Date getAt() { return at; }
    public void setAt(Date at) { this.at = at; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }
}
