package com.ruoyi.crm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 患者工作流自动化触发幂等日志
 */
public class CrmPatientAutomationLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String logId;
    private String contactId;
    private String automationKey;
    private Date dueDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date triggeredAt;
    private String workflowId;
    private String delFlag;

    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }
    public String getContactId() { return contactId; }
    public void setContactId(String contactId) { this.contactId = contactId; }
    public String getAutomationKey() { return automationKey; }
    public void setAutomationKey(String automationKey) { this.automationKey = automationKey; }
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    public Date getTriggeredAt() { return triggeredAt; }
    public void setTriggeredAt(Date triggeredAt) { this.triggeredAt = triggeredAt; }
    public String getWorkflowId() { return workflowId; }
    public void setWorkflowId(String workflowId) { this.workflowId = workflowId; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
