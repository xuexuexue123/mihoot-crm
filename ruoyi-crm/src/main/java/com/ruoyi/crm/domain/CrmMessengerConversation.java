package com.ruoyi.crm.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * Messenger 会话；一租户下一 psid 一行
 */
public class CrmMessengerConversation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String tenantId;
    private String psid;
    private String pageId;
    private String profileName;
    private String contactId;
    private String leadId;
    private java.util.Date lastMessageAt;
    private String lastMessagePreview;
    private java.util.Date lastCustomerMessageAt;
    private java.util.Date lastOutboundAt;
    private Integer unreadCount;
    private String status;
    private Long userId;
    private String owner;
    private java.util.Date assignedAt;
    private Boolean hostedByAi;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getPsid() { return psid; }
    public void setPsid(String psid) { this.psid = psid; }

    public String getPageId() { return pageId; }
    public void setPageId(String pageId) { this.pageId = pageId; }

    public String getProfileName() { return profileName; }
    public void setProfileName(String profileName) { this.profileName = profileName; }

    public String getContactId() { return contactId; }
    public void setContactId(String contactId) { this.contactId = contactId; }

    public String getLeadId() { return leadId; }
    public void setLeadId(String leadId) { this.leadId = leadId; }

    public java.util.Date getLastMessageAt() { return lastMessageAt; }
    public void setLastMessageAt(java.util.Date lastMessageAt) { this.lastMessageAt = lastMessageAt; }

    public String getLastMessagePreview() { return lastMessagePreview; }
    public void setLastMessagePreview(String lastMessagePreview) { this.lastMessagePreview = lastMessagePreview; }

    public java.util.Date getLastCustomerMessageAt() { return lastCustomerMessageAt; }
    public void setLastCustomerMessageAt(java.util.Date lastCustomerMessageAt) { this.lastCustomerMessageAt = lastCustomerMessageAt; }

    public java.util.Date getLastOutboundAt() { return lastOutboundAt; }
    public void setLastOutboundAt(java.util.Date lastOutboundAt) { this.lastOutboundAt = lastOutboundAt; }

    public Integer getUnreadCount() { return unreadCount; }
    public void setUnreadCount(Integer unreadCount) { this.unreadCount = unreadCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }

    public java.util.Date getAssignedAt() { return assignedAt; }
    public void setAssignedAt(java.util.Date assignedAt) { this.assignedAt = assignedAt; }

    public Boolean getHostedByAi() { return hostedByAi; }
    public void setHostedByAi(Boolean hostedByAi) { this.hostedByAi = hostedByAi; }
}
