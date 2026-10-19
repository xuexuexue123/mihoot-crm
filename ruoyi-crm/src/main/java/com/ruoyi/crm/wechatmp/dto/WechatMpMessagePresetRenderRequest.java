package com.ruoyi.crm.wechatmp.dto;

import java.util.Map;

public class WechatMpMessagePresetRenderRequest
{
    private String eventId;
    private String surveyId;
    private String contactId;
    /** 活动受众单发：签发含 o/a 的个性化报名链接 */
    private String openId;
    private String appId;
    /** 签发报名链接用；自动化 Worker 传入，管理端 render 可从当前租户上下文解析 */
    private String tenantId;
    private Map<String, String> extra;

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getSurveyId() { return surveyId; }
    public void setSurveyId(String surveyId) { this.surveyId = surveyId; }
    public String getContactId() { return contactId; }
    public void setContactId(String contactId) { this.contactId = contactId; }
    public String getOpenId() { return openId; }
    public void setOpenId(String openId) { this.openId = openId; }
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public Map<String, String> getExtra() { return extra; }
    public void setExtra(Map<String, String> extra) { this.extra = extra; }
}
