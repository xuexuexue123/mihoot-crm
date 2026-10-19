package com.ruoyi.crm.wechatmp.dto;

import com.alibaba.fastjson2.JSONObject;

public class WechatMpMessagePresetRenderResult
{
    private String appId;
    private String templateId;
    private String url;
    private JSONObject data;

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public JSONObject getData() { return data; }
    public void setData(JSONObject data) { this.data = data; }
}
