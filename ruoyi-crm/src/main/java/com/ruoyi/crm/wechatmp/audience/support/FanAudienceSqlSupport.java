package com.ruoyi.crm.wechatmp.audience.support;

public final class FanAudienceSqlSupport
{
    public static final String OPENID_EXPR =
            "COALESCE(a.payload::jsonb->>'open_id', a.payload::jsonb->>'openId', a.payload::jsonb->>'openid', "
                    + "a.context::jsonb->>'open_id', a.context::jsonb->>'openId', a.context::jsonb->>'openid')";

    public static final String MENU_KEY_EXPR =
            "COALESCE(a.payload::jsonb->>'menu_key', a.payload::jsonb->>'menuKey', "
                    + "a.payload::jsonb->>'event_key', a.payload::jsonb->>'eventKey')";

    public static final String APP_ID_EXPR =
            "COALESCE(a.context::jsonb->>'app_id', a.context::jsonb->>'appId')";

    public static final String SQL_NORMALIZE_MENU_KEY =
            "lower(regexp_replace(regexp_replace(trim(both from split_part(" + MENU_KEY_EXPR + ", '#', 1)), "
                    + "'^https?://', 'https://', 'i'), '/+', '/', 'g'))";

    private FanAudienceSqlSupport()
    {
    }
}
