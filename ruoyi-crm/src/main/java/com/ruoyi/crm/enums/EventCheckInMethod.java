package com.ruoyi.crm.enums;

import com.ruoyi.common.utils.StringUtils;

public enum EventCheckInMethod
{
    QR_TICKET("qr_ticket"),
    AUDIENCE_LOOKUP("audience_lookup"),
    WALK_IN("walk_in"),
    MANUAL("manual"),
    ONSITE_SELF("onsite_self");

    private final String code;

    EventCheckInMethod(String code)
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
        for (EventCheckInMethod m : values())
        {
            if (m.code.equals(code))
            {
                return true;
            }
        }
        return false;
    }
}
