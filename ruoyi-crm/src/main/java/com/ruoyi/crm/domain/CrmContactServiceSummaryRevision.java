package com.ruoyi.crm.domain;

import java.util.Date;

/**
 * Contact 服务摘要历史版本 crm_contact_service_summary_revision
 */
public class CrmContactServiceSummaryRevision
{
    private Long id;
    private String tenantId;
    private String contactId;
    private Long version;
    /** JSON snapshot */
    private String snapshot;
    private String trigger;
    private String sourceChannels;
    private String model;
    private String generatedBy;
    private String createdBy;
    private Date createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getContactId() { return contactId; }
    public void setContactId(String contactId) { this.contactId = contactId; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public String getSnapshot() { return snapshot; }
    public void setSnapshot(String snapshot) { this.snapshot = snapshot; }
    public String getTrigger() { return trigger; }
    public void setTrigger(String trigger) { this.trigger = trigger; }
    public String getSourceChannels() { return sourceChannels; }
    public void setSourceChannels(String sourceChannels) { this.sourceChannels = sourceChannels; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
