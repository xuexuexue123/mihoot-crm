package com.ruoyi.crm.domain.dto.wecom;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 停止朋友圈发表任务请求（#8 cancel_moment_task）。
 */
public class WecomMomentCancelTaskRequest
{
    @JsonProperty("moment_id")
    private String momentId;

    public String getMomentId()
    {
        return momentId;
    }

    public void setMomentId(String momentId)
    {
        this.momentId = momentId;
    }
}
