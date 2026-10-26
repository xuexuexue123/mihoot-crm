package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.dto.OutboundWebhookSaveRequest;
import com.ruoyi.crm.service.webhook.OutboundWebhookConstants;

public class OutboundWebhookValidatorTest
{
    @Test
    public void rejectsUnknownEventType()
    {
        OutboundWebhookSaveRequest request = valid();
        request.setEventType("lead.updated");
        assertThrows(ServiceException.class,
                () -> OutboundWebhookValidator.validateSaveRequest(request, false, true));
    }

    @Test
    public void appliesDefaults()
    {
        OutboundWebhookSaveRequest request = valid();
        request.setRetryCount(null);
        request.setTimeoutMs(null);
        OutboundWebhookValidator.ValidatedOutboundWebhook validated =
                OutboundWebhookValidator.validateSaveRequest(request, false, true);
        assertEquals(OutboundWebhookConstants.DEFAULT_RETRY, validated.retryCount);
        assertEquals(OutboundWebhookConstants.DEFAULT_TIMEOUT_MS, validated.timeoutMs);
        assertEquals(OutboundWebhookConstants.EVENT_LEAD_CREATED, validated.eventType);
    }

    private static OutboundWebhookSaveRequest valid()
    {
        OutboundWebhookSaveRequest request = new OutboundWebhookSaveRequest();
        request.setName("hook");
        request.setUrl("https://example.com/hook");
        request.setEventType(OutboundWebhookConstants.EVENT_LEAD_CREATED);
        return request;
    }
}
