package com.ruoyi.crm.service.support;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.StringUtils;

/**
 * 小程序 profileJson → crm_lead.custom_field（[{fieldName,value,fieldLabel}]）合并
 */
public final class MiniappLeadCustomFieldSupport
{
    private static final String EMPTY_FIELD_LABEL = "";
    private static final String PROFILE_PREFIX = "profile.";

    private MiniappLeadCustomFieldSupport()
    {
    }

    public static String mergeProfile(String existingCustomField, Map<String, Object> profileJson)
    {
        Map<String, Object> merged = parseArrayToMap(existingCustomField);
        if (profileJson != null)
        {
            for (Map.Entry<String, Object> entry : profileJson.entrySet())
            {
                if (entry.getKey() == null || entry.getValue() == null)
                {
                    continue;
                }
                String key = entry.getKey().trim();
                if (key.isEmpty())
                {
                    continue;
                }
                String value = String.valueOf(entry.getValue()).trim();
                if (value.isEmpty())
                {
                    continue;
                }
                String fieldName = key.startsWith(PROFILE_PREFIX) ? key : PROFILE_PREFIX + key;
                merged.put(fieldName, value);
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
            JSONObject item = new JSONObject();
            item.put("fieldName", entry.getKey());
            item.put("value", entry.getValue());
            item.put("fieldLabel", EMPTY_FIELD_LABEL);
            array.add(item);
        }
        return array.isEmpty() ? null : array.toJSONString();
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
