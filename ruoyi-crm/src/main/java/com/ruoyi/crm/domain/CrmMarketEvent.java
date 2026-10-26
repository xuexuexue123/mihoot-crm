package com.ruoyi.crm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 市场活动主档 crm_market_event
 */
public class CrmMarketEvent extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String id;

    private String titleCn;
    private String titleVn;
    private String titleEn;
    private String eventType;
    private String status;
    private String organizer;
    private String industry;
    /** YYYY-MM-DD */
    private String startDate;
    /** YYYY-MM-DD */
    private String endDate;
    private String province;
    private String venue;
    private String targetAudience;
    private String scale;
    private String entryFee;
    private String registrationLink;
    private String contactPerson;
    private String highlights;
    private String notes;
    /** 逻辑关联园区 ID */
    private String relatedParkId;
    /** 封面图（upload 相对路径或绝对 URL） */
    private String coverUrl;

    private String delFlag;

    /** 列表查询关键字（非表字段） */
    @JsonIgnore
    private String searchQuery;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getTitleCn()
    {
        return titleCn;
    }

    public void setTitleCn(String titleCn)
    {
        this.titleCn = titleCn;
    }

    public String getTitleVn()
    {
        return titleVn;
    }

    public void setTitleVn(String titleVn)
    {
        this.titleVn = titleVn;
    }

    public String getTitleEn()
    {
        return titleEn;
    }

    public void setTitleEn(String titleEn)
    {
        this.titleEn = titleEn;
    }

    public String getEventType()
    {
        return eventType;
    }

    public void setEventType(String eventType)
    {
        this.eventType = eventType;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getOrganizer()
    {
        return organizer;
    }

    public void setOrganizer(String organizer)
    {
        this.organizer = organizer;
    }

    public String getIndustry()
    {
        return industry;
    }

    public void setIndustry(String industry)
    {
        this.industry = industry;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public String getProvince()
    {
        return province;
    }

    public void setProvince(String province)
    {
        this.province = province;
    }

    public String getVenue()
    {
        return venue;
    }

    public void setVenue(String venue)
    {
        this.venue = venue;
    }

    public String getTargetAudience()
    {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience)
    {
        this.targetAudience = targetAudience;
    }

    public String getScale()
    {
        return scale;
    }

    public void setScale(String scale)
    {
        this.scale = scale;
    }

    public String getEntryFee()
    {
        return entryFee;
    }

    public void setEntryFee(String entryFee)
    {
        this.entryFee = entryFee;
    }

    public String getRegistrationLink()
    {
        return registrationLink;
    }

    public void setRegistrationLink(String registrationLink)
    {
        this.registrationLink = registrationLink;
    }

    public String getContactPerson()
    {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson)
    {
        this.contactPerson = contactPerson;
    }

    public String getHighlights()
    {
        return highlights;
    }

    public void setHighlights(String highlights)
    {
        this.highlights = highlights;
    }

    public String getNotes()
    {
        return notes;
    }

    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    public String getRelatedParkId()
    {
        return relatedParkId;
    }

    public void setRelatedParkId(String relatedParkId)
    {
        this.relatedParkId = relatedParkId;
    }

    public String getCoverUrl()
    {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl)
    {
        this.coverUrl = coverUrl;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getSearchQuery()
    {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery)
    {
        this.searchQuery = searchQuery;
    }
}
