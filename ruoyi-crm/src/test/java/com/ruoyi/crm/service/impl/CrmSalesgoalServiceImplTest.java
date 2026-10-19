package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class CrmSalesgoalServiceImplTest
{
    @Test
    public void computeCompletionRate_nullWhenGoalNonPositive()
    {
        assertNull(CrmSalesgoalServiceImpl.computeCompletionRate(BigDecimal.ZERO, BigDecimal.TEN));
        assertNull(CrmSalesgoalServiceImpl.computeCompletionRate(new BigDecimal("-1"), BigDecimal.TEN));
        assertNull(CrmSalesgoalServiceImpl.computeCompletionRate(BigDecimal.TEN, null));
    }

    @Test
    public void computeCompletionRate_divides()
    {
        assertEquals(0, new BigDecimal("0.8200")
                .compareTo(CrmSalesgoalServiceImpl.computeCompletionRate(
                        new BigDecimal("100000"), new BigDecimal("82000"))));
    }

    @Test
    public void parseIsActive_acceptsZeroOne()
    {
        assertEquals(Long.valueOf(0L), CrmSalesgoalServiceImpl.parseIsActive("0"));
        assertEquals(Long.valueOf(1L), CrmSalesgoalServiceImpl.parseIsActive("1"));
        assertNull(CrmSalesgoalServiceImpl.parseIsActive("2"));
        assertNull(CrmSalesgoalServiceImpl.parseIsActive(null));
    }
}
