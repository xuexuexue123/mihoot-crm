package com.ruoyi.crm.domain.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonFormat;

public class FamilyClvVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String accountId;
    private String accountName;
    private String currency;
    private Integer memberCount;
    private BigDecimal bookedTotal;
    private BigDecimal inflightTotal;
    private BigDecimal refundTotal;
    private BigDecimal netClv;
    private BigDecimal paidTotal;
    private BigDecimal pendingReceivable;
    private BigDecimal pltv12m;
    private BigDecimal acquisitionCost;
    private BigDecimal roi;
    private String tier;
    private Map<String, BigDecimal> byTreatment;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date lastVisitAt;
    private Integer daysSinceLastVisit;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date calculatedAt;

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public Integer getMemberCount() { return memberCount; }
    public void setMemberCount(Integer memberCount) { this.memberCount = memberCount; }
    public BigDecimal getBookedTotal() { return bookedTotal; }
    public void setBookedTotal(BigDecimal bookedTotal) { this.bookedTotal = bookedTotal; }
    public BigDecimal getInflightTotal() { return inflightTotal; }
    public void setInflightTotal(BigDecimal inflightTotal) { this.inflightTotal = inflightTotal; }
    public BigDecimal getRefundTotal() { return refundTotal; }
    public void setRefundTotal(BigDecimal refundTotal) { this.refundTotal = refundTotal; }
    public BigDecimal getNetClv() { return netClv; }
    public void setNetClv(BigDecimal netClv) { this.netClv = netClv; }
    public BigDecimal getPaidTotal() { return paidTotal; }
    public void setPaidTotal(BigDecimal paidTotal) { this.paidTotal = paidTotal; }
    public BigDecimal getPendingReceivable() { return pendingReceivable; }
    public void setPendingReceivable(BigDecimal pendingReceivable) { this.pendingReceivable = pendingReceivable; }
    public BigDecimal getPltv12m() { return pltv12m; }
    public void setPltv12m(BigDecimal pltv12m) { this.pltv12m = pltv12m; }
    public BigDecimal getAcquisitionCost() { return acquisitionCost; }
    public void setAcquisitionCost(BigDecimal acquisitionCost) { this.acquisitionCost = acquisitionCost; }
    public BigDecimal getRoi() { return roi; }
    public void setRoi(BigDecimal roi) { this.roi = roi; }
    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }
    public Map<String, BigDecimal> getByTreatment() { return byTreatment; }
    public void setByTreatment(Map<String, BigDecimal> byTreatment) { this.byTreatment = byTreatment; }
    public Date getLastVisitAt() { return lastVisitAt; }
    public void setLastVisitAt(Date lastVisitAt) { this.lastVisitAt = lastVisitAt; }
    public Integer getDaysSinceLastVisit() { return daysSinceLastVisit; }
    public void setDaysSinceLastVisit(Integer daysSinceLastVisit) { this.daysSinceLastVisit = daysSinceLastVisit; }
    public Date getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(Date calculatedAt) { this.calculatedAt = calculatedAt; }
}
