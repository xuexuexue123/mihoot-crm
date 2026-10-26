package com.ruoyi.crm.domain.vo;

public class LeadRecyclePoolConfigVo
{
    private Integer idleToPoolHours;
    private Integer maxHoldCount;
    private Boolean recycleEnabled;

    public Integer getIdleToPoolHours()
    {
        return idleToPoolHours;
    }

    public void setIdleToPoolHours(Integer idleToPoolHours)
    {
        this.idleToPoolHours = idleToPoolHours;
    }

    public Integer getMaxHoldCount()
    {
        return maxHoldCount;
    }

    public void setMaxHoldCount(Integer maxHoldCount)
    {
        this.maxHoldCount = maxHoldCount;
    }

    public Boolean getRecycleEnabled()
    {
        return recycleEnabled;
    }

    public void setRecycleEnabled(Boolean recycleEnabled)
    {
        this.recycleEnabled = recycleEnabled;
    }
}
