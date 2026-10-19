package com.ruoyi.crm.wechatmp.audience.dto;

import com.alibaba.fastjson2.JSONObject;

public class FanAudiencePreviewRequest
{
    private String appId;
    private JSONObject filterJson;

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    public JSONObject getFilterJson()
    {
        return filterJson;
    }

    public void setFilterJson(JSONObject filterJson)
    {
        this.filterJson = filterJson;
    }
}
