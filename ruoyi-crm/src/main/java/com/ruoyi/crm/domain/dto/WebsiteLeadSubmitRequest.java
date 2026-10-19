package com.ruoyi.crm.domain.dto;

/**
 * 官网在线预约提交（BFF → POST /websitelead）
 */
public class WebsiteLeadSubmitRequest
{
    private String tenantSlug;
    private String leadId;
    private String leadName;
    private String mobilePhone;
    private String workEmail;
    private String orgId;
    private String leadType;
    private String leadSource;
    private String stage;
    private String status;
    private String owner;
    private String createBy;
    private Long userId;
    private Long deptId;
    private String appointmentDate;
    private String appointmentTime;
    private String pagePath;
    private String blockId;
    private Boolean consentAccepted;
    private String customField;
    private String _sessionId;
    private String _browseHistory;

    public String getTenantSlug()
    {
        return tenantSlug;
    }

    public void setTenantSlug(String tenantSlug)
    {
        this.tenantSlug = tenantSlug;
    }

    public String getLeadId()
    {
        return leadId;
    }

    public void setLeadId(String leadId)
    {
        this.leadId = leadId;
    }

    public String getLeadName()
    {
        return leadName;
    }

    public void setLeadName(String leadName)
    {
        this.leadName = leadName;
    }

    public String getMobilePhone()
    {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone)
    {
        this.mobilePhone = mobilePhone;
    }

    public String getWorkEmail()
    {
        return workEmail;
    }

    public void setWorkEmail(String workEmail)
    {
        this.workEmail = workEmail;
    }

    public String getOrgId()
    {
        return orgId;
    }

    public void setOrgId(String orgId)
    {
        this.orgId = orgId;
    }

    public String getLeadType()
    {
        return leadType;
    }

    public void setLeadType(String leadType)
    {
        this.leadType = leadType;
    }

    public String getLeadSource()
    {
        return leadSource;
    }

    public void setLeadSource(String leadSource)
    {
        this.leadSource = leadSource;
    }

    public String getStage()
    {
        return stage;
    }

    public void setStage(String stage)
    {
        this.stage = stage;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getOwner()
    {
        return owner;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    public String getCreateBy()
    {
        return createBy;
    }

    public void setCreateBy(String createBy)
    {
        this.createBy = createBy;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public String getAppointmentDate()
    {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate)
    {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime()
    {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime)
    {
        this.appointmentTime = appointmentTime;
    }

    public String getPagePath()
    {
        return pagePath;
    }

    public void setPagePath(String pagePath)
    {
        this.pagePath = pagePath;
    }

    public String getBlockId()
    {
        return blockId;
    }

    public void setBlockId(String blockId)
    {
        this.blockId = blockId;
    }

    public Boolean getConsentAccepted()
    {
        return consentAccepted;
    }

    public void setConsentAccepted(Boolean consentAccepted)
    {
        this.consentAccepted = consentAccepted;
    }

    public String getCustomField()
    {
        return customField;
    }

    public void setCustomField(String customField)
    {
        this.customField = customField;
    }

    public String get_sessionId()
    {
        return _sessionId;
    }

    public void set_sessionId(String _sessionId)
    {
        this._sessionId = _sessionId;
    }

    public String get_browseHistory()
    {
        return _browseHistory;
    }

    public void set_browseHistory(String _browseHistory)
    {
        this._browseHistory = _browseHistory;
    }
}
