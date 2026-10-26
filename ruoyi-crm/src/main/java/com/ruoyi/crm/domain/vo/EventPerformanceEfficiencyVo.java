package com.ruoyi.crm.domain.vo;

import java.math.BigDecimal;

/**
 * 活动效果效率指标（CPL / CAC / ROI）
 */
public class EventPerformanceEfficiencyVo
{
    private BigDecimal cpl;
    private BigDecimal cac;
    private BigDecimal roi;
    private BigDecimal budgetUtilization;

    public BigDecimal getCpl() { return cpl; }
    public void setCpl(BigDecimal cpl) { this.cpl = cpl; }
    public BigDecimal getCac() { return cac; }
    public void setCac(BigDecimal cac) { this.cac = cac; }
    public BigDecimal getRoi() { return roi; }
    public void setRoi(BigDecimal roi) { this.roi = roi; }
    public BigDecimal getBudgetUtilization() { return budgetUtilization; }
    public void setBudgetUtilization(BigDecimal budgetUtilization) { this.budgetUtilization = budgetUtilization; }
}
