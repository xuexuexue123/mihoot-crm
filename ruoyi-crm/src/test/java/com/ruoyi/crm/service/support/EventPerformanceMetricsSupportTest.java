package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.ruoyi.crm.domain.vo.EventPerformanceEfficiencyVo;
import com.ruoyi.crm.domain.vo.EventPerformanceFunnelVo;
import com.ruoyi.crm.domain.vo.EventPerformanceRatesVo;

/**
 * P0 验收：转化率与 ROI 计算口径
 */
public class EventPerformanceMetricsSupportTest
{
    @Test
    public void roiAndCacMatchAcceptanceCase()
    {
        EventPerformanceEfficiencyVo efficiency = EventPerformanceMetricsSupport.computeEfficiency(
                new BigDecimal("10000"), new BigDecimal("50000"),
                new BigDecimal("50000"), 28, 5);
        assertEquals(new BigDecimal("2000.00"), efficiency.getCac());
        assertEquals(new BigDecimal("4.0000"), efficiency.getRoi());
        assertEquals(new BigDecimal("0.2000"), efficiency.getBudgetUtilization());
    }

    @Test
    public void roiNullWhenPaidZero()
    {
        EventPerformanceEfficiencyVo efficiency = EventPerformanceMetricsSupport.computeEfficiency(
                BigDecimal.ZERO, null, new BigDecimal("50000"), 10, 5);
        assertNull(efficiency.getRoi());
    }

    @Test
    public void ratesNullWhenDenominatorZero()
    {
        EventPerformanceFunnelVo funnel = new EventPerformanceFunnelVo();
        funnel.setTouchCount(0);
        funnel.setH5RegistrationCount(0);
        funnel.setLeadReleasedCount(0);
        funnel.setContactConvertedCount(0);
        EventPerformanceRatesVo rates = EventPerformanceMetricsSupport.computeRates(funnel);
        assertNull(rates.getTouchToRegistration());
        assertNull(rates.getRegistrationToLeadReleased());
        assertNull(rates.getLeadReleasedToContact());
        assertNull(rates.getTouchToContact());
    }

    @Test
    public void roiUsesWindowClvWhenRequested()
    {
        EventPerformanceEfficiencyVo efficiency = EventPerformanceMetricsSupport.computeEfficiency(
                new BigDecimal("10000"), null, new BigDecimal("50000"),
                10, 5);
        assertEquals(new BigDecimal("4.0000"), efficiency.getRoi());
    }

    @Test
    public void funnelRatesComputedWithFourDecimalPlaces()
    {
        EventPerformanceFunnelVo funnel = new EventPerformanceFunnelVo();
        funnel.setTouchCount(120);
        funnel.setH5RegistrationCount(45);
        funnel.setLeadReleasedCount(28);
        funnel.setContactConvertedCount(15);
        EventPerformanceRatesVo rates = EventPerformanceMetricsSupport.computeRates(funnel);
        assertEquals(new BigDecimal("0.3750"), rates.getTouchToRegistration());
        assertEquals(new BigDecimal("0.1250"), rates.getTouchToContact());
    }

    @Test
    public void registrationToCheckInAndTouchToCheckIn()
    {
        EventPerformanceFunnelVo funnel = new EventPerformanceFunnelVo();
        funnel.setTouchCount(100);
        funnel.setRegistrationConfirmedCount(50);
        funnel.setCheckedInCount(40);
        EventPerformanceRatesVo rates = EventPerformanceMetricsSupport.computeRates(funnel);
        assertEquals(new BigDecimal("0.8000"), rates.getRegistrationToCheckIn());
        assertEquals(new BigDecimal("0.4000"), rates.getTouchToCheckIn());
    }
}
