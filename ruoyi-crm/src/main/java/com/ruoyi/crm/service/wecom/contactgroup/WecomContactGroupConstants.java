package com.ruoyi.crm.service.wecom.contactgroup;

/**
 * 一客一群 / 建群任务 / 提醒常量
 */
public final class WecomContactGroupConstants
{
    private WecomContactGroupConstants()
    {
    }

    public static final String GROUP_PENDING = "pending";
    public static final String GROUP_CREATED = "created";
    public static final String GROUP_AWAITING_BOT = "awaiting_bot";
    public static final String GROUP_ACTIVE = "active";
    public static final String GROUP_FAILED = "failed";
    public static final String GROUP_DISMISSED = "dismissed";

    public static final String TASK_PENDING = "pending";
    public static final String TASK_DONE = "done";
    public static final String TASK_EXPIRED = "expired";
    public static final String TASK_CANCELLED = "cancelled";

    public static final String REMINDER_PENDING = "pending";
    public static final String REMINDER_SENT = "sent";
    public static final String REMINDER_FAILED = "failed";
    public static final String REMINDER_SKIPPED = "skipped";
    public static final String REMINDER_CANCELLED = "cancelled";

    /** 无可用服务群绑定（无行 / 无 chat_id / dismissed） */
    public static final String SKIP_NO_SERVICE_GROUP = "no_service_group";
    /** 无法解析负责人企微 userid */
    public static final String SKIP_NO_WECOM_USERID = "no_wecom_userid";

    public static final String ANCHOR_BOOKING_START = "booking.start";
    public static final String ANCHOR_OPPORTUNITY_WON = "opportunity.won";
    public static final String ANCHOR_RECALL_DUE = "recall.due";

    public static final String OFFSET_DAYS = "days";
    public static final String OFFSET_HOURS = "hours";

    public static final String ENABLED = "1";
    public static final String DISABLED = "0";
    public static final String IS_SYSTEM = "1";

    public static final String SYS_BOOKING_1D = "sys-wecom-booking-1d";
    public static final String SYS_BOOKING_2H = "sys-wecom-booking-2h";
    public static final String SYS_POSTOP_7D = "sys-wecom-postop-7d";
    public static final String SYS_RECALL_DUE = "sys-wecom-recall-due";

    public static final int MAX_RETRY = 3;
    public static final int CLAIM_LEASE_MINUTES = 5;
    public static final int DEFAULT_PENDING_LIMIT = 100;
}
