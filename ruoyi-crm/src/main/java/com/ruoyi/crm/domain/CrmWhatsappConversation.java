package com.ruoyi.crm.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * WhatsApp 会话主表，一个 wa_id 在租户下仅一条记录
 */
public class CrmWhatsappConversation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String tenantId;
    private String waId;
    private String phoneNumber;
    private String contactId;
    private String leadId;
    private String phoneNumberId;
    private java.util.Date lastMessageAt;
    private String lastMessagePreview;
    private java.util.Date lastCustomerMessageAt;
    private java.util.Date lastOutboundAt;
    private Integer unreadCount;
    private String status;
    /** 负责人系统用户 id；NULL = 公共池 */
    private Long userId;
    /** 负责人展示名 */
    private String owner;
    /** 最近一次领取 / 指派时间 */
    private java.util.Date assignedAt;
    /** true=AI托管; false=人工接管; null=跟随渠道 hosted */
    private Boolean hostedByAi;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getWaId() { return waId; }
    public void setWaId(String waId) { this.waId = waId; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getContactId() { return contactId; }
    public void setContactId(String contactId) { this.contactId = contactId; }

    public String getLeadId() { return leadId; }
    public void setLeadId(String leadId) { this.leadId = leadId; }

    public String getPhoneNumberId() { return phoneNumberId; }
    public void setPhoneNumberId(String phoneNumberId) { this.phoneNumberId = phoneNumberId; }

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
