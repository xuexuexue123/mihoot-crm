package com.ruoyi.crm.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 工作流自动化配置 crm_workflow_automation
 */
public class CrmWorkflowAutomation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String configId;
    private String tenantId;
    private String automationKey;
    private String workflowId;
    private String enabled;
    private String triggerEvent;
    private String configJson;
    private String delFlag;

    public String getConfigId()
    {
        return configId;
    }

    public void setConfigId(String configId)
    {
        this.configId = configId;
    }

    public String getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(String tenantId)
    {
        this.tenantId = tenantId;
    }

    public String getAutomationKey()
    {
        return automationKey;
    }

    public void setAutomationKey(String automationKey)
    {
        this.automationKey = automationKey;
    }

    public String getWorkflowId()
    {
        return workflowId;
    }

    public void setWorkflowId(String workflowId)
    {
        this.workflowId = workflowId;
    }

    public String getEnabled()
    {
        return enabled;
    }

    public void setEnabled(String enabled)
    {
        this.enabled = enabled;
    }

    public String getTriggerEvent()
    {
        return triggerEvent;
    }

    public void setTriggerEvent(String triggerEvent)
    {
        this.triggerEvent = triggerEvent;
    }

    public String getConfigJson()
    {
        return configJson;
    }

    public void setConfigJson(String configJson)
    {
        this.configJson = configJson;
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
