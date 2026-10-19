package com.ruoyi.crm.domain.enums;

/**
 * Unified Inbox channel identifiers (API / DB / frontend aligned).
 */
public enum InboxChannel
{
    WHATSAPP("whatsapp"),
    MESSENGER("messenger"),
    WECHAT_MP("wechat_mp"),
    WEBCHAT("webchat"),
    WECOM_KF("wecom_kf");

    private final String code;

    InboxChannel(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }

    public static InboxChannel fromCode(String code)
    {
        if (code == null)
        {
            return null;
        }
        for (InboxChannel ch : values())
        {
            if (ch.code.equalsIgnoreCase(code))
            {
                return ch;
            }
        }
        return null;
    }
}
