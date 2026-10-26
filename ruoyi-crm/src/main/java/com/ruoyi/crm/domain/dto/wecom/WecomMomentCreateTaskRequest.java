package com.ruoyi.crm.domain.dto.wecom;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 创建朋友圈发表任务请求（#6 add_moment_task）。
 */
public class WecomMomentCreateTaskRequest
{
    private WecomMomentText text;
    private List<WecomMomentAttachment> attachments;

    @JsonProperty("visible_range")
    private WecomMomentVisibleRange visibleRange;

    public WecomMomentText getText()
    {
        return text;
    }

    public void setText(WecomMomentText text)
    {
        this.text = text;
    }

    public List<WecomMomentAttachment> getAttachments()
    {
        return attachments;
    }

    public void setAttachments(List<WecomMomentAttachment> attachments)
    {
        this.attachments = attachments;
    }

    public WecomMomentVisibleRange getVisibleRange()
    {
        return visibleRange;
    }

    public void setVisibleRange(WecomMomentVisibleRange visibleRange)
    {
        this.visibleRange = visibleRange;
    }
}
