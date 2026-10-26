package com.ruoyi.crm.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * WhatsApp Template Message 缓存
 */
public class CrmWhatsappTemplate extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String tenantId;
    private String templateName;
    private String language;
    private String category;
    private String status;
    private String rejectionReason;
    private String components;
    private String previewUrl;
    private java.util.Date lastSyncTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public String getComponents() { return components; }
    public void setComponents(String components) { this.components = components; }

    public String getPreviewUrl() { return previewUrl; }
    public void setPreviewUrl(String previewUrl) { this.previewUrl = previewUrl; }

    public java.util.Date getLastSyncTime() { return lastSyncTime; }
    public void setLastSyncTime(java.util.Date lastSyncTime) { this.lastSyncTime = lastSyncTime; }
}
