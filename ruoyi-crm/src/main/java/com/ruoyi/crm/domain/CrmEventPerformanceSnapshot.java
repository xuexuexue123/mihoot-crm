package com.ruoyi.crm.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 活动效果 ROI 快照 crm_event_performance_snapshot
 */
public class CrmEventPerformanceSnapshot
{
    private String tenantId;
    private String eventId;
    private String subject;
    private String stage;
    private int touchCount;
    private int leadReleasedCount;
    private int contactConvertedCount;
    private BigDecimal netClvTotal;
    private BigDecimal paid;
    private BigDecimal roi;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date calculatedAt;

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getStage() { return stage; }
    public void setStage(String stage) { this.stage = stage; }
    public int getTouchCount() { return touchCount; }
    public void setTouchCount(int touchCount) { this.touchCount = touchCount; }
    public int getLeadReleasedCount() { return leadReleasedCount; }
    public void setLeadReleasedCount(int leadReleasedCount) { this.leadReleasedCount = leadReleasedCount; }
    public int getContactConvertedCount() { return contactConvertedCount; }
    public void setContactConvertedCount(int contactConvertedCount) { this.contactConvertedCount = contactConvertedCount; }
    public BigDecimal getNetClvTotal() { return netClvTotal; }
    public void setNetClvTotal(BigDecimal netClvTotal) { this.netClvTotal = netClvTotal; }
    public BigDecimal getPaid() { return paid; }
    public void setPaid(BigDecimal paid) { this.paid = paid; }
    public BigDecimal getRoi() { return roi; }
    public void setRoi(BigDecimal roi) { this.roi = roi; }
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public Date getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(Date calculatedAt) { this.calculatedAt = calculatedAt; }
}
