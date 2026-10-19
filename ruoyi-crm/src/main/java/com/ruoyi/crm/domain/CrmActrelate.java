package com.ruoyi.crm.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 关联的活动对象 crm_actrelate
 * 
 * @author luohd
 * @date 2024-01-22
 */
public class CrmActrelate extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String relateId;

    /** 活动编号 */
    @Excel(name = "活动编号")
    private String activityId;

    /** 角色 */
    @Excel(name = "角色")
    private String relateRole;

    /** 关联对象 */
    @Excel(name = "关联对象")
    private String relateTarget;

    /** 关联类型 */
    @Excel(name = "关联类型")
    private String targetType;

    /** 关联目标ID */
    @Excel(name = "关联目标ID")
    private String targetId;

    public void setRelateId(String relateId) 
    {
        this.relateId = relateId;
    }

    public String getRelateId() 
    {
        return relateId;
    }
    public void setActivityId(String activityId) 
    {
        this.activityId = activityId;
    }

    public String getActivityId() 
    {
        return activityId;
    }
    public void setRelateRole(String relateRole) 
    {
        this.relateRole = relateRole;
    }

    public String getRelateRole() 
    {
        return relateRole;
    }
    public void setRelateTarget(String relateTarget) 
    {
        this.relateTarget = relateTarget;
    }

    public String getRelateTarget() 
    {
        return relateTarget;
    }
    public void setTargetType(String targetType) 
    {
        this.targetType = targetType;
    }

    public String getTargetType() 
    {
        return targetType;
    }
    public void setTargetId(String targetId) 
    {
        this.targetId = targetId;
    }

    public String getTargetId() 
    {
        return targetId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("relateId", getRelateId())
            .append("activityId", getActivityId())
            .append("relateRole", getRelateRole())
            .append("relateTarget", getRelateTarget())
            .append("targetType", getTargetType())
            .append("targetId", getTargetId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
