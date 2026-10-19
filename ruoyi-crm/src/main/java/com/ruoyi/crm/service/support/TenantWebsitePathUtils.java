package com.ruoyi.crm.service.support;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.tenant.config.TenantWebsiteProperties;

@Component
public class TenantWebsitePathUtils
{
    private static final int MAX_URL_DECODE_PASSES = 3;

    private static final Pattern PAGE_PATH_PATTERN =
            Pattern.compile("^/(?:[a-z0-9][a-z0-9/-]{0,253})?$");

    @Autowired
    private TenantWebsiteProperties tenantWebsiteProperties;

    public String normalizePath(String path)
    {
        if (StringUtils.isBlank(path))
        {
            return "/";
        }
        String p = decodePathSafely(path.trim());
        if (!p.startsWith("/"))
        {
            p = "/" + p;
        }
        if (p.length() > 1 && p.endsWith("/"))
        {
            p = p.substring(0, p.length() - 1);
        }
        return p.toLowerCase(Locale.ROOT);
    }

    /**
     * 解码 query path（含 BFF 可能的双重编码，如 %252F → /）。
     */
    private static String decodePathSafely(String path)
    {
        String decoded = path;
        for (int i = 0; i < MAX_URL_DECODE_PASSES; i++)
        {
            if (!decoded.contains("%"))
            {
                break;
            }
            try
            {
                String next = URLDecoder.decode(decoded, StandardCharsets.UTF_8.name());
                if (next.equals(decoded))
                {
                    break;
                }
                decoded = next;
            }
            catch (IllegalArgumentException | UnsupportedEncodingException ex)
            {
                break;
            }
        }
        return decoded;
    }

    public void validatePathForCreate(String path)
    {
        String normalized = normalizePath(path);
        validateFormat(normalized);
        if (isReserved(normalized))
        {
            throw new ServiceException("PATH_RESERVED", HttpStatus.BAD_REQUEST);
        }
    }

    public void validatePathQuery(String path)
    {
        String normalized = normalizePath(path);
        validateFormat(normalized);
    }

    private void validateFormat(String path)
    {
        if (path.contains("..") || path.contains("\\") || path.contains("//"))
        {
            throw new ServiceException("Invalid path", HttpStatus.BAD_REQUEST);
        }
        if (path.contains("%"))
        {
            throw new ServiceException("Invalid path", HttpStatus.BAD_REQUEST);
        }
        if (!PAGE_PATH_PATTERN.matcher(path).matches())
        {
            throw new ServiceException("Invalid path", HttpStatus.BAD_REQUEST);
        }
    }

    public boolean isReserved(String path)
    {
        String normalized = normalizePath(path);
        for (String reserved : tenantWebsiteProperties.getReservedPagePathSet())
        {
            if (normalized.equals(reserved) || normalized.startsWith(reserved + "/"))
            {
                return true;
            }
        }
        return false;
    }

    public void requireNonBlankTitle(String title)
    {
        if (StringUtils.isBlank(title))
        {
            throw new ServiceException("title 不能为空", HttpStatus.BAD_REQUEST);
        }
    }
}
