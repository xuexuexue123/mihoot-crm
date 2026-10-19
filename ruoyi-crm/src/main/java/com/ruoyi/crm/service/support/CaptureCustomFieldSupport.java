package com.ruoyi.crm.service.support;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.StringUtils;

/**
 * Captures customField（[{fieldName, value, fieldLabel}]）构建与合并
 */
public final class CaptureCustomFieldSupport
{
    private static final String EMPTY_FIELD_LABEL = "";

    private CaptureCustomFieldSupport()
    {
    }

    public static String buildCustomField(Map<String, Object> extraFields, String captureId, String tenantSlug,
            String pageUrl)
    {
        Map<String, Object> merged = new LinkedHashMap<>();
        if (extraFields != null)
        {
            for (Map.Entry<String, Object> entry : extraFields.entrySet())
            {
                if (entry.getKey() != null && entry.getValue() != null
                        && StringUtils.isNotBlank(String.valueOf(entry.getValue())))
                {
                    merged.put(entry.getKey(), entry.getValue());
                }
            }
        }
        putIfNotBlank(merged, "captureId", captureId);
        putIfNotBlank(merged, "tenantSlug", tenantSlug);
        putIfNotBlank(merged, "pageUrl", pageUrl);
        return toNormalizedArrayJson(merged);
    }

    public static String mergeCustomField(String existingJson, String incomingJson)
    {
        Map<String, Object> merged = parseArrayToMap(existingJson);
        Map<String, Object> incoming = parseArrayToMap(incomingJson);
        for (Map.Entry<String, Object> entry : incoming.entrySet())
        {
            if (entry.getValue() != null && StringUtils.isNotBlank(String.valueOf(entry.getValue())))
            {
                merged.put(entry.getKey(), entry.getValue());
            }
        }
        return toNormalizedArrayJson(merged);
    }

    private static String toNormalizedArrayJson(Map<String, Object> fields)
    {
        if (fields == null || fields.isEmpty())
        {
            return null;
        }
        JSONArray array = new JSONArray();
        for (Map.Entry<String, Object> entry : fields.entrySet())
        {
            if (entry.getValue() == null || StringUtils.isBlank(String.valueOf(entry.getValue())))
            {
                continue;
            }
            array.add(normalizedItem(entry.getKey(), entry.getValue()));
        }
        return array.isEmpty() ? null : array.toJSONString();
    }

    private static JSONObject normalizedItem(String fieldName, Object value)
    {
        JSONObject item = new JSONObject();
        item.put("fieldName", fieldName);
        item.put("value", value);
        item.put("fieldLabel", EMPTY_FIELD_LABEL);
        return item;
    }

    private static void putIfNotBlank(Map<String, Object> target, String key, String value)
    {
        if (target != null && StringUtils.isNotBlank(value))
        {
            target.put(key, value.trim());
        }
    }

    private static Map<String, Object> parseArrayToMap(String customFieldJson)
    {
        Map<String, Object> map = new LinkedHashMap<>();
        if (StringUtils.isBlank(customFieldJson))
        {
            return map;
        }
        try
        {
            JSONArray array = JSON.parseArray(customFieldJson);
            if (array == null)
            {
                return map;
            }
            for (int i = 0; i < array.size(); i++)
            {
                JSONObject item = array.getJSONObject(i);
                if (item == null)
                {
                    continue;
                }
                String fieldName = item.getString("fieldName");
                if (StringUtils.isNotBlank(fieldName))
                {
                    map.put(fieldName, item.get("value"));
                }
            }
        }
        catch (Exception ignored)
        {
            // ignore malformed custom_field
        }
        return map;
    }
}
