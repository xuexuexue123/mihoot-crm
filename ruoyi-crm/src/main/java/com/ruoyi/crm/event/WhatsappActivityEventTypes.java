package com.ruoyi.crm.event;

/**
 * WhatsApp Activity Event Type 常量。
 *
 * <p>遵循现有 dot-delimited 命名惯例（{category}.{channel}.{action}）。
 * <p>category 由 CrmActivityServiceImpl.inferEventCategory() 根据前缀推断。
 */
public final class WhatsappActivityEventTypes
{
    public static final String MESSAGE_RECEIVED = "whatsapp.message.received";
    public static final String MESSAGE_RECEIVED_NAME = "收到WhatsApp消息";
    public static final int MESSAGE_RECEIVED_IMPORTANCE = 30;
    public static final String MESSAGE_RECEIVED_CATEGORY = "engagement";

    public static final String MESSAGE_SENT = "whatsapp.message.sent";
    public static final String MESSAGE_SENT_NAME = "发送WhatsApp消息";
    public static final int MESSAGE_SENT_IMPORTANCE = 20;
    public static final String MESSAGE_SENT_CATEGORY = "engagement";

    public static final String MESSAGE_DELIVERED = "whatsapp.message.delivered";
    public static final String MESSAGE_DELIVERED_NAME = "WhatsApp消息已送达";
    public static final int MESSAGE_DELIVERED_IMPORTANCE = 10;
    public static final String MESSAGE_DELIVERED_CATEGORY = "engagement";

    public static final String MESSAGE_READ = "whatsapp.message.read";
    public static final String MESSAGE_READ_NAME = "WhatsApp消息已读";
    public static final int MESSAGE_READ_IMPORTANCE = 10;
    public static final String MESSAGE_READ_CATEGORY = "engagement";

    public static final String MESSAGE_FAILED = "whatsapp.message.failed";
    public static final String MESSAGE_FAILED_NAME = "WhatsApp消息发送失败";
    public static final int MESSAGE_FAILED_IMPORTANCE = 40;
    public static final String MESSAGE_FAILED_CATEGORY = "engagement";

    public static final String TEMPLATE_SENT = "whatsapp.template.sent";
    public static final String TEMPLATE_SENT_NAME = "发送WhatsApp模板消息";
    public static final int TEMPLATE_SENT_IMPORTANCE = 30;
    public static final String TEMPLATE_SENT_CATEGORY = "engagement";

    public static final String LINKED = "whatsapp.linked";
    public static final String LINKED_NAME = "添加WhatsApp";
    public static final int LINKED_IMPORTANCE = 80;
    public static final String LINKED_CATEGORY = "channel";

    /** Inbox 扩展栏业务内容发送（对齐企微 channel.wecom.content.sent） */
    public static final String CONTENT_SENT = "channel.whatsapp.content.sent";
    public static final String CONTENT_SENT_NAME = "发送业务内容";
    public static final int CONTENT_SENT_IMPORTANCE = 55;
    public static final String CONTENT_SENT_CATEGORY = "engagement";

    private WhatsappActivityEventTypes() {}
}
