package com.ruoyi.crm.domain.dto.wecom;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 获取朋友圈发表列表请求（#1 get_moment_list）。
 */
public class WecomMomentListRequest
{
    @JsonProperty("start_time")
    private Long startTime;

    @JsonProperty("end_time")
    private Long endTime;

    private String creator;

    @JsonProperty("filter_type")
    private Integer filterType;

    private String cursor;
    private Integer limit;

    public Long getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Long startTime)
    {
        this.startTime = startTime;
    }

    public Long getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Long endTime)
    {
        this.endTime = endTime;
    }

    public String getCreator()
    {
        return creator;
    }

    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    public Integer getFilterType()
    {
        return filterType;
    }

    public void setFilterType(Integer filterType)
    {
        this.filterType = filterType;
    }

    public String getCursor()
    {
        return cursor;
    }

    public void setCursor(String cursor)
    {
        this.cursor = cursor;
    }

    public Integer getLimit()
    {
        return limit;
    }

    public void setLimit(Integer limit)
    {
        this.limit = limit;
    }
}
