package com.ruoyi.crm.domain.vo;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Playbook 列表项 VO
 */
public class AiPlaybookListVo
{
    private Long id;
    private String name;
    private String description;
    private List<String> triggerKeywords;
    private Long entrySkillId;
    private Map<String, Object> outputSchema;
    private Boolean enabled;
    private Integer version;
    private Integer stepCount;
    private List<String> trackKeys;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date updatedAt;

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

    public Integer getStepCount()
    {
        return stepCount;
    }

    public void setStepCount(Integer stepCount)
    {
        this.stepCount = stepCount;
    }

    public List<String> getTrackKeys()
    {
        return trackKeys;
    }

    public void setTrackKeys(List<String> trackKeys)
    {
        this.trackKeys = trackKeys;
    }

    public Date getUpdatedAt()
    {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt)
    {
        this.updatedAt = updatedAt;
    }
}
