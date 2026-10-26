package com.ruoyi.crm.wechatmp.dto;

public class WechatMpQrcodeCreateRequest
{
    private String appId;
    private String name;
    private String sceneStr;
    private String qrType;
    private Integer expireSeconds;
    private String crmEventId;
    private String campaignId;

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSceneStr() { return sceneStr; }
    public void setSceneStr(String sceneStr) { this.sceneStr = sceneStr; }
    public String getQrType() { return qrType; }
    public void setQrType(String qrType) { this.qrType = qrType; }
    public Integer getExpireSeconds() { return expireSeconds; }
    public void setExpireSeconds(Integer expireSeconds) { this.expireSeconds = expireSeconds; }
    public String getCrmEventId() { return crmEventId; }
    public void setCrmEventId(String crmEventId) { this.crmEventId = crmEventId; }
    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }
}
