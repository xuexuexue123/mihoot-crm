package com.ruoyi.crm.domain.vo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class StandupDayMetricsTest
{
    @Test
    public void zeroActionWhenFollowupAndMetAreZero()
    {
        StandupDayMetrics m = new StandupDayMetrics();
        m.setFollowupCount(BigDecimal.ZERO);
        m.setMetCustomers(BigDecimal.ZERO);
        m.setContractAmount(new BigDecimal("1000"));
        m.refreshZeroAction();
        assertTrue(m.isZeroAction());
    }

    @Test
    public void notZeroWhenHasFollowup()
    {
        StandupDayMetrics m = new StandupDayMetrics();
        m.setFollowupCount(BigDecimal.ONE);
        m.setMetCustomers(BigDecimal.ZERO);
        m.refreshZeroAction();
        assertFalse(m.isZeroAction());
    }

    @Test
    public void addSumsMemberMetrics()
    {
        StandupDayMetrics a = new StandupDayMetrics();
        a.setFollowupCount(new BigDecimal("2"));
        a.setMetCustomers(new BigDecimal("1"));
        StandupDayMetrics b = new StandupDayMetrics();
        b.setFollowupCount(new BigDecimal("3"));
        b.setMetCustomers(new BigDecimal("0"));
        a.add(b);
        assertTrue(new BigDecimal("5").compareTo(a.getFollowupCount()) == 0);
        assertTrue(new BigDecimal("1").compareTo(a.getMetCustomers()) == 0);
    }
}
