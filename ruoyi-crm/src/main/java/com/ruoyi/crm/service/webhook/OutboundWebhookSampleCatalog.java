package com.ruoyi.crm.service.webhook;

import com.alibaba.fastjson2.JSONObject;

public final class OutboundWebhookSampleCatalog
{
    private OutboundWebhookSampleCatalog()
    {
    }

    public static JSONObject samplePayload(String eventType)
    {
        if (OutboundWebhookConstants.EVENT_OPPORTUNITY_WON.equals(eventType))
        {
            JSONObject opportunity = obj("optyId", "opty-test-1", "name", "种植方案-A", "stage", "成交",
                    "owner", "张咨询师", "contactId", "contact-test-1", "closedWonBasis", "down_payment_received");
            opportunity.put("userId", 101);
            JSONObject previous = new JSONObject();
            previous.put("opportunity", obj("stage", "方案"));
            JSONObject payload = new JSONObject();
            payload.put("opportunity", opportunity);
            payload.put("contact", obj("contactId", "contact-test-1", "name", "李四", "mobile", "13800138000"));
            payload.put("previous", previous);
            payload.put("changedFields", java.util.Collections.singletonList("stage"));
            return payload;
        }
        if (OutboundWebhookConstants.EVENT_LEAD_ASSIGNED.equals(eventType))
        {
            JSONObject lead = obj("leadId", "lead-test-1", "leadName", "王五", "owner", "李咨询师",
                    "mobilePhone", "13800138000", "stage", "新诊");
            lead.put("userId", 102);
            JSONObject previous = new JSONObject();
            previous.put("lead", obj("owner", null));
            JSONObject payload = new JSONObject();
            payload.put("lead", lead);
            payload.put("previous", previous);
            payload.put("changedFields", java.util.Arrays.asList("owner", "userId"));
            return payload;
        }
        if (OutboundWebhookConstants.EVENT_LEAD_CREATED.equals(eventType))
        {
            JSONObject payload = new JSONObject();
            payload.put("lead", obj("leadId", "lead-test-1", "leadName", "王五", "mobilePhone", "13800138000",
                    "owner", "李咨询师"));
            return payload;
        }
        if (OutboundWebhookConstants.EVENT_CONTACT_CREATED.equals(eventType))
        {
            JSONObject payload = new JSONObject();
            payload.put("contact", obj("contactId", "contact-test-1", "name", "李四", "mobile", "13800138000"));
            return payload;
        }
        if (OutboundWebhookConstants.EVENT_PATIENT_RECALL_DUE.equals(eventType))
        {
            JSONObject payload = new JSONObject();
            payload.put("contact", obj("contactId", "contact-test-1", "name", "赵六"));
            payload.put("opportunity", obj("optyId", "opty-test-1", "treatmentItemCode", "种植牙"));
            payload.put("recallSchedule", obj("scheduleId", "sched-test-1", "policyId", "sys-recall-implant",
                    "nextDueAt", "2026-11-16T00:00:00+08:00", "anchorType", "opportunity.won", "anchorId", "opty-test-1"));
            return payload;
        }
        JSONObject payload = new JSONObject();
        payload.put("appointment", obj("appointmentId", "appt-test-1", "scheduledAt", "2026-08-18T10:00:00+08:00",
                "status", "noshow"));
        payload.put("contact", obj("contactId", "contact-test-1", "name", "钱七"));
        payload.put("lead", null);
        return payload;
    }

    public static String defaultTemplate(String eventType)
    {
        if (OutboundWebhookConstants.EVENT_OPPORTUNITY_WON.equals(eventType))
        {
            return "{\"contactId\":\"{{contact.contactId}}\",\"name\":\"{{contact.name}}\",\"mobile\":\"{{contact.mobile}}\","
                    + "\"optyId\":\"{{opportunity.optyId}}\",\"optyName\":\"{{opportunity.name}}\",\"stage\":\"{{opportunity.stage}}\"}";
        }
        if (OutboundWebhookConstants.EVENT_LEAD_ASSIGNED.equals(eventType)
                || OutboundWebhookConstants.EVENT_LEAD_CREATED.equals(eventType))
        {
            return "{\"leadId\":\"{{lead.leadId}}\",\"leadName\":\"{{lead.leadName}}\",\"mobilePhone\":\"{{lead.mobilePhone}}\","
                    + "\"owner\":\"{{lead.owner}}\"}";
        }
        if (OutboundWebhookConstants.EVENT_CONTACT_CREATED.equals(eventType))
        {
            return "{\"contactId\":\"{{contact.contactId}}\",\"name\":\"{{contact.name}}\",\"mobile\":\"{{contact.mobile}}\"}";
        }
        if (OutboundWebhookConstants.EVENT_PATIENT_RECALL_DUE.equals(eventType))
        {
            return "{\"contactId\":\"{{contact.contactId}}\",\"name\":\"{{contact.name}}\","
                    + "\"nextDueAt\":\"{{recallSchedule.nextDueAt}}\"}";
        }
        return "{\"appointmentId\":\"{{appointment.appointmentId}}\",\"status\":\"{{appointment.status}}\","
                + "\"contactId\":\"{{contact.contactId}}\"}";
    }

    private static JSONObject obj(String... kv)
    {
        JSONObject json = new JSONObject();
        for (int i = 0; i + 1 < kv.length; i += 2)
        {
            json.put(kv[i], kv[i + 1]);
        }
        return json;
    }
}
