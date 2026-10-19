package com.ruoyi.crm.domain.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 晨会成员战报行。
 */
public class StandupMemberRow
{
    private String ownerId;
    private String ownerName;
    private Long deptId;
    private String deptName;
    private String avatar;
    private StandupDayMetrics yesterday;
    private StandupMemberToday today;
    private List<StandupMemberKpi> monthKpis = new ArrayList<>();
    private List<String> flags = new ArrayList<>();

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

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public String getAvatar()
    {
        return avatar;
    }

    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

    public StandupDayMetrics getYesterday()
    {
        return yesterday;
    }

    public void setYesterday(StandupDayMetrics yesterday)
    {
        this.yesterday = yesterday;
    }

    public StandupMemberToday getToday()
    {
        return today;
    }

    public void setToday(StandupMemberToday today)
    {
        this.today = today;
    }

    public List<StandupMemberKpi> getMonthKpis()
    {
        return monthKpis;
    }

    public void setMonthKpis(List<StandupMemberKpi> monthKpis)
    {
        this.monthKpis = monthKpis != null ? monthKpis : new ArrayList<>();
    }

    public List<String> getFlags()
    {
        return flags;
    }

    public void setFlags(List<String> flags)
    {
        this.flags = flags != null ? flags : new ArrayList<>();
    }
}
