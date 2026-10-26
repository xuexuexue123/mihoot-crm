package com.ruoyi.crm.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmDataTableColumn;
import com.ruoyi.crm.domain.CrmDataTableRow;
import com.ruoyi.crm.domain.dto.datatable.DataTableConditionDTO;
import com.ruoyi.crm.domain.dto.datatable.DataTableRowDTO;
import com.ruoyi.crm.domain.dto.datatable.DataTableRowQuery;
import com.ruoyi.crm.domain.dto.datatable.DataTableRuntimeRequest;
import com.ruoyi.crm.mapper.CrmDataTableColumnMapper;
import com.ruoyi.crm.mapper.CrmDataTableMapper;
import com.ruoyi.crm.mapper.CrmDataTableRowMapper;
import com.ruoyi.crm.service.DataTableConditionService;
import com.ruoyi.crm.service.DataTableValidationService;
import com.ruoyi.crm.service.IDataTableOperationService;
import com.ruoyi.crm.service.support.DataTableConstants;
import com.ruoyi.crm.service.support.DataTableTenantSupport;

@Service
public class DataTableOperationServiceImpl implements IDataTableOperationService
{
    @Autowired
    private CrmDataTableMapper crmDataTableMapper;

    @Autowired
    private CrmDataTableColumnMapper crmDataTableColumnMapper;

    @Autowired
    private CrmDataTableRowMapper crmDataTableRowMapper;

    @Autowired
    private DataTableValidationService dataTableValidationService;

    @Autowired
    private DataTableConditionService dataTableConditionService;

