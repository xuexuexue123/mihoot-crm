package com.ruoyi.crm.wechatmp.dto;

import com.alibaba.fastjson2.JSONObject;

/**
 * 官方群发提交请求。
 */
public class WechatMpMassSendRequest
{
    private String appId;
    /** text | mpnews */
    private String msgType;
    private JSONObject content;
    /** all | tag */
    private String audienceType;
    private Long tagId;
    private Boolean isToAll;
    private String clientMsgId;
    private Integer sendIgnoreReprint;

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getMsgType() { return msgType; }
    public void setMsgType(String msgType) { this.msgType = msgType; }
    public JSONObject getContent() { return content; }
    public void setContent(JSONObject content) { this.content = content; }
    public String getAudienceType() { return audienceType; }
    public void setAudienceType(String audienceType) { this.audienceType = audienceType; }
    public Long getTagId() { return tagId; }
    public void setTagId(Long tagId) { this.tagId = tagId; }
    public Boolean getIsToAll() { return isToAll; }
    public void setIsToAll(Boolean isToAll) { this.isToAll = isToAll; }
    public String getClientMsgId() { return clientMsgId; }
    public void setClientMsgId(String clientMsgId) { this.clientMsgId = clientMsgId; }
    public Integer getSendIgnoreReprint() { return sendIgnoreReprint; }
    public void setSendIgnoreReprint(Integer sendIgnoreReprint) { this.sendIgnoreReprint = sendIgnoreReprint; }
}
