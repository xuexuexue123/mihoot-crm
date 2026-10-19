package com.ruoyi.crm.whatsapp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

/**
 * 出站模板消息落库：可读 content + 完整 rawJson，供 Inbox 气泡/列表解析展示。
 */
public final class WhatsappTemplateMessageStore
{
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{\\s*(\\d+)\\s*\\}\\}");

    private WhatsappTemplateMessageStore()
    {
    }

    public static final class StoredTemplate
    {
        public final String content;
        public final String rawJson;
        public final String preview;

        public StoredTemplate(String content, String rawJson, String preview)
        {
            this.content = content;
            this.rawJson = rawJson;
            this.preview = preview;
        }
    }

    /**
     * @param clientContent 前端传入的可读预览（优先）
     * @param templateName  模板名
     * @param language      语言
     * @param sendComponents Meta 发送用 components（可为 null）
     * @param definitionComponentsJson 缓存模板定义 components JSON（可为 null）
     */
    public static StoredTemplate build(String clientContent, String templateName, String language,
                                       JSONArray sendComponents, String definitionComponentsJson)
    {
        String name = templateName != null ? templateName.trim() : "";
        String lang = language != null ? language.trim() : "";

        String renderedBody = renderBody(definitionComponentsJson, sendComponents);
        String content;
        if (clientContent != null && !clientContent.trim().isEmpty())
        {
            content = clientContent.trim();
        }
        else if (renderedBody != null && !renderedBody.isEmpty())
        {
            content = name.isEmpty() ? renderedBody : (name + "\n" + renderedBody);
        }
        else
        {
            String paramsLine = joinBodyParamTexts(sendComponents);
            if (paramsLine != null && !paramsLine.isEmpty())
            {
                content = name.isEmpty() ? paramsLine : (name + "\n" + paramsLine);
            }
            else
            {
                content = name.isEmpty() ? "[模板]" : name;
            }
        }

        Map<String, Object> raw = new LinkedHashMap<>();
        raw.put("type", "template");
        Map<String, Object> tpl = new LinkedHashMap<>();
        tpl.put("name", name);
        tpl.put("language", lang);
        if (sendComponents != null)
        {
            tpl.put("components", sendComponents);
        }
        raw.put("template", tpl);
        if (definitionComponentsJson != null && !definitionComponentsJson.isEmpty())
        {
            try
            {
                raw.put("definitionComponents", JSON.parse(definitionComponentsJson));
            }
            catch (Exception ignored)
            {
                raw.put("definitionComponents", definitionComponentsJson);
            }
        }
        if (renderedBody != null)
        {
            raw.put("renderedBody", renderedBody);
        }

        String rawJson = JSON.toJSONString(raw);
        String preview = truncate(content, 200);
        return new StoredTemplate(content, rawJson, preview);
    }

    /** 用定义 BODY 文本 + 发送参数替换 {{n}} */
    static String renderBody(String definitionComponentsJson, JSONArray sendComponents)
    {
        String bodyText = extractBodyText(definitionComponentsJson);
        if (bodyText == null || bodyText.isEmpty())
        {
            return null;
        }
        List<String> params = extractBodyParamTexts(sendComponents);
        if (params.isEmpty())
        {
            // 无变量模板：直接返回定义正文
            return bodyText.contains("{{") ? null : bodyText;
        }
        Matcher m = PLACEHOLDER.matcher(bodyText);
        StringBuffer sb = new StringBuffer();
        while (m.find())
        {
            int idx;
            try
            {
                idx = Integer.parseInt(m.group(1));
            }
            catch (NumberFormatException e)
            {
                m.appendReplacement(sb, Matcher.quoteReplacement(m.group()));
                continue;
            }
            String replacement = (idx >= 1 && idx <= params.size()) ? params.get(idx - 1) : m.group();
            m.appendReplacement(sb, Matcher.quoteReplacement(replacement != null ? replacement : ""));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static String extractBodyText(String definitionComponentsJson)
    {
        if (definitionComponentsJson == null || definitionComponentsJson.isEmpty())
        {
            return null;
        }
        try
        {
            JSONArray arr = JSON.parseArray(definitionComponentsJson);
            if (arr == null)
            {
                return null;
            }
            for (int i = 0; i < arr.size(); i++)
            {
                JSONObject c = arr.getJSONObject(i);
                if (c == null)
                {
                    continue;
                }
                String type = c.getString("type");
                if (type != null && "BODY".equalsIgnoreCase(type))
                {
                    return c.getString("text");
                }
            }
        }
        catch (Exception ignored)
        {
            // ignore
        }
        return null;
    }

    private static List<String> extractBodyParamTexts(JSONArray sendComponents)
    {
        List<String> params = new ArrayList<>();
        if (sendComponents == null)
        {
            return params;
        }
        for (int i = 0; i < sendComponents.size(); i++)
        {
            JSONObject c = sendComponents.getJSONObject(i);
            if (c == null)
            {
                continue;
            }
            String type = c.getString("type");
            if (type == null || !"body".equalsIgnoreCase(type))
            {
                continue;
            }
            JSONArray parameters = c.getJSONArray("parameters");
            if (parameters == null)
            {
                continue;
            }
            for (int j = 0; j < parameters.size(); j++)
            {
                JSONObject p = parameters.getJSONObject(j);
                if (p == null)
                {
                    continue;
                }
                String text = p.getString("text");
                if (text != null)
                {
                    params.add(text);
                }
                else
                {
                    params.add(String.valueOf(p));
                }
            }
        }
        return params;
    }

    private static String joinBodyParamTexts(JSONArray sendComponents)
    {
        List<String> params = extractBodyParamTexts(sendComponents);
        if (params.isEmpty())
        {
            return null;
        }
        return String.join(" · ", params);
    }

    private static String truncate(String s, int max)
    {
        if (s == null)
        {
            return null;
        }
        String oneLine = s.replace('\n', ' ').trim();
        return oneLine.length() <= max ? oneLine : oneLine.substring(0, max);
    }
}
