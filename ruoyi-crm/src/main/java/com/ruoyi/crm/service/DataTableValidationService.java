package com.ruoyi.crm.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmDataTableColumn;
import com.ruoyi.crm.service.support.DataTableConstants;

/**
 * 行写入校验与类型 coercion
 */
@Service
public class DataTableValidationService
{
    public Map<String, Object> validateData(List<CrmDataTableColumn> columns, Map<String, Object> input,
            boolean isInsert)
    {
        Map<String, Object> source = input != null ? new HashMap<>(input) : new HashMap<>();
        Map<String, Object> result = new HashMap<>();

        for (CrmDataTableColumn column : columns)
        {
            String name = column.getName();
            Object raw = source.get(name);
            if (raw == null && StringUtils.isNotBlank(column.getDefaultValue()))
            {
                raw = column.getDefaultValue();
            }
            if (DataTableConstants.isSystemColumn(name))
            {
                continue;
            }
            if (raw == null && Boolean.TRUE.equals(column.getRequired()))
            {
                throw new ServiceException("必填列缺失: " + name);
            }
            if (raw != null)
            {
                result.put(name, coerceValue(raw, column.getType(), name));
            }
        }

        String nowIso = Instant.now().toString();
        if (isInsert)
        {
            String rowId = source.containsKey(DataTableConstants.COL_ID)
                    ? String.valueOf(source.get(DataTableConstants.COL_ID))
                    : IdUtils.fastSimpleUUID();
            if (StringUtils.isBlank(rowId))
            {
                rowId = IdUtils.fastSimpleUUID();
            }
            result.put(DataTableConstants.COL_ID, rowId);
            result.put(DataTableConstants.COL_CREATED_AT,
                    source.getOrDefault(DataTableConstants.COL_CREATED_AT, nowIso));
            result.put(DataTableConstants.COL_UPDATED_AT, nowIso);
        }
        else
        {
            if (source.containsKey(DataTableConstants.COL_ID))
            {
                result.put(DataTableConstants.COL_ID, String.valueOf(source.get(DataTableConstants.COL_ID)));
            }
            if (source.containsKey(DataTableConstants.COL_CREATED_AT))
            {
                result.put(DataTableConstants.COL_CREATED_AT,
                        coerceValue(source.get(DataTableConstants.COL_CREATED_AT), DataTableConstants.TYPE_DATE,
                                DataTableConstants.COL_CREATED_AT));
            }
            result.put(DataTableConstants.COL_UPDATED_AT, nowIso);
            for (Map.Entry<String, Object> entry : source.entrySet())
            {
                if (DataTableConstants.isSystemColumn(entry.getKey()))
                {
                    continue;
                }
                CrmDataTableColumn col = findColumn(columns, entry.getKey());
                if (col != null)
                {
                    result.put(entry.getKey(), coerceValue(entry.getValue(), col.getType(), entry.getKey()));
                }
            }
        }
        return result;
    }

    public Map<String, Object> mergeData(Map<String, Object> existing, Map<String, Object> patch,
            List<CrmDataTableColumn> columns)
    {
        Map<String, Object> merged = existing != null ? new HashMap<>(existing) : new HashMap<>();
        if (patch != null)
        {
            merged.putAll(patch);
        }
        return validateData(columns, merged, false);
    }

    public String inferColumnType(Object value)
    {
        if (value == null)
        {
            return DataTableConstants.TYPE_STRING;
        }
        if (value instanceof Boolean)
        {
            return DataTableConstants.TYPE_BOOLEAN;
        }
        if (value instanceof Number)
        {
            return DataTableConstants.TYPE_NUMBER;
        }
        if (value instanceof Map || value instanceof List)
        {
            return DataTableConstants.TYPE_JSON;
        }
        String text = String.valueOf(value);
        if ("true".equalsIgnoreCase(text) || "false".equalsIgnoreCase(text))
        {
            return DataTableConstants.TYPE_BOOLEAN;
        }
        try
        {
            new BigDecimal(text);
            return DataTableConstants.TYPE_NUMBER;
        }
        catch (NumberFormatException ignored)
        {
            // continue
        }
        try
        {
            Instant.parse(text);
            return DataTableConstants.TYPE_DATE;
        }
        catch (DateTimeParseException ignored)
        {
            // continue
        }
        if (text.trim().startsWith("{") || text.trim().startsWith("["))
        {
            try
            {
                JSON.parse(text);
                return DataTableConstants.TYPE_JSON;
            }
            catch (Exception ignored)
            {
                // fall through
            }
        }
        return DataTableConstants.TYPE_STRING;
    }

