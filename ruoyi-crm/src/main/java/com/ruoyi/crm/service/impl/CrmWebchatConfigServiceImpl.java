package com.ruoyi.crm.service.impl;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.crm.domain.CrmWebchatConfig;
import com.ruoyi.crm.mapper.CrmWebchatConfigMapper;
import com.ruoyi.crm.service.ICrmWebchatConfigService;
import com.ruoyi.crm.webchat.WebchatOriginValidator;

@Service
public class CrmWebchatConfigServiceImpl implements ICrmWebchatConfigService
{
    private static final SecureRandom RANDOM = new SecureRandom();

    @Autowired
    private CrmWebchatConfigMapper configMapper;

    @Value("${webchat.public-origin-hint:}")
    private String publicOriginHint;

    @Value("${webchat.embed-path:/embed/widget.js}")
    private String embedPath;

    @Override
    public Map<String, Object> getConfigForAdmin(String tenantId)
    {
        CrmWebchatConfig config = configMapper.selectByTenantId(tenantId);
        if (config == null)
        {
            return null;
        }
        return toAdminMap(config);
    }

    @Override
    public Map<String, Object> getPublicConfig(String tenantId)
    {
        CrmWebchatConfig config = configMapper.selectByTenantId(tenantId);
        if (config == null)
        {
            return null;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("displayName", config.getDisplayName());
        data.put("logoUrl", config.getLogoUrl());
        data.put("welcomeMessage", config.getWelcomeMessage());
        data.put("themeColor", config.getThemeColor());
        data.put("position", config.getPosition());
        data.put("aiEnabled", config.getAiEnabled() != null && config.getAiEnabled() == 1);
        data.put("offlineMessage", config.getOfflineMessage());
        data.put("enabled", config.getEnabled() != null && config.getEnabled() == 1);
        return data;
    }

    @Override
    public Map<String, Object> saveOrUpdate(String tenantId, Map<String, Object> fields)
    {
        if (!StringUtils.hasText(tenantId))
        {
            throw new ServiceException("tenantId is required", HttpStatus.BAD_REQUEST);
        }
        if (fields == null)
        {
            fields = new HashMap<>();
        }

        CrmWebchatConfig existing = configMapper.selectByTenantId(tenantId);
        Date now = new Date();
        String operator = safeUsername();

        String domainsJson;
        if (fields.containsKey("allowedDomains"))
        {
            List<String> domains = normalizeDomains(fields.get("allowedDomains"));
            domainsJson = WebchatOriginValidator.toJsonArray(domains);
        }
        else if (existing != null && StringUtils.hasText(existing.getAllowedDomains()))
        {
            domainsJson = existing.getAllowedDomains();
        }
        else
        {
            domainsJson = "[]";
        }

        CrmWebchatConfig row = new CrmWebchatConfig();
        row.setTenantId(tenantId);
        row.setEnabled(toIntFlag(fields.get("enabled"), existing != null ? existing.getEnabled() : 1));
        row.setDisplayName(strOr(fields.get("displayName"), existing != null ? existing.getDisplayName() : null));
        row.setLogoUrl(strOr(fields.get("logoUrl"), existing != null ? existing.getLogoUrl() : null));
        row.setWelcomeMessage(strOr(fields.get("welcomeMessage"),
                existing != null ? existing.getWelcomeMessage() : null));
        row.setThemeColor(strOr(fields.get("themeColor"), existing != null ? existing.getThemeColor() : null));
        row.setPosition(strOr(fields.get("position"),
                existing != null && StringUtils.hasText(existing.getPosition())
                        ? existing.getPosition() : "bottom-right"));
        row.setAllowedDomains(domainsJson);
        row.setAiEnabled(toIntFlag(fields.get("aiEnabled"), existing != null ? existing.getAiEnabled() : 0));
        row.setOfflineMessage(strOr(fields.get("offlineMessage"),
                existing != null ? existing.getOfflineMessage() : null));
        row.setUpdateBy(operator);
        row.setUpdateTime(now);

        if (existing == null)
        {
            row.setWebsiteToken(generateWebsiteToken());
            row.setCreateBy(operator);
            row.setCreateTime(now);
        }
        else
        {
            // keep existing token; ON CONFLICT does not overwrite website_token
            row.setWebsiteToken(existing.getWebsiteToken());
            row.setCreateBy(existing.getCreateBy());
            row.setCreateTime(existing.getCreateTime());
        }

        configMapper.insertOrUpdate(row);
        CrmWebchatConfig saved = configMapper.selectByTenantId(tenantId);
        return toAdminMap(saved);
    }

    @Override
    public Map<String, Object> rotateToken(String tenantId)
    {
        CrmWebchatConfig existing = configMapper.selectByTenantId(tenantId);
        if (existing == null)
        {
            throw new ServiceException("Webchat configuration not found", HttpStatus.NOT_FOUND);
        }
        String newToken = generateWebsiteToken();
        configMapper.updateWebsiteToken(tenantId, newToken, safeUsername());
        CrmWebchatConfig saved = configMapper.selectByTenantId(tenantId);
        return toAdminMap(saved);
    }

    private Map<String, Object> toAdminMap(CrmWebchatConfig config)
    {
        Map<String, Object> data = new HashMap<>();
        if (config == null)
        {
            return data;
        }
        data.put("id", config.getId());
        data.put("websiteToken", config.getWebsiteToken());
        data.put("enabled", config.getEnabled() != null && config.getEnabled() == 1);
        data.put("displayName", config.getDisplayName());
        data.put("logoUrl", config.getLogoUrl());
        data.put("welcomeMessage", config.getWelcomeMessage());
        data.put("themeColor", config.getThemeColor());
        data.put("position", config.getPosition());
        data.put("allowedDomains", parseDomainsList(config.getAllowedDomains()));
        data.put("aiEnabled", config.getAiEnabled() != null && config.getAiEnabled() == 1);
        data.put("offlineMessage", config.getOfflineMessage());
        data.put("embedSnippetHint", buildEmbedSnippetHint(config.getWebsiteToken()));
        data.put("updatedAt", config.getUpdateTime());
        return data;
    }

    private String buildEmbedSnippetHint(String token)
    {
        String origin = StringUtils.hasText(publicOriginHint) ? publicOriginHint.trim().replaceAll("/$", "") : "";
        String path = StringUtils.hasText(embedPath) ? embedPath.trim() : "/embed/widget.js";
        if (!path.startsWith("/"))
        {
            path = "/" + path;
        }
        String src = origin + path;
        String tok = StringUtils.hasText(token) ? token : "wc_xxx";
        return "<script src=\"" + src + "\" async></script>\n"
                + "<script>\n"
                + "  window.addEventListener(\"load\", function () {\n"
                + "    MihootCRMChat.init({ token: \"" + tok + "\" });\n"
                + "  });\n"
                + "</script>";
    }

    public static String generateWebsiteToken()
    {
        byte[] buf = new byte[18];
        RANDOM.nextBytes(buf);
        return "wc_" + Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }

    @SuppressWarnings("unchecked")
    private static List<String> normalizeDomains(Object raw)
    {
        List<String> out = new ArrayList<>();
        if (raw == null)
        {
            return out;
        }
        List<?> list;
        if (raw instanceof List)
        {
            list = (List<?>) raw;
        }
        else if (raw instanceof String)
        {
            String s = ((String) raw).trim();
            if (s.startsWith("["))
            {
                list = JSON.parseArray(s);
            }
            else if (s.isEmpty())
            {
                return out;
            }
            else
            {
                list = java.util.Collections.singletonList(s);
            }
        }
        else
        {
            throw new ServiceException("allowedDomains must be an array", HttpStatus.BAD_REQUEST);
        }
        for (Object item : list)
        {
            if (item == null)
            {
                continue;
            }
            try
            {
                String host = WebchatOriginValidator.normalizeHostname(String.valueOf(item));
                if (host != null && !out.contains(host))
                {
                    out.add(host);
                }
            }
            catch (IllegalArgumentException e)
            {
                throw new ServiceException(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
        return out;
    }

    private static List<String> parseDomainsList(String json)
    {
        return new ArrayList<>(WebchatOriginValidator.parseAllowedDomains(json));
    }

    private static Integer toIntFlag(Object raw, Integer fallback)
    {
        if (raw == null)
        {
            return fallback != null ? fallback : 0;
        }
        if (raw instanceof Boolean)
        {
            return Boolean.TRUE.equals(raw) ? 1 : 0;
        }
        if (raw instanceof Number)
        {
            return ((Number) raw).intValue() != 0 ? 1 : 0;
        }
        String s = String.valueOf(raw).trim();
        if ("true".equalsIgnoreCase(s) || "1".equals(s))
        {
            return 1;
        }
        if ("false".equalsIgnoreCase(s) || "0".equals(s))
        {
            return 0;
        }
        return fallback != null ? fallback : 0;
    }

    private static String strOr(Object raw, String fallback)
    {
        if (raw == null)
        {
            return fallback;
        }
        String s = String.valueOf(raw).trim();
        return s.isEmpty() ? null : s;
    }

    private static String safeUsername()
    {
        try
        {
            return SecurityUtils.getUsername();
        }
        catch (Exception e)
        {
            return "system";
        }
    }
}
