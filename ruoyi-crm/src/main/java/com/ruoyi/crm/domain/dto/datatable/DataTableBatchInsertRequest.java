package com.ruoyi.crm.domain.dto.datatable;

import java.util.List;

/**
 * 批量插入行请求
 */
public class DataTableBatchInsertRequest
{
    private List<DataTableRowDTO> rows;

    public List<DataTableRowDTO> getRows()
    {
        return rows;
    }

    public void setRows(List<DataTableRowDTO> rows)
    {
        this.rows = rows;
    }
}
