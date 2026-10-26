package com.ruoyi.crm.domain.vo;

import java.math.BigDecimal;

/**
 * 多联系人 CLV 聚合结果
 */
public class ContactClvAggregateVo
{
    private BigDecimal bookedClvTotal = BigDecimal.ZERO;
    private BigDecimal inflightClvTotal = BigDecimal.ZERO;
    private BigDecimal refundTotal = BigDecimal.ZERO;
    private BigDecimal netClvTotal = BigDecimal.ZERO;
    private boolean estimate;

    public BigDecimal getBookedClvTotal() { return bookedClvTotal; }
    public void setBookedClvTotal(BigDecimal bookedClvTotal) { this.bookedClvTotal = bookedClvTotal; }
    public BigDecimal getInflightClvTotal() { return inflightClvTotal; }
    public void setInflightClvTotal(BigDecimal inflightClvTotal) { this.inflightClvTotal = inflightClvTotal; }
    public BigDecimal getRefundTotal() { return refundTotal; }
    public void setRefundTotal(BigDecimal refundTotal) { this.refundTotal = refundTotal; }
    public BigDecimal getNetClvTotal() { return netClvTotal; }
    public void setNetClvTotal(BigDecimal netClvTotal) { this.netClvTotal = netClvTotal; }
    public boolean isEstimate() { return estimate; }
    public void setEstimate(boolean estimate) { this.estimate = estimate; }
}
