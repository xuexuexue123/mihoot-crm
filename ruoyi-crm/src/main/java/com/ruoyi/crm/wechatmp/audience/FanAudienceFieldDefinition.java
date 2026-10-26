package com.ruoyi.crm.wechatmp.audience;

import java.util.List;

public class FanAudienceFieldDefinition
{
    private final String field;
    private final String label;
    private final String type;
    private final List<String> operators;

    public FanAudienceFieldDefinition(String field, String label, String type, List<String> operators)
    {
        this.field = field;
        this.label = label;
        this.type = type;
        this.operators = operators;
    }

    public String getField()
    {
        return field;
    }

    public String getLabel()
    {
        return label;
    }

    public String getType()
    {
        return type;
    }

    public List<String> getOperators()
    {
        return operators;
    }
}
