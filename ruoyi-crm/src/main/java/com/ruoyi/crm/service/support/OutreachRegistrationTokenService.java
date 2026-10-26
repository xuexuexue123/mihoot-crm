package com.ruoyi.crm.service.support;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.domain.CrmSurveys;

/**
 * 活动 H5 报名 / 问卷 H5 / 认定问卷外投 无状态签名 token：HMAC-SHA256 + base64url payload
 */
@Component
public class OutreachRegistrationTokenService
{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final long MIN_EXP_SECONDS = 24 * 60 * 60;
    private static final long END_TIME_BUFFER_SECONDS = 7 * 24 * 60 * 60;
    private static final long DEFAULT_EXP_SECONDS = 365L * 24 * 60 * 60;
    private static final long QUALIFY_EXP_SECONDS = 8L * 60 * 60;
    public static final String CHANNEL_QUALIFY_QR = "qualify_qr";

    @Value("${outreach.registration.signing-secret:}")
    private String signingSecret;

    @Value("${outreach.registration.public-app-url:}")
    private String publicAppUrl;

    @Autowired
    private TenantPublicAppUrlResolver tenantPublicAppUrlResolver;

    public static final class VerifiedPayload
    {
        private final String tenantId;
        private final String eventId;
        private final long exp;
        private final String openId;
        private final String appId;

        public VerifiedPayload(String tenantId, String eventId, long exp)
        {
            this(tenantId, eventId, exp, null, null);
        }

        public VerifiedPayload(String tenantId, String eventId, long exp, String openId, String appId)
        {
            this.tenantId = tenantId;
            this.eventId = eventId;
            this.exp = exp;
            this.openId = openId;
            this.appId = appId;
        }

        public String getTenantId()
        {
            return tenantId;
        }

        public String getEventId()
        {
            return eventId;
        }

        public long getExp()
        {
            return exp;
        }

        public String getOpenId()
        {
            return openId;
        }

        public String getAppId()
        {
            return appId;
        }
    }

    public static final class SurveyVerifiedPayload
    {
        private final String tenantId;
        private final String surveyId;
        private final long exp;
        private final String openId;
        private final String appId;
        private final String contactId;
        private final String channel;

        public SurveyVerifiedPayload(String tenantId, String surveyId, long exp)
        {
            this(tenantId, surveyId, exp, null, null, null, null);
        }

        public SurveyVerifiedPayload(String tenantId, String surveyId, long exp,
                String openId, String appId, String contactId)
        {
            this(tenantId, surveyId, exp, openId, appId, contactId, null);
        }

        public SurveyVerifiedPayload(String tenantId, String surveyId, long exp,
                String openId, String appId, String contactId, String channel)
        {
            this.tenantId = tenantId;
            this.surveyId = surveyId;
            this.exp = exp;
            this.openId = openId;
            this.appId = appId;
            this.contactId = contactId;
            this.channel = channel;
        }

        public String getTenantId()
        {
            return tenantId;
        }

        public String getSurveyId()
        {
            return surveyId;
        }

        public long getExp()
        {
            return exp;
        }

        public String getOpenId()
        {
            return openId;
        }

        public String getAppId()
        {
            return appId;
        }

        public String getContactId()
        {
            return contactId;
        }

        public String getChannel()
        {
            return channel;
        }
    }

    public static final class QualifyVerifiedPayload
    {
        private final String tenantId;
        private final String leadId;
        private final String templateId;
        private final String channel;
        private final long exp;

        public QualifyVerifiedPayload(String tenantId, String leadId, String templateId, String channel, long exp)
        {
            this.tenantId = tenantId;
            this.leadId = leadId;
            this.templateId = templateId;
            this.channel = channel;
            this.exp = exp;
        }

        public String getTenantId()
        {
            return tenantId;
        }

        public String getLeadId()
        {
            return leadId;
        }

        public String getTemplateId()
        {
            return templateId;
        }

        public String getChannel()
        {
            return channel;
        }

