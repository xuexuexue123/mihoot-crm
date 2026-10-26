package com.ruoyi.crm.wechatmp.dto;

public class WechatMpMenuPublishRequest
{
    private String appId;
    private Long draftId;

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public Long getDraftId() { return draftId; }
    public void setDraftId(Long draftId) { this.draftId = draftId; }
}
