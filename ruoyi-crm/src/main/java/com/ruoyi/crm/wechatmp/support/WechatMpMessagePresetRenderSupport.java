package com.ruoyi.crm.wechatmp.support;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;

/**
 * 公众号消息预设占位符替换与渲染后校验
 */
public final class WechatMpMessagePresetRenderSupport
{
    private WechatMpMessagePresetRenderSupport() {}

    public static String substitute(String template, java.util.Map<String, String> vars)
    {
        if (template == null)
        {
            return null;
        }
        String result = template;
        for (java.util.Map.Entry<String, String> e : vars.entrySet())
        {
            String placeholder = "{{" + e.getKey() + "}}";
            String value = e.getValue() != null ? e.getValue() : "";
            result = result.replace(placeholder, value);
        }
        return result;
    }

    public static JSONObject substituteDataJson(JSONObject source, java.util.Map<String, String> vars)
    {
        if (source == null)
        {
            return new JSONObject();
        }
        JSONObject copy = JSONObject.parseObject(source.toJSONString());
        substituteDataJsonRecursive(copy, vars);
        return copy;
    }

    private static void substituteDataJsonRecursive(JSONObject node, java.util.Map<String, String> vars)
    {
        for (String key : node.keySet())
        {
            Object val = node.get(key);
            if (val instanceof JSONObject)
            {
                JSONObject obj = (JSONObject) val;
                if (obj.containsKey("value") && obj.get("value") instanceof String)
                {
                    obj.put("value", substitute((String) obj.get("value"), vars));
                }
                else
                {
                    substituteDataJsonRecursive(obj, vars);
                }
            }
            else if (val instanceof JSONArray)
            {
                JSONArray arr = (JSONArray) val;
                for (int i = 0; i < arr.size(); i++)
                {
                    Object item = arr.get(i);
                    if (item instanceof JSONObject)
                    {
                        substituteDataJsonRecursive((JSONObject) item, vars);
                    }
                }
            }
        }
    }

    public static void validateRenderedData(JSONObject data)
    {
        validateRenderedDataRecursive(data, "");
    }

    private static void validateRenderedDataRecursive(JSONObject node, String path)
    {
        for (String key : node.keySet())
        {
            String fieldPath = path.isEmpty() ? key : path + "." + key;
            Object val = node.get(key);
            if (val instanceof JSONObject)
            {
                JSONObject obj = (JSONObject) val;
                if (obj.containsKey("value"))
                {
                    Object value = obj.get("value");
                    if (value == null || !StringUtils.hasText(value.toString().trim()))
                    {
                        throw new ServiceException("模板字段 " + fieldPath + " 渲染后为空");
                    }
                }
                else
                {
                    validateRenderedDataRecursive(obj, fieldPath);
                }
            }
            else if (val instanceof JSONArray)
            {
                JSONArray arr = (JSONArray) val;
                for (int i = 0; i < arr.size(); i++)
                {
                    Object item = arr.get(i);
                    if (item instanceof JSONObject)
                    {
                        validateRenderedDataRecursive((JSONObject) item, fieldPath + "[" + i + "]");
                    }
                }
            }
        }
    }

    public static void validateDataJsonStructure(JSONObject node)
    {
        for (String key : node.keySet())
        {
            Object val = node.get(key);
            if (val instanceof JSONObject)
            {
                JSONObject obj = (JSONObject) val;
                if (obj.containsKey("value"))
                {
                    Object value = obj.get("value");
                    if (value != null && !(value instanceof String))
                    {
                        throw new ServiceException("模板字段 " + key + " 的 value 须为字符串");
                    }
                }
                else
                {
                    validateDataJsonStructure(obj);
                }
            }
            else if (val instanceof JSONArray)
            {
                JSONArray arr = (JSONArray) val;
                for (int i = 0; i < arr.size(); i++)
                {
                    Object item = arr.get(i);
                    if (item instanceof JSONObject)
                    {
                        validateDataJsonStructure((JSONObject) item);
                    }
                }
            }
        }
    }
}
