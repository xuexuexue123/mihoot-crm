package com.ruoyi.crm.domain.dto;

import java.util.List;
import java.util.Map;

public class AnalyticsQueryRequest
{
    private String metric;

    private Map<String, String> filters;

    private DateRange dateRange;

    private List<String> groupBy;

    private String currency;

    public static class DateRange
    {
        private String field;
        private String from;
        private String to;

        public String getField()
        {
            return field;
        }

        public void setField(String field)
        {
            this.field = field;
        }

        public String getFrom()
        {
            return from;
        }

        public void setFrom(String from)
        {
            this.from = from;
        }

        public String getTo()
        {
            return to;
        }

        public void setTo(String to)
        {
            this.to = to;
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

    public Map<String, String> getFilters()
    {
        return filters;
    }

    public void setFilters(Map<String, String> filters)
    {
        this.filters = filters;
    }

    public DateRange getDateRange()
    {
        return dateRange;
    }

    public void setDateRange(DateRange dateRange)
    {
        this.dateRange = dateRange;
    }

    public List<String> getGroupBy()
    {
        return groupBy;
    }

    public void setGroupBy(List<String> groupBy)
    {
        this.groupBy = groupBy;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }
}
