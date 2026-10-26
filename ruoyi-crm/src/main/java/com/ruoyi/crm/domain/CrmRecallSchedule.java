package com.ruoyi.crm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 复诊日程 crm_recall_schedule
 */
public class CrmRecallSchedule
{
    private String scheduleId;
    private String tenantId;
    private String contactId;
    private String policyId;
    private String anchorType;
    private String anchorId;
    private String treatmentItemCode;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date nextDueAt;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastFiredAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public String getScheduleId() { return scheduleId; }
    public void setScheduleId(String scheduleId) { this.scheduleId = scheduleId; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getContactId() { return contactId; }
    public void setContactId(String contactId) { this.contactId = contactId; }
    public String getPolicyId() { return policyId; }
    public void setPolicyId(String policyId) { this.policyId = policyId; }
    public String getAnchorType() { return anchorType; }
    public void setAnchorType(String anchorType) { this.anchorType = anchorType; }
    public String getAnchorId() { return anchorId; }
    public void setAnchorId(String anchorId) { this.anchorId = anchorId; }
    public String getTreatmentItemCode() { return treatmentItemCode; }
    public void setTreatmentItemCode(String treatmentItemCode) { this.treatmentItemCode = treatmentItemCode; }
    public Date getNextDueAt() { return nextDueAt; }
    public void setNextDueAt(Date nextDueAt) { this.nextDueAt = nextDueAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getLastFiredAt() { return lastFiredAt; }
    public void setLastFiredAt(Date lastFiredAt) { this.lastFiredAt = lastFiredAt; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
