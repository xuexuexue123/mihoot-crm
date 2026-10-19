package com.ruoyi.crm.domain.dto;

/**
 * 回访规则创建/更新请求体
 */
public class FollowupRuleSaveRequest
{
    private String ruleId;
    private String name;
    private String description;
    private String enabled;
    private Integer priority;
    private String triggerType;
    private String triggerConfig;
    private String delayMode;
    private Integer delayValue;
    private String delayUnit;
    private String delayField;
    private String taskTemplate;
    private String dedupeConfig;
    private String workflowId;
    private String catalogKey;

    public String getRuleId()
    {
        return ruleId;
    }

    public void setRuleId(String ruleId)
    {
        this.ruleId = ruleId;
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

    public String getEnabled()
    {
        return enabled;
    }

    public void setEnabled(String enabled)
    {
        this.enabled = enabled;
    }

    public Integer getPriority()
    {
        return priority;
    }

    public void setPriority(Integer priority)
    {
        this.priority = priority;
    }

    public String getTriggerType()
    {
        return triggerType;
    }

    public void setTriggerType(String triggerType)
    {
        this.triggerType = triggerType;
    }

    public String getTriggerConfig()
    {
        return triggerConfig;
    }

    public void setTriggerConfig(String triggerConfig)
    {
        this.triggerConfig = triggerConfig;
    }

    public String getDelayMode()
    {
        return delayMode;
    }

    public void setDelayMode(String delayMode)
    {
        this.delayMode = delayMode;
    }

    public Integer getDelayValue()
    {
        return delayValue;
    }

    public void setDelayValue(Integer delayValue)
    {
        this.delayValue = delayValue;
    }

    public String getDelayUnit()
    {
        return delayUnit;
    }

    public void setDelayUnit(String delayUnit)
    {
        this.delayUnit = delayUnit;
    }

    public String getDelayField()
    {
        return delayField;
    }

    public void setDelayField(String delayField)
    {
        this.delayField = delayField;
    }

    public String getTaskTemplate()
    {
        return taskTemplate;
    }

    public void setTaskTemplate(String taskTemplate)
    {
        this.taskTemplate = taskTemplate;
    }

    public String getDedupeConfig()
    {
        return dedupeConfig;
    }

    public void setDedupeConfig(String dedupeConfig)
    {
        this.dedupeConfig = dedupeConfig;
    }

    public String getWorkflowId()
    {
        return workflowId;
    }

    public void setWorkflowId(String workflowId)
    {
        this.workflowId = workflowId;
    }

    public String getCatalogKey()
    {
        return catalogKey;
    }

    public void setCatalogKey(String catalogKey)
    {
        this.catalogKey = catalogKey;
    }
}
