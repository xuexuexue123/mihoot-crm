package com.ruoyi.crm.domain.vo;

import java.util.ArrayList;
import java.util.List;
import com.ruoyi.crm.domain.CrmProductCategory;

/**
 * 产品分类树节点
 */
public class ProductCategoryTreeVo extends CrmProductCategory
{
    private static final long serialVersionUID = 1L;

    private List<ProductCategoryTreeVo> children = new ArrayList<>();

    public List<ProductCategoryTreeVo> getChildren()
    {
        return children;
    }

    public void setChildren(List<ProductCategoryTreeVo> children)
    {
        this.children = children;
    }
}