        public long getExp()
        {
            return exp;
        }
    }

    public boolean isSigningConfigured()
    {
        return StringUtils.isNotBlank(signingSecret);
    }

    public String getPublicAppUrl()
    {
        return publicAppUrl;
    }

    public boolean hasPublicBaseUrl(String tenantId)
    {
        return tenantPublicAppUrlResolver.hasResolvableBase(tenantId);
    }

    public String resolvePublicBaseUrl(String tenantId)
    {
        return TenantPublicAppUrlResolver.normalizeOrigin(tenantPublicAppUrlResolver.resolve(tenantId));
    }

    public String sign(String tenantId, String eventId, long expEpochSec)
    {
        if (!isSigningConfigured())
        {
            throw new ServiceException("signing secret not configured", HttpStatus.ERROR);
        }
        return sign(tenantId, eventId, expEpochSec, signingSecret);
    }

    public String sign(String tenantId, String eventId, long expEpochSec, String openId, String appId)
    {
        if (!isSigningConfigured())
        {
            throw new ServiceException("signing secret not configured", HttpStatus.ERROR);
        }
        return sign(tenantId, eventId, expEpochSec, openId, appId, signingSecret);
    }

    public VerifiedPayload verify(String token)
    {
        if (!isSigningConfigured())
        {
            throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
        }
        return verify(token, signingSecret);
    }

    public String signSurvey(String tenantId, String surveyId, long expEpochSec)
    {
        if (!isSigningConfigured())
        {
            throw new ServiceException("signing secret not configured", HttpStatus.ERROR);
        }
        return signSurvey(tenantId, surveyId, expEpochSec, signingSecret);
    }

    public String signSurvey(String tenantId, String surveyId, long expEpochSec,
            String openId, String appId, String contactId)
    {
        return signSurvey(tenantId, surveyId, expEpochSec, openId, appId, contactId, null);
    }

    public String signSurvey(String tenantId, String surveyId, long expEpochSec,
            String openId, String appId, String contactId, String channel)
    {
        if (!isSigningConfigured())
        {
            throw new ServiceException("signing secret not configured", HttpStatus.ERROR);
        }
        return signSurvey(tenantId, surveyId, expEpochSec, openId, appId, contactId, channel, signingSecret);
    }

    public SurveyVerifiedPayload verifySurvey(String token)
    {
        if (!isSigningConfigured())
        {
            throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
        }
        return verifySurvey(token, signingSecret);
    }

    public String signQualify(String tenantId, String leadId, String templateId, long expEpochSec)
    {
        if (!isSigningConfigured())
        {
            throw new ServiceException("signing secret not configured", HttpStatus.ERROR);
        }
        return signQualify(tenantId, leadId, templateId, CHANNEL_QUALIFY_QR, expEpochSec, signingSecret);
    }

    public QualifyVerifiedPayload verifyQualify(String token)
    {
        if (!isSigningConfigured())
        {
            throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
        }
        return verifyQualify(token, signingSecret);
    }

    public long computeQualifyExp()
    {
        return Instant.now().getEpochSecond() + QUALIFY_EXP_SECONDS;
    }

    public String buildQualifySurveyUrl(String tenantId, String leadId, String templateId, long expEpochSec)
    {
        String baseUrl = resolvePublicBaseUrl(tenantId);
        if (StringUtils.isBlank(baseUrl))
        {
            return "";
        }
        String token = signQualify(tenantId, leadId, templateId, expEpochSec);
        try
        {
            return baseUrl + "/qualify-survey?token=" + URLEncoder.encode(token, "UTF-8");
        }
        catch (UnsupportedEncodingException ex)
        {
            throw new IllegalStateException("UTF-8 not supported", ex);
        }
    }

    public long computeSurveyExp(CrmSurveys survey)
    {
        long now = Instant.now().getEpochSecond();
        long exp = now + DEFAULT_EXP_SECONDS;
        long minExp = now + MIN_EXP_SECONDS;
        return Math.max(exp, minExp);
    }

