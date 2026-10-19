package com.ruoyi.crm.wechatmp.audience.vo;

import java.util.List;

public class FanAudienceFieldMetaVo
{
    private String field;
    private String label;
    private String type;
    private List<String> operators;

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
}
