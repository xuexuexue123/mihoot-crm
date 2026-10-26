package com.ruoyi.crm.service.support;

import java.util.Date;

/** 活动受众：openid → 最近一次自动化触达发送时间 */
public class MpOutreachSentAtRow
{
    private String openId;
    private String appId;
    private Date sentAt;

    public String getOpenId() { return openId; }
    public void setOpenId(String openId) { this.openId = openId; }
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public Date getSentAt() { return sentAt; }
    public void setSentAt(Date sentAt) { this.sentAt = sentAt; }
}
