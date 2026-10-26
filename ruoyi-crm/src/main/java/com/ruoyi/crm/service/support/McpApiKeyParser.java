package com.ruoyi.crm.service.support;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ruoyi.common.utils.StringUtils;

/**
 * 解析 MCP API Key 明文：mcp_&lt;tenantCode&gt;_&lt;publicId&gt;_&lt;secret&gt;
 */
public final class McpApiKeyParser
{
    private static final Pattern PATTERN =
            Pattern.compile("^mcp_([A-Za-z0-9_-]{8})_([a-f0-9]{12})_(.+)$");

    private McpApiKeyParser()
    {
    }

    public static Optional<ParsedMcpKey> parse(String raw)
    {
        if (StringUtils.isBlank(raw))
        {
            return Optional.empty();
        }
        Matcher matcher = PATTERN.matcher(raw.trim());
        if (!matcher.matches())
        {
            return Optional.empty();
        }
        return Optional.of(new ParsedMcpKey(
                matcher.group(1),
                "mcp_" + matcher.group(2),
                matcher.group(3)));
    }

    public static final class ParsedMcpKey
    {
        private final String tenantCode;
        private final String keyPrefix;
        private final String secret;

        ParsedMcpKey(String tenantCode, String keyPrefix, String secret)
        {
            this.tenantCode = tenantCode;
            this.keyPrefix = keyPrefix;
            this.secret = secret;
        }

        public String getTenantCode()
        {
            return tenantCode;
        }

        public String getKeyPrefix()
        {
            return keyPrefix;
        }

        public String getSecret()
        {
            return secret;
        }
    }
}
