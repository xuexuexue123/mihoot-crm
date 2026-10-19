package com.ruoyi.ems.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 项目模版对象 sys_re_template
 * 
 * @author luohd
 * @date 2023-05-21
 */
public class SysReTemplate extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编码 */
    private String templateId;

    /** 业务Id */
    @Excel(name = "业务Id")
    private String caseApplicationId;

    /** 名称 */
    @Excel(name = "名称")
    private String templateName;

    /** 项目类型 */
    @Excel(name = "项目类型")
    private String category;

    /** 描述 */
    @Excel(name = "描述")
    private String content;

    /** 是否归档 */
    @Excel(name = "是否归档")
    private Long archived;

    /** 模版所有人 */
    @Excel(name = "模版所有人")
    private String owner;

    /** 设计 */
    @Excel(name = "设计")
    private String designContent;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    /** 颜色 */
    @Excel(name = "颜色")
    private String color;

    /** 次数 */
    @Excel(name = "次数")
    private String cookieName;

    public void setTemplateId(String templateId) 
    {
        this.templateId = templateId;
    }

    public String getTemplateId() 
    {
        return templateId;
    }
    public void setCaseApplicationId(String caseApplicationId) 
    {
        this.caseApplicationId = caseApplicationId;
    }

    public String getCaseApplicationId() 
    {
        return caseApplicationId;
    }
    public void setTemplateName(String templateName) 
    {
        this.templateName = templateName;
    }

    public String getTemplateName() 
    {
        return templateName;
    }
    public void setCategory(String category) 
    {
        this.category = category;
    }

    public String getCategory() 
    {
        return category;
    }
    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }
    public void setArchived(Long archived) 
    {
        this.archived = archived;
    }

    public Long getArchived() 
    {
        return archived;
    }
    public void setOwner(String owner) 
    {
        this.owner = owner;
    }

    public String getOwner() 
    {
        return owner;
    }

    public void setDesignContent(String designContent)
    {
        this.designContent = designContent;
    }

    public String getDesignContent()
    {
        return designContent;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }
    public void setColor(String color)
    {
        this.color = color;
    }

    public String getColor()
    {
        return color;
    }

    public void setCookieName(String cookieName)
    {
        this.cookieName = cookieName;
    }

    public String getCookieName()
    {
        return cookieName;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("templateId", getTemplateId())
            .append("caseApplicationId", getCaseApplicationId())
            .append("templateName", getTemplateName())
            .append("category", getCategory())
            .append("content", getContent())
            .append("archived", getArchived())
            .append("owner", getOwner())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("description", getDescription())
            .append("color", getColor())
            .toString();
    }
}
