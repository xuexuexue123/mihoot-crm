package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.service.support.OutreachTicketTokenService.VerifiedTicketPayload;

class OutreachTicketTokenServiceTest
{
    private static final String SECRET = "test-ticket-signing-secret-32-chars-min";
    private static final String TENANT = "tenant-abc";
    private static final String EVENT = "event-001";
    private static final String REG = "reg-001";
    private static final String SESSION = "sess-001";

    @Test
    void signAndVerify_roundTrip()
    {
        long exp = Instant.now().plus(7, ChronoUnit.DAYS).getEpochSecond();
        String token = OutreachTicketTokenService.sign(TENANT, EVENT, REG, SESSION, exp, SECRET);
        VerifiedTicketPayload payload = OutreachTicketTokenService.verify(token, EVENT, SECRET);
        assertEquals(TENANT, payload.getTenantId());
        assertEquals(EVENT, payload.getEventId());
        assertEquals(REG, payload.getRegistrationId());
        assertEquals(SESSION, payload.getSessionId());
    }

    @Test
    void hashToken_isDeterministic()
    {
        long exp = Instant.now().plus(1, ChronoUnit.DAYS).getEpochSecond();
        String token = OutreachTicketTokenService.sign(TENANT, EVENT, REG, SESSION, exp, SECRET);
        String hash1 = OutreachTicketTokenService.hashToken(token);
        String hash2 = OutreachTicketTokenService.hashToken(token);
        assertEquals(hash1, hash2);
        assertNotEquals(token, hash1);
    }

    @Test
    void verify_rejectsWrongEventId()
    {
        long exp = Instant.now().plus(1, ChronoUnit.DAYS).getEpochSecond();
        String token = OutreachTicketTokenService.sign(TENANT, EVENT, REG, SESSION, exp, SECRET);
        ServiceException ex = assertThrows(ServiceException.class,
                () -> OutreachTicketTokenService.verify(token, "other-event", SECRET));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode());
    }

    @Test
    void verify_rejectsExpiredToken()
    {
        long exp = Instant.now().minus(1, ChronoUnit.HOURS).getEpochSecond();
        String token = OutreachTicketTokenService.sign(TENANT, EVENT, REG, SESSION, exp, SECRET);
        ServiceException ex = assertThrows(ServiceException.class,
                () -> OutreachTicketTokenService.verify(token, EVENT, SECRET));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode());
    }
}
