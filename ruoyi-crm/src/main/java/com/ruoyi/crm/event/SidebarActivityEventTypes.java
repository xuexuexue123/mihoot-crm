package com.ruoyi.crm.event;

/**
 * 企微侧边栏 Timeline 事件元数据（与 Next activity-event-types.ts 对齐）
 */
public final class SidebarActivityEventTypes
{
    public static final String CONTENT_SENT = "channel.wecom.content.sent";
    public static final String CONTENT_SENT_NAME = "发送业务内容";
    public static final int CONTENT_SENT_IMPORTANCE = 55;
    public static final String CONTENT_SENT_CATEGORY = "engagement";

    public static final String DELIVER_SENT = "workflow.sidebar.deliver.sent";
    public static final String DELIVER_SENT_NAME = "工作流侧边栏投递";
    public static final int DELIVER_SENT_IMPORTANCE = 60;
    public static final String DELIVER_SENT_CATEGORY = "workflow";

    private SidebarActivityEventTypes()
    {
    }
}
