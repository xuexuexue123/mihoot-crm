package com.ruoyi.crm.wechatmp.dto;

import java.util.Date;

import com.alibaba.fastjson2.JSONObject;

public class WechatMpMessageBatchRequest
{
    private String appId;
    private String taskType;
    /** segment | openid_list | event_contacts | fan_filter */
    private String audienceType;
    /** segment_id / event_id 等 */
    private String audienceRef;
    private String templateId;
    private JSONObject contentJson;
    /** fan_filter 时必填 */
    private JSONObject filterJson;
    private Date scheduledAt;

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    public String getAudienceType() { return audienceType; }
    public void setAudienceType(String audienceType) { this.audienceType = audienceType; }
    public String getAudienceRef() { return audienceRef; }
    public void setAudienceRef(String audienceRef) { this.audienceRef = audienceRef; }
    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }
    public JSONObject getContentJson() { return contentJson; }
    public void setContentJson(JSONObject contentJson) { this.contentJson = contentJson; }
    public JSONObject getFilterJson() { return filterJson; }
    public void setFilterJson(JSONObject filterJson) { this.filterJson = filterJson; }
    public Date getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(Date scheduledAt) { this.scheduledAt = scheduledAt; }
}
