package com.ruoyi.crm.domain;

import java.util.Date;

/**
 * WhatsApp Reminder 短链
 */
public class CrmReminderLink
{
    private Long id;
    private String tenantId;
    private String token;
    private String type;
    private String contactId;
    private String waId;
    private String refType;
    private String refId;
    private String payload;
    private String actions;
    private String status;
    private Date expiresAt;
    private Date openedAt;
    private Integer openCount;
    private String createdBy;
    private Date createTime;
    private Date updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getContactId() { return contactId; }
    public void setContactId(String contactId) { this.contactId = contactId; }

    public String getWaId() { return waId; }
    public void setWaId(String waId) { this.waId = waId; }

    public String getRefType() { return refType; }
    public void setRefType(String refType) { this.refType = refType; }

    public String getRefId() { return refId; }
    public void setRefId(String refId) { this.refId = refId; }

    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }

    public String getActions() { return actions; }
    public void setActions(String actions) { this.actions = actions; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Date expiresAt) { this.expiresAt = expiresAt; }

    public Date getOpenedAt() { return openedAt; }
    public void setOpenedAt(Date openedAt) { this.openedAt = openedAt; }

    public Integer getOpenCount() { return openCount; }
    public void setOpenCount(Integer openCount) { this.openCount = openCount; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
