package com.ruoyi.crm.service.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruoyi.crm.domain.dto.SegmentWechatMpMenuRef;
import com.ruoyi.crm.wechatmp.support.WechatMpMenuUrlNormalizer;

/**
 * 分群规则：公众号菜单点击 → crm_activity EXISTS 子查询编译
 */
@Component
public class WechatMpMenuClickSegmentResolver
{
    private static final String MENU_KEY_COALESCE =
            "COALESCE(a.payload::jsonb->>'menu_key', a.payload::jsonb->>'menuKey', "
                    + "a.payload::jsonb->>'event_key', a.payload::jsonb->>'eventKey')";

    private static final String OPENID_EXPR =
            "COALESCE(a.payload::jsonb->>'open_id', a.payload::jsonb->>'openId', a.payload::jsonb->>'openid', "
                    + "a.context::jsonb->>'open_id', a.context::jsonb->>'openId', a.context::jsonb->>'openid')";

    private static final String SQL_NORMALIZE_MENU_KEY =
            "lower(regexp_replace(regexp_replace(trim(both from split_part(" + MENU_KEY_COALESCE + ", '#', 1)), "
                    + "'^https?://', 'https://', 'i'), '/+', '/', 'g'))";

    @Autowired
    private WechatMpMenuClickSegmentValidator menuClickSegmentValidator;

    public String compile(String operator, Object value, SegmentSqlParamBinder binder)
    {
        SegmentWechatMpMenuRef ref = menuClickSegmentValidator.parseAndValidateMenuRef(operator, value);
        String appIdPlaceholder = binder.bind(ref.getAppId());
        String menuKeyPredicate = buildMenuKeyPredicate(ref.getMenuKey(), binder);
        String existsSql = "EXISTS (SELECT 1 FROM crm_activity a "
                + "WHERE a.channel = 'wechat_mp' "
                + "AND a.event_type = 'channel.wechat_mp.menu.click' "
                + "AND COALESCE(a.context::jsonb->>'app_id', a.context::jsonb->>'appId') = "
                + appIdPlaceholder + " "
                + "AND (" + menuKeyPredicate + ") "
                + "AND ("
                + "(a.subject_type = 'Contact' AND a.subject_id = m.contact_id) "
                + "OR EXISTS (SELECT 1 FROM crm_customer_identity i "
                + "WHERE i.contact_id = m.contact_id "
                + "AND i.identity_type = 'wechat_mp_openid' "
                + "AND i.identity_value = " + OPENID_EXPR + ")"
                + "))";
        if (SegmentOperator.NOT_EXISTS.equals(operator))
        {
            return "NOT " + existsSql;
        }
        return existsSql;
    }

    private String buildMenuKeyPredicate(String menuKey, SegmentSqlParamBinder binder)
    {
        if (!WechatMpMenuUrlNormalizer.isUrlMenuKey(menuKey))
        {
            return MENU_KEY_COALESCE + " = " + binder.bind(menuKey);
        }
        String normalized = WechatMpMenuUrlNormalizer.normalize(menuKey);
        String sqlMatch = WechatMpMenuUrlNormalizer.normalizeForSqlMatch(menuKey);
        if (menuKey.equals(normalized))
        {
            return MENU_KEY_COALESCE + " = " + binder.bind(menuKey)
                    + " OR " + SQL_NORMALIZE_MENU_KEY + " = " + binder.bind(sqlMatch);
        }
        return MENU_KEY_COALESCE + " IN (" + binder.bind(menuKey) + ", " + binder.bind(normalized) + ")"
                + " OR " + SQL_NORMALIZE_MENU_KEY + " = " + binder.bind(sqlMatch);
    }
}
