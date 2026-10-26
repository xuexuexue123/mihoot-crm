package com.ruoyi.crm.service.webhook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class OutboundWebhookSignerTest
{
    @Test
    public void signsTimestampDotBody()
    {
        String body = "{\"id\":\"evt_1\"}";
        long ts = 1720000000L;
        String header = OutboundWebhookSigner.signHeaderValue("secret", ts, body);
        assertTrue(header.startsWith("sha256="));
        assertTrue(OutboundWebhookSigner.matches("secret", ts, body, header));
        assertFalse(OutboundWebhookSigner.matches("other", ts, body, header));
        String expectedHex = OutboundWebhookSigner.hmacSha256Hex("secret", ts + "." + body);
        assertEquals("sha256=" + expectedHex, header);
    }
}
