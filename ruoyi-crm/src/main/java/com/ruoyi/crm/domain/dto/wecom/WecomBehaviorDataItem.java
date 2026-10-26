package com.ruoyi.crm.domain.dto.wecom;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 企业微信「联系客户统计」单日行为数据项。
 * 字段命名与企微 API behavior_data[] 一致。
 */
public class WecomBehaviorDataItem
{
    /** 数据日期（Unix 秒级时间戳） */
    private Long statTime;

    /** 发起申请数 */
    private Integer newApplyCnt;

    /** 新增客户数 */
    private Integer newContactCnt;

    /** 聊天总数 */
    private Integer chatCnt;

    /** 发送消息数 */
    private Integer messageCnt;

    /** 已回复聊天占比（%） */
    private Double replyPercentage;

    /** 平均首次回复时长（分钟） */
    private Integer avgReplyTime;

    /** 删除/拉黑成员的客户数 */
    private Integer negativeFeedbackCnt;

    @JsonProperty("stat_time")
    public Long getStatTime()
    {
        return statTime;
    }

    @JsonProperty("stat_time")
    public void setStatTime(Long statTime)
    {
        this.statTime = statTime;
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
