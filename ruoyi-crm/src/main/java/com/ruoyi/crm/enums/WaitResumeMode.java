package com.ruoyi.crm.enums;

import com.ruoyi.common.utils.StringUtils;

/**
 * Wait 节点预注册模式（webhook / form 需持久化 resume key）
 */
public enum WaitResumeMode
{
    ON_WEBHOOK_CALL("on_webhook_call"),
    ON_FORM_SUBMITTED("on_form_submitted");

    private final String value;

    WaitResumeMode(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    public static WaitResumeMode fromValue(String mode)
    {
        if (StringUtils.isBlank(mode))
        {
            return null;
        }
        for (WaitResumeMode item : values())
        {
            if (item.value.equalsIgnoreCase(mode))
            {
                return item;
            }
        }
        return null;
    }
}
