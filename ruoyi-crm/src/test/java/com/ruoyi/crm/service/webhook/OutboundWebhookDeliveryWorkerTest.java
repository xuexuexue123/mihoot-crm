package com.ruoyi.crm.service.webhook;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class OutboundWebhookDeliveryWorkerTest
{
    @Test
    public void retryableOn5xxTimeoutAnd429()
    {
        assertTrue(OutboundWebhookDeliveryWorker.isRetryable(500, false));
        assertTrue(OutboundWebhookDeliveryWorker.isRetryable(429, false));
        assertTrue(OutboundWebhookDeliveryWorker.isRetryable(408, false));
        assertTrue(OutboundWebhookDeliveryWorker.isRetryable(null, true));
    }

    @Test
    public void notRetryableOn400()
    {
        assertFalse(OutboundWebhookDeliveryWorker.isRetryable(400, false));
        assertFalse(OutboundWebhookDeliveryWorker.isRetryable(404, false));
        assertFalse(OutboundWebhookDeliveryWorker.isRetryable(401, false));
    }
}
