package com.ruoyi.crm.service.followup;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.dto.FollowupRuleDispatchEvent;

/**
 * 幂等键：SHA256(tenant:triggerType:subjectType:subjectId:ruleId:bucket).slice(0, 32)
 */
public final class FollowupRuleIdempotency
{
    private static final ZoneId SHANGHAI = ZoneId.of(FollowupRuleConstants.ZONE_SHANGHAI);
    private static final DateTimeFormatter DAY = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter SECOND = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private FollowupRuleIdempotency()
    {
    }

    public static String compute(FollowupRuleDispatchEvent event, String ruleId, String subjectType, String subjectId)
    {
        String bucket = bucket(event, subjectType);
        String material = safe(event.getTenant()) + ":"
                + safe(event.getTriggerType()) + ":"
                + safe(subjectType) + ":"
                + safe(subjectId) + ":"
                + safe(ruleId) + ":"
                + safe(bucket);
        return sha256Prefix(material, 32);
    }

    static String bucket(FollowupRuleDispatchEvent event, String subjectType)
    {
        String trigger = event.getTriggerType();
        Instant occurred = event.getOccurredAt() != null ? event.getOccurredAt() : Instant.now();
        if (FollowupRuleConstants.TRIGGER_OPPORTUNITY_WON.equals(trigger))
        {
            return occurred.atZone(SHANGHAI).toLocalDateTime().format(SECOND);
        }
        if (FollowupRuleConstants.TRIGGER_LEAD_ASSIGNED.equals(trigger))
        {
            String ownerId = nestedString(event.getPayload(), "lead", "userId");
            if (StringUtils.isBlank(ownerId))
            {
                ownerId = nestedString(event.getPayload(), "lead", "owner");
            }
            return occurred.atZone(SHANGHAI).toLocalDate().format(DAY) + ":" + safe(ownerId);
        }
        if (FollowupRuleConstants.TRIGGER_PATIENT_RECALL_DUE.equals(trigger))
        {
            String due = nestedString(event.getPayload(), "recallSchedule", "nextDueAt");
            if (StringUtils.isBlank(due))
            {
                due = nestedString(event.getPayload(), "patientValue", "nextRecallDueAt");
            }
            String policyId = nestedString(event.getPayload(), "recallSchedule", "policyId");
            String day = StringUtils.isNotBlank(due)
                    ? toDay(due)
                    : occurred.atZone(SHANGHAI).toLocalDate().format(DAY);
            return day + ":" + safe(policyId);
        }
        if (FollowupRuleConstants.TRIGGER_APPOINTMENT_NOSHOW.equals(trigger))
        {
            return StringUtils.isNotBlank(event.getSourceRef()) ? event.getSourceRef() : "noshow";
        }
        return occurred.atZone(SHANGHAI).toLocalDate().format(DAY);
    }

    private static String toDay(String iso)
    {
        try
        {
            Instant instant = Instant.parse(iso);
            return instant.atZone(SHANGHAI).toLocalDate().format(DAY);
        }
        catch (Exception ex)
        {
            try
            {
                return LocalDate.parse(iso.substring(0, 10)).format(DAY);
            }
            catch (Exception ignored)
            {
                return iso;
            }
        }
    }

    static String nestedString(JSONObject payload, String parent, String field)
    {
        if (payload == null)
        {
            return null;
        }
        JSONObject obj = payload.getJSONObject(parent);
        if (obj == null)
        {
            return payload.getString(field);
        }
        Object value = obj.get(field);
        return value == null ? null : String.valueOf(value);
    }

    public static String sha256Prefix(String material, int length)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(material.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash)
            {
                hex.append(String.format("%02x", b));
            }
            String full = hex.toString();
            return full.substring(0, Math.min(length, full.length()));
        }
        catch (Exception ex)
        {
            throw new IllegalStateException("SHA-256 unavailable", ex);
        }
    }

    private static String safe(String value)
    {
        return value == null ? "" : value;
    }
}
