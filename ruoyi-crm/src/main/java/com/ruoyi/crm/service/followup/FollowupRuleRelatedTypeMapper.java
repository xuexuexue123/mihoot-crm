package com.ruoyi.crm.service.followup;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 任务 relatedTotype / taskType 对齐 ruoyi-ui 字典中文值。
 */
public final class FollowupRuleRelatedTypeMapper
{
    private static final Map<String, String> RELATED_TYPE;
    private static final Map<String, String> TASK_TYPE;

    static
    {
        Map<String, String> related = new HashMap<>();
        related.put("opportunity", "销售机会");
        related.put("opty", "销售机会");
        related.put("销售机会", "销售机会");
        related.put("lead", "业务线索");
        related.put("业务线索", "业务线索");
        related.put("contact", "联系人");
        related.put("联系人", "联系人");
        related.put("organization", "机构");
        related.put("account", "客户");
        related.put("客户", "客户");
        related.put("机构", "机构");
        RELATED_TYPE = Collections.unmodifiableMap(related);

        Map<String, String> task = new HashMap<>();
        task.put("after sales", "售后回访");
        task.put("after_sales", "售后回访");
        task.put("sales activity", "支持类活动");
        task.put("sales_activity", "支持类活动");
        task.put("售后回访", "售后回访");
        task.put("支持类活动", "支持类活动");
        TASK_TYPE = Collections.unmodifiableMap(task);
    }

    private FollowupRuleRelatedTypeMapper()
    {
    }

    public static String toUiRelatedType(String raw)
    {
        if (raw == null)
        {
            return null;
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty())
        {
            return trimmed;
        }
        String mapped = RELATED_TYPE.get(trimmed.toLowerCase(Locale.ROOT));
        if (mapped != null)
        {
            return mapped;
        }
        mapped = RELATED_TYPE.get(trimmed);
        return mapped != null ? mapped : trimmed;
    }

    public static String toUiTaskType(String raw)
    {
        if (raw == null || raw.trim().isEmpty())
        {
            return "支持类活动";
        }
        String trimmed = raw.trim();
        String mapped = TASK_TYPE.get(trimmed.toLowerCase(Locale.ROOT));
        if (mapped != null)
        {
            return mapped;
        }
        mapped = TASK_TYPE.get(trimmed);
        return mapped != null ? mapped : trimmed;
    }

    public static String toCanonicalSubjectType(String relatedType)
    {
        if (relatedType == null)
        {
            return FollowupRuleConstants.SUBJECT_CONTACT;
        }
        String ui = toUiRelatedType(relatedType);
        if ("销售机会".equals(ui))
        {
            return FollowupRuleConstants.SUBJECT_OPPORTUNITY;
        }
        if ("业务线索".equals(ui))
        {
            return FollowupRuleConstants.SUBJECT_LEAD;
        }
        return FollowupRuleConstants.SUBJECT_CONTACT;
    }
}
