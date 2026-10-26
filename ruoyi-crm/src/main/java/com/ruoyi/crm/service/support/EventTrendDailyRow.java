package com.ruoyi.crm.service.support;

/**
 * 活动效果趋势按日计数行
 */
public class EventTrendDailyRow
{
    private String bucketDate;
    private int count;

    public String getBucketDate() { return bucketDate; }
    public void setBucketDate(String bucketDate) { this.bucketDate = bucketDate; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}
