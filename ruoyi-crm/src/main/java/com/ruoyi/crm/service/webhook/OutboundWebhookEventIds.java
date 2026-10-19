package com.ruoyi.crm.service.webhook;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.dto.OutboundWebhookDispatchEvent;
import com.ruoyi.crm.service.followup.FollowupRuleIdempotency;

public final class OutboundWebhookEventIds
{
    private static final ZoneId SHANGHAI = ZoneId.of(OutboundWebhookConstants.ZONE_SHANGHAI);
    private static final DateTimeFormatter DAY = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter SECOND = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private OutboundWebhookEventIds()
    {
    }

    public static String forTest()
    {
        return "evt_test_" + IdUtils.fastSimpleUUID();
    }

    public static String forEvent(OutboundWebhookDispatchEvent event)
    {
        Instant occurred = event.getOccurredAt() != null ? event.getOccurredAt() : Instant.now();
        String type = event.getEventType();
        JSONObject payload = event.getPayload();
        String material;
        if (OutboundWebhookConstants.EVENT_OPPORTUNITY_WON.equals(type))
        {
            material = join(event.getTenant(), type, nested(payload, "opportunity", "optyId"),
                    occurred.atZone(SHANGHAI).toLocalDateTime().format(SECOND));
        }
        else if (OutboundWebhookConstants.EVENT_LEAD_ASSIGNED.equals(type))
        {
            String ownerId = nested(payload, "lead", "userId");
            if (StringUtils.isBlank(ownerId))
            {
                ownerId = nested(payload, "lead", "owner");
            }
            material = join(event.getTenant(), type, nested(payload, "lead", "leadId"), ownerId,
                    occurred.atZone(SHANGHAI).toLocalDate().format(DAY));
        }
        else if (OutboundWebhookConstants.EVENT_LEAD_CREATED.equals(type))
        {
            material = join(event.getTenant(), type, nested(payload, "lead", "leadId"));
        }
        else if (OutboundWebhookConstants.EVENT_CONTACT_CREATED.equals(type))
        {
            material = join(event.getTenant(), type, nested(payload, "contact", "contactId"));
        }
        else if (OutboundWebhookConstants.EVENT_PATIENT_RECALL_DUE.equals(type))
        {
            String due = nested(payload, "recallSchedule", "nextDueAt");
            String day;
            if (StringUtils.isNotBlank(due))
            {
                try
                {
                    day = Instant.parse(due).atZone(SHANGHAI).toLocalDate().format(DAY);
                }
                catch (Exception ex)
                {
                    day = due.length() >= 10 ? due.substring(0, 10) : due;
                }
            }
            else
            {
                day = occurred.atZone(SHANGHAI).toLocalDate().format(DAY);
            }
            material = join(event.getTenant(), type, nested(payload, "contact", "contactId"), day);
        }
        else if (OutboundWebhookConstants.EVENT_APPOINTMENT_NOSHOW.equals(type))
        {
            String ref = StringUtils.isNotBlank(event.getSourceRef())
                    ? event.getSourceRef()
                    : nested(payload, "appointment", "appointmentId");
            material = join(event.getTenant(), type, ref);
        }
        else
        {
            material = join(event.getTenant(), type, String.valueOf(occurred.getEpochSecond()));
        }
        return FollowupRuleIdempotency.sha256Prefix(material, 64);
    }

    private static String nested(JSONObject payload, String parent, String field)
    {
        if (payload == null)
        {
            return "";
        }
        JSONObject obj = payload.getJSONObject(parent);
        if (obj == null)
        {
            Object value = payload.get(field);
            return value == null ? "" : String.valueOf(value);
        }
        Object value = obj.get(field);
        return value == null ? "" : String.valueOf(value);
    }

    private static String join(String... parts)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++)
        {
            if (i > 0)
            {
                sb.append(':');
            }
            sb.append(parts[i] == null ? "" : parts[i]);
        }
        return sb.toString();
    }
}
