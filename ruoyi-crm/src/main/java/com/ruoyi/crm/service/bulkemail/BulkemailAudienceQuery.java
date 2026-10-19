package com.ruoyi.crm.service.bulkemail;

import java.util.ArrayList;
import java.util.List;

import com.ruoyi.crm.domain.dto.BulkemailAudienceFilter;

/**
 * 受众筛选 SQL 参数
 */
public class BulkemailAudienceQuery
{
    private Boolean hasWorkEmail;
    private String keyword;
    private List<String> tags = new ArrayList<>();
    private Integer lastVisitBeforeDays;
    private List<String> eventContactIds = new ArrayList<>();
    private boolean eventFilterActive;

    public static BulkemailAudienceQuery fromFilter(BulkemailAudienceFilter filter, List<String> eventContactIds,
            boolean eventFilterActive)
    {
        BulkemailAudienceQuery query = new BulkemailAudienceQuery();
        if (filter != null)
        {
            query.setHasWorkEmail(filter.getHasWorkEmail());
            query.setKeyword(filter.getKeyword());
            if (filter.getTags() != null)
            {
                query.setTags(filter.getTags());
            }
            query.setLastVisitBeforeDays(filter.getLastVisitBeforeDays());
        }
        query.setEventFilterActive(eventFilterActive);
        if (eventContactIds != null)
        {
            query.setEventContactIds(eventContactIds);
        }
        return query;
    }

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

    public List<String> getEventContactIds()
    {
        return eventContactIds;
    }

    public void setEventContactIds(List<String> eventContactIds)
    {
        this.eventContactIds = eventContactIds;
    }

    public boolean isEventFilterActive()
    {
        return eventFilterActive;
    }

    public void setEventFilterActive(boolean eventFilterActive)
    {
        this.eventFilterActive = eventFilterActive;
    }
}
