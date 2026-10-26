package com.ruoyi.crm.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 回访规则 crm_followup_rule
 */
public class CrmFollowupRule extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String ruleId;
    private String tenantId;
    private String name;
    private String description;
    private String enabled;
    private String isSystem;
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
    private String delFlag;

    public String getRuleId()
    {
        return ruleId;
    }

    public void setRuleId(String ruleId)
    {
        this.ruleId = ruleId;
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

    public String getEnabled()
    {
        return enabled;
    }

    public void setEnabled(String enabled)
    {
        this.enabled = enabled;
    }

    public String getIsSystem()
    {
        return isSystem;
    }

    public void setIsSystem(String isSystem)
    {
        this.isSystem = isSystem;
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

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }
}
