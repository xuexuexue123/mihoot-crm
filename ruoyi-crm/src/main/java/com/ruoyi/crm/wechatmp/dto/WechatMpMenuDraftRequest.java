package com.ruoyi.crm.wechatmp.dto;

public class WechatMpMenuDraftRequest
{
    private String appId;
    private String templateId;
    private String menuJson;

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }
    public String getMenuJson() { return menuJson; }
    public void setMenuJson(String menuJson) { this.menuJson = menuJson; }
}
