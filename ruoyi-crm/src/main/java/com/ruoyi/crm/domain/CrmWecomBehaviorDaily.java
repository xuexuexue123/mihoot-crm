package com.ruoyi.crm.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 企业微信成员联系客户行为数据（按成员按天快照）
 */
public class CrmWecomBehaviorDaily
{
    private Long id;
    private String corpId;
    private String userid;
    private String userName;
    private Long statTime;
    private Integer newApplyCnt;
    private Integer newContactCnt;
    private Integer chatCnt;
    private Integer messageCnt;
    private BigDecimal replyPercentage;
    private Integer avgReplyTime;
    private Integer negativeFeedbackCnt;
    private String rawJson;
    private Date createdAt;
    private Date updatedAt;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getCorpId()
    {
        return corpId;
    }

    public void setCorpId(String corpId)
    {
        this.corpId = corpId;
    }

    public String getUserid()
    {
        return userid;
    }

    public void setUserid(String userid)
    {
        this.userid = userid;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public Long getStatTime()
    {
        return statTime;
    }

    public void setStatTime(Long statTime)
    {
        this.statTime = statTime;
    }

    public Integer getNewApplyCnt()
    {
        return newApplyCnt;
    }

    public void setNewApplyCnt(Integer newApplyCnt)
    {
        this.newApplyCnt = newApplyCnt;
    }

    public Integer getNewContactCnt()
    {
        return newContactCnt;
    }

    public void setNewContactCnt(Integer newContactCnt)
    {
        this.newContactCnt = newContactCnt;
    }

    public Integer getChatCnt()
    {
        return chatCnt;
    }

    public void setChatCnt(Integer chatCnt)
    {
        this.chatCnt = chatCnt;
    }

    public Integer getMessageCnt()
    {
        return messageCnt;
    }

    public void setMessageCnt(Integer messageCnt)
    {
        this.messageCnt = messageCnt;
    }

    public BigDecimal getReplyPercentage()
    {
        return replyPercentage;
    }

    public void setReplyPercentage(BigDecimal replyPercentage)
    {
        this.replyPercentage = replyPercentage;
    }

    public Integer getAvgReplyTime()
    {
        return avgReplyTime;
    }

    public void setAvgReplyTime(Integer avgReplyTime)
    {
        this.avgReplyTime = avgReplyTime;
    }

    public Integer getNegativeFeedbackCnt()
    {
        return negativeFeedbackCnt;
    }

    public void setNegativeFeedbackCnt(Integer negativeFeedbackCnt)
    {
        this.negativeFeedbackCnt = negativeFeedbackCnt;
    }

    public String getRawJson()
    {
        return rawJson;
    }

    public void setRawJson(String rawJson)
    {
        this.rawJson = rawJson;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt()
    {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt)
    {
        this.updatedAt = updatedAt;
    }
}
