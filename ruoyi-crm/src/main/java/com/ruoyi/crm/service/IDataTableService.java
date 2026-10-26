package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.dto.datatable.DataTableSaveRequest;
import com.ruoyi.crm.domain.vo.DataTableVO;

public interface IDataTableService
{
    List<DataTableVO> listTables(String tenantId);

    DataTableVO getTable(String tenantId, String tableId);

    DataTableVO createTable(String tenantId, DataTableSaveRequest request);

    DataTableVO updateTable(String tenantId, DataTableSaveRequest request);

    void deleteTable(String tenantId, String tableId);

    void updateColumn(String tenantId, String tableId, String columnId,
            com.ruoyi.crm.domain.dto.datatable.DataTableColumnUpdateRequest request);

    void deleteColumn(String tenantId, String tableId, String columnId);

    void deleteAllColumns(String tenantId, String tableId);

    void emptyTable(String tenantId, String tableId);
}
