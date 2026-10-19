package com.ruoyi.crm.service.followup;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 回访规则常量与触发类型
 */
public final class FollowupRuleConstants
{
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_FIRED = "fired";
    public static final String STATUS_SKIPPED = "skipped";
    public static final String STATUS_CANCELLED = "cancelled";

    public static final String ENABLED = "1";
    public static final String DISABLED = "0";
    public static final String IS_SYSTEM = "1";

    public static final String TRIGGER_OPPORTUNITY_WON = "opportunity.won";
    public static final String TRIGGER_LEAD_ASSIGNED = "lead.assigned";
    public static final String TRIGGER_PATIENT_RECALL_DUE = "patient.recall_due";
    public static final String TRIGGER_APPOINTMENT_NOSHOW = "appointment.noshow";

    public static final String DELAY_IMMEDIATE = "immediate";
    public static final String DELAY_FIXED = "fixed_delay";
    public static final String DELAY_BUSINESS_DAYS = "business_days";
    public static final String DELAY_UNTIL_FIELD = "until_field";

    public static final String UNIT_MINUTES = "minutes";
    public static final String UNIT_HOURS = "hours";
    public static final String UNIT_DAYS = "days";

    public static final String DEDUPE_NONE = "none";
    public static final String DEDUPE_ONCE_PER_RECORD = "once_per_record";
    public static final String DEDUPE_WINDOW_DAYS = "window_days";
    public static final String DEDUPE_REPLACE_PENDING = "replace_pending";

    public static final String SOURCE_RECORD_TRIGGER = "record_trigger";
    public static final String SOURCE_EVENT_TRIGGER = "event_trigger";
    public static final String SOURCE_CRON_SCAN = "cron_scan";

    public static final String SUBJECT_OPPORTUNITY = "Opportunity";
    public static final String SUBJECT_LEAD = "Lead";
    public static final String SUBJECT_CONTACT = "Contact";

    public static final String SKIP_RULE_DISABLED = "rule_disabled";
    public static final String SKIP_CONDITION_NOT_MET = "condition_not_met";
    public static final String SKIP_DUPLICATE = "duplicate";
    public static final String SKIP_MISSING_ASSIGNEE = "missing_assignee";
    public static final String SKIP_MISSING_RELATED_ID = "missing_related_id";
    public static final String SKIP_TEMPLATE_RENDER_FAILED = "template_render_failed";
    public static final String SKIP_SUBJECT_DELETED = "subject_deleted";
    public static final String SKIP_CANCELLED_BY_EVENT = "cancelled_by_event";
    public static final String SKIP_RETRY_EXHAUSTED = "retry_exhausted";

    public static final String CANCEL_BY_EVENT = "cancelled_by_event";
    public static final String CREATE_BY = "回访规则";
    public static final String TASK_STATUS = "TODO";
    public static final String TASK_PRIORITY = "低";

    public static final String ZONE_SHANGHAI = "Asia/Shanghai";

    public static final String SYS_OPPORTUNITY_WON = "sys-opportunity-won";
    public static final String SYS_LEAD_ASSIGNED = "sys-lead-assigned";
    public static final String SYS_PATIENT_RECALL = "sys-patient-recall";
    public static final String SYS_APPOINTMENT_NOSHOW = "sys-appointment-noshow";

    public static final Set<String> TRIGGER_TYPES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            TRIGGER_OPPORTUNITY_WON, TRIGGER_LEAD_ASSIGNED, TRIGGER_PATIENT_RECALL_DUE, TRIGGER_APPOINTMENT_NOSHOW)));

    public static final Set<String> DELAY_MODES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            DELAY_IMMEDIATE, DELAY_FIXED, DELAY_BUSINESS_DAYS, DELAY_UNTIL_FIELD)));

    private FollowupRuleConstants()
    {
    }
}
