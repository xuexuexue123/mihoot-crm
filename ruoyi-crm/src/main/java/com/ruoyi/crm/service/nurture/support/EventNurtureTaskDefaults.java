package com.ruoyi.crm.service.nurture.support;

/**
 * 培育自动化创建 CRM 任务时的默认值（与手工活动任务字段对齐）
 */
public final class EventNurtureTaskDefaults
{
    /** 与 CRM 字典 crm_task_type 常见值一致，可在步骤 actionJson.taskType 覆盖 */
    public static final String TASK_TYPE = "支持类活动";

    public static final String PRIORITY = "Normal";

    public static final String STATUS = "Not Started";

    public static final String CREATE_BY = "nurture";

    private EventNurtureTaskDefaults()
    {
    }
}
