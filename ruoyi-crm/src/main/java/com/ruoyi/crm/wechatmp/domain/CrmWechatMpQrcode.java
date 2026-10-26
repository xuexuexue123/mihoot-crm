package com.ruoyi.crm.wechatmp.domain;

import java.util.Date;

public class CrmWechatMpQrcode
{
    private Long id;
    private String appId;
    private String name;
    private String sceneStr;
    private String qrType;
    private Integer expireSeconds;
    private String ticket;
    private String qrUrl;
    private String crmEventId;
    private String campaignId;
    private String statsJson;
    private String createBy;
    private Date createTime;
    private Date updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public String getTicket() { return ticket; }
    public void setTicket(String ticket) { this.ticket = ticket; }
    public String getQrUrl() { return qrUrl; }
    public void setQrUrl(String qrUrl) { this.qrUrl = qrUrl; }
    public String getCrmEventId() { return crmEventId; }
    public void setCrmEventId(String crmEventId) { this.crmEventId = crmEventId; }
    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }
    public String getStatsJson() { return statsJson; }
    public void setStatsJson(String statsJson) { this.statsJson = statsJson; }
    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
