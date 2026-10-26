package com.ruoyi.crm.domain.dto;

/**
 * 邮件 AI 分析实体项
 */
public class EmailAnalysisEntityItemDto
{
    private String category;
    private String value;
    private String subLabel;

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getSubLabel()
    {
        return subLabel;
    }

    public void setSubLabel(String subLabel)
    {
        this.subLabel = subLabel;
    }
}
