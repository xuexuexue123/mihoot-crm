package com.ruoyi.crm.domain.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Standup mapper 行：预约按 owner 计数。
 */
public class StandupBookingCountRow
{
    private String ownerId;
    private BigDecimal bookingCount;

    public String getOwnerId()
    {
        return ownerId;
    }

    public void setOwnerId(String ownerId)
    {
        this.ownerId = ownerId;
    }

    public BigDecimal getBookingCount()
    {
        return bookingCount;
    }

    public void setBookingCount(BigDecimal bookingCount)
    {
        this.bookingCount = bookingCount;
    }
}
