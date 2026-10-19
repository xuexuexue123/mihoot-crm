package com.ruoyi.crm.event;

/**
 * Messenger Activity Event Type 常量。
 *
 * <p>category 由 CrmActivityServiceImpl.inferEventCategory() 根据前缀推断。
 */
public final class MessengerActivityEventTypes
{
    public static final String MESSAGE_RECEIVED = "messenger.message.received";
    public static final String MESSAGE_RECEIVED_NAME = "收到Messenger消息";
    public static final int MESSAGE_RECEIVED_IMPORTANCE = 30;
    public static final String MESSAGE_RECEIVED_CATEGORY = "engagement";

    public static final String MESSAGE_SENT = "messenger.message.sent";
    public static final String MESSAGE_SENT_NAME = "发送Messenger消息";
    public static final int MESSAGE_SENT_IMPORTANCE = 20;
    public static final String MESSAGE_SENT_CATEGORY = "engagement";

    public static final String MESSAGE_DELIVERED = "messenger.message.delivered";
    public static final String MESSAGE_DELIVERED_NAME = "Messenger消息已送达";
    public static final int MESSAGE_DELIVERED_IMPORTANCE = 10;
    public static final String MESSAGE_DELIVERED_CATEGORY = "engagement";

    public static final String MESSAGE_READ = "messenger.message.read";
    public static final String MESSAGE_READ_NAME = "Messenger消息已读";
    public static final int MESSAGE_READ_IMPORTANCE = 10;
    public static final String MESSAGE_READ_CATEGORY = "engagement";

    public static final String MESSAGE_FAILED = "messenger.message.failed";
    public static final String MESSAGE_FAILED_NAME = "Messenger消息发送失败";
    public static final int MESSAGE_FAILED_IMPORTANCE = 40;
    public static final String MESSAGE_FAILED_CATEGORY = "engagement";

    public static final String LINKED = "messenger.linked";
    public static final String LINKED_NAME = "添加Messenger";
    public static final int LINKED_IMPORTANCE = 80;
    public static final String LINKED_CATEGORY = "channel";

    private MessengerActivityEventTypes() {}
}
