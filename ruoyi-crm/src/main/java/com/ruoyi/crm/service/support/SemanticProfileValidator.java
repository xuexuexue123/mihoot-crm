package com.ruoyi.crm.service.support;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.dto.SemanticProfileSaveRequest;
import com.ruoyi.crm.enums.SemanticEntityKey;

/**
 * 语义层 profile_json 校验（契约 §5）
 */
public final class SemanticProfileValidator
{
    private static final int MAX_PROFILE_JSON_BYTES = 256 * 1024;
    private static final int MAX_FIELDS = 50;
    private static final int MAX_BUSINESS_RULES = 30;
    private static final int MAX_RELATIONSHIPS = 20;
    private static final int MAX_TEXT_LENGTH = 500;

    private static final Set<String> INDUSTRY_KEYS = new HashSet<>();
    private static final Set<String> ENABLED_VALUES = new HashSet<>();

    static
    {
        INDUSTRY_KEYS.add("dental");
        INDUSTRY_KEYS.add("generic");
        INDUSTRY_KEYS.add("custom");
        ENABLED_VALUES.add("0");
        ENABLED_VALUES.add("1");
    }

    private SemanticProfileValidator()
    {
    }

    public static void validateSaveRequest(SemanticProfileSaveRequest request)
    {
        if (request == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        validateIndustryKey(request.getIndustryKey());
        validateEnabled(request.getEnabled());
        validateProfileJson(request.getProfileJson());
    }

    public static void validateIndustryKey(String industryKey)
    {
        if (StringUtils.isBlank(industryKey) || !INDUSTRY_KEYS.contains(industryKey.trim()))
        {
            throw new ServiceException("industryKey 必须为 dental、generic 或 custom", HttpStatus.BAD_REQUEST);
        }
    }

    public static void validateEnabled(String enabled)
    {
        if (StringUtils.isBlank(enabled) || !ENABLED_VALUES.contains(enabled.trim()))
        {
            throw new ServiceException("enabled 必须为 0 或 1", HttpStatus.BAD_REQUEST);
        }
    }

    public static JSONObject validateProfileJson(String profileJson)
    {
        if (StringUtils.isBlank(profileJson))
        {
            throw new ServiceException("profileJson 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (profileJson.getBytes(StandardCharsets.UTF_8).length > MAX_PROFILE_JSON_BYTES)
        {
            throw new ServiceException("profileJson 大小不能超过 256KB", HttpStatus.BAD_REQUEST);
        }

        JSONObject root;
        try
        {
            root = JSON.parseObject(profileJson);
        }
        catch (Exception ex)
        {
            throw new ServiceException("profileJson 必须是合法 JSON", HttpStatus.BAD_REQUEST);
        }
        if (root == null)
        {
            throw new ServiceException("profileJson 必须是 JSON 对象", HttpStatus.BAD_REQUEST);
        }

        validateEntities(root.getJSONObject("entities"));
        validateFields(root.getJSONArray("fields"));
        validateRelationships(root.getJSONArray("relationships"));
        validateBusinessRules(root.getJSONArray("businessRules"));
        return root;
    }

    private static void validateEntities(JSONObject entities)
    {
        if (entities == null || entities.isEmpty())
        {
            return;
        }
        for (String key : entities.keySet())
        {
            if (SemanticEntityKey.fromKey(key) == null)
            {
                throw new ServiceException("未知 entityKey: " + key, HttpStatus.BAD_REQUEST);
            }
        }
    }

    private static void validateFields(JSONArray fields)
    {
        if (fields == null)
        {
            return;
        }
        if (fields.size() > MAX_FIELDS)
        {
            throw new ServiceException("fields 最多 " + MAX_FIELDS + " 条", HttpStatus.BAD_REQUEST);
        }
        for (int i = 0; i < fields.size(); i++)
        {
            JSONObject item = fields.getJSONObject(i);
            if (item == null)
            {
                throw new ServiceException("fields[" + i + "] 必须是对象", HttpStatus.BAD_REQUEST);
            }
            String entityKey = item.getString("entityKey");
            String fieldKey = item.getString("fieldKey");
            if (StringUtils.isBlank(entityKey) || StringUtils.isBlank(fieldKey))
            {
                throw new ServiceException("fields[" + i + "].entityKey 与 fieldKey 不能为空", HttpStatus.BAD_REQUEST);
            }
            if (SemanticEntityKey.fromKey(entityKey.trim()) == null)
            {
                throw new ServiceException("fields[" + i + "] 未知 entityKey: " + entityKey, HttpStatus.BAD_REQUEST);
            }
            validateTextLength("fields[" + i + "].meaning", item.getString("meaning"));
        }
    }

    private static void validateRelationships(JSONArray relationships)
    {
        if (relationships == null)
        {
            return;
        }
        if (relationships.size() > MAX_RELATIONSHIPS)
        {
            throw new ServiceException("relationships 最多 " + MAX_RELATIONSHIPS + " 条", HttpStatus.BAD_REQUEST);
        }
        for (int i = 0; i < relationships.size(); i++)
        {
            JSONObject item = relationships.getJSONObject(i);
            if (item == null)
            {
                throw new ServiceException("relationships[" + i + "] 必须是对象", HttpStatus.BAD_REQUEST);
            }
            validateEntityRef("relationships[" + i + "].from", item.getString("from"));
            validateEntityRef("relationships[" + i + "].to", item.getString("to"));
        }
    }

    private static void validateBusinessRules(JSONArray businessRules)
    {
        if (businessRules == null)
        {
            return;
        }
        if (businessRules.size() > MAX_BUSINESS_RULES)
        {
            throw new ServiceException("businessRules 最多 " + MAX_BUSINESS_RULES + " 条", HttpStatus.BAD_REQUEST);
        }
        for (int i = 0; i < businessRules.size(); i++)
        {
            JSONObject item = businessRules.getJSONObject(i);
            if (item == null)
            {
                throw new ServiceException("businessRules[" + i + "] 必须是对象", HttpStatus.BAD_REQUEST);
            }
            validateTextLength("businessRules[" + i + "].rule", item.getString("rule"));
        }
    }

    private static void validateEntityRef(String fieldName, String entityKey)
    {
        if (StringUtils.isBlank(entityKey))
        {
            throw new ServiceException(fieldName + " 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (SemanticEntityKey.fromKey(entityKey.trim()) == null)
        {
            throw new ServiceException(fieldName + " 未知 entityKey: " + entityKey, HttpStatus.BAD_REQUEST);
        }
    }

    private static void validateTextLength(String fieldName, String value)
    {
        if (value != null && value.length() > MAX_TEXT_LENGTH)
        {
            throw new ServiceException(fieldName + " 长度不能超过 " + MAX_TEXT_LENGTH + " 字符", HttpStatus.BAD_REQUEST);
        }
    }
}
