package com.ruoyi.crm.wechatmp.support;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

/**
 * 公众号菜单 URL 规范化（与分群 / Dashboard menuKey 匹配口径对齐）
 */
public final class WechatMpMenuUrlNormalizer
{
    private WechatMpMenuUrlNormalizer()
    {
    }

    public static boolean isUrlMenuKey(String menuKey)
    {
        if (menuKey == null)
        {
            return false;
        }
        String lower = menuKey.trim().toLowerCase(Locale.ROOT);
        return lower.startsWith("http://") || lower.startsWith("https://");
    }

    /**
     * 非 URL 原样 trim；URL 则规范 scheme/path 后返回比较用字符串。
     */
    public static String normalize(String menuKey)
    {
        if (menuKey == null)
        {
            return null;
        }
        String trimmed = menuKey.trim();
        if (!isUrlMenuKey(trimmed))
        {
            return trimmed;
        }
        try
        {
            URI uri = new URI(trimmed);
            String host = uri.getHost();
            if (host == null)
            {
                return trimmed;
            }
            String path = uri.getPath();
            if (path == null || path.isEmpty())
            {
                path = "/";
            }
            path = collapseSlashes(path);
            String query = uri.getRawQuery();
            StringBuilder sb = new StringBuilder();
            sb.append("https://").append(host).append(path);
            if (query != null && !query.isEmpty())
            {
                sb.append('?').append(query);
            }
            return sb.toString();
        }
        catch (URISyntaxException e)
        {
            return trimmed;
        }
    }

    /**
     * 与 SQL 侧 regexp 规范化结果对齐，用于参数绑定。
     */
    public static String normalizeForSqlMatch(String menuKey)
    {
        String normalized = normalize(menuKey);
        if (normalized == null)
        {
            return null;
        }
        if (!isUrlMenuKey(normalized))
        {
            return normalized;
        }
        int hash = normalized.indexOf('#');
        if (hash >= 0)
        {
            normalized = normalized.substring(0, hash);
        }
        return normalized.toLowerCase(Locale.ROOT);
    }

    private static String collapseSlashes(String path)
    {
        return path.replaceAll("/+", "/");
    }
}
