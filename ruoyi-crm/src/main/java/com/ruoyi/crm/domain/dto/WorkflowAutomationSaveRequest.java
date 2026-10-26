package com.ruoyi.crm.domain.dto;

/**
 * 工作流自动化配置 Upsert 请求体
 */
public class WorkflowAutomationSaveRequest
{
    private String automationKey;
    private String workflowId;
    private String enabled;
    private String triggerEvent;
    private String configJson;
    private String remark;

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

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }
}
