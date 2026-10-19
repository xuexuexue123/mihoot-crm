package com.ruoyi.crm.domain.vo;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 单活动效果与 ROI 汇总
 */
public class EventPerformanceSummaryVo
{
    private String eventId;
    private String subject;
    private BigDecimal budget;
    private BigDecimal paid;
    private BigDecimal balance;
    private String currency;
    private EventPerformanceActivityWindowVo activityWindow;
    private EventPerformanceFunnelVo funnel;
    private EventPerformanceRatesVo rates;
    private EventPerformanceRevenueVo revenue;
    private EventPerformanceEfficiencyVo efficiency;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date calculatedAt;

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public BigDecimal getBudget() { return budget; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }
    public BigDecimal getPaid() { return paid; }
    public void setPaid(BigDecimal paid) { this.paid = paid; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public EventPerformanceActivityWindowVo getActivityWindow() { return activityWindow; }
    public void setActivityWindow(EventPerformanceActivityWindowVo activityWindow) { this.activityWindow = activityWindow; }
    public EventPerformanceFunnelVo getFunnel() { return funnel; }
    public void setFunnel(EventPerformanceFunnelVo funnel) { this.funnel = funnel; }
    public EventPerformanceRatesVo getRates() { return rates; }
    public void setRates(EventPerformanceRatesVo rates) { this.rates = rates; }
    public EventPerformanceRevenueVo getRevenue() { return revenue; }
    public void setRevenue(EventPerformanceRevenueVo revenue) { this.revenue = revenue; }
    public EventPerformanceEfficiencyVo getEfficiency() { return efficiency; }
    public void setEfficiency(EventPerformanceEfficiencyVo efficiency) { this.efficiency = efficiency; }
    public Date getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(Date calculatedAt) { this.calculatedAt = calculatedAt; }
}
