package com.ruoyi.crm.domain.dto.datatable;

import java.util.List;

/**
 * 行条件查询参数（Mapper 动态 SQL）
 */
public class DataTableRowQuery
{
    private String dataTableId;
    private List<DataTableConditionDTO> conditions;
    private String combineWith;
    private Integer limit;

    public String getDataTableId()
    {
        return dataTableId;
    }

    public void setDataTableId(String dataTableId)
    {
        this.dataTableId = dataTableId;
    }

    public List<DataTableConditionDTO> getConditions()
    {
        return conditions;
    }

    public void setConditions(List<DataTableConditionDTO> conditions)
    {
        this.conditions = conditions;
    }

    public String getCombineWith()
    {
        return combineWith;
    }

    public void setCombineWith(String combineWith)
    {
        this.combineWith = combineWith;
    }

    public Integer getLimit()
    {
        return limit;
    }

    public void setLimit(Integer limit)
    {
        this.limit = limit;
    }
}
