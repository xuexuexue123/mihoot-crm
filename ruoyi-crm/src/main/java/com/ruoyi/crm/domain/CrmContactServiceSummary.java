package com.ruoyi.crm.domain;

import java.util.Date;

/**
 * Contact 服务摘要当前态 crm_contact_service_summary
 */
public class CrmContactServiceSummary
{
    private String tenantId;
    private String contactId;
    private String currentNeed;
    private String confirmedFacts;
    private String commitmentsTodos;
    private String risksSentiment;
    private Date lastTouchAt;
    private String lastTouchChannel;
    private String lastTouchSummary;
    /** JSON array string, e.g. ["whatsapp","wecom_kf"] */
    private String sourceChannels;
    private String lastEventId;
    private Date lastEventAt;
    private Long version;
    private String status;
    private String generatedBy;
    /** JSON array string, e.g. ["commitmentsTodos"] */
    private String lockedFields;
    private String model;
    private String errorMessage;
    private Date createdAt;
    private Date updatedAt;

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getContactId() { return contactId; }
    public void setContactId(String contactId) { this.contactId = contactId; }
    public String getCurrentNeed() { return currentNeed; }
    public void setCurrentNeed(String currentNeed) { this.currentNeed = currentNeed; }
    public String getConfirmedFacts() { return confirmedFacts; }
    public void setConfirmedFacts(String confirmedFacts) { this.confirmedFacts = confirmedFacts; }
    public String getCommitmentsTodos() { return commitmentsTodos; }
    public void setCommitmentsTodos(String commitmentsTodos) { this.commitmentsTodos = commitmentsTodos; }
    public String getRisksSentiment() { return risksSentiment; }
    public void setRisksSentiment(String risksSentiment) { this.risksSentiment = risksSentiment; }
    public Date getLastTouchAt() { return lastTouchAt; }
    public void setLastTouchAt(Date lastTouchAt) { this.lastTouchAt = lastTouchAt; }
    public String getLastTouchChannel() { return lastTouchChannel; }
    public void setLastTouchChannel(String lastTouchChannel) { this.lastTouchChannel = lastTouchChannel; }
    public String getLastTouchSummary() { return lastTouchSummary; }
    public void setLastTouchSummary(String lastTouchSummary) { this.lastTouchSummary = lastTouchSummary; }
    public String getSourceChannels() { return sourceChannels; }
    public void setSourceChannels(String sourceChannels) { this.sourceChannels = sourceChannels; }
    public String getLastEventId() { return lastEventId; }
    public void setLastEventId(String lastEventId) { this.lastEventId = lastEventId; }
    public Date getLastEventAt() { return lastEventAt; }
    public void setLastEventAt(Date lastEventAt) { this.lastEventAt = lastEventAt; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }
    public String getLockedFields() { return lockedFields; }
    public void setLockedFields(String lockedFields) { this.lockedFields = lockedFields; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
