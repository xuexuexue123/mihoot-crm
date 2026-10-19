package com.ruoyi.crm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 企微客户群提醒规则
 */
public class CrmWecomGroupReminderRule extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String ruleId;
    private String tenantId;
    private String name;
    private String description;
    private String enabled;
    private String isSystem;
    private Integer priority;
    private String anchorType;
    private Integer offsetValue;
    private String offsetUnit;
    private String sendTime;
    private String templateBody;
    private String matchJson;
    private String requireActiveGroup;
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

    public String getAnchorType()
    {
        return anchorType;
    }

    public void setAnchorType(String anchorType)
    {
        this.anchorType = anchorType;
    }

    public Integer getOffsetValue()
    {
        return offsetValue;
    }

    public void setOffsetValue(Integer offsetValue)
    {
        this.offsetValue = offsetValue;
    }

    public String getOffsetUnit()
    {
        return offsetUnit;
    }

    public void setOffsetUnit(String offsetUnit)
    {
        this.offsetUnit = offsetUnit;
    }

    public String getSendTime()
    {
        return sendTime;
    }

    public void setSendTime(String sendTime)
    {
        this.sendTime = sendTime;
    }

    public String getTemplateBody()
    {
        return templateBody;
    }

    public void setTemplateBody(String templateBody)
    {
        this.templateBody = templateBody;
    }

    public String getMatchJson()
    {
        return matchJson;
    }

    public void setMatchJson(String matchJson)
    {
        this.matchJson = matchJson;
    }

    public String getRequireActiveGroup()
    {
        return requireActiveGroup;
    }

    public void setRequireActiveGroup(String requireActiveGroup)
    {
        this.requireActiveGroup = requireActiveGroup;
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
