package com.ruoyi.crm.service.webhook;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.service.followup.FollowupRuleTemplateRenderer;

/**
 * Render JSON object templates: string values containing {{path}} are interpolated;
 * if the result parses as object/array it is promoted.
 */
public final class OutboundWebhookJsonTemplateRenderer
{
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{([^}]+)\\}\\}");

    private OutboundWebhookJsonTemplateRenderer()
    {
    }

    public static JSONObject render(String templateJson, JSONObject context)
    {
        if (StringUtils.isBlank(templateJson))
        {
            return context == null ? new JSONObject() : context;
        }
        Object parsed = JSON.parse(templateJson);
        Object rendered = renderNode(parsed, context == null ? new JSONObject() : context);
        if (rendered instanceof JSONObject)
        {
            return (JSONObject) rendered;
        }
        JSONObject wrap = new JSONObject();
        wrap.put("value", rendered);
        return wrap;
    }

    static Object renderNode(Object node, JSONObject context)
    {
        if (node instanceof JSONObject)
        {
            JSONObject out = new JSONObject();
            JSONObject obj = (JSONObject) node;
            for (String key : obj.keySet())
            {
                out.put(key, renderNode(obj.get(key), context));
            }
            return out;
        }
        if (node instanceof JSONArray)
        {
            JSONArray out = new JSONArray();
            JSONArray arr = (JSONArray) node;
            for (int i = 0; i < arr.size(); i++)
            {
                out.add(renderNode(arr.get(i), context));
            }
            return out;
        }
        if (node instanceof String)
        {
            return renderString((String) node, context);
        }
        return node;
    }

    static Object renderString(String template, JSONObject context)
    {
        if (template == null || !template.contains("{{"))
        {
            return template;
        }
        Matcher matcher = PLACEHOLDER.matcher(template);
        StringBuffer sb = new StringBuffer();
        while (matcher.find())
        {
            String path = matcher.group(1).trim();
            String value = FollowupRuleTemplateRenderer.lookup(context, path);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(value == null ? "" : value));
        }
        matcher.appendTail(sb);
        String rendered = sb.toString();
        String trimmed = rendered.trim();
        if (trimmed.startsWith("{") || trimmed.startsWith("["))
        {
            try
            {
                Object parsed = JSON.parse(trimmed);
                if (parsed instanceof JSONObject || parsed instanceof JSONArray)
                {
                    return parsed;
                }
            }
            catch (Exception ignored)
            {
            }
        }
        return rendered;
    }
}
