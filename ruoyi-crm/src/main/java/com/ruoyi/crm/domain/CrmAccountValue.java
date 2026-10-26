package com.ruoyi.crm.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 家庭 CLV 快照 crm_account_value
 */
public class CrmAccountValue extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String valueId;
    private String accountId;
    private String tenantId;
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
    private String byTreatment;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date lastVisitAt;
    private Integer daysSinceLastVisit;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date calculatedAt;
    private String delFlag;

    private BigDecimal minNetClv;
    private BigDecimal maxNetClv;
    private String sortBy;
    private String sortOrder;
    private String owner;
    private String orgId;

    public String getValueId() { return valueId; }
    public void setValueId(String valueId) { this.valueId = valueId; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
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
    public String getByTreatment() { return byTreatment; }
    public void setByTreatment(String byTreatment) { this.byTreatment = byTreatment; }
    public Date getLastVisitAt() { return lastVisitAt; }
    public void setLastVisitAt(Date lastVisitAt) { this.lastVisitAt = lastVisitAt; }
    public Integer getDaysSinceLastVisit() { return daysSinceLastVisit; }
    public void setDaysSinceLastVisit(Integer daysSinceLastVisit) { this.daysSinceLastVisit = daysSinceLastVisit; }
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
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    public String getOrgId() { return orgId; }
    public void setOrgId(String orgId) { this.orgId = orgId; }
}
