package com.ruoyi.crm.domain.dto;

import java.util.List;
import java.util.Map;

/**
 * AI Skill Upsert 请求体
 */
public class AiSkillUpsertDto
{
    private Long id;
    private String name;
    private String description;
    private String instruction;
    private List<String> triggerKeywords;
    private List<String> triggerPatterns;
    private String scope;
    private Integer priority;
    private Boolean enabled;
    private String skillType;
    private String domain;
    private String trackKey;
    private List<Map<String, Object>> toolBindings;
    private List<String> inputContext;
    private String outputKey;
    private Map<String, Object> outputSchema;
    private List<Long> mcpServerIds;

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

    public String getInstruction()
    {
        return instruction;
    }

    public void setInstruction(String instruction)
    {
        this.instruction = instruction;
    }

    public List<String> getTriggerKeywords()
    {
        return triggerKeywords;
    }

    public void setTriggerKeywords(List<String> triggerKeywords)
    {
        this.triggerKeywords = triggerKeywords;
    }

    public List<String> getTriggerPatterns()
    {
        return triggerPatterns;
    }

    public void setTriggerPatterns(List<String> triggerPatterns)
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

    public Boolean getEnabled()
    {
        return enabled;
    }

    public void setEnabled(Boolean enabled)
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

    public List<Map<String, Object>> getToolBindings()
    {
        return toolBindings;
    }

    public void setToolBindings(List<Map<String, Object>> toolBindings)
    {
        this.toolBindings = toolBindings;
    }

    public List<String> getInputContext()
    {
        return inputContext;
    }

    public void setInputContext(List<String> inputContext)
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

    public Map<String, Object> getOutputSchema()
    {
        return outputSchema;
    }

    public void setOutputSchema(Map<String, Object> outputSchema)
    {
        this.outputSchema = outputSchema;
    }

    public List<Long> getMcpServerIds()
    {
        return mcpServerIds;
    }

    public void setMcpServerIds(List<Long> mcpServerIds)
    {
        this.mcpServerIds = mcpServerIds;
    }
}
