package com.ruoyi.crm.domain.vo;

import java.math.BigDecimal;

/**
 * 成员今日安排（MVP：预约数）。
 */
public class StandupMemberToday
{
    private BigDecimal bookingCount = BigDecimal.ZERO;

    public BigDecimal getBookingCount()
    {
        return bookingCount;
    }

    public void setBookingCount(BigDecimal bookingCount)
    {
        this.bookingCount = bookingCount != null ? bookingCount : BigDecimal.ZERO;
    }
}
