package com.ruoyi.crm.wechatmp.domain;

import java.util.Date;

public class CrmWechatMpMessage
{
    private Long id;
    private String appId;
    private String openId;
    private String direction;
    private String msgType;
    private String content;
    private String mediaId;
    private String picUrl;
    private String templateId;
    private String wxMsgId;
    private String activityPulseId;
    private Integer errcode;
    private String errmsg;
    private Date createTime;
    /** outbound origin: null=human, ai_inbox_agent=Inbox AI */
    private String source;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getOpenId() { return openId; }
    public void setOpenId(String openId) { this.openId = openId; }
    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
    public String getMsgType() { return msgType; }
    public void setMsgType(String msgType) { this.msgType = msgType; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getMediaId() { return mediaId; }
    public void setMediaId(String mediaId) { this.mediaId = mediaId; }
    public String getPicUrl() { return picUrl; }
    public void setPicUrl(String picUrl) { this.picUrl = picUrl; }
    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }
    public String getWxMsgId() { return wxMsgId; }
    public void setWxMsgId(String wxMsgId) { this.wxMsgId = wxMsgId; }
    public String getActivityPulseId() { return activityPulseId; }
    public void setActivityPulseId(String activityPulseId) { this.activityPulseId = activityPulseId; }
    public Integer getErrcode() { return errcode; }
    public void setErrcode(Integer errcode) { this.errcode = errcode; }
    public String getErrmsg() { return errmsg; }
    public void setErrmsg(String errmsg) { this.errmsg = errmsg; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
