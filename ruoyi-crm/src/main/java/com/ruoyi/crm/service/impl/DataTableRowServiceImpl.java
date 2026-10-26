package com.ruoyi.crm.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmDataTableColumn;
import com.ruoyi.crm.domain.CrmDataTableRow;
import com.ruoyi.crm.domain.dto.datatable.DataTableRowDTO;
import com.ruoyi.crm.mapper.CrmDataTableColumnMapper;
import com.ruoyi.crm.mapper.CrmDataTableMapper;
import com.ruoyi.crm.mapper.CrmDataTableRowMapper;
import com.ruoyi.crm.service.DataTableValidationService;
import com.ruoyi.crm.service.IDataTableRowService;
import com.ruoyi.crm.service.support.DataTableConstants;
import com.ruoyi.crm.service.support.DataTableTenantSupport;

@Service
public class DataTableRowServiceImpl implements IDataTableRowService
{
    @Autowired
    private CrmDataTableMapper crmDataTableMapper;

    @Autowired
    private CrmDataTableColumnMapper crmDataTableColumnMapper;

    @Autowired
    private CrmDataTableRowMapper crmDataTableRowMapper;

    @Autowired
    private DataTableValidationService dataTableValidationService;

    @Override
    public List<DataTableRowDTO> selectRowsPage(String tenantId, String tableId)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        DataTableTenantSupport.requireTable(crmDataTableMapper, tableId, tenantId);
        List<CrmDataTableRow> rows = crmDataTableRowMapper.selectPageByDataTableId(tableId);
        List<DataTableRowDTO> result = new ArrayList<>();
        for (CrmDataTableRow row : rows)
        {
            result.add(toRowDto(row));
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DataTableRowDTO insertRow(String tenantId, String tableId, Map<String, Object> data)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        DataTableTenantSupport.requireTable(crmDataTableMapper, tableId, tenantId);
        List<CrmDataTableColumn> columns = crmDataTableColumnMapper.selectByDataTableId(tableId);
        Map<String, Object> validated = dataTableValidationService.validateData(columns, data, true);
        return persistRow(tableId, validated);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<DataTableRowDTO> insertRows(String tenantId, String tableId, List<Map<String, Object>> rows)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        DataTableTenantSupport.requireTable(crmDataTableMapper, tableId, tenantId);
        List<CrmDataTableColumn> columns = crmDataTableColumnMapper.selectByDataTableId(tableId);
        List<DataTableRowDTO> result = new ArrayList<>();
        if (rows == null)
        {
            return result;
        }
        for (Map<String, Object> rowData : rows)
        {
            Map<String, Object> validated = dataTableValidationService.validateData(columns, rowData, true);
            result.add(persistRow(tableId, validated));
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DataTableRowDTO updateRow(String tenantId, String tableId, String rowId, Map<String, Object> data)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        DataTableTenantSupport.requireTable(crmDataTableMapper, tableId, tenantId);
        CrmDataTableRow row = crmDataTableRowMapper.selectByIdAndDataTableId(rowId, tableId);
        if (row == null)
        {
            throw new ServiceException("数据行不存在");
        }
        updateRowData(tenantId, tableId, row, data);
        return toRowDto(row);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRow(String tenantId, String tableId, String rowId)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        DataTableTenantSupport.requireTable(crmDataTableMapper, tableId, tenantId);
        CrmDataTableRow row = crmDataTableRowMapper.selectByIdAndDataTableId(rowId, tableId);
        if (row == null)
        {
            return;
        }
        crmDataTableRowMapper.deleteById(rowId);
    }

    @Override
    public DataTableRowDTO toRowDto(CrmDataTableRow row)
    {
        DataTableRowDTO dto = new DataTableRowDTO();
        dto.setId(row.getId());
        dto.setData(parseData(row.getData()));
        return dto;
    }

    @Override
    public List<CrmDataTableColumn> getColumns(String tenantId, String tableId)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        DataTableTenantSupport.requireTable(crmDataTableMapper, tableId, tenantId);
        return crmDataTableColumnMapper.selectByDataTableId(tableId);
    }

    @Transactional(rollbackFor = Exception.class)
    public CrmDataTableRow updateRowData(String tenantId, String tableId, CrmDataTableRow row,
            Map<String, Object> patch)
    {
        DataTableTenantSupport.requireTable(crmDataTableMapper, tableId, tenantId);
        List<CrmDataTableColumn> columns = crmDataTableColumnMapper.selectByDataTableId(tableId);
        Map<String, Object> existing = parseData(row.getData());
        if (patch != null)
        {
            existing.putAll(patch);
        }
        Map<String, Object> validated = dataTableValidationService.validateData(columns, existing, false);
        row.setData(JSON.toJSONString(validated));
        row.setUpdateTime(DateUtils.getNowDate());
        crmDataTableRowMapper.update(row);
        return row;
    }

    private DataTableRowDTO persistRow(String tableId, Map<String, Object> validated)
    {
        String rowId = String.valueOf(validated.get(DataTableConstants.COL_ID));
        Date now = DateUtils.getNowDate();
        CrmDataTableRow row = new CrmDataTableRow();
        row.setId(rowId);
        row.setDataTableId(tableId);
        row.setData(JSON.toJSONString(validated));
        row.setCreateTime(now);
        row.setUpdateTime(now);
        crmDataTableRowMapper.insert(row);
        return toRowDto(row);
    }

    private Map<String, Object> parseData(String json)
    {
        if (StringUtils.isBlank(json))
        {
            return new HashMap<>();
        }
        return JSON.parseObject(json, new TypeReference<Map<String, Object>>()
        {
        });
    }
}
