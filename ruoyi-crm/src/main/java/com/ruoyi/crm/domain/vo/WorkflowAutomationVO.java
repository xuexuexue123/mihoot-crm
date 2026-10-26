package com.ruoyi.crm.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 工作流自动化配置列表/详情 VO
 */
public class WorkflowAutomationVO
{
    private String configId;
    private String automationKey;
    private String workflowId;
    private String workflowName;
    private String enabled;
    private String triggerEvent;
    private String configJson;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public String getConfigId()
    {
        return configId;
    }

    public void setConfigId(String configId)
    {
        this.configId = configId;
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

    public String getWorkflowName()
    {
        return workflowName;
    }

    public void setWorkflowName(String workflowName)
    {
        this.workflowName = workflowName;
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

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }
}
