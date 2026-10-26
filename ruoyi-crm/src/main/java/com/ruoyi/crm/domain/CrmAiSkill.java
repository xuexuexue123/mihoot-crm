package com.ruoyi.crm.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 租户 AI Skill crm_ai_skill
 */
public class CrmAiSkill extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String tenantId;
    private String name;
    private String description;
    private String instruction;
    private String triggerKeywords;
    private String triggerPatterns;
    private String scope;
    private Integer priority;
    private Integer enabled;
    private String skillType;
    private String domain;
    private String trackKey;
    private String toolBindings;
    private String inputContext;
    private String outputKey;
    private String outputSchema;
    private String mcpServerIds;

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

    public String getInstruction()
    {
        return instruction;
    }

    public void setInstruction(String instruction)
    {
        this.instruction = instruction;
    }

    public String getTriggerKeywords()
    {
        return triggerKeywords;
    }

    public void setTriggerKeywords(String triggerKeywords)
    {
        this.triggerKeywords = triggerKeywords;
    }

    public String getTriggerPatterns()
    {
        return triggerPatterns;
    }

    public void setTriggerPatterns(String triggerPatterns)
    {
        this.triggerPatterns = triggerPatterns;
    }

    public String getScope()
    {
        return scope;
    }

    public void setScope(String scope)
    {
        this.scope = scope;
    }

    public Integer getPriority()
    {
        return priority;
    }

    public void setPriority(Integer priority)
    {
        this.priority = priority;
    }

    public Integer getEnabled()
    {
        return enabled;
    }

    public void setEnabled(Integer enabled)
    {
        this.enabled = enabled;
    }

    public String getSkillType()
    {
        return skillType;
    }

    public void setSkillType(String skillType)
    {
        this.skillType = skillType;
    }

    public String getDomain()
    {
        return domain;
    }

    public void setDomain(String domain)
    {
        this.domain = domain;
    }

    public String getTrackKey()
    {
        return trackKey;
    }

    public void setTrackKey(String trackKey)
    {
        this.trackKey = trackKey;
    }

    public String getToolBindings()
    {
        return toolBindings;
    }

    public void setToolBindings(String toolBindings)
    {
        this.toolBindings = toolBindings;
    }

    public String getInputContext()
    {
        return inputContext;
    }

    public void setInputContext(String inputContext)
    {
        this.inputContext = inputContext;
    }

    public String getOutputKey()
    {
        return outputKey;
    }

    public void setOutputKey(String outputKey)
    {
        this.outputKey = outputKey;
    }

    public String getOutputSchema()
    {
        return outputSchema;
    }

    public void setOutputSchema(String outputSchema)
    {
        this.outputSchema = outputSchema;
    }

    public String getMcpServerIds()
    {
        return mcpServerIds;
    }

    public void setMcpServerIds(String mcpServerIds)
    {
        this.mcpServerIds = mcpServerIds;
    }
}
