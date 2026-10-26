package com.ruoyi.crm.service.nurture.support;

import java.util.Collections;
import java.util.Map;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.StringUtils;

public final class EventNurtureJsonSupport
{
    private EventNurtureJsonSupport()
    {
    }

    public static String toJsonString(Map<String, Object> map)
    {
        if (map == null || map.isEmpty())
        {
            return "{}";
        }
        return JSON.toJSONString(map);
    }

    public static Map<String, Object> parseObject(String json)
    {
        if (StringUtils.isBlank(json))
        {
            return Collections.emptyMap();
        }
        JSONObject obj = JSON.parseObject(json);
        return obj != null ? obj : Collections.emptyMap();
    }

    public static JSONObject parseJsonObject(String json)
    {
        if (StringUtils.isBlank(json))
        {
            return new JSONObject();
        }
        JSONObject obj = JSON.parseObject(json);
        return obj != null ? obj : new JSONObject();
    }
}
