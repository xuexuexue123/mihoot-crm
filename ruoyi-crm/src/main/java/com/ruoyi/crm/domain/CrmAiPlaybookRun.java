package com.ruoyi.crm.domain;

import java.util.Date;

/**
 * Playbook 执行审计 crm_ai_playbook_run
 */
public class CrmAiPlaybookRun
{
    private Long id;
    private String tenantId;
    private Long playbookId;
    private Integer playbookVersion;
    private String subjectType;
    private String subjectId;
    private String triggerSource;
    private String status;
    private String contextJson;
    private String finalOutput;
    private String errorMessage;
    private Date startedAt;
    private Date finishedAt;
    private String createBy;
    private Date createTime;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(String tenantId)
    {
        this.tenantId = tenantId;
    }

    public Long getPlaybookId()
    {
        return playbookId;
    }

    public void setPlaybookId(Long playbookId)
    {
        this.playbookId = playbookId;
    }

    public Integer getPlaybookVersion()
    {
        return playbookVersion;
    }

    public void setPlaybookVersion(Integer playbookVersion)
    {
        this.playbookVersion = playbookVersion;
    }

    public String getSubjectType()
    {
        return subjectType;
    }

    public void setSubjectType(String subjectType)
    {
        this.subjectType = subjectType;
    }

    public String getSubjectId()
    {
        return subjectId;
    }

    public void setSubjectId(String subjectId)
    {
        this.subjectId = subjectId;
    }

    public String getTriggerSource()
    {
        return triggerSource;
    }

    public void setTriggerSource(String triggerSource)
    {
        this.triggerSource = triggerSource;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getContextJson()
    {
        return contextJson;
    }

    public void setContextJson(String contextJson)
    {
        this.contextJson = contextJson;
    }

    public String getFinalOutput()
    {
        return finalOutput;
    }

    public void setFinalOutput(String finalOutput)
    {
        this.finalOutput = finalOutput;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public Date getStartedAt()
    {
        return startedAt;
    }

    public void setStartedAt(Date startedAt)
    {
        this.startedAt = startedAt;
    }

    public Date getFinishedAt()
    {
        return finishedAt;
    }

    public void setFinishedAt(Date finishedAt)
    {
        this.finishedAt = finishedAt;
    }

    public String getCreateBy()
    {
        return createBy;
    }

    public void setCreateBy(String createBy)
    {
        this.createBy = createBy;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }
}
