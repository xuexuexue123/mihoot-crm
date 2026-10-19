package com.ruoyi.crm.domain.dto.wecom;

/**
 * 朋友圈发表任务成员行（#2 get_moment_task）。
 */
public class WecomMomentTaskItem
{
    private String userid;

    @com.fasterxml.jackson.annotation.JsonProperty("publish_status")
    private Integer publishStatus;

    public String getUserid()
    {
        return userid;
    }

    public void setUserid(String userid)
    {
        this.userid = userid;
    }

    public Integer getPublishStatus()
    {
        return publishStatus;
    }

    public void setPublishStatus(Integer publishStatus)
    {
        this.publishStatus = publishStatus;
    }
}
