package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.junit.jupiter.api.Test;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.service.support.OutreachRegistrationTokenService.QualifyVerifiedPayload;
import com.ruoyi.crm.service.support.OutreachRegistrationTokenService.SurveyVerifiedPayload;
import com.ruoyi.crm.service.support.OutreachRegistrationTokenService.VerifiedPayload;

class OutreachRegistrationTokenServiceTest
{
    private static final String SECRET = "test-signing-secret-at-least-32-bytes-long";
    private static final String TENANT = "tenant-abc";
    private static final String EVENT = "a814999a-e5f6-7890-abcd-ef999a8eff4";
    private static final String SURVEY = "b925000a-f6e7-8901-bcde-f0000b9eff5";
    private static final String LEAD = "c036111b-g7f8-9012-cdef-g1111c0fgg6";
    private static final String TEMPLATE = "8a530792-da39-4230-826f-fa768b54373e";

    @Test
    void signAndVerify_roundTrip()
    {
        long exp = Instant.now().plus(7, ChronoUnit.DAYS).getEpochSecond();
        String token = OutreachRegistrationTokenService.sign(TENANT, EVENT, exp, SECRET);
        VerifiedPayload payload = OutreachRegistrationTokenService.verify(token, SECRET);
        assertEquals(TENANT, payload.getTenantId());
        assertEquals(EVENT, payload.getEventId());
        assertEquals(exp, payload.getExp());
    }

