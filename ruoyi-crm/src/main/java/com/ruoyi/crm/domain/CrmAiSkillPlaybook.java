package com.ruoyi.crm.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * AI Skill Playbook crm_ai_skill_playbook
 */
public class CrmAiSkillPlaybook extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String tenantId;
    private String name;
    private String description;
    private String triggerKeywords;
    private Long entrySkillId;
    private String outputSchema;
    private Integer enabled;
    private Integer version;

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

    public String getTriggerKeywords()
    {
        return triggerKeywords;
    }

    public void setTriggerKeywords(String triggerKeywords)
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

    public String getOutputSchema()
    {
        return outputSchema;
    }

    public void setOutputSchema(String outputSchema)
    {
        this.outputSchema = outputSchema;
    }

    public Integer getEnabled()
    {
        return enabled;
    }

    public void setEnabled(Integer enabled)
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
}
