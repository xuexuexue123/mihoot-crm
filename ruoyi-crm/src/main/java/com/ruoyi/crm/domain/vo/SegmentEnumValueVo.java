package com.ruoyi.crm.domain.vo;

/**
 * 分群枚举字段可选值
 */
public class SegmentEnumValueVo
{
    private String value;
    private String label;

    public SegmentEnumValueVo()
    {
    }

    public SegmentEnumValueVo(String value, String label)
    {
        this.value = value;
        this.label = label;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }
}
