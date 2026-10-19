package com.ruoyi.crm.service.support;

import java.util.Date;

/** 活动受众：audience_key → 最近签到时间 */
public class EventCheckInAggRow
{
    private String audienceKey;
    private Date checkedInAt;

    public String getAudienceKey() { return audienceKey; }
    public void setAudienceKey(String audienceKey) { this.audienceKey = audienceKey; }
    public Date getCheckedInAt() { return checkedInAt; }
    public void setCheckedInAt(Date checkedInAt) { this.checkedInAt = checkedInAt; }
}
