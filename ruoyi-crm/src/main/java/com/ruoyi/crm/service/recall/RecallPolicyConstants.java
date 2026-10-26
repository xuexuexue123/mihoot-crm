package com.ruoyi.crm.service.recall;

public final class RecallPolicyConstants
{
    public static final String ENABLED = "1";
    public static final String DISABLED = "0";
    public static final String IS_SYSTEM = "1";

    public static final String STATUS_SCHEDULED = "scheduled";
    public static final String STATUS_FIRED = "fired";
    public static final String STATUS_CANCELLED = "cancelled";

    public static final String ANCHOR_OPPORTUNITY_WON = "opportunity.won";

    public static final String UNIT_DAYS = "days";
    public static final String UNIT_MONTHS = "months";

    public static final String SYS_IMPLANT = "sys-recall-implant";
    public static final String SYS_PREVENTIVE = "sys-recall-preventive";
    public static final String SYS_RESTORATIVE = "sys-recall-restorative";
    public static final String SYS_DEFAULT = "sys-recall-default";

    public static final String PATIENT_RECALL_DELAY_FIELD = "recallSchedule.nextDueAt";
    public static final String PATIENT_RECALL_TASK_TEMPLATE =
            "{\"taskSubject\":\"复诊回访：{{contact.name}}（{{opportunity.treatmentItemCode}}）\","
            + "\"taskType\":\"售后回访\",\"relatedTotype\":\"联系人\","
            + "\"relatedToid\":\"{{contact.contactId}}\",\"relatedTo\":\"{{contact.name}}\","
            + "\"assigneeMode\":\"record_owner\",\"dueDateMode\":\"from_field\","
            + "\"dueDateField\":\"recallSchedule.nextDueAt\",\"writePulse\":false}";

    private RecallPolicyConstants()
    {
    }
}
