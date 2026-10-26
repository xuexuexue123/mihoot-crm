package com.ruoyi.crm.enums;

import com.ruoyi.common.utils.StringUtils;

public enum EventRegistrationSource
{
    H5("h5"),
    WEBSITE("website"),
    CRM_MANUAL("crm_manual"),
    WALK_IN("walk_in"),
    /** 七库市场活动报名；event_id = crm_market_event.id */
    MARKET_EVENT("market_event");

    private final String code;

    EventRegistrationSource(String code)
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
        for (EventRegistrationSource s : values())
        {
            if (s.code.equals(code))
            {
                return true;
            }
        }
        return false;
    }
}
