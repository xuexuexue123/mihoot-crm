package com.ruoyi.crm.domain.vo;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Playbook 执行记录 VO
 */
public class AiPlaybookRunVo
{
    private Long id;
    private Long playbookId;
    private Integer playbookVersion;
    private String subjectType;
    private String subjectId;
    private String triggerSource;
    private String status;
    private Map<String, Object> contextJson;
    private Map<String, Object> finalOutput;
    private String errorMessage;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date startedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date finishedAt;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
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

    public Map<String, Object> getContextJson()
    {
        return contextJson;
    }

    public void setContextJson(Map<String, Object> contextJson)
    {
        this.contextJson = contextJson;
    }

    public Map<String, Object> getFinalOutput()
    {
        return finalOutput;
    }

    public void setFinalOutput(Map<String, Object> finalOutput)
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
}
