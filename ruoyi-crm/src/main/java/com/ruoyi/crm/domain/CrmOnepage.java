package com.ruoyi.crm.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 一页对象 crm_onepage
 * 
 * @author luohd
 * @date 2024-08-15
 */
public class CrmOnepage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String pageId;

    /** 类型 */
    @Excel(name = "类型")
    private String pageType;

    /** 关联ID */
    @Excel(name = "关联ID")
    private String relateId;

    /** 关联名称 */
    @Excel(name = "关联名称")
    private String relateName;

    /** 内容 */
    @Excel(name = "内容")
    private String pageContent;

    public void setPageId(String pageId) 
    {
        this.pageId = pageId;
    }

    public String getPageId() 
    {
        return pageId;
    }
    public void setPageType(String pageType) 
    {
        this.pageType = pageType;
    }

    public String getPageType() 
    {
        return pageType;
    }
    public void setRelateId(String relateId) 
    {
        this.relateId = relateId;
    }

    public String getRelateId() 
    {
        return relateId;
    }
    public void setRelateName(String relateName) 
    {
        this.relateName = relateName;
    }

    public String getRelateName() 
    {
        return relateName;
    }
    public void setPageContent(String pageContent) 
    {
        this.pageContent = pageContent;
    }

    public String getPageContent() 
    {
        return pageContent;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("pageId", getPageId())
            .append("pageType", getPageType())
            .append("relateId", getRelateId())
            .append("relateName", getRelateName())
            .append("pageContent", getPageContent())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
