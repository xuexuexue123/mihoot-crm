package com.ruoyi.crm.domain.vo;

/**
 * KF summary 兼容响应（薄代理）
 */
public class KfSummaryCompatVo
{
    private String summary;
    private ContactServiceSummaryVo.SummarySections structured;
    private String contactId;
    private Boolean persisted;
    private Long version;

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public ContactServiceSummaryVo.SummarySections getStructured() { return structured; }
    public void setStructured(ContactServiceSummaryVo.SummarySections structured) { this.structured = structured; }
    public String getContactId() { return contactId; }
    public void setContactId(String contactId) { this.contactId = contactId; }
    public Boolean getPersisted() { return persisted; }
    public void setPersisted(Boolean persisted) { this.persisted = persisted; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
