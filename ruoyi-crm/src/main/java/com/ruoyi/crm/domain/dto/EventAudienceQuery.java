package com.ruoyi.crm.domain.dto;

/**
 * 活动受众列表查询参数
 */
public class EventAudienceQuery
{
    private Integer pageNum = 1;
    private Integer pageSize = 20;
    private String channels;
    private String audienceStatus;
    private String keyword;
    private Boolean dedupe = true;
    private String checkInStatus;

    public Integer getPageNum()
    {
        return pageNum;
    }

    public void setPageNum(Integer pageNum)
    {
        this.pageNum = pageNum;
    }

    public Integer getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(Integer pageSize)
    {
        this.pageSize = pageSize;
    }

    public String getChannels()
    {
        return channels;
    }

    public void setChannels(String channels)
    {
        this.channels = channels;
    }

    public String getAudienceStatus()
    {
        return audienceStatus;
    }

    public void setAudienceStatus(String audienceStatus)
    {
        this.audienceStatus = audienceStatus;
    }

    public String getKeyword()
    {
        return keyword;
    }

    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }

    public Boolean getDedupe()
    {
        return dedupe;
    }

    public void setDedupe(Boolean dedupe)
    {
        this.dedupe = dedupe;
    }

    public String getCheckInStatus()
    {
        return checkInStatus;
    }

    public void setCheckInStatus(String checkInStatus)
    {
        this.checkInStatus = checkInStatus;
    }
}
