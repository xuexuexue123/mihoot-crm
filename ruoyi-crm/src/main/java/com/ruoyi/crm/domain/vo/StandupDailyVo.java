package com.ruoyi.crm.domain.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * GET /crm/standup/daily 主包。
 */
public class StandupDailyVo
{
    private String date;
    private String timezone;
    private StandupTimeWindow yesterday;
    private StandupTimeWindow today;
    private StandupPeriodBrief period;
    private StandupTeamSummary teamSummary;
    private List<StandupKpiSlice> monthProgress = new ArrayList<>();
    private List<StandupMemberRow> members = new ArrayList<>();

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getTimezone()
    {
        return timezone;
    }

    public void setTimezone(String timezone)
    {
        this.timezone = timezone;
    }

    public StandupTimeWindow getYesterday()
    {
        return yesterday;
    }

    public void setYesterday(StandupTimeWindow yesterday)
    {
        this.yesterday = yesterday;
    }

    public StandupTimeWindow getToday()
    {
        return today;
    }

    public void setToday(StandupTimeWindow today)
    {
        this.today = today;
    }

    public StandupPeriodBrief getPeriod()
    {
        return period;
    }

    public void setPeriod(StandupPeriodBrief period)
    {
        this.period = period;
    }

    public StandupTeamSummary getTeamSummary()
    {
        return teamSummary;
    }

    public void setTeamSummary(StandupTeamSummary teamSummary)
    {
        this.teamSummary = teamSummary;
    }

    public List<StandupKpiSlice> getMonthProgress()
    {
        return monthProgress;
    }

    public void setMonthProgress(List<StandupKpiSlice> monthProgress)
    {
        this.monthProgress = monthProgress != null ? monthProgress : new ArrayList<>();
    }

    public List<StandupMemberRow> getMembers()
    {
        return members;
    }

    public void setMembers(List<StandupMemberRow> members)
    {
        this.members = members != null ? members : new ArrayList<>();
    }
}
