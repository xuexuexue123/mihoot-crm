package com.ruoyi.crm.domain.vo;

import java.math.BigDecimal;

/**
 * 晨会日维度指标（不含 win_rate）。
 */
public class StandupDayMetrics
{
    private BigDecimal contractAmount = BigDecimal.ZERO;
    private BigDecimal wonContractCount = BigDecimal.ZERO;
    private BigDecimal followupCount = BigDecimal.ZERO;
    private BigDecimal followedCustomers = BigDecimal.ZERO;
    private BigDecimal metCustomers = BigDecimal.ZERO;
    private BigDecimal assignedCustomers = BigDecimal.ZERO;
    private BigDecimal optyCount = BigDecimal.ZERO;
    private BigDecimal optyAmount = BigDecimal.ZERO;
    private BigDecimal bookingCount = BigDecimal.ZERO;
    private boolean zeroAction;

    public BigDecimal getContractAmount()
    {
        return contractAmount;
    }

    public void setContractAmount(BigDecimal contractAmount)
    {
        this.contractAmount = contractAmount != null ? contractAmount : BigDecimal.ZERO;
    }

    public BigDecimal getWonContractCount()
    {
        return wonContractCount;
    }

    public void setWonContractCount(BigDecimal wonContractCount)
    {
        this.wonContractCount = wonContractCount != null ? wonContractCount : BigDecimal.ZERO;
    }

    public BigDecimal getFollowupCount()
    {
        return followupCount;
    }

    public void setFollowupCount(BigDecimal followupCount)
    {
        this.followupCount = followupCount != null ? followupCount : BigDecimal.ZERO;
    }

    public BigDecimal getFollowedCustomers()
    {
        return followedCustomers;
    }

    public void setFollowedCustomers(BigDecimal followedCustomers)
    {
        this.followedCustomers = followedCustomers != null ? followedCustomers : BigDecimal.ZERO;
    }

    public BigDecimal getMetCustomers()
    {
        return metCustomers;
    }

    public void setMetCustomers(BigDecimal metCustomers)
    {
        this.metCustomers = metCustomers != null ? metCustomers : BigDecimal.ZERO;
    }

    public BigDecimal getAssignedCustomers()
    {
        return assignedCustomers;
    }

    public void setAssignedCustomers(BigDecimal assignedCustomers)
    {
        this.assignedCustomers = assignedCustomers != null ? assignedCustomers : BigDecimal.ZERO;
    }

    public BigDecimal getOptyCount()
    {
        return optyCount;
    }

    public void setOptyCount(BigDecimal optyCount)
    {
        this.optyCount = optyCount != null ? optyCount : BigDecimal.ZERO;
    }

    public BigDecimal getOptyAmount()
    {
        return optyAmount;
    }

    public void setOptyAmount(BigDecimal optyAmount)
    {
        this.optyAmount = optyAmount != null ? optyAmount : BigDecimal.ZERO;
    }

    public BigDecimal getBookingCount()
    {
        return bookingCount;
    }

    public void setBookingCount(BigDecimal bookingCount)
    {
        this.bookingCount = bookingCount != null ? bookingCount : BigDecimal.ZERO;
    }

    public boolean isZeroAction()
    {
        return zeroAction;
    }

    public void setZeroAction(boolean zeroAction)
    {
        this.zeroAction = zeroAction;
    }

    /** 按跟进次数与见面客户派生零动作标记。 */
    public void refreshZeroAction()
    {
        this.zeroAction = isZero(followupCount) && isZero(metCustomers);
    }

    private static boolean isZero(BigDecimal v)
    {
        return v == null || v.compareTo(BigDecimal.ZERO) == 0;
    }

    public void add(StandupDayMetrics other)
    {
        if (other == null)
        {
            return;
        }
        setContractAmount(nz(contractAmount).add(nz(other.contractAmount)));
        setWonContractCount(nz(wonContractCount).add(nz(other.wonContractCount)));
        setFollowupCount(nz(followupCount).add(nz(other.followupCount)));
        setFollowedCustomers(nz(followedCustomers).add(nz(other.followedCustomers)));
        setMetCustomers(nz(metCustomers).add(nz(other.metCustomers)));
        setAssignedCustomers(nz(assignedCustomers).add(nz(other.assignedCustomers)));
        setOptyCount(nz(optyCount).add(nz(other.optyCount)));
        setOptyAmount(nz(optyAmount).add(nz(other.optyAmount)));
        setBookingCount(nz(bookingCount).add(nz(other.bookingCount)));
        refreshZeroAction();
    }

    private static BigDecimal nz(BigDecimal v)
    {
        return v != null ? v : BigDecimal.ZERO;
    }
}
