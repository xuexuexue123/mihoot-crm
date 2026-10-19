package com.ruoyi.crm.domain.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonFormat;

public class ContactClvVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String contactId;
    private String accountId;
    private String currency;
    private BigDecimal bookedTotal;
    private BigDecimal inflightTotal;
    private BigDecimal refundTotal;
    private BigDecimal netClv;
    private BigDecimal acquisitionCost;
    private Boolean cacPending;
    private BigDecimal pltv12m;
    private BigDecimal pltv36m;
    private String tier;
    private BigDecimal churnRiskScore;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date firstVisitAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date lastVisitAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date nextRecallDueAt;
    private Integer visitCount12m;
    private BigDecimal avgVisitAmount;
    private Map<String, BigDecimal> byTreatment;
    private String originalSource;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date calculatedAt;

    public String getContactId() { return contactId; }
    public void setContactId(String contactId) { this.contactId = contactId; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public BigDecimal getBookedTotal() { return bookedTotal; }
    public void setBookedTotal(BigDecimal bookedTotal) { this.bookedTotal = bookedTotal; }
    public BigDecimal getInflightTotal() { return inflightTotal; }
    public void setInflightTotal(BigDecimal inflightTotal) { this.inflightTotal = inflightTotal; }
    public BigDecimal getRefundTotal() { return refundTotal; }
    public void setRefundTotal(BigDecimal refundTotal) { this.refundTotal = refundTotal; }
    public BigDecimal getNetClv() { return netClv; }
    public void setNetClv(BigDecimal netClv) { this.netClv = netClv; }
    public BigDecimal getAcquisitionCost() { return acquisitionCost; }
    public void setAcquisitionCost(BigDecimal acquisitionCost) { this.acquisitionCost = acquisitionCost; }
    public Boolean getCacPending() { return cacPending; }
    public void setCacPending(Boolean cacPending) { this.cacPending = cacPending; }
    public BigDecimal getPltv12m() { return pltv12m; }
    public void setPltv12m(BigDecimal pltv12m) { this.pltv12m = pltv12m; }
    public BigDecimal getPltv36m() { return pltv36m; }
    public void setPltv36m(BigDecimal pltv36m) { this.pltv36m = pltv36m; }
    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }
    public BigDecimal getChurnRiskScore() { return churnRiskScore; }
    public void setChurnRiskScore(BigDecimal churnRiskScore) { this.churnRiskScore = churnRiskScore; }
    public Date getFirstVisitAt() { return firstVisitAt; }
    public void setFirstVisitAt(Date firstVisitAt) { this.firstVisitAt = firstVisitAt; }
    public Date getLastVisitAt() { return lastVisitAt; }
    public void setLastVisitAt(Date lastVisitAt) { this.lastVisitAt = lastVisitAt; }
    public Date getNextRecallDueAt() { return nextRecallDueAt; }
    public void setNextRecallDueAt(Date nextRecallDueAt) { this.nextRecallDueAt = nextRecallDueAt; }
    public Integer getVisitCount12m() { return visitCount12m; }
    public void setVisitCount12m(Integer visitCount12m) { this.visitCount12m = visitCount12m; }
    public BigDecimal getAvgVisitAmount() { return avgVisitAmount; }
    public void setAvgVisitAmount(BigDecimal avgVisitAmount) { this.avgVisitAmount = avgVisitAmount; }
    public Map<String, BigDecimal> getByTreatment() { return byTreatment; }
    public void setByTreatment(Map<String, BigDecimal> byTreatment) { this.byTreatment = byTreatment; }
    public String getOriginalSource() { return originalSource; }
    public void setOriginalSource(String originalSource) { this.originalSource = originalSource; }
    public Date getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(Date calculatedAt) { this.calculatedAt = calculatedAt; }
}
