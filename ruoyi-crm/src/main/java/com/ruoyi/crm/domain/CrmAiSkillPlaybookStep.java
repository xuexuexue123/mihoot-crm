package com.ruoyi.crm.domain;

/**
 * Playbook 步骤 crm_ai_skill_playbook_step
 */
public class CrmAiSkillPlaybookStep
{
    private Long id;
    private Long playbookId;
    private String stepKey;
    private Long skillId;
    private String stepType;
    private String parallelGroup;
    private String dependsOn;
    private String trackKey;
    private Integer sortOrder;
    private Integer enabled;
    private String skillName;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getPlaybookId()
    {
        return playbookId;
    }

    public void setPlaybookId(Long playbookId)
    {
        this.playbookId = playbookId;
    }

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

    public String getDependsOn()
    {
        return dependsOn;
    }

    public void setDependsOn(String dependsOn)
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

    public Integer getEnabled()
    {
        return enabled;
    }

    public void setEnabled(Integer enabled)
    {
        this.enabled = enabled;
    }

    public String getSkillName()
    {
        return skillName;
    }

    public void setSkillName(String skillName)
    {
        this.skillName = skillName;
    }
}
