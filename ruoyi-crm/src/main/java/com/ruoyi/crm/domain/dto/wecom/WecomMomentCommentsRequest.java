package com.ruoyi.crm.domain.dto.wecom;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 查询朋友圈互动数据请求（#5 get_moment_comments）。
 */
public class WecomMomentCommentsRequest
{
    @JsonProperty("moment_id")
    private String momentId;

    private String userid;

    public String getMomentId()
    {
        return momentId;
    }

    public void setMomentId(String momentId)
    {
        this.momentId = momentId;
    }

    public String getUserid()
    {
        return userid;
    }

    public void setUserid(String userid)
    {
        this.userid = userid;
    }
}
