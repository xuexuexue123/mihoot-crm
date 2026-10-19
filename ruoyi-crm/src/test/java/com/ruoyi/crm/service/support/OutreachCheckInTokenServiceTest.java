package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.service.support.OutreachCheckInTokenService.VerifiedCheckInPayload;

class OutreachCheckInTokenServiceTest
{
    private static final String SECRET = "test-checkin-signing-secret-32-chars-min";
    private static final String TENANT = "tenant-abc";
    private static final String EVENT = "event-001";
    private static final String SESSION = "sess-001";

    @Test
    void signAndVerify_roundTrip()
    {
        long exp = Instant.now().plus(7, ChronoUnit.DAYS).getEpochSecond();
        String token = OutreachCheckInTokenService.sign(TENANT, EVENT, SESSION, exp, null, null, SECRET);
        VerifiedCheckInPayload payload = OutreachCheckInTokenService.verify(token, SECRET);
        assertEquals(TENANT, payload.getTenantId());
        assertEquals(EVENT, payload.getEventId());
        assertEquals(SESSION, payload.getSessionId());
        assertNull(payload.getOpenId());
    }

    @Test
    void signAndVerify_withOpenId()
    {
        long exp = Instant.now().plus(7, ChronoUnit.DAYS).getEpochSecond();
        String token = OutreachCheckInTokenService.sign(TENANT, EVENT, SESSION, exp, "o1", "wx1", SECRET);
        VerifiedCheckInPayload payload = OutreachCheckInTokenService.verify(token, SECRET);
        assertEquals("o1", payload.getOpenId());
        assertEquals("wx1", payload.getAppId());
    }

    @Test
    void verify_rejectsWrongTyp()
    {
        long exp = Instant.now().plus(1, ChronoUnit.DAYS).getEpochSecond();
        String ticketToken = OutreachTicketTokenService.sign(TENANT, EVENT, "reg-1", SESSION, exp, SECRET);
        ServiceException ex = assertThrows(ServiceException.class,
                () -> OutreachCheckInTokenService.verify(ticketToken, SECRET));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode());
    }

    @Test
    void verify_rejectsTamperedToken()
    {
        long exp = Instant.now().plus(1, ChronoUnit.DAYS).getEpochSecond();
        String token = OutreachCheckInTokenService.sign(TENANT, EVENT, SESSION, exp, null, null, SECRET);
        String tampered = token.substring(0, token.length() - 1) + (token.endsWith("A") ? "B" : "A");
        ServiceException ex = assertThrows(ServiceException.class,
                () -> OutreachCheckInTokenService.verify(tampered, SECRET));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode());
    }

    @Test
    void verify_rejectsExpiredToken()
    {
        long exp = Instant.now().minus(1, ChronoUnit.HOURS).getEpochSecond();
        String token = OutreachCheckInTokenService.sign(TENANT, EVENT, SESSION, exp, null, null, SECRET);
        ServiceException ex = assertThrows(ServiceException.class,
                () -> OutreachCheckInTokenService.verify(token, SECRET));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode());
    }
}
