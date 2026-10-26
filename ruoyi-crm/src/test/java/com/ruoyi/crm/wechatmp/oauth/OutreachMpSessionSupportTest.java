package com.ruoyi.crm.wechatmp.oauth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class OutreachMpSessionSupportTest
{
    private OutreachMpSessionSupport support;

    @BeforeEach
    void setUp()
    {
        support = new OutreachMpSessionSupport();
        ReflectionTestUtils.setField(support, "checkinSigningSecret", "test-session-secret-32-characters-min");
    }

    @Test
    void encodeAndDecode_roundTrip()
    {
        OutreachMpSessionPayload payload = new OutreachMpSessionPayload();
        payload.setOpenId("o123456");
        payload.setAppId("wxapp");
        payload.setTenantId("tenant-1");
        payload.setEventId("event-1");
        payload.setExp(Instant.now().plus(1, ChronoUnit.HOURS).getEpochSecond());

        String encoded = support.encode(payload);
        OutreachMpSessionPayload decoded = support.decode(encoded);
        assertNotNull(decoded);
        assertEquals("o123456", decoded.getOpenId());
        assertEquals("tenant-1", decoded.getTenantId());
    }

    @Test
    void decode_rejectsExpired()
    {
        OutreachMpSessionPayload payload = new OutreachMpSessionPayload();
        payload.setOpenId("o123456");
        payload.setAppId("wxapp");
        payload.setTenantId("tenant-1");
        payload.setEventId("event-1");
        payload.setExp(Instant.now().minus(1, ChronoUnit.HOURS).getEpochSecond());

        String encoded = support.encode(payload);
        assertNull(support.decode(encoded));
    }

    @Test
    void maskOpenId()
    {
        assertEquals("o1***56", OutreachMpSessionSupport.maskOpenId("o123456"));
    }
}
