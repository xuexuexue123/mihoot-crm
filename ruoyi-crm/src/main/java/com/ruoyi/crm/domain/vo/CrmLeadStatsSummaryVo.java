package com.ruoyi.crm.domain.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class CrmLeadStatsSummaryVo
{
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date from;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date to;

    private long newLeads;
    private long activeLeads;
    private long convertedCount;
    private BigDecimal conversionRate;
    private long openPoolCount;
    private long assignedCount;

    public Date getFrom()
    {
        return from;
    }

    public void setFrom(Date from)
    {
        this.from = from;
    }

    public Date getTo()
    {
        return to;
    }

    public void setTo(Date to)
    {
        this.to = to;
    }

    public long getNewLeads()
    {
        return newLeads;
    }

    public void setNewLeads(long newLeads)
    {
        this.newLeads = newLeads;
    }

    public long getActiveLeads()
    {
        return activeLeads;
    }

    public void setActiveLeads(long activeLeads)
    {
        this.activeLeads = activeLeads;
    }

    public long getConvertedCount()
    {
        return convertedCount;
    }

    public void setConvertedCount(long convertedCount)
    {
        this.convertedCount = convertedCount;
    }

    public BigDecimal getConversionRate()
    {
        return conversionRate;
    }

    public void setConversionRate(BigDecimal conversionRate)
    {
        this.conversionRate = conversionRate;
    }

    public long getOpenPoolCount()
    {
        return openPoolCount;
    }

    public void setOpenPoolCount(long openPoolCount)
    {
        this.openPoolCount = openPoolCount;
    }

    public long getAssignedCount()
    {
        return assignedCount;
    }

    public void setAssignedCount(long assignedCount)
    {
        this.assignedCount = assignedCount;
    }
}
