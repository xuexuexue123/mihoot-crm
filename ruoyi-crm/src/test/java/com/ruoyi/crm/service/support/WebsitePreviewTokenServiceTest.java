package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.service.support.WebsitePreviewTokenService.PreviewClaims;

class WebsitePreviewTokenServiceTest
{
    private static final String SECRET = "website-preview-secret-at-least-32-chars";
    private static final String TENANT = "demo_tenant";
    private static final String PAGE_ID = "550e8400-e29b-41d4-a716-446655440000";
    private static final String PATH = "/";

    @Test
    void signAndVerify_roundTrip()
    {
        long exp = Instant.now().plus(15, ChronoUnit.MINUTES).getEpochSecond();
        String token = WebsitePreviewTokenService.sign(TENANT, PAGE_ID, PATH, exp, SECRET);
        PreviewClaims claims = WebsitePreviewTokenService.verifyToken(token, SECRET);
        assertEquals(TENANT, claims.getTenantName());
        assertEquals(PAGE_ID, claims.getPageId());
        assertEquals(PATH, claims.getPath());
        assertEquals(exp, claims.getExp());
    }

    @Test
    void verify_rejectsTamperedSignature()
    {
        long exp = Instant.now().plus(15, ChronoUnit.MINUTES).getEpochSecond();
        String token = WebsitePreviewTokenService.sign(TENANT, PAGE_ID, PATH, exp, SECRET);
        String tampered = token.substring(0, token.length() - 1) + (token.endsWith("A") ? "B" : "A");
        ServiceException ex = assertThrows(ServiceException.class,
                () -> WebsitePreviewTokenService.verifyToken(tampered, SECRET));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode());
    }

    @Test
    void verify_rejectsExpiredToken()
    {
        long exp = Instant.now().minus(1, ChronoUnit.MINUTES).getEpochSecond();
        String token = WebsitePreviewTokenService.sign(TENANT, PAGE_ID, PATH, exp, SECRET);
        ServiceException ex = assertThrows(ServiceException.class,
                () -> WebsitePreviewTokenService.verifyToken(token, SECRET));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode());
    }

    @Test
    void verify_rejectsOutreachEventToken()
    {
        long exp = Instant.now().plus(15, ChronoUnit.MINUTES).getEpochSecond();
        String outreachStyle = String.format("{\"t\":\"%s\",\"e\":\"evt\",\"exp\":%d}", TENANT, exp);
        byte[] payloadBytes = outreachStyle.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        String token = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(payloadBytes)
                + ".invalid";
        ServiceException ex = assertThrows(ServiceException.class,
                () -> WebsitePreviewTokenService.verifyToken(token, SECRET));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode());
    }
}