    @Autowired
    private DataTableRowServiceImpl dataTableRowService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> runtimeInsert(String tenantId, DataTableRuntimeRequest request)
    {
        String tableId = requireTableId(request);
        DataTableTenantSupport.requireTable(crmDataTableMapper, tableId, tenantId);
        DataTableRowDTO row = dataTableRowService.insertRow(tenantId, tableId, request.getData());
        return buildInsertResponse(row);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> runtimeUpdate(String tenantId, DataTableRuntimeRequest request)
    {
        String tableId = requireTableId(request);
        DataTableTenantSupport.requireTable(crmDataTableMapper, tableId, tenantId);
        List<CrmDataTableColumn> columns = crmDataTableColumnMapper.selectByDataTableId(tableId);
        DataTableRowQuery query = dataTableConditionService.buildQuery(tableId, request.getConditions(),
                request.getCombineWith(), null, columns);
        List<CrmDataTableRow> matched = crmDataTableRowMapper.selectByConditions(query);
        int updated = 0;
        for (CrmDataTableRow row : matched)
        {
            dataTableRowService.updateRowData(tenantId, tableId, row, request.getData());
            updated++;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("updated", updated);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> runtimeUpsert(String tenantId, DataTableRuntimeRequest request)
    {
        String tableId = requireTableId(request);
        DataTableTenantSupport.requireTable(crmDataTableMapper, tableId, tenantId);
        List<String> keyFields = request.getKeyFields();
        if (keyFields == null || keyFields.isEmpty())
        {
            throw new ServiceException("upsert 需要 keyFields");
        }
        List<CrmDataTableColumn> columns = crmDataTableColumnMapper.selectByDataTableId(tableId);
        List<DataTableConditionDTO> conditions = buildKeyConditions(keyFields, request.getData());
        DataTableRowQuery query = dataTableConditionService.buildQuery(tableId, conditions,
                DataTableConstants.COMBINE_AND, 1, columns);
        List<CrmDataTableRow> matched = crmDataTableRowMapper.selectByConditions(query);

        if (matched.isEmpty())
        {
            DataTableRowDTO inserted = dataTableRowService.insertRow(tenantId, tableId, request.getData());
            Map<String, Object> result = buildInsertResponse(inserted);
            result.put("action", "insert");
            return result;
        }

        CrmDataTableRow existing = matched.get(0);
        Map<String, Object> merged = parseData(existing.getData());
        if (request.getData() != null)
        {
            merged.putAll(request.getData());
        }
        dataTableRowService.updateRowData(tenantId, tableId, existing, merged);
        DataTableRowDTO dto = dataTableRowService.toRowDto(existing);
        Map<String, Object> result = buildInsertResponse(dto);
        result.put("action", "update");
        return result;
    }

    @Override
    public Map<String, Object> runtimeGet(String tenantId, DataTableRuntimeRequest request)
    {
        String tableId = requireTableId(request);
        DataTableTenantSupport.requireTable(crmDataTableMapper, tableId, tenantId);
        List<CrmDataTableColumn> columns = crmDataTableColumnMapper.selectByDataTableId(tableId);
        DataTableRowQuery query = dataTableConditionService.buildQuery(tableId, request.getConditions(),
                request.getCombineWith(), 1, columns);
        List<CrmDataTableRow> matched = crmDataTableRowMapper.selectByConditions(query);
        if (matched.isEmpty())
        {
            return null;
        }
        return dataTableRowService.toRowDto(matched.get(0)).getData();
    }

    @Override
    public List<Map<String, Object>> runtimeList(String tenantId, DataTableRuntimeRequest request)
    {
        String tableId = requireTableId(request);
        DataTableTenantSupport.requireTable(crmDataTableMapper, tableId, tenantId);
        List<CrmDataTableColumn> columns = crmDataTableColumnMapper.selectByDataTableId(tableId);
        int limit = dataTableConditionService.normalizeLimit(request.getLimit());
        DataTableRowQuery query = dataTableConditionService.buildQuery(tableId, request.getConditions(),
                request.getCombineWith(), limit, columns);
        List<CrmDataTableRow> matched = crmDataTableRowMapper.selectByConditions(query);
        List<Map<String, Object>> rows = new ArrayList<>();
        for (CrmDataTableRow row : matched)
        {
            rows.add(dataTableRowService.toRowDto(row).getData());
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> runtimeDelete(String tenantId, DataTableRuntimeRequest request)
    {
        String tableId = requireTableId(request);
        DataTableTenantSupport.requireTable(crmDataTableMapper, tableId, tenantId);
        List<CrmDataTableColumn> columns = crmDataTableColumnMapper.selectByDataTableId(tableId);
        DataTableRowQuery query = dataTableConditionService.buildQuery(tableId, request.getConditions(),
                request.getCombineWith(), null, columns);
        List<CrmDataTableRow> matched = crmDataTableRowMapper.selectByConditions(query);
        int deleted = 0;
        for (CrmDataTableRow row : matched)
        {
            crmDataTableRowMapper.deleteById(row.getId());
            deleted++;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("deleted", deleted);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> runtimeEnsureColumns(String tenantId, DataTableRuntimeRequest request)
    {
        String tableId = requireTableId(request);
        DataTableTenantSupport.requireTable(crmDataTableMapper, tableId, tenantId);
        Map<String, Object> data = request.getData();
        if (data == null || data.isEmpty())
        {
            throw new ServiceException("data 不能为空");
        }
        List<CrmDataTableColumn> columns = crmDataTableColumnMapper.selectByDataTableId(tableId);
        Map<String, CrmDataTableColumn> byName = new HashMap<>();
        int maxOrder = 0;
        for (CrmDataTableColumn column : columns)
        {
            byName.put(column.getName(), column);
            if (column.getSortOrder() != null && column.getSortOrder() > maxOrder)
            {
                maxOrder = column.getSortOrder();
            }
        }

        List<CrmDataTableColumn> toInsert = new ArrayList<>();
        for (Map.Entry<String, Object> entry : flattenObject(data).entrySet())
        {
            String key = entry.getKey();
            if (DataTableConstants.isSystemColumn(key) || byName.containsKey(key))
            {
                continue;
            }
            CrmDataTableColumn column = new CrmDataTableColumn();
            column.setId(IdUtils.fastSimpleUUID());
            column.setDataTableId(tableId);
            column.setName(key);
            column.setType(dataTableValidationService.inferColumnType(entry.getValue()));
            column.setRequired(false);
            column.setSortOrder(++maxOrder);
            column.setIsSystem(false);
            toInsert.add(column);
            byName.put(key, column);
        }
        if (!toInsert.isEmpty())
        {
            crmDataTableColumnMapper.batchInsert(toInsert);
        }
        return runtimeInsert(tenantId, request);
    }

    private String requireTableId(DataTableRuntimeRequest request)
    {
        if (request == null || StringUtils.isBlank(request.getTableId()))
        {
            throw new ServiceException("tableId 不能为空");
        }
        return request.getTableId();
    }

    private List<DataTableConditionDTO> buildKeyConditions(List<String> keyFields, Map<String, Object> data)
    {
        List<DataTableConditionDTO> conditions = new ArrayList<>();
        if (data == null)
        {
            throw new ServiceException("upsert 需要 data");
        }
        for (String field : keyFields)
        {
            if (!data.containsKey(field))
            {
                throw new ServiceException("keyFields 缺少值: " + field);
            }
            DataTableConditionDTO condition = new DataTableConditionDTO();
            condition.setField(field);
            condition.setOperator("equals");
            condition.setValue(data.get(field));
            conditions.add(condition);
        }
        return conditions;
    }

    private Map<String, Object> buildInsertResponse(DataTableRowDTO row)
    {
        Map<String, Object> data = row.getData();
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("id", row.getId());
        result.put("createdAt", data != null ? data.get(DataTableConstants.COL_CREATED_AT) : null);
        result.put("updatedAt", data != null ? data.get(DataTableConstants.COL_UPDATED_AT) : null);
        return result;
    }

    private Map<String, Object> flattenObject(Map<String, Object> data)
    {
        Map<String, Object> flat = new HashMap<>();
        flattenInto(flat, "", data);
        return flat;
    }

    @SuppressWarnings("unchecked")
    private void flattenInto(Map<String, Object> flat, String prefix, Object value)
    {
        if (value instanceof Map)
        {
            Map<String, Object> map = (Map<String, Object>) value;
            for (Map.Entry<String, Object> entry : map.entrySet())
            {
                String key = StringUtils.isBlank(prefix) ? entry.getKey() : prefix + "." + entry.getKey();
                flattenInto(flat, key, entry.getValue());
            }
        }
        else
        {
            flat.put(prefix, value);
        }
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
