package com.ruoyi.crm.domain.vo;

import java.math.BigDecimal;
import java.util.List;

public class AnalyticsQueryResponse
{
    private String metric;

    private BigDecimal value;

    private String unit;

    private List<BreakdownItem> breakdown;

    private String asOf;

    public static class BreakdownItem
    {
        private String dimension;
        private String key;
        private BigDecimal value;
        private Long count;

        public String getDimension()
        {
            return dimension;
        }

        public void setDimension(String dimension)
        {
            this.dimension = dimension;
        }

        public String getKey()
        {
            return key;
        }

        public void setKey(String key)
        {
            this.key = key;
        }

        public BigDecimal getValue()
        {
            return value;
        }

        public void setValue(BigDecimal value)
        {
            this.value = value;
        }

        public Long getCount()
        {
            return count;
        }

        public void setCount(Long count)
        {
            this.count = count;
        }
    }

    public String getMetric()
    {
        return metric;
    }

    public void setMetric(String metric)
    {
        this.metric = metric;
    }

    public BigDecimal getValue()
    {
        return value;
    }

    public void setValue(BigDecimal value)
    {
        this.value = value;
    }

    public String getUnit()
    {
        return unit;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    public List<BreakdownItem> getBreakdown()
    {
        return breakdown;
    }

    public void setBreakdown(List<BreakdownItem> breakdown)
    {
        this.breakdown = breakdown;
    }

    public String getAsOf()
    {
        return asOf;
    }

    public void setAsOf(String asOf)
    {
        this.asOf = asOf;
    }
}
