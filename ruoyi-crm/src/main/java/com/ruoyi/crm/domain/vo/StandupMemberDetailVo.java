package com.ruoyi.crm.domain.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 成员下钻明细。
 */
public class StandupMemberDetailVo
{
    private String ownerId;
    private String ownerName;
    private StandupDayMetrics yesterday;
    private List<StandupActivityBrief> yesterdayActivities = new ArrayList<>();
    private List<StandupAgendaItem> todayBookings = new ArrayList<>();
    private List<StandupMemberKpi> monthKpis = new ArrayList<>();

    public String getOwnerId()
    {
        return ownerId;
    }

    public void setOwnerId(String ownerId)
    {
        this.ownerId = ownerId;
    }

    public String getOwnerName()
    {
        return ownerName;
    }

    public void setOwnerName(String ownerName)
    {
        this.ownerName = ownerName;
    }

    public StandupDayMetrics getYesterday()
    {
        return yesterday;
    }

    public void setYesterday(StandupDayMetrics yesterday)
    {
        this.yesterday = yesterday;
    }

    public List<StandupActivityBrief> getYesterdayActivities()
    {
        return yesterdayActivities;
    }

    public void setYesterdayActivities(List<StandupActivityBrief> yesterdayActivities)
    {
        this.yesterdayActivities = yesterdayActivities != null ? yesterdayActivities : new ArrayList<>();
    }

    public List<StandupAgendaItem> getTodayBookings()
    {
        return todayBookings;
    }

    public void setTodayBookings(List<StandupAgendaItem> todayBookings)
    {
        this.todayBookings = todayBookings != null ? todayBookings : new ArrayList<>();
    }

    public List<StandupMemberKpi> getMonthKpis()
    {
        return monthKpis;
    }

    public void setMonthKpis(List<StandupMemberKpi> monthKpis)
    {
        this.monthKpis = monthKpis != null ? monthKpis : new ArrayList<>();
    }
}
