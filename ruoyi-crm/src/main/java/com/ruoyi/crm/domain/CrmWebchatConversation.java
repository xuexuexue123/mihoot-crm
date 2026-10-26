package com.ruoyi.crm.domain;

import java.util.Date;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * Web Chat 会话；一 visitor 同时最多一条 active
 */
public class CrmWebchatConversation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String tenantId;
    private String visitorId;
    private String profileName;
    private String contactId;
    private String leadId;
    private Date lastMessageAt;
    private String lastMessagePreview;
    private Date lastCustomerMessageAt;
    private Date lastOutboundAt;
    private Integer unreadCount;
    private String status;
    private Long userId;
    private String owner;
    private Date assignedAt;
    private Boolean hostedByAi;
    private Date closedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getVisitorId() { return visitorId; }
    public void setVisitorId(String visitorId) { this.visitorId = visitorId; }

    public String getProfileName() { return profileName; }
    public void setProfileName(String profileName) { this.profileName = profileName; }

    public String getContactId() { return contactId; }
    public void setContactId(String contactId) { this.contactId = contactId; }

    public String getLeadId() { return leadId; }
    public void setLeadId(String leadId) { this.leadId = leadId; }

    public Date getLastMessageAt() { return lastMessageAt; }
    public void setLastMessageAt(Date lastMessageAt) { this.lastMessageAt = lastMessageAt; }

    public String getLastMessagePreview() { return lastMessagePreview; }
    public void setLastMessagePreview(String lastMessagePreview) { this.lastMessagePreview = lastMessagePreview; }

    public Date getLastCustomerMessageAt() { return lastCustomerMessageAt; }
    public void setLastCustomerMessageAt(Date lastCustomerMessageAt) { this.lastCustomerMessageAt = lastCustomerMessageAt; }

    public Date getLastOutboundAt() { return lastOutboundAt; }
    public void setLastOutboundAt(Date lastOutboundAt) { this.lastOutboundAt = lastOutboundAt; }

    public Integer getUnreadCount() { return unreadCount; }
    public void setUnreadCount(Integer unreadCount) { this.unreadCount = unreadCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }

    public Date getAssignedAt() { return assignedAt; }
    public void setAssignedAt(Date assignedAt) { this.assignedAt = assignedAt; }

    public Boolean getHostedByAi() { return hostedByAi; }
    public void setHostedByAi(Boolean hostedByAi) { this.hostedByAi = hostedByAi; }

    public Date getClosedAt() { return closedAt; }
    public void setClosedAt(Date closedAt) { this.closedAt = closedAt; }
}
