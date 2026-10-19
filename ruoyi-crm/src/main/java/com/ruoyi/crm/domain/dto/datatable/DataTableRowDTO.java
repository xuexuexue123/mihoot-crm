package com.ruoyi.crm.domain.dto.datatable;

import java.util.Map;

/**
 * Data Table 行 DTO
 */
public class DataTableRowDTO
{
    private String id;
    private Map<String, Object> data;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public Map<String, Object> getData()
    {
        return data;
    }

    public void setData(Map<String, Object> data)
    {
        this.data = data;
    }
}
