package com.ruoyi.crm.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmDataTable;
import com.ruoyi.crm.domain.CrmDataTableColumn;
import com.ruoyi.crm.domain.CrmDataTableRow;
import com.ruoyi.crm.domain.dto.datatable.DataTableColumnDTO;
import com.ruoyi.crm.domain.dto.datatable.DataTableSaveRequest;
import com.ruoyi.crm.domain.vo.DataTableVO;
import com.ruoyi.crm.mapper.CrmDataTableColumnMapper;
import com.ruoyi.crm.mapper.CrmDataTableMapper;
import com.ruoyi.crm.mapper.CrmDataTableRowMapper;
import com.ruoyi.crm.service.IDataTableService;
import com.ruoyi.crm.service.support.DataTableConstants;
import com.ruoyi.crm.service.support.DataTableTenantSupport;

@Service
public class DataTableServiceImpl implements IDataTableService
{
    @Autowired
    private CrmDataTableMapper crmDataTableMapper;

    @Autowired
    private CrmDataTableColumnMapper crmDataTableColumnMapper;

    @Autowired
    private CrmDataTableRowMapper crmDataTableRowMapper;

    @Override
    public List<DataTableVO> listTables(String tenantId)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        List<DataTableVO> list = crmDataTableMapper.selectListByTenant(tenantId);
        for (DataTableVO vo : list)
        {
            vo.setColumns(toColumnDtos(crmDataTableColumnMapper.selectByDataTableId(vo.getId())));
        }
        return list;
    }

    @Override
    public DataTableVO getTable(String tenantId, String tableId)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        CrmDataTable table = DataTableTenantSupport.requireTable(crmDataTableMapper, tableId, tenantId);
        return toVo(table);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DataTableVO createTable(String tenantId, DataTableSaveRequest request)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        validateSaveRequest(request, true);
        if (crmDataTableMapper.countByTenantAndName(tenantId, request.getName()) > 0)
        {
            throw new ServiceException("表名已存在: " + request.getName());
        }

        String username = SecurityUtils.getUsername();
        Date now = DateUtils.getNowDate();
        CrmDataTable table = new CrmDataTable();
        table.setId(IdUtils.fastSimpleUUID());
        table.setTenantId(tenantId);
        table.setName(request.getName().trim());
        table.setDescription(request.getDescription());
        table.setCreateBy(username);
        table.setCreateTime(now);
        table.setUpdateTime(now);
        crmDataTableMapper.insert(table);

        List<CrmDataTableColumn> columns = buildColumns(table.getId(), request.getColumns(), true);
        crmDataTableColumnMapper.batchInsert(columns);
        return toVo(table);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DataTableVO updateTable(String tenantId, DataTableSaveRequest request)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        validateSaveRequest(request, false);
        if (StringUtils.isBlank(request.getId()))
        {
            throw new ServiceException("id 不能为空");
        }
        CrmDataTable table = DataTableTenantSupport.requireTable(crmDataTableMapper, request.getId(), tenantId);
        if (crmDataTableMapper.countByTenantAndNameExcludeId(tenantId, request.getName(), request.getId()) > 0)
        {
            throw new ServiceException("表名已存在: " + request.getName());
        }

        table.setName(request.getName().trim());
        table.setDescription(request.getDescription());
        table.setUpdateTime(DateUtils.getNowDate());
        crmDataTableMapper.update(table);

        crmDataTableColumnMapper.deleteNonSystemByDataTableId(table.getId());
        List<CrmDataTableColumn> columns = buildColumns(table.getId(), request.getColumns(), false);
        if (!columns.isEmpty())
        {
            crmDataTableColumnMapper.batchInsert(columns);
        }
        return toVo(table);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTable(String tenantId, String tableId)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        DataTableTenantSupport.requireTable(crmDataTableMapper, tableId, tenantId);
        crmDataTableMapper.deleteByIdAndTenant(tableId, tenantId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateColumn(String tenantId, String tableId, String columnId,
            com.ruoyi.crm.domain.dto.datatable.DataTableColumnUpdateRequest request)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        DataTableTenantSupport.requireTable(crmDataTableMapper, tableId, tenantId);
        if (request == null)
        {
            throw new ServiceException("请提供 name、type、options 或列元数据");
        }
        String newName = request.getName();
        // Do not rely on *Present flags — Jackson/Fastjson may set fields without calling setters.
        boolean updateType = StringUtils.isNotBlank(request.getType());
        boolean updateOptions = request.isOptionsPresent() || request.getOptions() != null;
        boolean updateUnit = request.isUnitPresent();
        boolean updateFormat = request.isFormatPresent();
        boolean updateSection = request.isSectionPresent() || request.getSection() != null;
        boolean hasName = StringUtils.isNotBlank(newName);
        if (!hasName && !updateType && !updateOptions && !updateUnit && !updateFormat && !updateSection)
        {
            throw new ServiceException("请提供 name、type、options 或列元数据");
        }
        CrmDataTableColumn column = crmDataTableColumnMapper.selectById(columnId);
        if (column == null || !tableId.equals(column.getDataTableId()))
        {
            throw new ServiceException("列不存在");
        }
        if (Boolean.TRUE.equals(column.getIsSystem()))
        {
            throw new ServiceException("不能修改系统列");
        }

        boolean dirty = false;
        if (hasName)
        {
            String trimmed = newName.trim();
            if (!trimmed.equals(column.getName()))
            {
                CrmDataTableColumn existing = crmDataTableColumnMapper.selectByDataTableIdAndName(tableId, trimmed);
                if (existing != null)
                {
                    throw new ServiceException("列名已存在: " + trimmed);
                }
                String oldName = column.getName();
                column.setName(trimmed);
                dirty = true;

                List<CrmDataTableRow> rows = crmDataTableRowMapper.selectByDataTableId(tableId);
                for (CrmDataTableRow row : rows)
                {
                    Map<String, Object> data = parseData(row.getData());
                    if (data.containsKey(oldName))
                    {
                        Object value = data.remove(oldName);
                        data.put(trimmed, value);
                        row.setData(JSON.toJSONString(data));
                        row.setUpdateTime(DateUtils.getNowDate());
                        crmDataTableRowMapper.update(row);
                    }
                }
            }
        }

        if (updateType)
        {
            column.setType(normalizeType(request.getType()));
            dirty = true;
        }

        // When type is in the payload (edit dialog always sends it), sync related meta together.
        if (updateType)
        {
            String nextType = column.getType();
            if (DataTableConstants.TYPE_SELECT.equals(nextType)
                    || DataTableConstants.TYPE_MULTI_SELECT.equals(nextType))
            {
                List<String> normalized = normalizeOptions(request.getOptions());
                column.setOptions(normalized == null ? null : JSON.toJSONString(normalized));
            }
            else
            {
                column.setOptions(null);
            }
            if (DataTableConstants.TYPE_NUMBER.equals(nextType))
            {
                column.setUnit(blankToNull(request.getUnit()));
                column.setFormat(normalizeFormat(request.getFormat()));
            }
            else
            {
                column.setUnit(null);
                column.setFormat(null);
            }
            column.setSection(blankToNull(request.getSection()));
            dirty = true;
        }
        else
        {
            if (updateOptions)
            {
                String colType = column.getType();
                if (DataTableConstants.TYPE_SELECT.equals(colType)
                        || DataTableConstants.TYPE_MULTI_SELECT.equals(colType)
                        || DataTableConstants.TYPE_STRING.equals(colType))
                {
                    List<String> normalized = normalizeOptions(request.getOptions());
                    column.setOptions(normalized == null ? null : JSON.toJSONString(normalized));
                }
                else
                {
                    column.setOptions(null);
                }
                dirty = true;
            }
            if (updateUnit)
            {
                column.setUnit(
                        DataTableConstants.TYPE_NUMBER.equals(column.getType())
                                ? blankToNull(request.getUnit())
                                : null);
                dirty = true;
            }
            if (updateFormat)
            {
                column.setFormat(
                        DataTableConstants.TYPE_NUMBER.equals(column.getType())
                                ? normalizeFormat(request.getFormat())
                                : null);
                dirty = true;
            }
            if (updateSection)
            {
                column.setSection(blankToNull(request.getSection()));
                dirty = true;
            }
        }

        if (dirty)
        {
            crmDataTableColumnMapper.update(column);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteColumn(String tenantId, String tableId, String columnId)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        DataTableTenantSupport.requireTable(crmDataTableMapper, tableId, tenantId);
        CrmDataTableColumn column = crmDataTableColumnMapper.selectById(columnId);
        if (column == null || !tableId.equals(column.getDataTableId()))
        {
            throw new ServiceException("列不存在");
        }
        if (Boolean.TRUE.equals(column.getIsSystem()))
        {
            throw new ServiceException("不能删除系统列");
        }
        int nonSystemCount = crmDataTableColumnMapper.countNonSystemByDataTableId(tableId);
        if (nonSystemCount <= 1)
        {
            throw new ServiceException("至少保留一列非系统列");
        }
        crmDataTableColumnMapper.deleteById(columnId);

        List<CrmDataTableRow> rows = crmDataTableRowMapper.selectByDataTableId(tableId);
        for (CrmDataTableRow row : rows)
        {
            Map<String, Object> data = parseData(row.getData());
            if (data.remove(column.getName()) != null)
            {
                row.setData(JSON.toJSONString(data));
                row.setUpdateTime(DateUtils.getNowDate());
                crmDataTableRowMapper.update(row);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllColumns(String tenantId, String tableId)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        DataTableTenantSupport.requireTable(crmDataTableMapper, tableId, tenantId);
        if (crmDataTableRowMapper.countByDataTableId(tableId) > 0)
        {
            throw new ServiceException("表中有数据，不能删除所有列");
        }
        crmDataTableColumnMapper.deleteNonSystemByDataTableId(tableId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void emptyTable(String tenantId, String tableId)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        DataTableTenantSupport.requireTable(crmDataTableMapper, tableId, tenantId);
        crmDataTableRowMapper.deleteByDataTableId(tableId);
    }

    private void validateSaveRequest(DataTableSaveRequest request, boolean isCreate)
    {
        if (request == null || StringUtils.isBlank(request.getName()))
        {
            throw new ServiceException("表名不能为空");
        }
        if (!isCreate && request.getColumns() == null)
        {
            throw new ServiceException("columns 不能为空");
        }
    }

    private List<CrmDataTableColumn> buildColumns(String tableId, List<DataTableColumnDTO> userColumns,
            boolean includeSystem)
    {
        List<CrmDataTableColumn> columns = new ArrayList<>();
        int order = 0;
        if (includeSystem)
        {
            columns.add(systemColumn(tableId, DataTableConstants.COL_ID, DataTableConstants.TYPE_STRING, order++));
            columns.add(systemColumn(tableId, DataTableConstants.COL_CREATED_AT, DataTableConstants.TYPE_DATE, order++));
            columns.add(systemColumn(tableId, DataTableConstants.COL_UPDATED_AT, DataTableConstants.TYPE_DATE, order++));
        }
        if (userColumns != null)
        {
            for (DataTableColumnDTO dto : userColumns)
            {
                if (dto == null || StringUtils.isBlank(dto.getName()))
                {
                    continue;
                }
                if (DataTableConstants.isSystemColumn(dto.getName()))
                {
                    throw new ServiceException("不能自定义系统列: " + dto.getName());
                }
                CrmDataTableColumn column = new CrmDataTableColumn();
                column.setId(IdUtils.fastSimpleUUID());
                column.setDataTableId(tableId);
                column.setName(dto.getName().trim());
                column.setType(normalizeType(dto.getType()));
                column.setRequired(Boolean.TRUE.equals(dto.getRequired()));
                column.setDefaultValue(dto.getDefaultValue());
                column.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : order++);
                column.setIsSystem(false);
                List<String> normalizedOptions = normalizeOptions(dto.getOptions());
                column.setOptions(normalizedOptions == null ? null : JSON.toJSONString(normalizedOptions));
                column.setUnit(blankToNull(dto.getUnit()));
                column.setFormat(normalizeFormat(dto.getFormat()));
                column.setSection(blankToNull(dto.getSection()));
                columns.add(column);
            }
        }
        return columns;
    }

    private CrmDataTableColumn systemColumn(String tableId, String name, String type, int sortOrder)
    {
        CrmDataTableColumn column = new CrmDataTableColumn();
        column.setId(IdUtils.fastSimpleUUID());
        column.setDataTableId(tableId);
        column.setName(name);
        column.setType(type);
        column.setRequired(false);
        column.setSortOrder(sortOrder);
        column.setIsSystem(true);
        return column;
    }

    private String normalizeType(String type)
    {
        if (StringUtils.isBlank(type))
        {
            return DataTableConstants.TYPE_STRING;
        }
        String upper = type.toUpperCase();
        switch (upper)
        {
            case DataTableConstants.TYPE_NUMBER:
            case DataTableConstants.TYPE_BOOLEAN:
            case DataTableConstants.TYPE_DATE:
            case DataTableConstants.TYPE_JSON:
            case DataTableConstants.TYPE_MULTI_SELECT:
            case DataTableConstants.TYPE_SELECT:
            case DataTableConstants.TYPE_URL:
                return upper;
            default:
                return DataTableConstants.TYPE_STRING;
        }
    }

    private String blankToNull(String value)
    {
        if (StringUtils.isBlank(value))
        {
            return null;
        }
        return value.trim();
    }

    private String normalizeFormat(String format)
    {
        if (StringUtils.isBlank(format))
        {
            return null;
        }
        String lower = format.trim().toLowerCase();
        if ("percent".equals(lower) || "decimal".equals(lower))
        {
            return lower;
        }
        return null;
    }

    private DataTableVO toVo(CrmDataTable table)
    {
        DataTableVO vo = new DataTableVO();
        vo.setId(table.getId());
        vo.setName(table.getName());
        vo.setDescription(table.getDescription());
        vo.setCreateBy(table.getCreateBy());
        vo.setCreateTime(table.getCreateTime());
        vo.setUpdateTime(table.getUpdateTime());
        vo.setRowCount(crmDataTableRowMapper.countByDataTableId(table.getId()));
        vo.setColumns(toColumnDtos(crmDataTableColumnMapper.selectByDataTableId(table.getId())));
        return vo;
    }

    private List<DataTableColumnDTO> toColumnDtos(List<CrmDataTableColumn> columns)
    {
        List<DataTableColumnDTO> dtos = new ArrayList<>();
        for (CrmDataTableColumn column : columns)
        {
            DataTableColumnDTO dto = new DataTableColumnDTO();
            dto.setId(column.getId());
            dto.setName(column.getName());
            dto.setType(column.getType());
            dto.setRequired(column.getRequired());
            dto.setDefaultValue(column.getDefaultValue());
            dto.setSortOrder(column.getSortOrder());
            dto.setIsSystem(column.getIsSystem());
            dto.setOptions(parseOptions(column.getOptions()));
            dto.setUnit(column.getUnit());
            dto.setFormat(column.getFormat());
            dto.setSection(column.getSection());
            dtos.add(dto);
        }
        return dtos;
    }

    private List<String> normalizeOptions(List<String> raw)
    {
        if (raw == null)
        {
            return null;
        }
        LinkedHashSet<String> seen = new LinkedHashSet<>();
        for (String s : raw)
        {
            if (s == null)
            {
                continue;
            }
            String t = s.trim();
            if (!t.isEmpty())
            {
                seen.add(t);
            }
        }
        return seen.isEmpty() ? null : new ArrayList<>(seen);
    }

    private List<String> parseOptions(String json)
    {
        if (StringUtils.isBlank(json))
        {
            return null;
        }
        try
        {
            List<String> list = JSON.parseArray(json, String.class);
            return normalizeOptions(list);
        }
        catch (Exception e)
        {
            return null;
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
