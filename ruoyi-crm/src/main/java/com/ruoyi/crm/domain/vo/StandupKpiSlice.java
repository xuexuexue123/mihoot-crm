package com.ruoyi.crm.domain.vo;

import java.math.BigDecimal;

/**
 * 团队月度 KPI 进度条。
 */
public class StandupKpiSlice
{
    private String kpiCode;
    private BigDecimal goalValue;
    private BigDecimal actualValue;
    private BigDecimal completionRate;
    private int assessedMemberCount;

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

    public int getAssessedMemberCount()
    {
        return assessedMemberCount;
    }

    public void setAssessedMemberCount(int assessedMemberCount)
    {
        this.assessedMemberCount = assessedMemberCount;
    }
}
