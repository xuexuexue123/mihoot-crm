package com.ruoyi.crm.enums;

import com.ruoyi.common.utils.StringUtils;

/**
 * 活动培育入组触发器
 */
public enum EventNurtureTriggerType
{
    MP_SUBSCRIBE_ATTRIBUTED("mp_subscribe_attributed"),
    H5_REGISTRATION_CREATED("h5_registration_created"),
    LEAD_RELEASED("lead_released"),
    LEAD_IDLE("lead_idle"),
    REGISTRATION_PENDING_IDLE("registration_pending_idle"),
    AUDIENCE_STATUS_ENTER("audience_status_enter");

    private final String code;

    EventNurtureTriggerType(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }

    public static boolean isValid(String value)
    {
        return fromCode(value) != null;
    }

    public static EventNurtureTriggerType fromCode(String value)
    {
        if (StringUtils.isBlank(value))
        {
            return null;
        }
        for (EventNurtureTriggerType type : values())
        {
            if (type.code.equals(value))
            {
                return type;
            }
        }
        return null;
    }
}
