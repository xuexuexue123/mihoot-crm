package com.ruoyi.crm.domain.vo;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 活动列表 ROI 摘要行
 */
public class EventPerformanceListRowVo
{
    private String eventId;
    private String subject;
    private String stage;
    private BigDecimal paid;
    private int touchCount;
    private int leadReleasedCount;
    private int contactConvertedCount;
    private BigDecimal netClvTotal;
    private BigDecimal cac;
    private BigDecimal roi;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getStage() { return stage; }
    public void setStage(String stage) { this.stage = stage; }
    public BigDecimal getPaid() { return paid; }
    public void setPaid(BigDecimal paid) { this.paid = paid; }
    public int getTouchCount() { return touchCount; }
    public void setTouchCount(int touchCount) { this.touchCount = touchCount; }
    public int getLeadReleasedCount() { return leadReleasedCount; }
    public void setLeadReleasedCount(int leadReleasedCount) { this.leadReleasedCount = leadReleasedCount; }
    public int getContactConvertedCount() { return contactConvertedCount; }
    public void setContactConvertedCount(int contactConvertedCount) { this.contactConvertedCount = contactConvertedCount; }
    public BigDecimal getNetClvTotal() { return netClvTotal; }
    public void setNetClvTotal(BigDecimal netClvTotal) { this.netClvTotal = netClvTotal; }
    public BigDecimal getCac() { return cac; }
    public void setCac(BigDecimal cac) { this.cac = cac; }
    public BigDecimal getRoi() { return roi; }
    public void setRoi(BigDecimal roi) { this.roi = roi; }
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
}
