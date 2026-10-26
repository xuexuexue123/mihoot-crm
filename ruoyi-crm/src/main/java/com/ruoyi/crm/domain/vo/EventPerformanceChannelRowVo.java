package com.ruoyi.crm.domain.vo;

import java.math.BigDecimal;

/**
 * 活动效果渠道对比行
 */
public class EventPerformanceChannelRowVo
{
    private String channelKey;
    private String channelLabel;
    private int touchCount;
    private int h5RegistrationCount;
    private int leadReleasedCount;
    private int contactConvertedCount;
    private BigDecimal spendAllocated;
    private BigDecimal cpl;
    private BigDecimal cac;
    private BigDecimal shareOfTouch;

    public String getChannelKey() { return channelKey; }
    public void setChannelKey(String channelKey) { this.channelKey = channelKey; }
    public String getChannelLabel() { return channelLabel; }
    public void setChannelLabel(String channelLabel) { this.channelLabel = channelLabel; }
    public int getTouchCount() { return touchCount; }
    public void setTouchCount(int touchCount) { this.touchCount = touchCount; }
    public int getH5RegistrationCount() { return h5RegistrationCount; }
    public void setH5RegistrationCount(int h5RegistrationCount) { this.h5RegistrationCount = h5RegistrationCount; }
    public int getLeadReleasedCount() { return leadReleasedCount; }
    public void setLeadReleasedCount(int leadReleasedCount) { this.leadReleasedCount = leadReleasedCount; }
    public int getContactConvertedCount() { return contactConvertedCount; }
    public void setContactConvertedCount(int contactConvertedCount) { this.contactConvertedCount = contactConvertedCount; }
    public BigDecimal getSpendAllocated() { return spendAllocated; }
    public void setSpendAllocated(BigDecimal spendAllocated) { this.spendAllocated = spendAllocated; }
    public BigDecimal getCpl() { return cpl; }
    public void setCpl(BigDecimal cpl) { this.cpl = cpl; }
    public BigDecimal getCac() { return cac; }
    public void setCac(BigDecimal cac) { this.cac = cac; }
    public BigDecimal getShareOfTouch() { return shareOfTouch; }
    public void setShareOfTouch(BigDecimal shareOfTouch) { this.shareOfTouch = shareOfTouch; }
}
