package com.ruoyi.web.controller.crm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.dto.datatable.DataTableBatchInsertRequest;
import com.ruoyi.crm.domain.dto.datatable.DataTableColumnUpdateRequest;
import com.ruoyi.crm.domain.dto.datatable.DataTableRowDTO;
import com.ruoyi.crm.domain.dto.datatable.DataTableRuntimeRequest;
import com.ruoyi.crm.domain.dto.datatable.DataTableSaveRequest;
import com.ruoyi.crm.domain.vo.DataTableVO;
import com.ruoyi.crm.service.IDataTableOperationService;
import com.ruoyi.crm.service.IDataTableRowService;
import com.ruoyi.crm.service.IDataTableService;
import com.ruoyi.crm.service.support.DataTableTenantSupport;

@RestController
@RequestMapping("/crm/data-table")
public class CrmDataTableController extends BaseController
{
    @Autowired
    private IDataTableService dataTableService;

    @Autowired
    private IDataTableRowService dataTableRowService;

    @Autowired
    private IDataTableOperationService dataTableOperationService;

    @PreAuthorize("@ss.hasPermi('crm:datatable:view')")
    @GetMapping("/list")
    public AjaxResult list(@RequestHeader(value = "tenant", required = false) String tenant)
    {
        List<DataTableVO> list = dataTableService.listTables(DataTableTenantSupport.requireTenantId(tenant));
        return success(list);
    }

    @PreAuthorize("@ss.hasPermi('crm:datatable:view')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        return success(dataTableService.getTable(DataTableTenantSupport.requireTenantId(tenant), id));
    }

