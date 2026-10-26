package com.ruoyi.crm.domain;

import java.util.Date;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * Web Chat 访客；visitor_id 由浏览器持久化
 */
public class CrmWebchatVisitor extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String tenantId;
    private String visitorId;
    private String websiteSessionId;
    private String displayName;
    private String email;
    private String mobilePhone;
    private String whatsapp;
    private String contactId;
    private String leadId;
    private String userAgent;
    private Date firstSeenAt;
    private Date lastSeenAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getVisitorId() { return visitorId; }
    public void setVisitorId(String visitorId) { this.visitorId = visitorId; }

    public String getWebsiteSessionId() { return websiteSessionId; }
    public void setWebsiteSessionId(String websiteSessionId) { this.websiteSessionId = websiteSessionId; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobilePhone() { return mobilePhone; }
    public void setMobilePhone(String mobilePhone) { this.mobilePhone = mobilePhone; }

    public String getWhatsapp() { return whatsapp; }
    public void setWhatsapp(String whatsapp) { this.whatsapp = whatsapp; }

    public String getContactId() { return contactId; }
    public void setContactId(String contactId) { this.contactId = contactId; }

    public String getLeadId() { return leadId; }
    public void setLeadId(String leadId) { this.leadId = leadId; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public Date getFirstSeenAt() { return firstSeenAt; }
    public void setFirstSeenAt(Date firstSeenAt) { this.firstSeenAt = firstSeenAt; }

    public Date getLastSeenAt() { return lastSeenAt; }
    public void setLastSeenAt(Date lastSeenAt) { this.lastSeenAt = lastSeenAt; }
}
