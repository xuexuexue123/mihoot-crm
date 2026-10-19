package com.ruoyi.crm.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * Subject 继承映射对象 crm_subject_map
 *
 * @author luohd
 * @date 2026-06-10
 */
public class CrmSubjectMap extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 父主体类型（如 Contact） */
    private String parentSubjectType;

    /** 父主体 ID */
    private String parentSubjectId;

    /** 子主体类型（如 Lead） */
    private String childSubjectType;

    /** 子主体 ID */
    private String childSubjectId;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getParentSubjectType()
    {
        return parentSubjectType;
    }

    public void setParentSubjectType(String parentSubjectType)
    {
        this.parentSubjectType = parentSubjectType;
    }

    public String getParentSubjectId()
    {
        return parentSubjectId;
    }

    public void setParentSubjectId(String parentSubjectId)
    {
        this.parentSubjectId = parentSubjectId;
    }

    public String getChildSubjectType()
    {
        return childSubjectType;
    }

    public void setChildSubjectType(String childSubjectType)
    {
        this.childSubjectType = childSubjectType;
    }

    public String getChildSubjectId()
    {
        return childSubjectId;
    }

    public void setChildSubjectId(String childSubjectId)
    {
        this.childSubjectId = childSubjectId;
    }

    @Override
    public String toString()
    {
        return "CrmSubjectMap{" +
                "id=" + id +
                ", parentSubjectType='" + parentSubjectType + '\'' +
                ", parentSubjectId='" + parentSubjectId + '\'' +
                ", childSubjectType='" + childSubjectType + '\'' +
                ", childSubjectId='" + childSubjectId + '\'' +
                '}';
    }
}
