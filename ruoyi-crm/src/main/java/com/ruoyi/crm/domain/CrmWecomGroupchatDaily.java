package com.ruoyi.crm.domain;

import java.util.Date;

/**
 * 企业微信群聊数据统计（按群主按天快照）
 */
public class CrmWecomGroupchatDaily
{
    private Long id;
    private String corpId;
    private String ownerUserid;
    private String ownerName;
    private Long statTime;
    private Integer newChatCnt;
    private Integer chatTotal;
    private Integer chatHasMsg;
    private Integer newMemberCnt;
    private Integer memberTotal;
    private Integer memberHasMsg;
    private Integer msgTotal;
    private Integer migrateTraineeChatCnt;
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

    public String getOwnerUserid()
    {
        return ownerUserid;
    }

    public void setOwnerUserid(String ownerUserid)
    {
        this.ownerUserid = ownerUserid;
    }

    public String getOwnerName()
    {
        return ownerName;
    }

    public void setOwnerName(String ownerName)
    {
        this.ownerName = ownerName;
    }

    public Long getStatTime()
    {
        return statTime;
    }

    public void setStatTime(Long statTime)
    {
        this.statTime = statTime;
    }

    public Integer getNewChatCnt()
    {
        return newChatCnt;
    }

    public void setNewChatCnt(Integer newChatCnt)
    {
        this.newChatCnt = newChatCnt;
    }

    public Integer getChatTotal()
    {
        return chatTotal;
    }

    public void setChatTotal(Integer chatTotal)
    {
        this.chatTotal = chatTotal;
    }

    public Integer getChatHasMsg()
    {
        return chatHasMsg;
    }

    public void setChatHasMsg(Integer chatHasMsg)
    {
        this.chatHasMsg = chatHasMsg;
    }

    public Integer getNewMemberCnt()
    {
        return newMemberCnt;
    }

    public void setNewMemberCnt(Integer newMemberCnt)
    {
        this.newMemberCnt = newMemberCnt;
    }

    public Integer getMemberTotal()
    {
        return memberTotal;
    }

    public void setMemberTotal(Integer memberTotal)
    {
        this.memberTotal = memberTotal;
    }

    public Integer getMemberHasMsg()
    {
        return memberHasMsg;
    }

    public void setMemberHasMsg(Integer memberHasMsg)
    {
        this.memberHasMsg = memberHasMsg;
    }

    public Integer getMsgTotal()
    {
        return msgTotal;
    }

    public void setMsgTotal(Integer msgTotal)
    {
        this.msgTotal = msgTotal;
    }

    public Integer getMigrateTraineeChatCnt()
    {
        return migrateTraineeChatCnt;
    }

    public void setMigrateTraineeChatCnt(Integer migrateTraineeChatCnt)
    {
        this.migrateTraineeChatCnt = migrateTraineeChatCnt;
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
