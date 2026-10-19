package com.ruoyi.crm.webchat;

/**
 * Website Chat Activity Event Type 常量。
 */
public final class WebchatActivityEventTypes
{
    public static final String CONVERSATION_STARTED = "webchat.conversation.started";
    public static final String CONVERSATION_STARTED_NAME = "开始网站会话";
    public static final int CONVERSATION_STARTED_IMPORTANCE = 40;
    public static final String CONVERSATION_STARTED_CATEGORY = "engagement";

    public static final String MESSAGE_RECEIVED = "webchat.message.received";
    public static final String MESSAGE_RECEIVED_NAME = "收到网站聊天消息";
    public static final int MESSAGE_RECEIVED_IMPORTANCE = 30;
    public static final String MESSAGE_RECEIVED_CATEGORY = "engagement";

    public static final String MESSAGE_SENT = "webchat.message.sent";
    public static final String MESSAGE_SENT_NAME = "发送网站聊天消息";
    public static final int MESSAGE_SENT_IMPORTANCE = 20;
    public static final String MESSAGE_SENT_CATEGORY = "engagement";

    public static final String AI_REPLIED = "webchat.ai.replied";
    public static final String AI_REPLIED_NAME = "AI回复网站聊天";
    public static final int AI_REPLIED_IMPORTANCE = 25;
    public static final String AI_REPLIED_CATEGORY = "engagement";

    public static final String CONVERSATION_ASSIGNED = "webchat.conversation.assigned";
    public static final String CONVERSATION_ASSIGNED_NAME = "分配网站会话";
    public static final int CONVERSATION_ASSIGNED_IMPORTANCE = 35;
    public static final String CONVERSATION_ASSIGNED_CATEGORY = "engagement";

    public static final String CONVERSATION_CLOSED = "webchat.conversation.closed";
    public static final String CONVERSATION_CLOSED_NAME = "关闭网站会话";
    public static final int CONVERSATION_CLOSED_IMPORTANCE = 30;
    public static final String CONVERSATION_CLOSED_CATEGORY = "engagement";

    public static final String IDENTIFIED = "webchat.identified";
    public static final String IDENTIFIED_NAME = "网站访客留资";
    public static final int IDENTIFIED_IMPORTANCE = 45;
    public static final String IDENTIFIED_CATEGORY = "engagement";

    public static final String LEAD_CREATED = "webchat.lead.created";
    public static final String LEAD_CREATED_NAME = "网站聊天创建线索";
    public static final int LEAD_CREATED_IMPORTANCE = 50;
    public static final String LEAD_CREATED_CATEGORY = "conversion";

    public static final String LEAD_LINKED = "webchat.lead.linked";
    public static final String LEAD_LINKED_NAME = "网站聊天关联线索";
    public static final int LEAD_LINKED_IMPORTANCE = 50;
    public static final String LEAD_LINKED_CATEGORY = "conversion";

    public static final String LEAD_UNLINKED = "webchat.lead.unlinked";
    public static final String LEAD_UNLINKED_NAME = "网站聊天取消关联线索";
    public static final int LEAD_UNLINKED_IMPORTANCE = 40;
    public static final String LEAD_UNLINKED_CATEGORY = "conversion";

    public static final String LINKED = "webchat.linked";
    public static final String LINKED_NAME = "绑定网站访客";
    public static final int LINKED_IMPORTANCE = 80;
    public static final String LINKED_CATEGORY = "channel";

    private WebchatActivityEventTypes() {}
}
