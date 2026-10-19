package com.ruoyi.crm.domain.dto.wecom;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 朋友圈互动项（评论/点赞，用于 #5 get_moment_comments 响应）。
 */
public class WecomMomentInteractionItem
{
    private String userid;

    @JsonProperty("external_userid")
    private String externalUserid;

    @JsonProperty("create_time")
    private Long createTime;

    public String getUserid()
    {
        return userid;
    }

    public void setUserid(String userid)
    {
        this.userid = userid;
    }

    public String getExternalUserid()
    {
        return externalUserid;
    }

    public void setExternalUserid(String externalUserid)
    {
        this.externalUserid = externalUserid;
    }

    public Long getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Long createTime)
    {
        this.createTime = createTime;
    }
}
