package com.ruoyi.crm.service.bulkemail;

/**
 * 邮件活动发送生命周期常量
 */
public final class BulkemailLifecycleConstants
{
    public static final String SEND_MODE_IMMEDIATE = "immediate";
    public static final String SEND_MODE_SCHEDULED = "scheduled";
    public static final String SEND_MODE_DRAFT = "draft";

    public static final String STATUS_DRAFT = "draft";
    public static final String STATUS_SCHEDULED = "scheduled";
    public static final String STATUS_SENDING = "sending";
    public static final String STATUS_SENT = "sent";
    public static final String STATUS_FAILED = "failed";
    public static final String STATUS_CANCELLED = "cancelled";
    public static final String STATUS_ACTIVE_LEGACY = "active";

    public static final String DELIVERY_PENDING = "pending";
    public static final String DELIVERY_SENT = "sent";
    public static final String DELIVERY_FAILED = "failed";

    public static final int BATCH_CONTACTS_MAX = 2000;
    public static final int SEND_BATCH_SIZE = 50;

    private BulkemailLifecycleConstants()
    {
    }

    public static void normalizeLegacyStatus(com.ruoyi.crm.domain.CrmBulkemail campaign)
    {
        if (campaign == null)
        {
            return;
        }
        if (STATUS_ACTIVE_LEGACY.equals(campaign.getStatus()))
        {
            campaign.setStatus(STATUS_SENT);
        }
    }
}
