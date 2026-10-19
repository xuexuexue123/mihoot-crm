package com.ruoyi.crm.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 文档关系对象 crm_docrelation
 * 
 * @author luohd
 * @date 2024-02-02
 */
public class CrmDocrelation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String relateId;

    /** 关联对象id */
    @Excel(name = "关联对象id")
    private String sourceId;

    /** 关联对象名 */
    @Excel(name = "关联对象名")
    private String sourceName;

    /** 关联角色(文档类型) */
    @Excel(name = "关联角色(文档类型)")
    private String relateRole;

    /** 关联目标ID */
    @Excel(name = "关联目标ID")
    private String targetId;

    /** 关联类型(联系人、客户、组织) */
    @Excel(name = "关联类型(联系人、客户、组织)")
    private String targetType;

    /** 对象名称 */
    @Excel(name = "对象名称")
    private String targetName;

    /** 关系描述 */
    @Excel(name = "关系描述")
    private String relationDesc;

    public void setRelateId(String relateId) 
    {
        this.relateId = relateId;
    }

    public String getRelateId() 
    {
        return relateId;
    }
    public void setSourceId(String sourceId) 
    {
        this.sourceId = sourceId;
    }

    public String getSourceId() 
    {
        return sourceId;
    }
    public void setSourceName(String sourceName) 
    {
        this.sourceName = sourceName;
    }

    public String getSourceName() 
    {
        return sourceName;
    }
    public void setRelateRole(String relateRole) 
    {
        this.relateRole = relateRole;
    }

    public String getRelateRole() 
    {
        return relateRole;
    }
    public void setTargetId(String targetId) 
    {
        this.targetId = targetId;
    }

    public String getTargetId() 
    {
        return targetId;
    }
    public void setTargetType(String targetType) 
    {
        this.targetType = targetType;
    }

    public String getTargetType() 
    {
        return targetType;
    }
    public void setTargetName(String targetName) 
    {
        this.targetName = targetName;
    }

    public String getTargetName() 
    {
        return targetName;
    }
    public void setRelationDesc(String relationDesc) 
    {
        this.relationDesc = relationDesc;
    }

    public String getRelationDesc() 
    {
        return relationDesc;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("relateId", getRelateId())
            .append("sourceId", getSourceId())
            .append("sourceName", getSourceName())
            .append("relateRole", getRelateRole())
            .append("targetId", getTargetId())
            .append("targetType", getTargetType())
            .append("targetName", getTargetName())
            .append("relationDesc", getRelationDesc())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
