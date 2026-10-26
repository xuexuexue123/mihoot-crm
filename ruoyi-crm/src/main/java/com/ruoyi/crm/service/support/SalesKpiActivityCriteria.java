package com.ruoyi.crm.service.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * KPI 活动类口径：显式列出已落库 / 约定 event_type（不以 eventCategory=followup 为准，
 * 因 ActivityEventProducer 主要以 event_type 写入）。
 *
 * @see docs/salesgoal-kpi-backend-plan.md §4.4–4.7
 */
public final class SalesKpiActivityCriteria
{
    /** 跟进类：计入 followed_customers / followup_count */
    public static final List<String> FOLLOWUP_EVENT_TYPES = Collections.unmodifiableList(Arrays.asList(
            "followup.started",
            "followup.completed",
            "followup.failed",
            "activity.followup",
            "crm.followup"
    ));

    /** 见面/到院类：计入 met_customers（排除 cancel / noshow） */
    public static final List<String> MET_CUSTOMER_EVENT_TYPES = Collections.unmodifiableList(Arrays.asList(
            "appointment.completed",
            "visit.first",
            "visit.return",
            "appointment.arrived",
            "appointment.check-in",
            "appointment.check_in"
    ));

    /** 明确不计见面的事件 */
    public static final List<String> MET_EXCLUDED_EVENT_TYPES = Collections.unmodifiableList(Arrays.asList(
            "appointment.noshow",
            "appointment.cancelled",
            "appointment.canceled"
    ));

    public static final String LEAD_ASSIGNED_EVENT_TYPE = "lead.assigned";

    /** 企微转接等不计入分配客户数的 event_type 前缀/精确值 */
    public static final List<String> ASSIGNED_EXCLUDED_EVENT_TYPES = Collections.unmodifiableList(Arrays.asList(
            "channel.wecom.transfer_customer",
            "wecom.transfer_customer",
            "transfer_customer"
    ));

    private static final Set<String> FOLLOWUP_SET = new HashSet<>(FOLLOWUP_EVENT_TYPES);
    private static final Set<String> MET_SET = new HashSet<>(MET_CUSTOMER_EVENT_TYPES);
    private static final Set<String> MET_EXCLUDED_SET = new HashSet<>(MET_EXCLUDED_EVENT_TYPES);
    private static final Set<String> ASSIGNED_EXCLUDED_SET = new HashSet<>(ASSIGNED_EXCLUDED_EVENT_TYPES);

    private SalesKpiActivityCriteria()
    {
    }

    public static boolean isFollowupEvent(String eventType)
    {
        return eventType != null && FOLLOWUP_SET.contains(eventType);
    }

    public static boolean isMetCustomerEvent(String eventType)
    {
        if (eventType == null || MET_EXCLUDED_SET.contains(eventType))
        {
            return false;
        }
        if (MET_SET.contains(eventType))
        {
            return true;
        }
        String lower = eventType.toLowerCase(Locale.ROOT);
        return lower.contains("arrived") || lower.contains("check-in") || lower.contains("check_in")
                || lower.contains("到院");
    }

    public static boolean isAssignedExcluded(String eventType)
    {
        return eventType != null && ASSIGNED_EXCLUDED_SET.contains(eventType);
    }
}
