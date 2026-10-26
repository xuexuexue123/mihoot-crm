package com.ruoyi.crm.domain.dto.wecom;

/**
 * 客服回复消息记录请求。
 */
public class KfMessageReplyRequest
{
    private String externalUserid;
    private String openKfid;
    private String msgtype;
    private String content;
    private String msgid;
    private Long sendTime;

    public String getExternalUserid()
    {
        return externalUserid;
    }

    public void setExternalUserid(String externalUserid)
    {
        this.externalUserid = externalUserid;
    }

    public String getOpenKfid()
    {
        return openKfid;
    }

    public void setOpenKfid(String openKfid)
    {
        this.openKfid = openKfid;
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

    public String getMsgid()
    {
        return msgid;
    }

    public void setMsgid(String msgid)
    {
        this.msgid = msgid;
    }

    public Long getSendTime()
    {
        return sendTime;
    }

    public void setSendTime(Long sendTime)
    {
        this.sendTime = sendTime;
    }
}
