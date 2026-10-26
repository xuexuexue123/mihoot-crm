package com.ruoyi.crm.service.webhook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class OutboundWebhookUrlValidatorTest
{
    @Test
    public void rejectsLoopbackWhenHttpsRequired()
    {
        assertEquals("生产环境仅允许 https://", OutboundWebhookUrlValidator.validate("http://127.0.0.1/hook", false));
        assertEquals(OutboundWebhookConstants.ERROR_SSRF,
                OutboundWebhookUrlValidator.validate("https://127.0.0.1/hook", false));
        assertEquals(OutboundWebhookConstants.ERROR_SSRF,
                OutboundWebhookUrlValidator.validate("https://localhost/hook", true));
    }

    @Test
    public void rejectsPrivateAndMetadata()
    {
        assertEquals(OutboundWebhookConstants.ERROR_SSRF,
                OutboundWebhookUrlValidator.validate("https://10.0.0.1/hook", false));
        assertEquals(OutboundWebhookConstants.ERROR_SSRF,
                OutboundWebhookUrlValidator.validate("https://192.168.1.8/hook", false));
        assertEquals(OutboundWebhookConstants.ERROR_SSRF,
                OutboundWebhookUrlValidator.validate("https://169.254.169.254/latest", false));
    }

    @Test
    public void allowsPublicHttps()
    {
        assertNull(OutboundWebhookUrlValidator.validate("https://webhook.site/abc", false));
    }

    @Test
    public void allowHttpStillBlocksLoopback()
    {
        String result = OutboundWebhookUrlValidator.validate("http://127.0.0.1/hook", true);
        assertEquals(OutboundWebhookConstants.ERROR_SSRF, result);
        assertNotNull(result);
        assertTrue(OutboundWebhookUrlValidator.isBlockedHostName("localhost"));
    }
}
