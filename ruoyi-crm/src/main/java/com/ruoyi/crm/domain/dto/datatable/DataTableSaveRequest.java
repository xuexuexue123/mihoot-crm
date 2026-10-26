package com.ruoyi.crm.domain.dto.datatable;

import java.util.List;

/**
 * 创建/更新 Data Table 请求
 */
public class DataTableSaveRequest
{
    private String id;
    private String name;
    private String description;
    private List<DataTableColumnDTO> columns;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public List<DataTableColumnDTO> getColumns()
    {
        return columns;
    }

    public void setColumns(List<DataTableColumnDTO> columns)
    {
        this.columns = columns;
    }
}
