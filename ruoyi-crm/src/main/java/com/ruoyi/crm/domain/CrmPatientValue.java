package com.ruoyi.crm.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 患者 CLV 快照 crm_patient_value
 */
public class CrmPatientValue extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String contactId;
    private String accountId;
    private String tenantId;
    private String currency;
    private BigDecimal bookedTotal;
    private BigDecimal inflightTotal;
    private BigDecimal refundTotal;
    private BigDecimal netClv;
    private BigDecimal acquisitionCost;
    private BigDecimal pltv12m;
    private BigDecimal pltv36m;
    private String tier;
    private BigDecimal churnRiskScore;
    private Integer visitCount12m;
    private BigDecimal avgVisitAmount;
    private String byTreatment;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date firstVisitAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date lastVisitAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date nextRecallDueAt;
    private String originalSource;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date calculatedAt;
    private String delFlag;

    /** 列表查询扩展 */
    private BigDecimal minNetClv;
    private BigDecimal maxNetClv;
    private String sortBy;
    private String sortOrder;
    private String treatmentCategory;
    private Integer daysSinceLastVisitGte;
    private String owner;
    private String orgId;

    public String getContactId() { return contactId; }
    public void setContactId(String contactId) { this.contactId = contactId; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
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
    public BigDecimal getPltv12m() { return pltv12m; }
    public void setPltv12m(BigDecimal pltv12m) { this.pltv12m = pltv12m; }
    public BigDecimal getPltv36m() { return pltv36m; }
    public void setPltv36m(BigDecimal pltv36m) { this.pltv36m = pltv36m; }
    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }
    public BigDecimal getChurnRiskScore() { return churnRiskScore; }
    public void setChurnRiskScore(BigDecimal churnRiskScore) { this.churnRiskScore = churnRiskScore; }
    public Integer getVisitCount12m() { return visitCount12m; }
    public void setVisitCount12m(Integer visitCount12m) { this.visitCount12m = visitCount12m; }
    public BigDecimal getAvgVisitAmount() { return avgVisitAmount; }
    public void setAvgVisitAmount(BigDecimal avgVisitAmount) { this.avgVisitAmount = avgVisitAmount; }
    public String getByTreatment() { return byTreatment; }
    public void setByTreatment(String byTreatment) { this.byTreatment = byTreatment; }
    public Date getFirstVisitAt() { return firstVisitAt; }
    public void setFirstVisitAt(Date firstVisitAt) { this.firstVisitAt = firstVisitAt; }
    public Date getLastVisitAt() { return lastVisitAt; }
    public void setLastVisitAt(Date lastVisitAt) { this.lastVisitAt = lastVisitAt; }
    public Date getNextRecallDueAt() { return nextRecallDueAt; }
    public void setNextRecallDueAt(Date nextRecallDueAt) { this.nextRecallDueAt = nextRecallDueAt; }
    public String getOriginalSource() { return originalSource; }
    public void setOriginalSource(String originalSource) { this.originalSource = originalSource; }
    public Date getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(Date calculatedAt) { this.calculatedAt = calculatedAt; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public BigDecimal getMinNetClv() { return minNetClv; }
    public void setMinNetClv(BigDecimal minNetClv) { this.minNetClv = minNetClv; }
    public BigDecimal getMaxNetClv() { return maxNetClv; }
    public void setMaxNetClv(BigDecimal maxNetClv) { this.maxNetClv = maxNetClv; }
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    public String getSortOrder() { return sortOrder; }
    public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }
    public String getTreatmentCategory() { return treatmentCategory; }
    public void setTreatmentCategory(String treatmentCategory) { this.treatmentCategory = treatmentCategory; }
    public Integer getDaysSinceLastVisitGte() { return daysSinceLastVisitGte; }
    public void setDaysSinceLastVisitGte(Integer daysSinceLastVisitGte) { this.daysSinceLastVisitGte = daysSinceLastVisitGte; }
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    public String getOrgId() { return orgId; }
    public void setOrgId(String orgId) { this.orgId = orgId; }
}
