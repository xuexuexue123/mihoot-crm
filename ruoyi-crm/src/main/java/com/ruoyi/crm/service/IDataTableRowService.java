package com.ruoyi.crm.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.crm.domain.CrmDataTableColumn;
import com.ruoyi.crm.domain.dto.datatable.DataTableRowDTO;

public interface IDataTableRowService
{
    List<DataTableRowDTO> selectRowsPage(String tenantId, String tableId);

    DataTableRowDTO insertRow(String tenantId, String tableId, Map<String, Object> data);

    List<DataTableRowDTO> insertRows(String tenantId, String tableId, List<Map<String, Object>> rows);

    DataTableRowDTO updateRow(String tenantId, String tableId, String rowId, Map<String, Object> data);

    void deleteRow(String tenantId, String tableId, String rowId);

    DataTableRowDTO toRowDto(com.ruoyi.crm.domain.CrmDataTableRow row);

    List<CrmDataTableColumn> getColumns(String tenantId, String tableId);
}
