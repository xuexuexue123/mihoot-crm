package com.ruoyi.crm.domain.dto;

import java.util.Map;

/**
 * Playbook 执行审计写入请求
 */
public class AiPlaybookRunCreateDto
{
    private Long playbookId;
    private Integer playbookVersion;
    private String subjectType;
    private String subjectId;
    private String triggerSource;
    private String status;
    private Map<String, Object> contextJson;
    private Map<String, Object> finalOutput;
    private String errorMessage;

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
}
