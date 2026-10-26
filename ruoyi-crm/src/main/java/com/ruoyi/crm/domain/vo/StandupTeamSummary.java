package com.ruoyi.crm.domain.vo;

/**
 * 团队汇总：昨日指标 + 今日预约总数。
 */
public class StandupTeamSummary
{
    private StandupDayMetrics yesterday;
    private long todayBookingCount;

    public StandupDayMetrics getYesterday()
    {
        return yesterday;
    }

    public void setYesterday(StandupDayMetrics yesterday)
    {
        this.yesterday = yesterday;
    }

    public long getTodayBookingCount()
    {
        return todayBookingCount;
    }

    public void setTodayBookingCount(long todayBookingCount)
    {
        this.todayBookingCount = todayBookingCount;
    }
}
