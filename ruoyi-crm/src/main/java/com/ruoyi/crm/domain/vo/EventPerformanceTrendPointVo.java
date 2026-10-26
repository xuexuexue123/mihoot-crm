package com.ruoyi.crm.domain.vo;

/**
 * 活动效果趋势单日数据点
 */
public class EventPerformanceTrendPointVo
{
    private String date;
    private int touchCount;
    private int h5RegistrationCount;
    private int leadReleasedCount;
    private int contactConvertedCount;

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public int getTouchCount() { return touchCount; }
    public void setTouchCount(int touchCount) { this.touchCount = touchCount; }
    public int getH5RegistrationCount() { return h5RegistrationCount; }
    public void setH5RegistrationCount(int h5RegistrationCount) { this.h5RegistrationCount = h5RegistrationCount; }
    public int getLeadReleasedCount() { return leadReleasedCount; }
    public void setLeadReleasedCount(int leadReleasedCount) { this.leadReleasedCount = leadReleasedCount; }
    public int getContactConvertedCount() { return contactConvertedCount; }
    public void setContactConvertedCount(int contactConvertedCount) { this.contactConvertedCount = contactConvertedCount; }
}
