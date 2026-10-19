package com.ruoyi.crm.wechatmp.dto;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonAlias;

public class WechatMpMessageTemplateRequest
{
    private String appId;
    private String openId;
    /** 受众行级公众号 AppID，优先于活动默认与 appId */
    @JsonAlias("wechat_mp_app_id")
    private String wechatMpAppId;
    private String templateId;
    private String url;
    private JSONObject data;
    /** 活动受众发模板：写入 outreach 日志供 mpOutreachSentAt */
    private String eventId;
    private Long presetId;
    /** 问卷投放关联 */
    private String surveyId;
    private String outreachChannel;

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getOpenId() { return openId; }
    public void setOpenId(String openId) { this.openId = openId; }
    public String getWechatMpAppId() { return wechatMpAppId; }
    public void setWechatMpAppId(String wechatMpAppId) { this.wechatMpAppId = wechatMpAppId; }
    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public JSONObject getData() { return data; }
    public void setData(JSONObject data) { this.data = data; }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public Long getPresetId() { return presetId; }
    public void setPresetId(Long presetId) { this.presetId = presetId; }
    public String getSurveyId() { return surveyId; }
    public void setSurveyId(String surveyId) { this.surveyId = surveyId; }
    public String getOutreachChannel() { return outreachChannel; }
    public void setOutreachChannel(String outreachChannel) { this.outreachChannel = outreachChannel; }
}
