package com.ruoyi.crm.domain.dto.datatable;

/**
 * JSONB 条件查询 DTO
 */
public class DataTableConditionDTO
{
    private String field;
    private Object value;
    private String operator;
    private String dataType;

    public String getField()
    {
        return field;
    }

    public void setField(String field)
    {
        this.field = field;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

    public String getOperator()
    {
        return operator;
    }

    public void setOperator(String operator)
    {
        this.operator = operator;
    }

    public String getDataType()
    {
        return dataType;
    }

    public void setDataType(String dataType)
    {
        this.dataType = dataType;
    }
}
