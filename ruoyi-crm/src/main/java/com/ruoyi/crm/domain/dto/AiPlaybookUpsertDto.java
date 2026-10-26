package com.ruoyi.crm.domain.dto;

import java.util.List;
import java.util.Map;

/**
 * Playbook Upsert 请求体
 */
public class AiPlaybookUpsertDto
{
    private Long id;
    private String name;
    private String description;
    private List<String> triggerKeywords;
    private Long entrySkillId;
    private Map<String, Object> outputSchema;
    private Boolean enabled;
    private Integer version;
    private List<AiPlaybookStepDto> steps;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public List<String> getTriggerKeywords()
    {
        return triggerKeywords;
    }

    public void setTriggerKeywords(List<String> triggerKeywords)
    {
        this.triggerKeywords = triggerKeywords;
    }

    public Long getEntrySkillId()
    {
        return entrySkillId;
    }

    public void setEntrySkillId(Long entrySkillId)
    {
        this.entrySkillId = entrySkillId;
    }

    public Map<String, Object> getOutputSchema()
    {
        return outputSchema;
    }

    public void setOutputSchema(Map<String, Object> outputSchema)
    {
        this.outputSchema = outputSchema;
    }

    public Boolean getEnabled()
    {
        return enabled;
    }

    public void setEnabled(Boolean enabled)
    {
        this.enabled = enabled;
    }

    public Integer getVersion()
    {
        return version;
    }

    public void setVersion(Integer version)
    {
        this.version = version;
    }

    public List<AiPlaybookStepDto> getSteps()
    {
        return steps;
    }

    public void setSteps(List<AiPlaybookStepDto> steps)
    {
        this.steps = steps;
    }
}
