package com.ruoyi.crm.domain.vo;

import java.util.List;

/**
 * 分群字段元数据（驱动前端规则构建器）
 */
public class SegmentFieldMetaVo
{
    private String source;
    private String field;
    private String label;
    private String type;
    private List<String> operators;
    private List<SegmentEnumValueVo> enumValues;

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public String getField()
    {
        return field;
    }

    public void setField(String field)
    {
        this.field = field;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public List<String> getOperators()
    {
        return operators;
    }

    public void setOperators(List<String> operators)
    {
        this.operators = operators;
    }

    public List<SegmentEnumValueVo> getEnumValues()
    {
        return enumValues;
    }

    public void setEnumValues(List<SegmentEnumValueVo> enumValues)
    {
        this.enumValues = enumValues;
    }
}
