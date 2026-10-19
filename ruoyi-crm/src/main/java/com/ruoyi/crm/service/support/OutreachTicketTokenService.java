package com.ruoyi.crm.service.support;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmEventSession;

/**
 * 活动票券 token：typ=event_ticket，独立于报名 registrationToken
 */
@Component
public class OutreachTicketTokenService
{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String TICKET_TYPE = "event_ticket";
    private static final long MIN_EXP_SECONDS = 24 * 60 * 60;
    private static final long END_TIME_BUFFER_SECONDS = 24 * 60 * 60;

    @Value("${outreach.ticket.signing-secret:}")
    private String ticketSigningSecret;

    @Value("${outreach.registration.signing-secret:}")
    private String registrationSigningSecret;

    public static final class VerifiedTicketPayload
    {
        private final String tenantId;
        private final String eventId;
        private final String registrationId;
        private final String sessionId;
        private final long exp;

        public VerifiedTicketPayload(String tenantId, String eventId, String registrationId,
                String sessionId, long exp)
        {
            this.tenantId = tenantId;
            this.eventId = eventId;
            this.registrationId = registrationId;
            this.sessionId = sessionId;
            this.exp = exp;
        }

        public String getTenantId() { return tenantId; }
        public String getEventId() { return eventId; }
        public String getRegistrationId() { return registrationId; }
        public String getSessionId() { return sessionId; }
        public long getExp() { return exp; }
    }

    public String sign(String tenantId, String eventId, String registrationId, String sessionId,
            CrmEventSession session)
    {
        long exp = computeExp(session);
        return sign(tenantId, eventId, registrationId, sessionId, exp, requireSecret());
    }

    public VerifiedTicketPayload verify(String token, String expectedEventId)
    {
        return verify(token, expectedEventId, requireSecret());
    }

    public static String hashToken(String token)
    {
        if (StringUtils.isBlank(token))
        {
            return null;
        }
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
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

    public long computeExp(CrmEventSession session)
    {
        long now = Instant.now().getEpochSecond();
        long exp;
        if (session != null && session.getEndTime() != null)
        {
            exp = session.getEndTime().toInstant().getEpochSecond() + END_TIME_BUFFER_SECONDS;
        }
        else
        {
            exp = now + MIN_EXP_SECONDS;
        }
        return Math.max(exp, now + MIN_EXP_SECONDS);
    }

    private String requireSecret()
    {
        if (StringUtils.isNotBlank(ticketSigningSecret))
        {
            return ticketSigningSecret;
        }
        if (StringUtils.isNotBlank(registrationSigningSecret))
        {
            return registrationSigningSecret;
        }
        throw new ServiceException("signing secret not configured", HttpStatus.ERROR);
    }

    static String sign(String tenantId, String eventId, String registrationId, String sessionId,
            long expEpochSec, String secret)
    {
        if (StringUtils.isBlank(secret))
        {
            throw new ServiceException("signing secret not configured", HttpStatus.ERROR);
        }
        String payloadJson = String.format(
                "{\"t\":\"%s\",\"e\":\"%s\",\"r\":\"%s\",\"s\":\"%s\",\"exp\":%d,\"typ\":\"%s\"}",
                escapeJson(tenantId), escapeJson(eventId), escapeJson(registrationId),
                escapeJson(sessionId), expEpochSec, TICKET_TYPE);
        byte[] payloadBytes = payloadJson.getBytes(StandardCharsets.UTF_8);
        byte[] sig = hmacSha256(payloadBytes, secret);
        return base64UrlEncode(payloadBytes) + "." + base64UrlEncode(sig);
    }

    static VerifiedTicketPayload verify(String token, String expectedEventId, String secret)
    {
        if (StringUtils.isBlank(token))
        {
            throw new ServiceException("Missing ticket token", HttpStatus.BAD_REQUEST);
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
            if (node == null || !node.has("t") || !node.has("e") || !node.has("r")
                    || !node.has("s") || !node.has("exp") || !node.has("typ"))
            {
                throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
            }
            if (!TICKET_TYPE.equals(node.get("typ").asText()))
            {
                throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
            }
            long exp = node.get("exp").asLong();
            if (Instant.now().getEpochSecond() > exp)
            {
                throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
            }
            String eventId = node.get("e").asText();
            if (StringUtils.isNotBlank(expectedEventId) && !expectedEventId.equals(eventId))
            {
                throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
            }
            return new VerifiedTicketPayload(
                    node.get("t").asText(),
                    eventId,
                    node.get("r").asText(),
                    node.get("s").asText(),
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
