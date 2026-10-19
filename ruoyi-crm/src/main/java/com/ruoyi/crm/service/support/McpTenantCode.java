package com.ruoyi.crm.service.support;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.StringUtils;

/**
 * MCP API Key tenantCode：HMAC-SHA256(obfuscationSecret, tenantId) → base64url 前 8 位
 */
@Component
public class McpTenantCode
{
    private static final String PREFIX = "mcp_";
    private static final int TENANT_CODE_LENGTH = 8;

    @Value("${mcp.tenant-obfuscation-secret:}")
    private String obfuscationSecret;

    public String compute(String tenantId)
    {
        if (StringUtils.isBlank(tenantId))
        {
            throw new IllegalArgumentException("tenantId is blank");
        }
        if (StringUtils.isBlank(obfuscationSecret))
        {
            throw new IllegalStateException("mcp.tenant-obfuscation-secret is not configured");
        }
        return compute(tenantId.trim(), obfuscationSecret);
    }

    public static String compute(String tenantId, String obfuscationSecret)
    {
        if (StringUtils.isBlank(tenantId) || StringUtils.isBlank(obfuscationSecret))
        {
            throw new IllegalArgumentException("tenantId and obfuscationSecret are required");
        }
        try
        {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(obfuscationSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal(tenantId.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(digest)
                    .substring(0, TENANT_CODE_LENGTH);
        }
        catch (Exception ex)
        {
            throw new IllegalStateException("HmacSHA256 unavailable", ex);
        }
    }

    public String displayPrefix(String tenantCode, String keyPrefix)
    {
        if (StringUtils.isBlank(tenantCode) || StringUtils.isBlank(keyPrefix))
        {
            return null;
        }
        if (!keyPrefix.startsWith(PREFIX))
        {
            return null;
        }
        String publicId = keyPrefix.substring(PREFIX.length());
        return PREFIX + tenantCode + "_" + publicId;
    }
}
