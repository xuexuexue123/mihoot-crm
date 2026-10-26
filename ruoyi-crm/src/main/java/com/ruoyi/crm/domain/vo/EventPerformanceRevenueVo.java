package com.ruoyi.crm.domain.vo;

import java.math.BigDecimal;

/**
 * 活动效果收入与 CLV 归因
 */
public class EventPerformanceRevenueVo
{
    private int attributedContactCount;
    private BigDecimal bookedClvTotal;
    private BigDecimal inflightClvTotal;
    private BigDecimal refundTotal;
    private BigDecimal netClvTotal;
    private String attributionMode;
    private BigDecimal windowNetClvTotal;
    private Integer windowDays;
    private boolean isEstimate;

    public int getAttributedContactCount() { return attributedContactCount; }
    public void setAttributedContactCount(int attributedContactCount) { this.attributedContactCount = attributedContactCount; }
    public BigDecimal getBookedClvTotal() { return bookedClvTotal; }
    public void setBookedClvTotal(BigDecimal bookedClvTotal) { this.bookedClvTotal = bookedClvTotal; }
    public BigDecimal getInflightClvTotal() { return inflightClvTotal; }
    public void setInflightClvTotal(BigDecimal inflightClvTotal) { this.inflightClvTotal = inflightClvTotal; }
    public BigDecimal getRefundTotal() { return refundTotal; }
    public void setRefundTotal(BigDecimal refundTotal) { this.refundTotal = refundTotal; }
    public BigDecimal getNetClvTotal() { return netClvTotal; }
    public void setNetClvTotal(BigDecimal netClvTotal) { this.netClvTotal = netClvTotal; }
    public String getAttributionMode() { return attributionMode; }
    public void setAttributionMode(String attributionMode) { this.attributionMode = attributionMode; }
    public BigDecimal getWindowNetClvTotal() { return windowNetClvTotal; }
    public void setWindowNetClvTotal(BigDecimal windowNetClvTotal) { this.windowNetClvTotal = windowNetClvTotal; }
    public Integer getWindowDays() { return windowDays; }
    public void setWindowDays(Integer windowDays) { this.windowDays = windowDays; }
    public boolean isEstimate() { return isEstimate; }
    public void setEstimate(boolean estimate) { isEstimate = estimate; }
}
