package com.ruoyi.crm.service.support;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.tenant.config.TenantWebsiteProperties;

/**
 * 官网 Preview signed token：HMAC-SHA256 + base64url payload
 */
@Component
public class WebsitePreviewTokenService
{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private TenantWebsiteProperties tenantWebsiteProperties;

    public static final class PreviewClaims
    {
        private final String tenantName;
        private final String pageId;
        private final String path;
        private final long exp;

        public PreviewClaims(String tenantName, String pageId, String path, long exp)
        {
            this.tenantName = tenantName;
            this.pageId = pageId;
            this.path = path;
            this.exp = exp;
        }

        public String getTenantName()
        {
            return tenantName;
        }

        public String getPageId()
        {
            return pageId;
        }

        public String getPath()
        {
            return path;
        }

        public long getExp()
        {
            return exp;
        }

        public Instant getExpiresAt()
        {
            return Instant.ofEpochSecond(exp);
        }
    }

    public boolean isSigningConfigured()
    {
        return StringUtils.isNotBlank(tenantWebsiteProperties.getPreviewTokenSecret());
    }

    public PreviewTokenIssueResult issue(String tenantName, String pageId, String path)
    {
        if (!isSigningConfigured())
        {
            throw new ServiceException("preview token secret not configured", HttpStatus.ERROR);
        }
        long ttlMinutes = tenantWebsiteProperties.getPreviewTokenTtlMinutes();
        if (ttlMinutes <= 0)
        {
            ttlMinutes = 15;
        }
        long exp = Instant.now().getEpochSecond() + ttlMinutes * 60L;
        String token = sign(tenantName, pageId, path, exp, tenantWebsiteProperties.getPreviewTokenSecret());
        return new PreviewTokenIssueResult(token, Instant.ofEpochSecond(exp));
    }

    public PreviewClaims verify(String token, String slug, String path, String expectedTenantName)
    {
        if (!isSigningConfigured())
        {
            throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
        }
        PreviewClaims claims = verifyToken(token, tenantWebsiteProperties.getPreviewTokenSecret());
        if (expectedTenantName == null || !expectedTenantName.equals(claims.getTenantName()))
        {
            throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
        }
        String normalizedPath = normalizePathForCompare(path);
        if (!claims.getPath().equals(normalizedPath))
        {
            throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
        }
        return claims;
    }

    public static final class PreviewTokenIssueResult
    {
        private final String token;
        private final Instant expiresAt;

        public PreviewTokenIssueResult(String token, Instant expiresAt)
        {
            this.token = token;
            this.expiresAt = expiresAt;
        }

        public String getToken()
        {
            return token;
        }

        public Instant getExpiresAt()
        {
            return expiresAt;
        }
    }

    static String sign(String tenantName, String pageId, String path, long expEpochSec, String secret)
    {
        if (StringUtils.isBlank(tenantName) || StringUtils.isBlank(pageId) || StringUtils.isBlank(path)
                || StringUtils.isBlank(secret))
        {
            throw new IllegalArgumentException("tenantName, pageId, path and secret are required");
        }
        String payloadJson = buildPayloadJson(tenantName, pageId, normalizePathForCompare(path), expEpochSec);
        byte[] payloadBytes = payloadJson.getBytes(StandardCharsets.UTF_8);
        byte[] sig = hmacSha256(payloadBytes, secret);
        return base64UrlEncode(payloadBytes) + "." + base64UrlEncode(sig);
    }

    static PreviewClaims verifyToken(String token, String secret)
    {
        if (StringUtils.isBlank(token))
        {
            throw new ServiceException("Missing required parameters", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(secret))
        {
            throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
        }

        String[] parts = token.split("\\.", 2);
        if (parts.length != 2)
        {
            throw new ServiceException("Invalid token format", HttpStatus.BAD_REQUEST);
        }

        byte[] payloadBytes;
        byte[] actualSig;
        try
        {
            payloadBytes = base64UrlDecode(parts[0]);
            actualSig = base64UrlDecode(parts[1]);
        }
        catch (IllegalArgumentException ex)
        {
            throw new ServiceException("Invalid token format", HttpStatus.BAD_REQUEST);
        }

        byte[] expectedSig = hmacSha256(payloadBytes, secret);
        if (!MessageDigest.isEqual(expectedSig, actualSig))
        {
            throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
        }

        try
        {
            JsonNode node = OBJECT_MAPPER.readTree(payloadBytes);
            if (node == null || !node.has("t") || !node.has("p") || !node.has("path") || !node.has("exp")
                    || node.has("e") || node.has("s"))
            {
                throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
            }
            long exp = node.get("exp").asLong();
            if (Instant.now().getEpochSecond() > exp)
            {
                throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
            }
            return new PreviewClaims(
                    node.get("t").asText(),
                    node.get("p").asText(),
                    node.get("path").asText(),
                    exp);
        }
        catch (ServiceException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
        }
    }

    static String buildPayloadJson(String tenantName, String pageId, String path, long expEpochSec)
    {
        return String.format("{\"t\":\"%s\",\"p\":\"%s\",\"path\":\"%s\",\"exp\":%d}",
                escapeJson(tenantName), escapeJson(pageId), escapeJson(path), expEpochSec);
    }

    static String escapeJson(String value)
    {
        if (value == null)
        {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static String normalizePathForCompare(String path)
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

    private static byte[] hmacSha256(byte[] payloadBytes, String secret)
    {
        try
        {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return mac.doFinal(payloadBytes);
        }
        catch (Exception ex)
        {
            throw new IllegalStateException("HmacSHA256 unavailable", ex);
        }
    }

    private static String base64UrlEncode(byte[] bytes)
    {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static byte[] base64UrlDecode(String value)
    {
        return Base64.getUrlDecoder().decode(value);
    }
}
