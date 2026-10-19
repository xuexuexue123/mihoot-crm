package com.ruoyi.crm.service.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.ruoyi.common.utils.StringUtils;

/**
 * Captures allowed_origins 序列化 / CORS Origin 校验
 */
public final class CaptureOriginGuard
{
    private CaptureOriginGuard()
    {
    }

    public static String serializeOrigins(List<String> origins)
    {
        if (origins == null || origins.isEmpty())
        {
            return null;
        }
        List<String> cleaned = new ArrayList<>();
        for (String origin : origins)
        {
            if (StringUtils.isNotBlank(origin))
            {
                cleaned.add(origin.trim());
            }
        }
        return cleaned.isEmpty() ? null : JSON.toJSONString(cleaned);
    }

    public static List<String> parseOrigins(String allowedOriginsJson)
    {
        if (StringUtils.isBlank(allowedOriginsJson))
        {
            return Collections.emptyList();
        }
        try
        {
            JSONArray array = JSON.parseArray(allowedOriginsJson);
            if (array == null || array.isEmpty())
            {
                return Collections.emptyList();
            }
            List<String> result = new ArrayList<>(array.size());
            for (int i = 0; i < array.size(); i++)
            {
                String value = array.getString(i);
                if (StringUtils.isNotBlank(value))
                {
                    result.add(value.trim());
                }
            }
            return result;
        }
        catch (Exception ignored)
        {
            return Collections.emptyList();
        }
    }

    /**
     * @return true if request origin is allowed (or whitelist empty)
     */
    public static boolean isOriginAllowed(String allowedOriginsJson, String requestOrigin)
    {
        List<String> allowed = parseOrigins(allowedOriginsJson);
        if (allowed.isEmpty())
        {
            return true;
        }
        if (StringUtils.isBlank(requestOrigin))
        {
            return false;
        }
        String origin = requestOrigin.trim();
        for (String candidate : allowed)
        {
            if (candidate.equalsIgnoreCase(origin))
            {
                return true;
            }
        }
        return false;
    }
}
