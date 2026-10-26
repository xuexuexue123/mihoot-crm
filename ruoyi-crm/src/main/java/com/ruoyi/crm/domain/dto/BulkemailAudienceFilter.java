package com.ruoyi.crm.domain.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 邮件营销受众筛选条件
 */
public class BulkemailAudienceFilter
{
    private Boolean hasWorkEmail = Boolean.TRUE;
    private String keyword;
    private List<String> tags = new ArrayList<>();
    private Integer lastVisitBeforeDays;
    private String eventId;
    private Boolean excludeUnsubscribed = Boolean.TRUE;

    public Boolean getHasWorkEmail()
    {
        return hasWorkEmail;
    }

    public void setHasWorkEmail(Boolean hasWorkEmail)
    {
        this.hasWorkEmail = hasWorkEmail;
    }

    public String getKeyword()
    {
        return keyword;
    }

    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }

    public List<String> getTags()
    {
        return tags;
    }

    public void setTags(List<String> tags)
    {
        this.tags = tags;
    }

    public Integer getLastVisitBeforeDays()
    {
        return lastVisitBeforeDays;
    }

    public void setLastVisitBeforeDays(Integer lastVisitBeforeDays)
    {
        this.lastVisitBeforeDays = lastVisitBeforeDays;
    }

    public String getEventId()
    {
        return eventId;
    }

    public void setEventId(String eventId)
    {
        this.eventId = eventId;
    }

    public Boolean getExcludeUnsubscribed()
    {
        return excludeUnsubscribed;
    }

    public void setExcludeUnsubscribed(Boolean excludeUnsubscribed)
    {
        this.excludeUnsubscribed = excludeUnsubscribed;
    }
}
