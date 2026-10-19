package com.ruoyi.crm.service.support;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * MCP API Key secret 哈希（SHA-256 hex，与 sevencrm stub 一致）
 */
public final class McpApiKeyHashUtil
{
    private McpApiKeyHashUtil()
    {
    }

    public static String hashSecret(String secret)
    {
        if (secret == null)
        {
            return "";
        }
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(secret.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash)
            {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }
        catch (Exception ex)
        {
            throw new IllegalStateException("SHA-256 unavailable", ex);
        }
    }

    public static boolean verifySecret(String secret, String expectedHash)
    {
        if (expectedHash == null)
        {
            return false;
        }
        return expectedHash.equals(hashSecret(secret));
    }
}
