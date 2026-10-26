package com.ruoyi.crm.domain.vo;

import java.util.List;

/**
 * Playbook 步骤 VO
 */
public class AiPlaybookStepVo
{
    private String stepKey;
    private Long skillId;
    private String skillName;
    private String stepType;
    private String parallelGroup;
    private List<String> dependsOn;
    private String trackKey;
    private Integer sortOrder;
    private Boolean enabled;

    public String getStepKey()
    {
        return stepKey;
    }

    public void setStepKey(String stepKey)
    {
        this.stepKey = stepKey;
    }

    public Long getSkillId()
    {
        return skillId;
    }

    public void setSkillId(Long skillId)
    {
        this.skillId = skillId;
    }

    public String getSkillName()
    {
        return skillName;
    }

    public void setSkillName(String skillName)
    {
        this.skillName = skillName;
    }

    public String getStepType()
    {
        return stepType;
    }

    public void setStepType(String stepType)
    {
        this.stepType = stepType;
    }

    public String getParallelGroup()
    {
        return parallelGroup;
    }

    public void setParallelGroup(String parallelGroup)
    {
        this.parallelGroup = parallelGroup;
    }

    public List<String> getDependsOn()
    {
        return dependsOn;
    }

    public void setDependsOn(List<String> dependsOn)
    {
        this.dependsOn = dependsOn;
    }

    public String getTrackKey()
    {
        return trackKey;
    }

    public void setTrackKey(String trackKey)
    {
        this.trackKey = trackKey;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public Boolean getEnabled()
    {
        return enabled;
    }

    public void setEnabled(Boolean enabled)
    {
        this.enabled = enabled;
    }
}
