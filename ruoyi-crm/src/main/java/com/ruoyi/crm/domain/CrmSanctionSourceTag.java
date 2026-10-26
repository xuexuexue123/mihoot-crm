package com.ruoyi.crm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 制裁源标签 crm_sanction_source_tag
 */
public class CrmSanctionSourceTag extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String id;

    private String tagCode;
    private String tagName;
    private String authority;
    private String description;

    private String delFlag;

    /** 列表查询关键字（非表字段） */
    @JsonIgnore
    private String searchQuery;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getTagCode()
    {
        return tagCode;
    }

    public void setTagCode(String tagCode)
    {
        this.tagCode = tagCode;
    }

    public String getTagName()
    {
        return tagName;
    }

    public void setTagName(String tagName)
    {
        this.tagName = tagName;
    }

    public String getAuthority()
    {
        return authority;
    }

    public void setAuthority(String authority)
    {
        this.authority = authority;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getSearchQuery()
    {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery)
    {
        this.searchQuery = searchQuery;
    }
}
