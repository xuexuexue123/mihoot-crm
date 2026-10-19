package com.ruoyi.crm.domain.dto;

/**
 * ownerId → 聚合实际值 行（Mapper 结果）。
 */
public class SalesKpiActualRow
{
    private String ownerId;
    private java.math.BigDecimal actualValue;

    public String getOwnerId()
    {
        return ownerId;
    }

    public void setOwnerId(String ownerId)
    {
        this.ownerId = ownerId;
    }

    public java.math.BigDecimal getActualValue()
    {
        return actualValue;
    }

    public void setActualValue(java.math.BigDecimal actualValue)
    {
        this.actualValue = actualValue;
    }
}
