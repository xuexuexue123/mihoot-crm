package com.ruoyi.crm.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 跟进序列配置 crm_follow_up_cadence
 */
public class CrmFollowUpCadence extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String cadenceId;
    private String tenantId;
    private String name;
    private String description;
    private String enabled;
    private String startTrigger;
    private String stepsJson;
    private String delFlag;

    public String getCadenceId()
    {
        return cadenceId;
    }

    public void setCadenceId(String cadenceId)
    {
        this.cadenceId = cadenceId;
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

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }
}
