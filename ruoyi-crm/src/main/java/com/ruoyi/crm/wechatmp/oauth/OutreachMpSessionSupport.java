package com.ruoyi.crm.wechatmp.oauth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;

/**
 * HttpOnly cookie outreach_mp_session：HMAC 签名 JSON
 */
@Component
public class OutreachMpSessionSupport
{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String COOKIE_NAME = "outreach_mp_session";

    @Value("${outreach.checkin.signing-secret:}")
    private String checkinSigningSecret;

    @Value("${outreach.registration.signing-secret:}")
    private String registrationSigningSecret;

    public static String cookieName()
    {
        return COOKIE_NAME;
    }

    public String encode(OutreachMpSessionPayload payload)
    {
        try
        {
            byte[] json = OBJECT_MAPPER.writeValueAsBytes(payload);
            byte[] sig = hmacSha256(json, requireSecret());
            return base64UrlEncode(json) + "." + base64UrlEncode(sig);
        }
        catch (Exception ex)
        {
            throw new IllegalStateException("Failed to encode mp session", ex);
        }
    }

    public OutreachMpSessionPayload decode(String cookieValue)
    {
        if (StringUtils.isBlank(cookieValue))
        {
            return null;
        }
        String[] parts = cookieValue.split("\\.", 2);
        if (parts.length != 2)
        {
            return null;
        }
        try
        {
            byte[] json = base64UrlDecode(parts[0]);
            byte[] actualSig = base64UrlDecode(parts[1]);
            byte[] expectedSig = hmacSha256(json, requireSecret());
            if (!MessageDigest.isEqual(expectedSig, actualSig))
            {
                return null;
            }
            OutreachMpSessionPayload payload = OBJECT_MAPPER.readValue(json, OutreachMpSessionPayload.class);
            if (payload == null || Instant.now().getEpochSecond() > payload.getExp())
            {
                return null;
            }
            return payload;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public static String maskOpenId(String openId)
    {
        if (StringUtils.isBlank(openId) || openId.length() <= 4)
        {
            return "*";
        }
        return openId.substring(0, 2) + "***" + openId.substring(openId.length() - 2);
    }

    private String requireSecret()
    {
        if (StringUtils.isNotBlank(checkinSigningSecret))
        {
            return checkinSigningSecret;
        }
        if (StringUtils.isNotBlank(registrationSigningSecret))
        {
            return registrationSigningSecret;
        }
        throw new ServiceException("signing secret not configured", HttpStatus.ERROR);
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
