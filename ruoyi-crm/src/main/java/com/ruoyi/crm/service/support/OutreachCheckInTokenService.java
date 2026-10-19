package com.ruoyi.crm.service.support;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.domain.CrmEventSession;

/**
 * 现场自助签到 token：typ=event_checkin，独立于 registrationToken / ticketToken
 */
@Component
public class OutreachCheckInTokenService
{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String CHECKIN_TYPE = "event_checkin";
    private static final long MIN_EXP_SECONDS = 24 * 60 * 60;
    private static final long END_TIME_BUFFER_SECONDS = 24 * 60 * 60;

    @Value("${outreach.checkin.signing-secret:}")
    private String checkinSigningSecret;

    @Value("${outreach.registration.signing-secret:}")
    private String registrationSigningSecret;

    @Value("${outreach.registration.public-app-url:}")
    private String publicAppUrl;

    public static final class VerifiedCheckInPayload
    {
        private final String tenantId;
        private final String eventId;
        private final String sessionId;
        private final String openId;
        private final String appId;
        private final long exp;

        public VerifiedCheckInPayload(String tenantId, String eventId, String sessionId,
                String openId, String appId, long exp)
        {
            this.tenantId = tenantId;
            this.eventId = eventId;
            this.sessionId = sessionId;
            this.openId = openId;
            this.appId = appId;
            this.exp = exp;
        }

        public String getTenantId() { return tenantId; }
        public String getEventId() { return eventId; }
        public String getSessionId() { return sessionId; }
        public String getOpenId() { return openId; }
        public String getAppId() { return appId; }
        public long getExp() { return exp; }
    }

    public boolean isSigningConfigured()
    {
        return StringUtils.isNotBlank(checkinSigningSecret) || StringUtils.isNotBlank(registrationSigningSecret);
    }

    public String getPublicAppUrl()
    {
        return publicAppUrl;
    }

    public String sign(String tenantId, String eventId, String sessionId, long expEpochSec)
    {
        return sign(tenantId, eventId, sessionId, expEpochSec, null, null, requireSecret());
    }

    public VerifiedCheckInPayload verify(String token)
    {
        return verify(token, requireSecret());
    }

    public long computeExp(CrmEventSession session, CrmEvent event)
    {
        long now = Instant.now().getEpochSecond();
        long sessionEnd = session != null && session.getEndTime() != null
                ? session.getEndTime().toInstant().getEpochSecond()
                : 0L;
        long eventEnd = event != null && event.getEndTime() != null
                ? event.getEndTime().toInstant().getEpochSecond()
                : 0L;
        long baseEnd = Math.max(sessionEnd, eventEnd);
        long exp = baseEnd > 0 ? baseEnd + END_TIME_BUFFER_SECONDS : now + MIN_EXP_SECONDS;
        return Math.max(exp, now + MIN_EXP_SECONDS);
    }

    public String buildCheckInUrl(String tenantId, String eventId, String sessionId, long expEpochSec)
    {
        String baseUrl = normalizePublicAppUrl(publicAppUrl);
        if (StringUtils.isBlank(baseUrl))
        {
            return "";
        }
        String token = sign(tenantId, eventId, sessionId, expEpochSec);
        try
        {
            return baseUrl + "/event-check-in?token=" + URLEncoder.encode(token, "UTF-8");
        }
        catch (UnsupportedEncodingException ex)
        {
            throw new IllegalStateException("UTF-8 not supported", ex);
        }
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

    static String sign(String tenantId, String eventId, String sessionId, long expEpochSec,
            String openId, String appId, String secret)
    {
        if (StringUtils.isBlank(secret))
        {
            throw new ServiceException("signing secret not configured", HttpStatus.ERROR);
        }
        boolean hasOpenId = StringUtils.isNotBlank(openId);
        boolean hasAppId = StringUtils.isNotBlank(appId);
        if (hasOpenId != hasAppId)
        {
            throw new IllegalArgumentException("openId and appId must appear together");
        }
        StringBuilder json = new StringBuilder();
        json.append("{\"typ\":\"").append(CHECKIN_TYPE).append("\"");
        json.append(",\"t\":\"").append(escapeJson(tenantId)).append("\"");
        json.append(",\"e\":\"").append(escapeJson(eventId)).append("\"");
        if (StringUtils.isNotBlank(sessionId))
        {
            json.append(",\"s\":\"").append(escapeJson(sessionId)).append("\"");
        }
        json.append(",\"exp\":").append(expEpochSec);
        if (hasOpenId)
        {
            json.append(",\"o\":\"").append(escapeJson(openId)).append("\"");
            json.append(",\"a\":\"").append(escapeJson(appId)).append("\"");
        }
        json.append("}");
        byte[] payloadBytes = json.toString().getBytes(StandardCharsets.UTF_8);
        byte[] sig = hmacSha256(payloadBytes, secret);
        return base64UrlEncode(payloadBytes) + "." + base64UrlEncode(sig);
    }

    static VerifiedCheckInPayload verify(String token, String secret)
    {
        if (StringUtils.isBlank(token))
        {
            throw new ServiceException("Missing check-in token", HttpStatus.BAD_REQUEST);
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
            if (node == null || !node.has("t") || !node.has("e") || !node.has("exp") || !node.has("typ"))
            {
                throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
            }
            if (!CHECKIN_TYPE.equals(node.get("typ").asText()))
            {
                throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
            }
            long exp = node.get("exp").asLong();
            if (Instant.now().getEpochSecond() > exp)
            {
                throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
            }
            String openId = node.has("o") && !node.get("o").isNull() ? node.get("o").asText() : null;
            String appId = node.has("a") && !node.get("a").isNull() ? node.get("a").asText() : null;
            if (StringUtils.isNotBlank(openId) != StringUtils.isNotBlank(appId))
            {
                throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
            }
            String sessionId = node.has("s") && !node.get("s").isNull() ? node.get("s").asText() : null;
            return new VerifiedCheckInPayload(
                    node.get("t").asText(),
                    node.get("e").asText(),
                    sessionId,
                    openId,
                    appId,
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

    private static String normalizePublicAppUrl(String url)
    {
        if (StringUtils.isBlank(url))
        {
            return "";
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
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
