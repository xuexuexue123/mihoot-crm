package com.ruoyi.crm.webchat;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;

/**
 * HMAC-SHA256 signed opaque session token: payload visitorId|tenantId|exp, Base64URL.
 */
@Component
public class WebchatSessionTokenService
{
    private static final String DEFAULT_DEV_KEY = "sdjfjsk-dev123123-session-signing-key-change-me123123123123";
    private static final long TTL_MS = TimeUnit.DAYS.toMillis(7);

    @Value("${webchat.session-signing-key:}")
    private String signingKey;

    public IssuedToken issue(String tenantId, String visitorId)
    {
        if (!StringUtils.hasText(tenantId) || !StringUtils.hasText(visitorId))
        {
            throw new ServiceException("tenantId and visitorId are required", HttpStatus.BAD_REQUEST);
        }
        long exp = System.currentTimeMillis() + TTL_MS;
        String payload = visitorId.trim() + "|" + tenantId.trim() + "|" + exp;
        String payloadB64 = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        String sig = sign(payloadB64);
        String token = payloadB64 + "." + sig;
        return new IssuedToken(token, exp);
    }

    public SessionClaims verify(String token)
    {
        if (!StringUtils.hasText(token))
        {
            throw new ServiceException("sessionToken is required", HttpStatus.UNAUTHORIZED);
        }
        String raw = token.trim();
        int dot = raw.lastIndexOf('.');
        if (dot <= 0 || dot >= raw.length() - 1)
        {
            throw new ServiceException("invalid sessionToken", HttpStatus.UNAUTHORIZED);
        }
        String payloadB64 = raw.substring(0, dot);
        String sig = raw.substring(dot + 1);
        String expected = sign(payloadB64);
        if (!constantTimeEquals(expected, sig))
        {
            throw new ServiceException("invalid sessionToken", HttpStatus.UNAUTHORIZED);
        }
        String payload;
        try
        {
            payload = new String(Base64.getUrlDecoder().decode(payloadB64), StandardCharsets.UTF_8);
        }
        catch (Exception e)
        {
            throw new ServiceException("invalid sessionToken", HttpStatus.UNAUTHORIZED);
        }
        String[] parts = payload.split("\\|", 3);
        if (parts.length != 3)
        {
            throw new ServiceException("invalid sessionToken", HttpStatus.UNAUTHORIZED);
        }
        long exp;
        try
        {
            exp = Long.parseLong(parts[2]);
        }
        catch (NumberFormatException e)
        {
            throw new ServiceException("invalid sessionToken", HttpStatus.UNAUTHORIZED);
        }
        if (System.currentTimeMillis() > exp)
        {
            throw new ServiceException("sessionToken expired", HttpStatus.UNAUTHORIZED);
        }
        return new SessionClaims(parts[0], parts[1], exp);
    }

    private String sign(String payloadB64)
    {
        try
        {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(resolveKey().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal(payloadB64.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        }
        catch (Exception e)
        {
            throw new ServiceException("session signing failed", HttpStatus.ERROR);
        }
    }

    private String resolveKey()
    {
        return StringUtils.hasText(signingKey) ? signingKey.trim() : DEFAULT_DEV_KEY;
    }

    private static boolean constantTimeEquals(String a, String b)
    {
        if (a == null || b == null)
        {
            return false;
        }
        byte[] x = a.getBytes(StandardCharsets.UTF_8);
        byte[] y = b.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(x, y);
    }

    public static final class IssuedToken
    {
        private final String token;
        private final long expiresAtEpochMs;

        public IssuedToken(String token, long expiresAtEpochMs)
        {
            this.token = token;
            this.expiresAtEpochMs = expiresAtEpochMs;
        }

        public String getToken() { return token; }
        public long getExpiresAtEpochMs() { return expiresAtEpochMs; }
    }

    public static final class SessionClaims
    {
        private final String visitorId;
        private final String tenantId;
        private final long expiresAtEpochMs;

        public SessionClaims(String visitorId, String tenantId, long expiresAtEpochMs)
        {
            this.visitorId = visitorId;
            this.tenantId = tenantId;
            this.expiresAtEpochMs = expiresAtEpochMs;
        }

        public String getVisitorId() { return visitorId; }
        public String getTenantId() { return tenantId; }
        public long getExpiresAtEpochMs() { return expiresAtEpochMs; }
    }
}
