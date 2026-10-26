package com.ruoyi.crm.domain.vo;

/**
 * 晨会响应中的 Monthly period 摘要。
 */
public class StandupPeriodBrief
{
    private String periodId;
    private String periodName;
    private String periodType;

    public String getPeriodId()
    {
        return periodId;
    }

    public void setPeriodId(String periodId)
    {
        this.periodId = periodId;
    }

    public String getPeriodName()
    {
        return periodName;
    }

    public void setPeriodName(String periodName)
    {
        this.periodName = periodName;
    }

    public String getPeriodType()
    {
        return periodType;
    }

    public void setPeriodType(String periodType)
    {
        this.periodType = periodType;
    }
}
