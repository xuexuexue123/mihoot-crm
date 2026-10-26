package com.ruoyi.crm.service.webhook;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class OutboundWebhookConstants
{
    public static final String EVENT_OPPORTUNITY_WON = "opportunity.won";
    public static final String EVENT_LEAD_ASSIGNED = "lead.assigned";
    public static final String EVENT_LEAD_CREATED = "lead.created";
    public static final String EVENT_CONTACT_CREATED = "contact.created";
    public static final String EVENT_PATIENT_RECALL_DUE = "patient.recall_due";
    public static final String EVENT_APPOINTMENT_NOSHOW = "appointment.noshow";

    public static final Set<String> EVENT_TYPES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            EVENT_OPPORTUNITY_WON, EVENT_LEAD_ASSIGNED, EVENT_LEAD_CREATED, EVENT_CONTACT_CREATED,
            EVENT_PATIENT_RECALL_DUE, EVENT_APPOINTMENT_NOSHOW)));

    public static final String SOURCE_EVENT = "event";
    public static final String SOURCE_TEST = "test";

    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_RETRYING = "retrying";
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_FAILED = "failed";

    public static final String ENABLED = "1";
    public static final String DISABLED = "0";

    public static final String ZONE_SHANGHAI = "Asia/Shanghai";

    public static final int MAX_PER_TENANT = 20;
    public static final int MIN_RETRY = 0;
    public static final int MAX_RETRY = 8;
    public static final int MIN_TIMEOUT_MS = 1000;
    public static final int MAX_TIMEOUT_MS = 30000;
    public static final int DEFAULT_RETRY = 3;
    public static final int DEFAULT_TIMEOUT_MS = 5000;

    public static final String ERROR_SSRF = "ssrf_blocked";

    private OutboundWebhookConstants()
    {
    }
}