    public String buildSurveyUrl(String tenantId, String surveyId, long expEpochSec)
    {
        return buildSurveyUrl(tenantId, surveyId, expEpochSec, null, null, null);
    }

    public String buildSurveyUrl(String tenantId, String surveyId, long expEpochSec,
            String openId, String appId, String contactId)
    {
        return buildSurveyUrl(tenantId, surveyId, expEpochSec, openId, appId, contactId, null);
    }

    public String buildSurveyUrl(String tenantId, String surveyId, long expEpochSec,
            String openId, String appId, String contactId, String channel)
    {
        String baseUrl = resolvePublicBaseUrl(tenantId);
        if (StringUtils.isBlank(baseUrl))
        {
            return "";
        }
        String token;
        if (StringUtils.isNotBlank(openId) && StringUtils.isNotBlank(appId))
        {
            token = signSurvey(tenantId, surveyId, expEpochSec, openId, appId, contactId, channel);
        }
        else if (StringUtils.isNotBlank(contactId))
        {
            token = signSurvey(tenantId, surveyId, expEpochSec, null, null, contactId, channel);
        }
        else
        {
            token = signSurvey(tenantId, surveyId, expEpochSec);
        }
        try
        {
            return baseUrl + "/surveypage?token=" + URLEncoder.encode(token, "UTF-8");
        }
        catch (UnsupportedEncodingException ex)
        {
            throw new IllegalStateException("UTF-8 not supported", ex);
        }
    }

    public long computeExp(CrmEvent event)
    {
        long now = Instant.now().getEpochSecond();
        long exp;
        if (event != null && event.getEndTime() != null)
        {
            exp = event.getEndTime().toInstant().getEpochSecond() + END_TIME_BUFFER_SECONDS;
        }
        else
        {
            exp = now + DEFAULT_EXP_SECONDS;
        }
        long minExp = now + MIN_EXP_SECONDS;
        return Math.max(exp, minExp);
    }

    public String buildRegistrationUrl(String tenantId, String eventId, long expEpochSec)
    {
        return buildRegistrationUrl(tenantId, eventId, expEpochSec, null, null);
    }

    public String buildRegistrationUrl(String tenantId, String eventId, long expEpochSec,
            String openId, String appId)
    {
        String baseUrl = resolvePublicBaseUrl(tenantId);
        if (StringUtils.isBlank(baseUrl))
        {
            return "";
        }
        String token = StringUtils.isNotBlank(openId) && StringUtils.isNotBlank(appId)
                ? sign(tenantId, eventId, expEpochSec, openId, appId)
                : sign(tenantId, eventId, expEpochSec);
        try
        {
            return baseUrl + "/event-registration?token=" + URLEncoder.encode(token, "UTF-8");
        }
        catch (UnsupportedEncodingException ex)
        {
            throw new IllegalStateException("UTF-8 not supported", ex);
        }
    }

    static String sign(String tenantId, String eventId, long expEpochSec, String secret)
    {
        return sign(tenantId, eventId, expEpochSec, null, null, secret);
    }

    static String sign(String tenantId, String eventId, long expEpochSec,
            String openId, String appId, String secret)
    {
        if (StringUtils.isBlank(tenantId) || StringUtils.isBlank(eventId) || StringUtils.isBlank(secret))
        {
            throw new IllegalArgumentException("tenantId, eventId and secret are required");
        }
        boolean hasOpenId = StringUtils.isNotBlank(openId);
        boolean hasAppId = StringUtils.isNotBlank(appId);
        if (hasOpenId != hasAppId)
        {
            throw new IllegalArgumentException("openId and appId must appear together");
        }
        String payloadJson = buildPayloadJson(tenantId, eventId, expEpochSec, openId, appId);
        byte[] payloadBytes = payloadJson.getBytes(StandardCharsets.UTF_8);
        byte[] sig = hmacSha256(payloadBytes, secret);
        return base64UrlEncode(payloadBytes) + "." + base64UrlEncode(sig);
    }

