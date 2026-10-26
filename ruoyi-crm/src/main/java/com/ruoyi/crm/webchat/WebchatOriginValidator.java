package com.ruoyi.crm.webchat;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;

/**
 * Origin / Referer hostname 与 allowed_domains 精确匹配。
 */
public final class WebchatOriginValidator
{
    private WebchatOriginValidator()
    {
    }

    public static String extractHostname(String originOrReferer)
    {
        if (!StringUtils.hasText(originOrReferer))
        {
            return null;
        }
        String raw = originOrReferer.trim();
        try
        {
            URI uri = URI.create(raw.contains("://") ? raw : ("https://" + raw));
            String host = uri.getHost();
            if (StringUtils.hasText(host))
            {
                return host.toLowerCase();
            }
        }
        catch (Exception ignored)
        {
        }
        // fallback: strip path/port
        String hostPart = raw;
        int scheme = hostPart.indexOf("://");
        if (scheme >= 0)
        {
            hostPart = hostPart.substring(scheme + 3);
        }
        int slash = hostPart.indexOf('/');
        if (slash >= 0)
        {
            hostPart = hostPart.substring(0, slash);
        }
        int colon = hostPart.indexOf(':');
        if (colon >= 0)
        {
            hostPart = hostPart.substring(0, colon);
        }
        return StringUtils.hasText(hostPart) ? hostPart.toLowerCase() : null;
    }

    public static Set<String> parseAllowedDomains(String allowedDomainsJson)
    {
        if (!StringUtils.hasText(allowedDomainsJson))
        {
            return Collections.emptySet();
        }
        try
        {
            JSONArray arr = JSON.parseArray(allowedDomainsJson);
            if (arr == null || arr.isEmpty())
            {
                return Collections.emptySet();
            }
            Set<String> set = new HashSet<>();
            for (int i = 0; i < arr.size(); i++)
            {
                String host = normalizeHostname(arr.getString(i));
                if (host != null)
                {
                    set.add(host);
                }
            }
            return set;
        }
        catch (Exception e)
        {
            return Collections.emptySet();
        }
    }

    /**
     * hostname only；拒绝 *；剥 scheme/path/port。
     *
     * @throws IllegalArgumentException if input invalid (wildcard etc.)
     */
    public static String normalizeHostname(String raw)
    {
        if (!StringUtils.hasText(raw))
        {
            return null;
        }
        String s = raw.trim();
        if ("*".equals(s) || s.contains("*"))
        {
            throw new IllegalArgumentException("wildcard domains are not allowed");
        }
        String host = extractHostname(s);
        if (!StringUtils.hasText(host))
        {
            throw new IllegalArgumentException("invalid domain: " + raw);
        }
        if (host.contains("/") || host.contains(" "))
        {
            throw new IllegalArgumentException("invalid domain: " + raw);
        }
        return host.toLowerCase();
    }

    public static boolean isOriginAllowed(String origin, String referer, String allowedDomainsJson)
    {
        Set<String> allowed = parseAllowedDomains(allowedDomainsJson);
        if (allowed.isEmpty())
        {
            return false;
        }
        String host = extractHostname(origin);
        if (!StringUtils.hasText(host))
        {
            host = extractHostname(referer);
        }
        if (!StringUtils.hasText(host))
        {
            return false;
        }
        return allowed.contains(host.toLowerCase());
    }

    public static String toJsonArray(List<String> domains)
    {
        if (domains == null || domains.isEmpty())
        {
            return "[]";
        }
        return JSON.toJSONString(domains);
    }
}
