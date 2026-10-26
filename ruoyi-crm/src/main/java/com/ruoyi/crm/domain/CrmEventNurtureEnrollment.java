package com.ruoyi.crm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 活动培育入组 crm_event_nurture_enrollment
 */
public class CrmEventNurtureEnrollment
{
    private Long id;
    private String tenantId;
    private String eventId;
    private Long sequenceId;
    private String audienceKey;
    private String leadId;
    private String contactId;
    private String wechatMpOpenid;
    private String wechatMpAppId;
    private String wecomExternalUserid;
    private String displayName;
    private String mobilePhone;
    private String status;
    private Integer currentStepOrder;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date nextRunAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date enrolledAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date exitedAt;

    private String exitReason;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public Long getSequenceId() { return sequenceId; }
    public void setSequenceId(Long sequenceId) { this.sequenceId = sequenceId; }
    public String getAudienceKey() { return audienceKey; }
    public void setAudienceKey(String audienceKey) { this.audienceKey = audienceKey; }
    public String getLeadId() { return leadId; }
    public void setLeadId(String leadId) { this.leadId = leadId; }
    public String getContactId() { return contactId; }
    public void setContactId(String contactId) { this.contactId = contactId; }
    public String getWechatMpOpenid() { return wechatMpOpenid; }
    public void setWechatMpOpenid(String wechatMpOpenid) { this.wechatMpOpenid = wechatMpOpenid; }
    public String getWechatMpAppId() { return wechatMpAppId; }
    public void setWechatMpAppId(String wechatMpAppId) { this.wechatMpAppId = wechatMpAppId; }
    public String getWecomExternalUserid() { return wecomExternalUserid; }
    public void setWecomExternalUserid(String wecomExternalUserid) { this.wecomExternalUserid = wecomExternalUserid; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getMobilePhone() { return mobilePhone; }
    public void setMobilePhone(String mobilePhone) { this.mobilePhone = mobilePhone; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getCurrentStepOrder() { return currentStepOrder; }
    public void setCurrentStepOrder(Integer currentStepOrder) { this.currentStepOrder = currentStepOrder; }
    public Date getNextRunAt() { return nextRunAt; }
    public void setNextRunAt(Date nextRunAt) { this.nextRunAt = nextRunAt; }
    public Date getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(Date enrolledAt) { this.enrolledAt = enrolledAt; }
    public Date getExitedAt() { return exitedAt; }
    public void setExitedAt(Date exitedAt) { this.exitedAt = exitedAt; }
    public String getExitReason() { return exitReason; }
    public void setExitReason(String exitReason) { this.exitReason = exitReason; }
}
