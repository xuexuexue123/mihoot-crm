package com.ruoyi.crm.service.webhook;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

public final class OutboundWebhookEnvelope
{
    private OutboundWebhookEnvelope()
    {
    }

    public static String toJson(String eventId, String eventType, Instant occurredAt, String tenant, JSONObject data)
    {
        JSONObject envelope = new JSONObject();
        envelope.put("id", eventId);
        envelope.put("type", eventType);
        Instant at = occurredAt == null ? Instant.now() : occurredAt;
        envelope.put("occurredAt", OffsetDateTime.ofInstant(at, ZoneId.of(OutboundWebhookConstants.ZONE_SHANGHAI)).toString());
        envelope.put("tenant", tenant);
        envelope.put("data", data == null ? new JSONObject() : data);
        return JSON.toJSONString(envelope, JSONWriter.Feature.MapSortField);
    }
}
