package com.ruoyi.crm.service.support;

import java.security.SecureRandom;
import java.util.Base64;

import com.ruoyi.common.utils.StringUtils;

/**
 * 生成 MCP API Key 明文：mcp_&lt;tenantCode&gt;_&lt;publicId&gt;_&lt;secret&gt;
 */
public final class McpApiKeyMaterialGenerator
{
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String PREFIX = "mcp_";

    private McpApiKeyMaterialGenerator()
    {
    }

    public static final class GeneratedMaterial
    {
        private final String keyPrefix;
        private final String secret;
        private final String apiKey;
        private final String keyHash;

        GeneratedMaterial(String keyPrefix, String secret, String apiKey, String keyHash)
        {
            this.keyPrefix = keyPrefix;
            this.secret = secret;
            this.apiKey = apiKey;
            this.keyHash = keyHash;
        }

        public String getKeyPrefix()
        {
            return keyPrefix;
        }

        public String getSecret()
        {
            return secret;
        }

        public String getApiKey()
        {
            return apiKey;
        }

        public String getKeyHash()
        {
            return keyHash;
        }
    }

    public static GeneratedMaterial generate(String tenantCode)
    {
        if (StringUtils.isBlank(tenantCode))
        {
            throw new IllegalArgumentException("tenantCode is blank");
        }
        String code = tenantCode.trim();

        byte[] publicBytes = new byte[6];
        RANDOM.nextBytes(publicBytes);
        StringBuilder hex = new StringBuilder(12);
        for (byte b : publicBytes)
        {
            hex.append(String.format("%02x", b));
        }
        String publicId = hex.toString();
        String keyPrefix = PREFIX + publicId;

        byte[] secretBytes = new byte[24];
        RANDOM.nextBytes(secretBytes);
        String secret = Base64.getUrlEncoder().withoutPadding().encodeToString(secretBytes);
        String apiKey = PREFIX + code + "_" + publicId + "_" + secret;
        String keyHash = McpApiKeyHashUtil.hashSecret(secret);
        return new GeneratedMaterial(keyPrefix, secret, apiKey, keyHash);
    }
}
