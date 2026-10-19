package com.ruoyi.crm.domain.vo;

import java.math.BigDecimal;

/**
 * 成员月度 KPI 切片。
 */
public class StandupMemberKpi
{
    private String kpiCode;
    private BigDecimal goalValue;
    private BigDecimal actualValue;
    private BigDecimal completionRate;
    /** "0" 考核中；"1" 未考核 */
    private String isActive;

    public String getKpiCode()
    {
        return kpiCode;
    }

    public void setKpiCode(String kpiCode)
    {
        this.kpiCode = kpiCode;
    }

    public BigDecimal getGoalValue()
    {
        return goalValue;
    }

    public void setGoalValue(BigDecimal goalValue)
    {
        this.goalValue = goalValue;
    }

    public BigDecimal getActualValue()
    {
        return actualValue;
    }

    public void setActualValue(BigDecimal actualValue)
    {
        this.actualValue = actualValue;
    }

    public BigDecimal getCompletionRate()
    {
        return completionRate;
    }

    public void setCompletionRate(BigDecimal completionRate)
    {
        this.completionRate = completionRate;
    }

    public String getIsActive()
    {
        return isActive;
    }

    public void setIsActive(String isActive)
    {
        this.isActive = isActive;
    }
}
