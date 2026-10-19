package com.ruoyi.crm.domain.dto.wecom;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 查询朋友圈发表任务请求（#2 get_moment_task）。
 */
public class WecomMomentTaskRequest
{
    @JsonProperty("moment_id")
    private String momentId;

    private String cursor;
    private Integer limit;

    public String getMomentId()
    {
        return momentId;
    }

    public void setMomentId(String momentId)
    {
        this.momentId = momentId;
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