    static VerifiedPayload verify(String token, String secret)
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
            if (node == null || !node.has("t") || !node.has("e") || !node.has("exp") || node.has("s"))
            {
                throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
            }
            boolean hasOpenId = node.has("o");
            boolean hasAppId = node.has("a");
            if (hasOpenId != hasAppId)
            {
                throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
            }
            long exp = node.get("exp").asLong();
            if (Instant.now().getEpochSecond() > exp)
            {
                throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
            }
            String openId = hasOpenId ? node.get("o").asText() : null;
            String appId = hasAppId ? node.get("a").asText() : null;
            return new VerifiedPayload(node.get("t").asText(), node.get("e").asText(), exp, openId, appId);
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

    static String buildPayloadJson(String tenantId, String eventId, long expEpochSec)
    {
        return buildPayloadJson(tenantId, eventId, expEpochSec, null, null);
    }

    static String buildPayloadJson(String tenantId, String eventId, long expEpochSec,
            String openId, String appId)
    {
        if (StringUtils.isNotBlank(openId) && StringUtils.isNotBlank(appId))
        {
            return String.format("{\"t\":\"%s\",\"e\":\"%s\",\"exp\":%d,\"o\":\"%s\",\"a\":\"%s\"}",
                    escapeJson(tenantId), escapeJson(eventId), expEpochSec,
                    escapeJson(openId), escapeJson(appId));
        }
        return String.format("{\"t\":\"%s\",\"e\":\"%s\",\"exp\":%d}",
                escapeJson(tenantId), escapeJson(eventId), expEpochSec);
    }

    static String signSurvey(String tenantId, String surveyId, long expEpochSec, String secret)
    {
        return signSurvey(tenantId, surveyId, expEpochSec, null, null, null, null, secret);
    }

    static String signSurvey(String tenantId, String surveyId, long expEpochSec,
            String openId, String appId, String contactId, String channel, String secret)
    {
        if (StringUtils.isBlank(tenantId) || StringUtils.isBlank(surveyId) || StringUtils.isBlank(secret))
        {
            throw new IllegalArgumentException("tenantId, surveyId and secret are required");
        }
        boolean hasOpenId = StringUtils.isNotBlank(openId);
        boolean hasAppId = StringUtils.isNotBlank(appId);
        if (hasOpenId != hasAppId)
        {
            throw new IllegalArgumentException("openId and appId must appear together");
        }
        String payloadJson = buildSurveyPayloadJson(tenantId, surveyId, expEpochSec, openId, appId, contactId, channel);
        byte[] payloadBytes = payloadJson.getBytes(StandardCharsets.UTF_8);
        byte[] sig = hmacSha256(payloadBytes, secret);
        return base64UrlEncode(payloadBytes) + "." + base64UrlEncode(sig);
    }

    static SurveyVerifiedPayload verifySurvey(String token, String secret)
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
            if (node == null || !node.has("t") || !node.has("s") || !node.has("exp") || node.has("e"))
            {
                throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
            }
            boolean hasOpenId = node.has("o");
            boolean hasAppId = node.has("a");
            if (hasOpenId != hasAppId)
            {
                throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
            }
            long exp = node.get("exp").asLong();
            if (Instant.now().getEpochSecond() > exp)
            {
                throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
            }
            String openId = hasOpenId ? node.get("o").asText() : null;
            String appId = hasAppId ? node.get("a").asText() : null;
            String contactId = node.has("c") ? node.get("c").asText() : null;
            String channel = node.has("ch") ? node.get("ch").asText() : null;
            return new SurveyVerifiedPayload(
                    node.get("t").asText(),
                    node.get("s").asText(),
                    exp,
                    openId,
                    appId,
                    contactId,
                    channel);
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

    static String buildSurveyPayloadJson(String tenantId, String surveyId, long expEpochSec)
    {
        return buildSurveyPayloadJson(tenantId, surveyId, expEpochSec, null, null, null, null);
    }