    @PreAuthorize("@ss.hasPermi('crm:datatable:edit')")
    @Log(title = "Data Table", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult create(@RequestBody DataTableSaveRequest request,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        return success(dataTableService.createTable(DataTableTenantSupport.requireTenantId(tenant), request));
    }

    @PreAuthorize("@ss.hasPermi('crm:datatable:edit')")
    @Log(title = "Data Table", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult update(@RequestBody DataTableSaveRequest request,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        return success(dataTableService.updateTable(DataTableTenantSupport.requireTenantId(tenant), request));
    }

    @PreAuthorize("@ss.hasPermi('crm:datatable:edit')")
    @Log(title = "Data Table", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable("id") String id,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        dataTableService.deleteTable(DataTableTenantSupport.requireTenantId(tenant), id);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:datatable:view')")
    @GetMapping("/{id}/rows")
    public TableDataInfo listRows(@PathVariable("id") String id,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        startPage();
        List<DataTableRowDTO> rows = dataTableRowService.selectRowsPage(
                DataTableTenantSupport.requireTenantId(tenant), id);
        return getDataTable(rows);
    }

    @PreAuthorize("@ss.hasPermi('crm:datatable:edit')")
    @Log(title = "Data Table Row", businessType = BusinessType.INSERT)
    @PostMapping("/{id}/rows")
    public AjaxResult insertRow(@PathVariable("id") String id, @RequestBody DataTableRowDTO request,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        Map<String, Object> data = request != null ? request.getData() : null;
        return success(dataTableRowService.insertRow(DataTableTenantSupport.requireTenantId(tenant), id, data));
    }

    @PreAuthorize("@ss.hasPermi('crm:datatable:edit')")
    @Log(title = "Data Table Rows", businessType = BusinessType.INSERT)
    @PostMapping("/{id}/rows/batch")
    public AjaxResult insertRows(@PathVariable("id") String id, @RequestBody DataTableBatchInsertRequest request,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        List<Map<String, Object>> payload = new ArrayList<>();
        if (request != null && request.getRows() != null)
        {
            for (DataTableRowDTO row : request.getRows())
            {
                if (row != null && row.getData() != null)
                {
                    payload.add(row.getData());
                }
            }
        }
        return success(dataTableRowService.insertRows(DataTableTenantSupport.requireTenantId(tenant), id, payload));
    }

    @PreAuthorize("@ss.hasPermi('crm:datatable:edit')")
    @Log(title = "Data Table Row", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/rows/{rowId}")
    public AjaxResult updateRow(@PathVariable("id") String id, @PathVariable("rowId") String rowId,
            @RequestBody DataTableRowDTO request,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        Map<String, Object> data = request != null ? request.getData() : null;
        return success(dataTableRowService.updateRow(
                DataTableTenantSupport.requireTenantId(tenant), id, rowId, data));
    }

    @PreAuthorize("@ss.hasPermi('crm:datatable:edit')")
    @Log(title = "Data Table Row", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}/rows/{rowId}")
    public AjaxResult deleteRow(@PathVariable("id") String id, @PathVariable("rowId") String rowId,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        dataTableRowService.deleteRow(DataTableTenantSupport.requireTenantId(tenant), id, rowId);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:datatable:edit')")
    @Log(title = "Data Table Column", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/columns/{columnId}")
    public AjaxResult updateColumn(@PathVariable("id") String id, @PathVariable("columnId") String columnId,
            @RequestBody DataTableColumnUpdateRequest request,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        dataTableService.updateColumn(DataTableTenantSupport.requireTenantId(tenant), id, columnId, request);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:datatable:edit')")
    @Log(title = "Data Table Column", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}/columns/{columnId}")
    public AjaxResult deleteColumn(@PathVariable("id") String id, @PathVariable("columnId") String columnId,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        dataTableService.deleteColumn(DataTableTenantSupport.requireTenantId(tenant), id, columnId);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:datatable:edit')")
    @Log(title = "Data Table", businessType = BusinessType.CLEAN)
    @PostMapping("/{id}/empty")
    public AjaxResult emptyTable(@PathVariable("id") String id,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        dataTableService.emptyTable(DataTableTenantSupport.requireTenantId(tenant), id);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:datatable:edit')")
    @Log(title = "Data Table Columns", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}/columns")
    public AjaxResult deleteAllColumns(@PathVariable("id") String id,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        dataTableService.deleteAllColumns(DataTableTenantSupport.requireTenantId(tenant), id);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:datatable:execute')")
    @PostMapping("/runtime/insert")
    public AjaxResult runtimeInsert(@RequestBody DataTableRuntimeRequest request,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        return success(dataTableOperationService.runtimeInsert(DataTableTenantSupport.requireTenantId(tenant), request));
    }

    @PreAuthorize("@ss.hasPermi('crm:datatable:execute')")
    @PostMapping("/runtime/update")
    public AjaxResult runtimeUpdate(@RequestBody DataTableRuntimeRequest request,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        return success(dataTableOperationService.runtimeUpdate(DataTableTenantSupport.requireTenantId(tenant), request));
    }

    @PreAuthorize("@ss.hasPermi('crm:datatable:execute')")
    @PostMapping("/runtime/upsert")
    public AjaxResult runtimeUpsert(@RequestBody DataTableRuntimeRequest request,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        return success(dataTableOperationService.runtimeUpsert(DataTableTenantSupport.requireTenantId(tenant), request));
    }

    @PreAuthorize("@ss.hasPermi('crm:datatable:execute')")
    @PostMapping("/runtime/get")
    public AjaxResult runtimeGet(@RequestBody DataTableRuntimeRequest request,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        return success(dataTableOperationService.runtimeGet(DataTableTenantSupport.requireTenantId(tenant), request));
    }

    @PreAuthorize("@ss.hasPermi('crm:datatable:execute')")
    @PostMapping("/runtime/list")
    public AjaxResult runtimeList(@RequestBody DataTableRuntimeRequest request,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        return success(dataTableOperationService.runtimeList(DataTableTenantSupport.requireTenantId(tenant), request));
    }

    @PreAuthorize("@ss.hasPermi('crm:datatable:execute')")
    @PostMapping("/runtime/delete")
    public AjaxResult runtimeDelete(@RequestBody DataTableRuntimeRequest request,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        return success(dataTableOperationService.runtimeDelete(DataTableTenantSupport.requireTenantId(tenant), request));
    }

    @PreAuthorize("@ss.hasPermi('crm:datatable:execute')")
    @PostMapping("/runtime/ensure-columns")
    public AjaxResult runtimeEnsureColumns(@RequestBody DataTableRuntimeRequest request,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        return success(dataTableOperationService.runtimeEnsureColumns(
                DataTableTenantSupport.requireTenantId(tenant), request));
    }
}
