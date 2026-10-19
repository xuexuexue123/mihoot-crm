package com.ruoyi.crm.domain.dto;

import java.util.List;
import java.util.Map;

/**
 * 创建 Reminder Link 请求
 */
public class ReminderLinkCreateRequest
{
    private String type;
    private String contactId;
    private String waId;
    private String refType;
    private String refId;
    private Map<String, Object> payload;
    private List<String> actions;
    private Long expiresInSeconds;
    private Boolean revokeSameRef;
    /** 前端 Host 解析的公开子域名 slug，用于拼 publicUrl，避免再查主库 */
    private String websiteSlug;

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

    public Map<String, Object> getPayload() { return payload; }
    public void setPayload(Map<String, Object> payload) { this.payload = payload; }

    public List<String> getActions() { return actions; }
    public void setActions(List<String> actions) { this.actions = actions; }

    public Long getExpiresInSeconds() { return expiresInSeconds; }
    public void setExpiresInSeconds(Long expiresInSeconds) { this.expiresInSeconds = expiresInSeconds; }

    public Boolean getRevokeSameRef() { return revokeSameRef; }
    public void setRevokeSameRef(Boolean revokeSameRef) { this.revokeSameRef = revokeSameRef; }

    public String getWebsiteSlug() { return websiteSlug; }
    public void setWebsiteSlug(String websiteSlug) { this.websiteSlug = websiteSlug; }
}