    static String buildSurveyPayloadJson(String tenantId, String surveyId, long expEpochSec,
            String openId, String appId, String contactId)
    {
        return buildSurveyPayloadJson(tenantId, surveyId, expEpochSec, openId, appId, contactId, null);
    }

    static String buildSurveyPayloadJson(String tenantId, String surveyId, long expEpochSec,
            String openId, String appId, String contactId, String channel)
    {
        if (StringUtils.isNotBlank(openId) && StringUtils.isNotBlank(appId))
        {
            StringBuilder json = new StringBuilder();
            json.append(String.format("{\"t\":\"%s\",\"s\":\"%s\",\"exp\":%d,\"o\":\"%s\",\"a\":\"%s\"",
                    escapeJson(tenantId), escapeJson(surveyId), expEpochSec,
                    escapeJson(openId), escapeJson(appId)));
            if (StringUtils.isNotBlank(contactId))
            {
                json.append(String.format(",\"c\":\"%s\"", escapeJson(contactId)));
            }
            appendChannelField(json, channel);
            json.append('}');
            return json.toString();
        }
        if (StringUtils.isNotBlank(contactId))
        {
            StringBuilder json = new StringBuilder();
            json.append(String.format("{\"t\":\"%s\",\"s\":\"%s\",\"exp\":%d,\"c\":\"%s\"",
                    escapeJson(tenantId), escapeJson(surveyId), expEpochSec, escapeJson(contactId)));
            appendChannelField(json, channel);
            json.append('}');
            return json.toString();
        }
        return String.format("{\"t\":\"%s\",\"s\":\"%s\",\"exp\":%d}",
                escapeJson(tenantId), escapeJson(surveyId), expEpochSec);
    }

    private static void appendChannelField(StringBuilder json, String channel)
    {
        if (StringUtils.isNotBlank(channel))
        {
            json.append(String.format(",\"ch\":\"%s\"", escapeJson(channel)));
        }
    }

    static String signQualify(String tenantId, String leadId, String templateId, String channel,
            long expEpochSec, String secret)
    {
        if (StringUtils.isBlank(tenantId) || StringUtils.isBlank(leadId) || StringUtils.isBlank(templateId)
                || StringUtils.isBlank(channel) || StringUtils.isBlank(secret))
        {
            throw new IllegalArgumentException("tenantId, leadId, templateId, channel and secret are required");
        }
        String payloadJson = buildQualifyPayloadJson(tenantId, leadId, templateId, channel, expEpochSec);
        byte[] payloadBytes = payloadJson.getBytes(StandardCharsets.UTF_8);
        byte[] sig = hmacSha256(payloadBytes, secret);
        return base64UrlEncode(payloadBytes) + "." + base64UrlEncode(sig);
    }

    static QualifyVerifiedPayload verifyQualify(String token, String secret)
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
            // Qualify tokens: t + l + tpl + ch + exp; reject survey (s) / event (e) payloads
            if (node == null || !node.has("t") || !node.has("l") || !node.has("tpl") || !node.has("ch")
                    || !node.has("exp") || node.has("s") || node.has("e"))
            {
                throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
            }
            long exp = node.get("exp").asLong();
            if (Instant.now().getEpochSecond() > exp)
            {
                throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
            }
            String channel = node.get("ch").asText();
            if (!CHANNEL_QUALIFY_QR.equals(channel))
            {
                throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
            }
            return new QualifyVerifiedPayload(
                    node.get("t").asText(),
                    node.get("l").asText(),
                    node.get("tpl").asText(),
                    channel,
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

    static String buildQualifyPayloadJson(String tenantId, String leadId, String templateId, String channel,
            long expEpochSec)
    {
        return String.format("{\"t\":\"%s\",\"l\":\"%s\",\"tpl\":\"%s\",\"ch\":\"%s\",\"exp\":%d}",
                escapeJson(tenantId), escapeJson(leadId), escapeJson(templateId), escapeJson(channel),
                expEpochSec);
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
