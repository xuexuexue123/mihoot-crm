package com.ruoyi.crm.domain.dto.datatable;

import java.util.List;
import java.util.Map;

/**
 * 工作流 Runtime 请求体
 */
public class DataTableRuntimeRequest
{
    private String tableId;
    private Map<String, Object> data;
    private List<DataTableConditionDTO> conditions;
    private String combineWith;
    private List<String> keyFields;
    private Integer limit;

    public String getTableId()
    {
        return tableId;
    }

    public void setTableId(String tableId)
    {
        this.tableId = tableId;
    }

    public Map<String, Object> getData()
    {
        return data;
    }

    public void setData(Map<String, Object> data)
    {
        this.data = data;
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

    public List<String> getKeyFields()
    {
        return keyFields;
    }

    public void setKeyFields(List<String> keyFields)
    {
        this.keyFields = keyFields;
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
