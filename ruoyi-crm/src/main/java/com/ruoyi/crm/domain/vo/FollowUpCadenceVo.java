package com.ruoyi.crm.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 跟进序列列表/详情 VO
 */
public class FollowUpCadenceVo
{
    private String cadenceId;
    private String name;
    private String description;
    private String enabled;
    private String startTrigger;
    private String stepsJson;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

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

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }
}
