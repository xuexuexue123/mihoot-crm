package com.ruoyi.crm.enums;

import com.ruoyi.common.utils.StringUtils;

/**
 * 活动培育步骤动作类型
 */
public enum EventNurtureActionType
{
    DELAY("delay"),
    MP_TEMPLATE("mp_template"),
    CREATE_TASK("create_task"),
    WECOM_MASS_TEXT("wecom_mass_text"),
    CONDITION("condition"),
    NOOP("noop");

    private final String code;

    EventNurtureActionType(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }

    public static boolean isValidP0(String value)
    {
        EventNurtureActionType type = fromCode(value);
        return type == DELAY || type == MP_TEMPLATE || type == CREATE_TASK;
    }

    public static EventNurtureActionType fromCode(String value)
    {
        if (StringUtils.isBlank(value))
        {
            return null;
        }
        for (EventNurtureActionType type : values())
        {
            if (type.code.equals(value))
            {
                return type;
            }
        }
        return null;
    }
}
