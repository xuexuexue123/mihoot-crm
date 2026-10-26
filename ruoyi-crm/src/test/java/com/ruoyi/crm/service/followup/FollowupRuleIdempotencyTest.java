package com.ruoyi.crm.service.followup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.domain.dto.FollowupRuleDispatchEvent;

public class FollowupRuleIdempotencyTest
{
    @Test
    public void sameWonEventSameKey()
    {
        FollowupRuleDispatchEvent event = wonEvent("opty-1", Instant.parse("2026-08-18T06:30:10Z"));
        String a = FollowupRuleIdempotency.compute(event, "sys-opportunity-won", "Opportunity", "opty-1");
        String b = FollowupRuleIdempotency.compute(event, "sys-opportunity-won", "Opportunity", "opty-1");
        assertEquals(a, b);
        assertEquals(32, a.length());
    }

    @Test
    public void differentOptyDifferentKey()
    {
        Instant t = Instant.parse("2026-08-18T06:30:10Z");
        String a = FollowupRuleIdempotency.compute(wonEvent("opty-1", t), "sys-opportunity-won", "Opportunity", "opty-1");
        String b = FollowupRuleIdempotency.compute(wonEvent("opty-2", t), "sys-opportunity-won", "Opportunity", "opty-2");
        assertNotEquals(a, b);
    }

    @Test
    public void leadAssignedIncludesOwnerBucket()
    {
        FollowupRuleDispatchEvent event = new FollowupRuleDispatchEvent();
        event.setTenant("demo");
        event.setTriggerType(FollowupRuleConstants.TRIGGER_LEAD_ASSIGNED);
        event.setOccurredAt(Instant.parse("2026-08-18T01:00:00Z"));
        JSONObject payload = new JSONObject();
        JSONObject lead = new JSONObject();
        lead.put("leadId", "lead-1");
        lead.put("userId", 101);
        payload.put("lead", lead);
        event.setPayload(payload);
        String key = FollowupRuleIdempotency.compute(event, "sys-lead-assigned", "Lead", "lead-1");
        assertNotNull(key);
        assertEquals(32, key.length());
    }

    private static FollowupRuleDispatchEvent wonEvent(String optyId, Instant occurred)
    {
        FollowupRuleDispatchEvent event = new FollowupRuleDispatchEvent();
        event.setTenant("demo");
        event.setTriggerType(FollowupRuleConstants.TRIGGER_OPPORTUNITY_WON);
        event.setOccurredAt(occurred);
        JSONObject payload = new JSONObject();
        JSONObject opty = new JSONObject();
        opty.put("optyId", optyId);
        payload.put("opportunity", opty);
        event.setPayload(payload);
        return event;
    }

    @Test
    public void patientRecallBucketIncludesPolicyAndDueDay()
    {
        FollowupRuleDispatchEvent event = new FollowupRuleDispatchEvent();
        event.setTenant("demo");
        event.setTriggerType(FollowupRuleConstants.TRIGGER_PATIENT_RECALL_DUE);
        event.setOccurredAt(Instant.parse("2026-08-18T00:00:00Z"));
        JSONObject payload = new JSONObject();
        JSONObject contact = new JSONObject();
        contact.put("contactId", "c-1");
        payload.put("contact", contact);
        JSONObject schedule = new JSONObject();
        schedule.put("policyId", "sys-recall-implant");
        schedule.put("nextDueAt", "2026-11-16T00:00:00+08:00");
        payload.put("recallSchedule", schedule);
        event.setPayload(payload);

        String a = FollowupRuleIdempotency.compute(event, "sys-patient-recall", "Contact", "c-1");
        schedule.put("policyId", "sys-recall-default");
        String b = FollowupRuleIdempotency.compute(event, "sys-patient-recall", "Contact", "c-1");
        assertEquals(32, a.length());
        assertNotEquals(a, b);

        schedule.put("policyId", "sys-recall-implant");
        schedule.put("nextDueAt", "2026-11-17T00:00:00+08:00");
        String c = FollowupRuleIdempotency.compute(event, "sys-patient-recall", "Contact", "c-1");
        assertNotEquals(a, c);
    }

    @Test
    public void patientRecallFallsBackToPatientValueDueDate()
    {
        FollowupRuleDispatchEvent event = new FollowupRuleDispatchEvent();
        event.setTenant("demo");
        event.setTriggerType(FollowupRuleConstants.TRIGGER_PATIENT_RECALL_DUE);
        event.setOccurredAt(Instant.parse("2026-08-18T00:00:00Z"));
        JSONObject payload = new JSONObject();
        JSONObject pv = new JSONObject();
        pv.put("nextRecallDueAt", "2026-11-16T00:00:00Z");
        payload.put("patientValue", pv);
        event.setPayload(payload);
        String key = FollowupRuleIdempotency.compute(event, "sys-patient-recall", "Contact", "c-1");
        assertEquals(32, key.length());
    }
}
