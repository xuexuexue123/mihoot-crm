package com.ruoyi.crm.enums;

import com.ruoyi.common.utils.StringUtils;

/**
 * 工作流自动化类型（契约 §3）
 */
public enum WorkflowAutomationKey
{
    LEAD_TAGGING("LEAD_TAGGING", "LEAD_CREATED"),
    LEAD_CONVERT("LEAD_CONVERT", "LEAD_CONVERT_REQUEST"),
    LEAD_FOLLOWUP("LEAD_FOLLOWUP", "LEAD_ASSIGNED"),
    LEAD_CALLBACK("LEAD_CALLBACK", "LEAD_CALLBACK_DUE"),
    LEAD_APPOINTMENT("LEAD_APPOINTMENT", "LEAD_APPOINTMENT_REQUEST"),
    PATIENT_RECALL("PATIENT_RECALL", "PATIENT_RECALL_DUE"),
    PATIENT_REACTIVATION("PATIENT_REACTIVATION", "PATIENT_INACTIVE"),
    PATIENT_UPSELL("PATIENT_UPSELL", "PATIENT_TREATMENT_COMPLETED");

    private final String key;
    private final String defaultTriggerEvent;

    WorkflowAutomationKey(String key, String defaultTriggerEvent)
    {
        this.key = key;
        this.defaultTriggerEvent = defaultTriggerEvent;
    }

    public String getKey()
    {
        return key;
    }

    public String getDefaultTriggerEvent()
    {
        return defaultTriggerEvent;
    }

    public static WorkflowAutomationKey fromKey(String automationKey)
    {
        if (StringUtils.isBlank(automationKey))
        {
            return null;
        }
        for (WorkflowAutomationKey item : values())
        {
            if (item.key.equals(automationKey))
            {
                return item;
            }
        }
        return null;
    }
}
