package com.ruoyi.crm.service.support;

/**
 * 企微外部联系人与子渠道（效果归因用）
 */
public class WecomChannelContactRow
{
    private String externalUserid;
    private String state;
    private String crmContactId;
    private String crmLeadId;
    private String leadStatus;

    public String getExternalUserid() { return externalUserid; }
    public void setExternalUserid(String externalUserid) { this.externalUserid = externalUserid; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getCrmContactId() { return crmContactId; }
    public void setCrmContactId(String crmContactId) { this.crmContactId = crmContactId; }
    public String getCrmLeadId() { return crmLeadId; }
    public void setCrmLeadId(String crmLeadId) { this.crmLeadId = crmLeadId; }
    public String getLeadStatus() { return leadStatus; }
    public void setLeadStatus(String leadStatus) { this.leadStatus = leadStatus; }
}
