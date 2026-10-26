package com.ruoyi.crm.domain;

import java.util.Date;

/**
 * 微信客服会话消息记录。
 */
public class CrmWecomKfMessage
{
    private Long id;
    private String corpId;
    private String openKfid;
    private String externalUserid;
    private String msgid;
    private String msgtype;
    private String content;
    private Integer direction;
    private Long sendTime;
    private String rawJson;
    private Long conversationId;
    private String mediaId;
    private String picUrl;
    /** outbound origin: null=human, ai_inbox_agent=Inbox AI */
    private String source;
    private Date createdAt;

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

    public String getOpenKfid()
    {
        return openKfid;
    }

    public void setOpenKfid(String openKfid)
    {
        this.openKfid = openKfid;
    }

    public String getExternalUserid()
    {
        return externalUserid;
    }

    public void setExternalUserid(String externalUserid)
    {
        this.externalUserid = externalUserid;
    }

    public String getMsgid()
    {
        return msgid;
    }

    public void setMsgid(String msgid)
    {
        this.msgid = msgid;
    }

    public String getMsgtype()
    {
        return msgtype;
    }

    public void setMsgtype(String msgtype)
    {
        this.msgtype = msgtype;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public Integer getDirection()
    {
        return direction;
    }

    public void setDirection(Integer direction)
    {
        this.direction = direction;
    }

    public Long getSendTime()
    {
        return sendTime;
    }

    public void setSendTime(Long sendTime)
    {
        this.sendTime = sendTime;
    }

    public String getRawJson()
    {
        return rawJson;
    }

    public void setRawJson(String rawJson)
    {
        this.rawJson = rawJson;
    }

    public Long getConversationId()
    {
        return conversationId;
    }

    public void setConversationId(Long conversationId)
    {
        this.conversationId = conversationId;
    }

    public String getMediaId()
    {
        return mediaId;
    }

    public void setMediaId(String mediaId)
    {
        this.mediaId = mediaId;
    }

    public String getPicUrl()
    {
        return picUrl;
    }

    public void setPicUrl(String picUrl)
    {
        this.picUrl = picUrl;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }
}
