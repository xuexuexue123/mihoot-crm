package com.ruoyi.crm.service.support;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Collections;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.tenant.config.TenantWebsiteProperties;

@Component
public class TenantWebsiteSchemaValidator
{
    public static final int SUPPORTED_SCHEMA_VERSION = 1;

    private static final String[] HTML_KEY_FRAGMENTS = { "html", "htmlcontent", "publishedhtml" };

    private static final String[] HTML_VALUE_FRAGMENTS = { "<html", "<script", "<iframe" };

    private static final Set<String> ALLOWED_BLOCK_TYPES;

    static
    {
        Set<String> types = new HashSet<>();
        types.add("navbar");
        types.add("hero");
        types.add("featureGrid");
        types.add("featureCard");
        types.add("logoBar");
        types.add("cardGrid");
        types.add("serviceList");
        types.add("teamGrid");
        types.add("testimonials");
        types.add("customerVoice");
        types.add("contact");
        types.add("contactWidget");
        types.add("onlineRegister");
        types.add("eventList");
        types.add("contentList");
        types.add("contentDetail");
        types.add("videoCard");
        types.add("videoGrid");
        types.add("doctorGrid");
        types.add("clinicCard");
        types.add("pictureGrid");
        types.add("qandACard");
        types.add("stats");
        types.add("cta");
        types.add("richText");
        types.add("pricing");
        types.add("footer");
        types.add("section");
        types.add("eventDetail");
        types.add("newsList");
        types.add("newsContent");
        ALLOWED_BLOCK_TYPES = Collections.unmodifiableSet(types);
    }

    @Autowired
    private TenantWebsiteProperties tenantWebsiteProperties;

    public void validateSchemaLight(JsonNode schema, String expectedPath)
    {
        if (schema == null || schema.isNull() || !schema.isObject())
        {
            throw new ServiceException("INVALID_SCHEMA", HttpStatus.BAD_REQUEST);
        }
        rejectHtmlKeys(schema);
        requireFields(schema, "version", "meta", "blocks");
        if (!schema.path("version").isInt())
        {
            throw new ServiceException("INVALID_SCHEMA", HttpStatus.BAD_REQUEST);
        }
        int version = schema.path("version").asInt();
        if (version > SUPPORTED_SCHEMA_VERSION)
        {
            throw new ServiceException("INVALID_SCHEMA", HttpStatus.BAD_REQUEST);
        }
        if (!schema.path("blocks").isArray())
        {
            throw new ServiceException("INVALID_SCHEMA", HttpStatus.BAD_REQUEST);
        }
        validateBlockTypes(schema.path("blocks"));
        rejectHtmlInProps(schema.path("blocks"));
        if (expectedPath != null)
        {
            String metaPath = schema.path("meta").path("path").asText(null);
            if (metaPath != null && !expectedPath.equals(tenantWebsitePathNormalize(metaPath)))
            {
                throw new ServiceException("meta.path 与页面 path 不一致", HttpStatus.BAD_REQUEST);
            }
        }
        int bytes = schema.toString().getBytes(StandardCharsets.UTF_8).length;
        if (bytes > tenantWebsiteProperties.getMaxSchemaBytes())
        {
            throw new ServiceException("Schema 过大", 413);
        }
    }

    private static String tenantWebsitePathNormalize(String path)
    {
        if (StringUtils.isBlank(path))
        {
            return "/";
        }
        String p = path.trim();
        if (!p.startsWith("/"))
        {
            p = "/" + p;
        }
        if (p.length() > 1 && p.endsWith("/"))
        {
            p = p.substring(0, p.length() - 1);
        }
        return p;
    }

    private void requireFields(JsonNode schema, String... fields)
    {
        for (String field : fields)
        {
            if (!schema.has(field) || schema.get(field).isNull())
            {
                throw new ServiceException("INVALID_SCHEMA", HttpStatus.BAD_REQUEST);
            }
        }
    }

    private void validateBlockTypes(JsonNode blocks)
    {
        if (blocks == null || !blocks.isArray())
        {
            return;
        }
        for (JsonNode block : blocks)
        {
            String type = block.path("type").asText(null);
            if (type == null || !ALLOWED_BLOCK_TYPES.contains(type))
            {
                throw new ServiceException("INVALID_BLOCK_TYPE", HttpStatus.BAD_REQUEST);
            }
        }
    }

    private void rejectHtmlInProps(JsonNode node)
    {
        if (node == null)
        {
            return;
        }
        if (node.isObject())
        {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext())
            {
                Map.Entry<String, JsonNode> entry = fields.next();
                if ("props".equals(entry.getKey()))
                {
                    scanPropsForHtml(entry.getValue());
                }
                else
                {
                    rejectHtmlInProps(entry.getValue());
                }
            }
        }
        else if (node.isArray())
        {
            for (JsonNode child : node)
            {
                rejectHtmlInProps(child);
            }
        }
    }

    private void scanPropsForHtml(JsonNode node)
    {
        if (node == null)
        {
            return;
        }
        if (node.isTextual())
        {
            rejectHtmlString(node.asText());
            return;
        }
        if (node.isObject())
        {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext())
            {
                scanPropsForHtml(fields.next().getValue());
            }
            return;
        }
        if (node.isArray())
        {
            for (JsonNode child : node)
            {
                scanPropsForHtml(child);
            }
        }
    }

    private void rejectHtmlString(String value)
    {
        if (value == null)
        {
            return;
        }
        String lower = value.toLowerCase(Locale.ROOT);
        for (String fragment : HTML_VALUE_FRAGMENTS)
        {
            if (lower.contains(fragment))
            {
                throw new ServiceException("HTML_NOT_ALLOWED", HttpStatus.BAD_REQUEST);
            }
        }
    }

    private void rejectHtmlKeys(JsonNode node)
    {
        if (node == null)
        {
            return;
        }
        if (node.isObject())
        {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext())
            {
                Map.Entry<String, JsonNode> entry = fields.next();
                String keyLower = entry.getKey().toLowerCase();
                for (String fragment : HTML_KEY_FRAGMENTS)
                {
                    if (keyLower.contains(fragment))
                    {
                        throw new ServiceException("HTML_NOT_ALLOWED", HttpStatus.BAD_REQUEST);
                    }
                }
                rejectHtmlKeys(entry.getValue());
            }
        }
        else if (node.isArray())
        {
            for (JsonNode child : node)
            {
                rejectHtmlKeys(child);
            }
        }
    }
}