    @Test
    void verify_rejectsTamperedSignature()
    {
        long exp = Instant.now().plus(1, ChronoUnit.DAYS).getEpochSecond();
        String token = OutreachRegistrationTokenService.sign(TENANT, EVENT, exp, SECRET);
        String tampered = token.substring(0, token.length() - 1) + (token.endsWith("A") ? "B" : "A");
        ServiceException ex = assertThrows(ServiceException.class,
                () -> OutreachRegistrationTokenService.verify(tampered, SECRET));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode());
    }

    @Test
    void verify_rejectsExpiredToken()
    {
        long exp = Instant.now().minus(1, ChronoUnit.HOURS).getEpochSecond();
        String token = OutreachRegistrationTokenService.sign(TENANT, EVENT, exp, SECRET);
        ServiceException ex = assertThrows(ServiceException.class,
                () -> OutreachRegistrationTokenService.verify(token, SECRET));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode());
    }

    @Test
    void verify_rejectsInvalidFormat()
    {
        ServiceException ex = assertThrows(ServiceException.class,
                () -> OutreachRegistrationTokenService.verify("not-a-valid-token", SECRET));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getCode());
    }

    @Test
    void verify_rejectsBlankTokenWithBadRequest()
    {
        ServiceException ex = assertThrows(ServiceException.class,
                () -> OutreachRegistrationTokenService.verify("", SECRET));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getCode());
    }

    @Test
    void verify_rejectsEmptySecret()
    {
        long exp = Instant.now().plus(1, ChronoUnit.DAYS).getEpochSecond();
        String token = OutreachRegistrationTokenService.sign(TENANT, EVENT, exp, SECRET);
        ServiceException ex = assertThrows(ServiceException.class,
                () -> OutreachRegistrationTokenService.verify(token, ""));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode());
    }

    @Test
    void computeExp_usesEndTimePlusSevenDays()
    {
        OutreachRegistrationTokenService service = new OutreachRegistrationTokenService();
        CrmEvent event = new CrmEvent();
        Date endTime = Date.from(Instant.now().plus(30, ChronoUnit.DAYS));
        event.setEndTime(endTime);

        long exp = service.computeExp(event);
        long expected = endTime.toInstant().getEpochSecond() + 7L * 24 * 60 * 60;
        assertEquals(expected, exp);
    }

    @Test
    void computeExp_defaultsToOneYearWhenNoEndTime()
    {
        OutreachRegistrationTokenService service = new OutreachRegistrationTokenService();
        long before = Instant.now().getEpochSecond();
        long exp = service.computeExp(new CrmEvent());
        long after = Instant.now().getEpochSecond();
        long oneYear = 365L * 24 * 60 * 60;
        assertTrue(exp >= before + oneYear);
        assertTrue(exp <= after + oneYear);
    }

    @Test
    void computeExp_enforcesMinimumTwentyFourHours()
    {
        OutreachRegistrationTokenService service = new OutreachRegistrationTokenService();
        CrmEvent event = new CrmEvent();
        event.setEndTime(Date.from(Instant.now().minus(30, ChronoUnit.DAYS)));

        long exp = service.computeExp(event);
        long minExp = Instant.now().getEpochSecond() + 24 * 60 * 60;
        assertTrue(exp >= minExp - 2);
    }

    @Test
    void buildPayloadJson_escapesSpecialCharacters()
    {
        String json = OutreachRegistrationTokenService.buildPayloadJson("ten\"ant", "ev\\ent", 123);
        assertTrue(json.contains("\\\""));
        assertTrue(json.contains("\\\\"));
    }

    @Test
    void signAndVerify_roundTripWithOpenIdAndAppId()
    {
        long exp = Instant.now().plus(7, ChronoUnit.DAYS).getEpochSecond();
        String openId = "oABC123";
        String appId = "wx_app_1";
        String token = OutreachRegistrationTokenService.sign(TENANT, EVENT, exp, openId, appId, SECRET);
        VerifiedPayload payload = OutreachRegistrationTokenService.verify(token, SECRET);
        assertEquals(TENANT, payload.getTenantId());
        assertEquals(EVENT, payload.getEventId());
        assertEquals(exp, payload.getExp());
        assertEquals(openId, payload.getOpenId());
        assertEquals(appId, payload.getAppId());
    }

    @Test
    void verify_legacyTokenWithoutOpenIdStillValid()
    {
        long exp = Instant.now().plus(7, ChronoUnit.DAYS).getEpochSecond();
        String token = OutreachRegistrationTokenService.sign(TENANT, EVENT, exp, SECRET);
        VerifiedPayload payload = OutreachRegistrationTokenService.verify(token, SECRET);
        assertEquals(null, payload.getOpenId());
        assertEquals(null, payload.getAppId());
    }

    @Test
    void verify_rejectsOpenIdWithoutAppId()
    {
        long exp = Instant.now().plus(7, ChronoUnit.DAYS).getEpochSecond();
        String payloadJson = String.format("{\"t\":\"%s\",\"e\":\"%s\",\"exp\":%d,\"o\":\"oABC\"}",
                TENANT, EVENT, exp);
        byte[] payloadBytes = payloadJson.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        String token = buildTestToken(payloadBytes, SECRET);
        ServiceException ex = assertThrows(ServiceException.class,
                () -> OutreachRegistrationTokenService.verify(token, SECRET));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode());
    }

    @Test
    void signSurveyAndVerifySurvey_roundTrip()
    {
        long exp = Instant.now().plus(7, ChronoUnit.DAYS).getEpochSecond();
        String token = OutreachRegistrationTokenService.signSurvey(TENANT, SURVEY, exp, SECRET);
        SurveyVerifiedPayload payload = OutreachRegistrationTokenService.verifySurvey(token, SECRET);
        assertEquals(TENANT, payload.getTenantId());
        assertEquals(SURVEY, payload.getSurveyId());
        assertEquals(exp, payload.getExp());
    }

    @Test
    void verifySurvey_rejectsTamperedSignature()
    {
        long exp = Instant.now().plus(1, ChronoUnit.DAYS).getEpochSecond();
        String token = OutreachRegistrationTokenService.signSurvey(TENANT, SURVEY, exp, SECRET);
        String tampered = token.substring(0, token.length() - 1) + (token.endsWith("A") ? "B" : "A");
        ServiceException ex = assertThrows(ServiceException.class,
                () -> OutreachRegistrationTokenService.verifySurvey(tampered, SECRET));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode());
    }

    @Test
    void verifySurvey_rejectsExpiredToken()
    {
        long exp = Instant.now().minus(1, ChronoUnit.HOURS).getEpochSecond();
        String token = OutreachRegistrationTokenService.signSurvey(TENANT, SURVEY, exp, SECRET);
        ServiceException ex = assertThrows(ServiceException.class,
                () -> OutreachRegistrationTokenService.verifySurvey(token, SECRET));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode());
    }

    @Test
    void verifySurvey_rejectsEventToken()
    {
        long exp = Instant.now().plus(7, ChronoUnit.DAYS).getEpochSecond();
        String eventToken = OutreachRegistrationTokenService.sign(TENANT, EVENT, exp, SECRET);
        ServiceException ex = assertThrows(ServiceException.class,
                () -> OutreachRegistrationTokenService.verifySurvey(eventToken, SECRET));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode());
    }

    @Test
    void verify_rejectsSurveyToken()
    {
        long exp = Instant.now().plus(7, ChronoUnit.DAYS).getEpochSecond();
        String surveyToken = OutreachRegistrationTokenService.signSurvey(TENANT, SURVEY, exp, SECRET);
        ServiceException ex = assertThrows(ServiceException.class,
                () -> OutreachRegistrationTokenService.verify(surveyToken, SECRET));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode());
    }

    @Test
    void computeSurveyExp_defaultsToOneYearWhenNoEndTime()
    {
        OutreachRegistrationTokenService service = new OutreachRegistrationTokenService();
        long before = Instant.now().getEpochSecond();
        long exp = service.computeSurveyExp(new com.ruoyi.crm.domain.CrmSurveys());
        long after = Instant.now().getEpochSecond();
        long oneYear = 365L * 24 * 60 * 60;
        assertTrue(exp >= before + oneYear);
        assertTrue(exp <= after + oneYear);
    }

    @Test
    void computeSurveyExp_enforcesMinimumTwentyFourHours()
    {
        OutreachRegistrationTokenService service = new OutreachRegistrationTokenService();
        long exp = service.computeSurveyExp(new com.ruoyi.crm.domain.CrmSurveys());
        long minExp = Instant.now().getEpochSecond() + 24 * 60 * 60;
        assertTrue(exp >= minExp - 2);
    }

    @Test
    void signSurveyAndVerifySurvey_roundTripWithOpenIdAppIdAndContactId()
    {
        long exp = Instant.now().plus(7, ChronoUnit.DAYS).getEpochSecond();
        String openId = "oABC123";
        String appId = "wx_app_1";
        String contactId = "contact-uuid-1";
        String token = OutreachRegistrationTokenService.signSurvey(
                TENANT, SURVEY, exp, openId, appId, contactId, null, SECRET);
        SurveyVerifiedPayload payload = OutreachRegistrationTokenService.verifySurvey(token, SECRET);
        assertEquals(TENANT, payload.getTenantId());
        assertEquals(SURVEY, payload.getSurveyId());
        assertEquals(exp, payload.getExp());
        assertEquals(openId, payload.getOpenId());
        assertEquals(appId, payload.getAppId());
        assertEquals(contactId, payload.getContactId());
    }

    @Test
    void signSurveyAndVerifySurvey_roundTripWithContactIdAndChannelOnly()
    {
        long exp = Instant.now().plus(7, ChronoUnit.DAYS).getEpochSecond();
        String contactId = "contact-uuid-1";
        String channel = "wecom_sidebar";
        String token = OutreachRegistrationTokenService.signSurvey(
                TENANT, SURVEY, exp, null, null, contactId, channel, SECRET);
        SurveyVerifiedPayload payload = OutreachRegistrationTokenService.verifySurvey(token, SECRET);
        assertEquals(TENANT, payload.getTenantId());
        assertEquals(SURVEY, payload.getSurveyId());
        assertEquals(exp, payload.getExp());
        assertEquals(null, payload.getOpenId());
        assertEquals(null, payload.getAppId());
        assertEquals(contactId, payload.getContactId());
        assertEquals(channel, payload.getChannel());
    }

    @Test
    void signSurveyAndVerifySurvey_roundTripWithOpenIdAppIdContactIdAndChannel()
    {
        long exp = Instant.now().plus(7, ChronoUnit.DAYS).getEpochSecond();
        String openId = "oABC123";
        String appId = "wx_app_1";
        String contactId = "contact-uuid-1";
        String channel = "wechat_mp";
        String token = OutreachRegistrationTokenService.signSurvey(
                TENANT, SURVEY, exp, openId, appId, contactId, channel, SECRET);
        SurveyVerifiedPayload payload = OutreachRegistrationTokenService.verifySurvey(token, SECRET);
        assertEquals(openId, payload.getOpenId());
        assertEquals(appId, payload.getAppId());
        assertEquals(contactId, payload.getContactId());
        assertEquals(channel, payload.getChannel());
    }

    @Test
    void verifySurvey_legacyGenericTokenStillValid()
    {
        long exp = Instant.now().plus(7, ChronoUnit.DAYS).getEpochSecond();
        String token = OutreachRegistrationTokenService.signSurvey(TENANT, SURVEY, exp, SECRET);
        SurveyVerifiedPayload payload = OutreachRegistrationTokenService.verifySurvey(token, SECRET);
        assertEquals(null, payload.getContactId());
        assertEquals(null, payload.getChannel());
    }

    @Test
    void signQualifyAndVerifyQualify_roundTrip()
    {
        long exp = Instant.now().plus(8, ChronoUnit.HOURS).getEpochSecond();
        String token = OutreachRegistrationTokenService.signQualify(
                TENANT, LEAD, TEMPLATE, OutreachRegistrationTokenService.CHANNEL_QUALIFY_QR, exp, SECRET);
        QualifyVerifiedPayload payload = OutreachRegistrationTokenService.verifyQualify(token, SECRET);
        assertEquals(TENANT, payload.getTenantId());
        assertEquals(LEAD, payload.getLeadId());
        assertEquals(TEMPLATE, payload.getTemplateId());
        assertEquals(OutreachRegistrationTokenService.CHANNEL_QUALIFY_QR, payload.getChannel());
        assertEquals(exp, payload.getExp());
    }

    @Test
    void verifyQualify_rejectsTamperedSignature()
    {
        long exp = Instant.now().plus(1, ChronoUnit.HOURS).getEpochSecond();
        String token = OutreachRegistrationTokenService.signQualify(
                TENANT, LEAD, TEMPLATE, OutreachRegistrationTokenService.CHANNEL_QUALIFY_QR, exp, SECRET);
        String tampered = token.substring(0, token.length() - 1) + (token.endsWith("A") ? "B" : "A");
        ServiceException ex = assertThrows(ServiceException.class,
                () -> OutreachRegistrationTokenService.verifyQualify(tampered, SECRET));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode());
    }

    @Test
    void verifyQualify_rejectsExpiredToken()
    {
        long exp = Instant.now().minus(1, ChronoUnit.HOURS).getEpochSecond();
        String token = OutreachRegistrationTokenService.signQualify(
                TENANT, LEAD, TEMPLATE, OutreachRegistrationTokenService.CHANNEL_QUALIFY_QR, exp, SECRET);
        ServiceException ex = assertThrows(ServiceException.class,
                () -> OutreachRegistrationTokenService.verifyQualify(token, SECRET));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode());
    }

    @Test
    void verifyQualify_rejectsSurveyToken()
    {
        long exp = Instant.now().plus(7, ChronoUnit.DAYS).getEpochSecond();
        String surveyToken = OutreachRegistrationTokenService.signSurvey(TENANT, SURVEY, exp, SECRET);
        ServiceException ex = assertThrows(ServiceException.class,
                () -> OutreachRegistrationTokenService.verifyQualify(surveyToken, SECRET));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode());
    }

    @Test
    void verifyQualify_rejectsEventToken()
    {
        long exp = Instant.now().plus(7, ChronoUnit.DAYS).getEpochSecond();
        String eventToken = OutreachRegistrationTokenService.sign(TENANT, EVENT, exp, SECRET);
        ServiceException ex = assertThrows(ServiceException.class,
                () -> OutreachRegistrationTokenService.verifyQualify(eventToken, SECRET));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode());
    }

    @Test
    void verifySurvey_rejectsQualifyToken()
    {
        long exp = Instant.now().plus(8, ChronoUnit.HOURS).getEpochSecond();
        String qualifyToken = OutreachRegistrationTokenService.signQualify(
                TENANT, LEAD, TEMPLATE, OutreachRegistrationTokenService.CHANNEL_QUALIFY_QR, exp, SECRET);
        ServiceException ex = assertThrows(ServiceException.class,
                () -> OutreachRegistrationTokenService.verifySurvey(qualifyToken, SECRET));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode());
    }

    @Test
    void computeQualifyExp_isEightHours()
    {
        OutreachRegistrationTokenService service = new OutreachRegistrationTokenService();
        long before = Instant.now().getEpochSecond();
        long exp = service.computeQualifyExp();
        long after = Instant.now().getEpochSecond();
        long eightHours = 8L * 60 * 60;
        assertTrue(exp >= before + eightHours);
        assertTrue(exp <= after + eightHours);
    }

    private static String buildTestToken(byte[] payloadBytes, String secret)
    {
        try
        {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(new javax.crypto.spec.SecretKeySpec(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                    "HmacSHA256"));
            byte[] sig = mac.doFinal(payloadBytes);
            return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(payloadBytes)
                    + "." + java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(sig);
        }
        catch (Exception ex)
        {
            throw new IllegalStateException(ex);
        }
    }
}
