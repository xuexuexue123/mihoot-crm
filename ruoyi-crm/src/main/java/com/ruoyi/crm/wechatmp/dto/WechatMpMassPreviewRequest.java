package com.ruoyi.crm.wechatmp.dto;

import com.alibaba.fastjson2.JSONObject;

/**
 * 官方群发预览请求。
 */
public class WechatMpMassPreviewRequest
{
    private String appId;
    /** text | mpnews */
    private String msgType;
    private JSONObject content;
    private String touser;
    private String towxname;

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getMsgType() { return msgType; }
    public void setMsgType(String msgType) { this.msgType = msgType; }
    public JSONObject getContent() { return content; }
    public void setContent(JSONObject content) { this.content = content; }
    public String getTouser() { return touser; }
    public void setTouser(String touser) { this.touser = touser; }
    public String getTowxname() { return towxname; }
    public void setTowxname(String towxname) { this.towxname = towxname; }
}
