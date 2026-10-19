package com.ruoyi.crm.wechatmp.dto;

public class WechatMpMessageReplyRequest
{
    private String appId;
    private String openId;
    private String msgType;
    private String content;

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getOpenId() { return openId; }
    public void setOpenId(String openId) { this.openId = openId; }
    public String getMsgType() { return msgType; }
    public void setMsgType(String msgType) { this.msgType = msgType; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