    private CrmDataTableColumn findColumn(List<CrmDataTableColumn> columns, String name)
    {
        for (CrmDataTableColumn column : columns)
        {
            if (name.equals(column.getName()))
            {
                return column;
            }
        }
        return null;
    }

    private Object coerceValue(Object raw, String type, String columnName)
    {
        if (raw == null)
        {
            return null;
        }
        String normalized = type != null ? type.toUpperCase() : DataTableConstants.TYPE_STRING;
        try
        {
            switch (normalized)
            {
                case DataTableConstants.TYPE_NUMBER:
                    if (raw instanceof Number)
                    {
                        return raw;
                    }
                    return new BigDecimal(String.valueOf(raw));
                case DataTableConstants.TYPE_BOOLEAN:
                    if (raw instanceof Boolean)
                    {
                        return raw;
                    }
                    return Boolean.parseBoolean(String.valueOf(raw));
                case DataTableConstants.TYPE_DATE:
                    return coerceDateValue(raw);
                case DataTableConstants.TYPE_JSON:
                    if (raw instanceof Map || raw instanceof List)
                    {
                        return raw;
                    }
                    return JSON.parse(String.valueOf(raw));
                case DataTableConstants.TYPE_MULTI_SELECT:
                    return coerceMultiSelect(raw);
                case DataTableConstants.TYPE_SELECT:
                case DataTableConstants.TYPE_STRING:
                    return String.valueOf(raw);
                case DataTableConstants.TYPE_URL:
                    return coerceUrl(raw, columnName);
                default:
                    return String.valueOf(raw);
            }
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ServiceException("列 " + columnName + " 类型转换失败: " + e.getMessage());
        }
    }

    private List<String> coerceMultiSelect(Object raw)
    {
        LinkedHashSet<String> seen = new LinkedHashSet<>();
        if (raw instanceof List)
        {
            for (Object item : (List<?>) raw)
            {
                if (item == null)
                {
                    continue;
                }
                String t = String.valueOf(item).trim();
                if (!t.isEmpty())
                {
                    seen.add(t);
                }
            }
            return new ArrayList<>(seen);
        }
        String text = String.valueOf(raw).trim();
        if (text.isEmpty())
        {
            return new ArrayList<>();
        }
        if (text.startsWith("["))
        {
            List<String> parsed = JSON.parseArray(text, String.class);
            if (parsed != null)
            {
                for (String s : parsed)
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
                return new ArrayList<>(seen);
            }
        }
        for (String part : text.split("[,，;；|]"))
        {
            String t = part.trim();
            if (!t.isEmpty())
            {
                seen.add(t);
            }
        }
        return new ArrayList<>(seen);
    }

    private String coerceUrl(Object raw, String columnName)
    {
        String text = String.valueOf(raw).trim();
        if (text.isEmpty())
        {
            return null;
        }
        String lower = text.toLowerCase();
        if (!(lower.startsWith("http://") || lower.startsWith("https://") || lower.startsWith("/")))
        {
            // Allow bare domains by prefixing https
            if (text.contains(".") && !text.contains(" "))
            {
                return "https://" + text;
            }
            throw new ServiceException("列 " + columnName + " 不是有效链接");
        }
        return text;
    }

    /**
     * Accept Instant ISO (with Z/offset), local datetime without zone, or date-only.
     * Always persist as Instant ISO-8601 string.
     */
    private String coerceDateValue(Object raw)
    {
        if (raw instanceof Number)
        {
            return Instant.ofEpochMilli(((Number) raw).longValue()).toString();
        }
        String text = String.valueOf(raw).trim();
        if (StringUtils.isEmpty(text))
        {
            return null;
        }
        try
        {
            return Instant.parse(text).toString();
        }
        catch (DateTimeParseException ignored)
        {
            // fall through
        }
        try
        {
            LocalDateTime ldt = LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return ldt.toInstant(ZoneOffset.UTC).toString();
        }
        catch (DateTimeParseException ignored)
        {
            // fall through
        }
        try
        {
            LocalDateTime ldt = LocalDateTime.parse(text,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            return ldt.toInstant(ZoneOffset.UTC).toString();
        }
        catch (DateTimeParseException ignored)
        {
            // fall through
        }
        try
        {
            LocalDate ld = LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE);
            return ld.atStartOfDay(ZoneOffset.UTC).toInstant().toString();
        }
        catch (DateTimeParseException ignored)
        {
            // fall through
        }
        throw new DateTimeParseException("Unsupported date format", text, 0);
    }
}
