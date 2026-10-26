package com.ruoyi.crm.service.support;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.ruoyi.crm.domain.vo.EventPerformanceEfficiencyVo;
import com.ruoyi.crm.domain.vo.EventPerformanceFunnelVo;
import com.ruoyi.crm.domain.vo.EventPerformanceRatesVo;

/**
 * 活动效果转化率与效率指标计算
 */
public final class EventPerformanceMetricsSupport
{
    private static final int RATE_SCALE = 4;

    private EventPerformanceMetricsSupport()
    {
    }

    public static EventPerformanceRatesVo computeRates(EventPerformanceFunnelVo funnel)
    {
        EventPerformanceRatesVo rates = new EventPerformanceRatesVo();
        rates.setTouchToRegistration(rate(funnel.getH5RegistrationCount(), funnel.getTouchCount()));
        rates.setRegistrationToLeadReleased(rate(funnel.getLeadReleasedCount(), funnel.getH5RegistrationCount()));
        rates.setLeadReleasedToContact(rate(funnel.getContactConvertedCount(), funnel.getLeadReleasedCount()));
        rates.setTouchToContact(rate(funnel.getContactConvertedCount(), funnel.getTouchCount()));
        rates.setRegistrationToCheckIn(rate(funnel.getCheckedInCount(), funnel.getRegistrationConfirmedCount()));
        rates.setTouchToCheckIn(rate(funnel.getCheckedInCount(), funnel.getTouchCount()));
        return rates;
    }

    public static EventPerformanceEfficiencyVo computeEfficiency(BigDecimal paid, BigDecimal budget,
            BigDecimal netClvTotal, int leadReleasedCount, int contactConvertedCount)
    {
        EventPerformanceEfficiencyVo efficiency = new EventPerformanceEfficiencyVo();
        BigDecimal spend = nz(paid);
        BigDecimal netClv = nz(netClvTotal);
        efficiency.setCpl(divide(spend, Math.max(leadReleasedCount, 1)));
        efficiency.setCac(divide(spend, Math.max(contactConvertedCount, 1)));
        if (spend.compareTo(BigDecimal.ZERO) > 0)
        {
            efficiency.setRoi(netClv.subtract(spend).divide(spend, RATE_SCALE, RoundingMode.HALF_UP));
        }
        if (budget != null && budget.compareTo(BigDecimal.ZERO) > 0)
        {
            efficiency.setBudgetUtilization(spend.divide(budget, RATE_SCALE, RoundingMode.HALF_UP));
        }
        return efficiency;
    }

    public static BigDecimal rate(int numerator, int denominator)
    {
        if (denominator <= 0)
        {
            return null;
        }
        return BigDecimal.valueOf(numerator)
                .divide(BigDecimal.valueOf(denominator), RATE_SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal divide(BigDecimal numerator, int denominator)
    {
        return numerator.divide(BigDecimal.valueOf(denominator), 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal allocateSpend(BigDecimal paid, int channelTouch, int totalTouch)
    {
        if (totalTouch <= 0)
        {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return nz(paid).multiply(BigDecimal.valueOf(channelTouch))
                .divide(BigDecimal.valueOf(totalTouch), 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal computeBalance(BigDecimal budget, BigDecimal paid)
    {
        if (budget == null)
        {
            return null;
        }
        return budget.subtract(nz(paid));
    }

    public static BigDecimal nz(BigDecimal value)
    {
        return value != null ? value : BigDecimal.ZERO;
    }
}
