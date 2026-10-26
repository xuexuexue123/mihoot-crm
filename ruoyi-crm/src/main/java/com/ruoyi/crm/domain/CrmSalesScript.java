package com.ruoyi.crm.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 话术库对象 crm_sales_script
 */
public class CrmSalesScript extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 UUID */
    private String scriptId;

    /** marketing / qa / sop */
    private String scriptType;

    /** 自由分类名 */
    private String category;

    /** 标题；Q&A 即问题 */
    private String title;

    /** 正文；Q&A 即答案 */
    private String content;

    /** 可选关联企微素材 id */
    private Long materialId;

    /** 排序权重，越大越靠前 */
    private Integer sortOrder;

    /** 0 启用 / 1 停用 */
    private String status;

    /** 逻辑删除 0 正常 / 1 已删 */
    private String delFlag;

    /** 列表查询关键字（非表字段）：匹配 title + content */
    private String keyword;

    public String getScriptId()
    {
        return scriptId;
    }

    public void setScriptId(String scriptId)
    {
        this.scriptId = scriptId;
    }

    public String getScriptType()
    {
        return scriptType;
    }

    public void setScriptType(String scriptType)
    {
        this.scriptType = scriptType;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public Long getMaterialId()
    {
        return materialId;
    }

    public void setMaterialId(Long materialId)
    {
        this.materialId = materialId;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getKeyword()
    {
        return keyword;
    }

    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("scriptId", getScriptId())
            .append("scriptType", getScriptType())
            .append("category", getCategory())
            .append("title", getTitle())
            .append("content", getContent())
            .append("materialId", getMaterialId())
            .append("sortOrder", getSortOrder())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
