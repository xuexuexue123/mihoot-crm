package com.ruoyi.crm.domain.dto.wecom;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 企微成员行为数据聚合行（按 member 聚合指定时间范围内的指标）。
 */
public class WecomBehaviorMemberRow
{
    private String userid;
    private String name;
    private Boolean active;

    private Integer newApplyCnt;
    private Integer newContactCnt;
    private Integer chatCnt;
    private Integer messageCnt;
    private Double replyPercentage;
    private Integer avgReplyTime;
    private Integer negativeFeedbackCnt;

    public String getUserid()
    {
        return userid;
    }

    public void setUserid(String userid)
    {
        this.userid = userid;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Boolean getActive()
    {
        return active;
    }

    public void setActive(Boolean active)
    {
        this.active = active;
    }

    @JsonProperty("new_apply_cnt")
    public Integer getNewApplyCnt()
    {
        return newApplyCnt;
    }

    @JsonProperty("new_apply_cnt")
    public void setNewApplyCnt(Integer newApplyCnt)
    {
        this.newApplyCnt = newApplyCnt;
    }

    @JsonProperty("new_contact_cnt")
    public Integer getNewContactCnt()
    {
        return newContactCnt;
    }

    @JsonProperty("new_contact_cnt")
    public void setNewContactCnt(Integer newContactCnt)
    {
        this.newContactCnt = newContactCnt;
    }

    @JsonProperty("chat_cnt")
    public Integer getChatCnt()
    {
        return chatCnt;
    }

    @JsonProperty("chat_cnt")
    public void setChatCnt(Integer chatCnt)
    {
        this.chatCnt = chatCnt;
    }

    @JsonProperty("message_cnt")
    public Integer getMessageCnt()
    {
        return messageCnt;
    }

    @JsonProperty("message_cnt")
    public void setMessageCnt(Integer messageCnt)
    {
        this.messageCnt = messageCnt;
    }

    @JsonProperty("reply_percentage")
    public Double getReplyPercentage()
    {
        return replyPercentage;
    }

    @JsonProperty("reply_percentage")
    public void setReplyPercentage(Double replyPercentage)
    {
        this.replyPercentage = replyPercentage;
    }

    @JsonProperty("avg_reply_time")
    public Integer getAvgReplyTime()
    {
        return avgReplyTime;
    }

    @JsonProperty("avg_reply_time")
    public void setAvgReplyTime(Integer avgReplyTime)
    {
        this.avgReplyTime = avgReplyTime;
    }

    @JsonProperty("negative_feedback_cnt")
    public Integer getNegativeFeedbackCnt()
    {
        return negativeFeedbackCnt;
    }

    @JsonProperty("negative_feedback_cnt")
    public void setNegativeFeedbackCnt(Integer negativeFeedbackCnt)
    {
        this.negativeFeedbackCnt = negativeFeedbackCnt;
    }
}
