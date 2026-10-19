package com.ruoyi.crm.enums;

import com.ruoyi.common.utils.StringUtils;

public enum EventRegistrationStatus
{
    CONFIRMED("confirmed"),
    WAITLIST("waitlist"),
    CANCELLED("cancelled");

    private final String code;

    EventRegistrationStatus(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }

    public static boolean isValid(String code)
    {
        if (StringUtils.isBlank(code))
        {
            return false;
        }
        for (EventRegistrationStatus s : values())
        {
            if (s.code.equals(code))
            {
                return true;
            }
        }
        return false;
    }

    public static EventRegistrationStatus fromCode(String code)
    {
        for (EventRegistrationStatus s : values())
        {
            if (s.code.equals(code))
            {
                return s;
            }
        }
        return null;
    }
}
