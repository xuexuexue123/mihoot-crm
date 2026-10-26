package com.ruoyi.crm.domain.dto;

/**
 * 分类移动请求
 */
public class ProductCategoryMoveRequest
{
    private String parentId;
    private Integer sortOrder;

    public String getParentId()
    {
        return parentId;
    }

    public void setParentId(String parentId)
    {
        this.parentId = parentId;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }
}
