package com.ruoyi.crm.domain.dto;

/**
 * 活动效果趋势查询参数
 */
public class EventPerformanceTrendsQuery
{
    private String granularity = "day";
    private String dateFrom;
    private String dateTo;
    private Boolean cumulative = false;

    public String getGranularity() { return granularity; }
    public void setGranularity(String granularity) { this.granularity = granularity; }
    public String getDateFrom() { return dateFrom; }
    public void setDateFrom(String dateFrom) { this.dateFrom = dateFrom; }
    public String getDateTo() { return dateTo; }
    public void setDateTo(String dateTo) { this.dateTo = dateTo; }
    public Boolean getCumulative() { return cumulative; }
    public void setCumulative(Boolean cumulative) { this.cumulative = cumulative; }
}
