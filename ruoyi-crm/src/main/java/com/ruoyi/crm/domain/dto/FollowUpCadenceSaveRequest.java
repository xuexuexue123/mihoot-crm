package com.ruoyi.crm.domain.dto;

/**
 * 跟进序列创建/更新请求体
 */
public class FollowUpCadenceSaveRequest
{
    private String cadenceId;
    private String name;
    private String description;
    private String enabled;
    private String startTrigger;
    private String stepsJson;
    private String remark;

    public String getCadenceId()
    {
        return cadenceId;
    }

    public void setCadenceId(String cadenceId)
    {
        this.cadenceId = cadenceId;
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

    public String getStartTrigger()
    {
        return startTrigger;
    }

    public void setStartTrigger(String startTrigger)
    {
        this.startTrigger = startTrigger;
    }

    public String getStepsJson()
    {
        return stepsJson;
    }

    public void setStepsJson(String stepsJson)
    {
        this.stepsJson = stepsJson;
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
