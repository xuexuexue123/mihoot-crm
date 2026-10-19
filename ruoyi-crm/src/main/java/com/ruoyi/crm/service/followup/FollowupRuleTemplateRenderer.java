package com.ruoyi.crm.service.followup;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.StringUtils;

/**
 * 渲染 task_template 中的 {{a.b}} 占位符；缺失时用 subjectId 兜底。
 */
public final class FollowupRuleTemplateRenderer
{
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{([^}]+)\\}\\}");

    private FollowupRuleTemplateRenderer()
    {
    }

    public static String render(String template, JSONObject payload, String subjectId)
    {
        if (template == null)
        {
            return "";
        }
        Matcher matcher = PLACEHOLDER.matcher(template);
        StringBuffer sb = new StringBuffer();
        while (matcher.find())
        {
            String path = matcher.group(1).trim();
            String value = lookup(payload, path);
            if (StringUtils.isBlank(value))
            {
                value = fallback(path, subjectId);
            }
            matcher.appendReplacement(sb, Matcher.quoteReplacement(value == null ? "" : value));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static Map<String, String> flatten(JSONObject payload)
    {
        Map<String, String> map = new HashMap<>();
        flattenInto(payload, "", map);
        return map;
    }

    public static String lookup(JSONObject payload, String path)
    {
        if (payload == null || StringUtils.isBlank(path))
        {
            return null;
        }
        Object current = payload;
        for (String part : path.split("\\."))
        {
            if (!(current instanceof JSONObject))
            {
                return null;
            }
            current = ((JSONObject) current).get(part);
        }
        return current == null ? null : String.valueOf(current);
    }

    private static String fallback(String path, String subjectId)
    {
        if (path != null && (path.endsWith("Id") || path.endsWith(".id") || "subjectId".equals(path)))
        {
            return subjectId;
        }
        return subjectId;
    }

    private static void flattenInto(JSONObject obj, String prefix, Map<String, String> out)
    {
        if (obj == null)
        {
            return;
        }
        for (String key : obj.keySet())
        {
            Object value = obj.get(key);
            String next = prefix.isEmpty() ? key : prefix + "." + key;
            if (value instanceof JSONObject)
            {
                flattenInto((JSONObject) value, next, out);
            }
            else if (value != null)
            {
                out.put(next, String.valueOf(value));
            }
        }
    }
}
