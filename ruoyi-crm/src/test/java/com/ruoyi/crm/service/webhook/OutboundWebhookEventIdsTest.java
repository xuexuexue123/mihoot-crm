package com.ruoyi.crm.service.webhook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.domain.dto.OutboundWebhookDispatchEvent;

public class OutboundWebhookEventIdsTest
{
    @Test
    public void leadCreatedIsStable()
    {
        OutboundWebhookDispatchEvent event = new OutboundWebhookDispatchEvent();
        event.setTenant("demo");
        event.setEventType(OutboundWebhookConstants.EVENT_LEAD_CREATED);
        event.setOccurredAt(Instant.parse("2026-08-19T01:00:00Z"));
        JSONObject payload = new JSONObject();
        JSONObject lead = new JSONObject();
        lead.put("leadId", "lead-1");
        payload.put("lead", lead);
        event.setPayload(payload);
        String first = OutboundWebhookEventIds.forEvent(event);
        String second = OutboundWebhookEventIds.forEvent(event);
        assertEquals(first, second);
        assertEquals(64, first.length());
    }

    @Test
    public void testIdsAreUnique()
    {
        assertTrue(OutboundWebhookEventIds.forTest().startsWith("evt_test_"));
        assertNotEquals(OutboundWebhookEventIds.forTest(), OutboundWebhookEventIds.forTest());
    }
}
