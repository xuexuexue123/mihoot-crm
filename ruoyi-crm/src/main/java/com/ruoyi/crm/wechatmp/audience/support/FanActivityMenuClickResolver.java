package com.ruoyi.crm.wechatmp.audience.support;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.service.support.SegmentOperator;
import com.ruoyi.crm.service.support.SegmentSqlParamBinder;
import com.ruoyi.crm.wechatmp.audience.FanAudienceRuleValidator;
import com.ruoyi.crm.wechatmp.audience.dto.FanWechatMpMenuRef;
import com.ruoyi.crm.wechatmp.support.WechatMpMenuUrlNormalizer;

@Component
public class FanActivityMenuClickResolver
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FanAudienceRuleValidator ruleValidator;

    @Autowired
    private FanAudienceScopeHelper scopeHelper;

    public Set<String> resolve(String appId, String operator, Object value)
    {
        FanWechatMpMenuRef ref = ruleValidator.parseMenuRef(operator, value);
        Set<String> matched = queryMatchedOpenIds(appId, ref.getMenuKey().trim());
        if (SegmentOperator.EXISTS.equals(operator))
        {
            return matched;
        }
        if (SegmentOperator.NOT_EXISTS.equals(operator))
        {
            Set<String> scoped = scopeHelper.loadScopedOpenIds(appId);
            scoped.removeAll(matched);
            return scoped;
        }
        throw new ServiceException("非法算子");
    }

    private Set<String> queryMatchedOpenIds(String appId, String menuKey)
    {
        SegmentSqlParamBinder binder = new SegmentSqlParamBinder();
        String menuPredicate = buildMenuKeyPredicate(menuKey, binder);
        String sql = "SELECT DISTINCT " + FanAudienceSqlSupport.OPENID_EXPR + " AS open_id "
                + "FROM crm_activity a "
                + "WHERE a.channel = 'wechat_mp' "
                + "AND a.event_type = 'channel.wechat_mp.menu.click' "
                + "AND " + FanAudienceSqlSupport.APP_ID_EXPR + " = ? "
                + "AND (" + menuPredicate + ") "
                + "AND " + FanAudienceSqlSupport.OPENID_EXPR + " IS NOT NULL";
        List<Object> args = new ArrayList<>();
        args.add(appId);
        args.addAll(binder.getParams());
        return new HashSet<>(jdbcTemplate.query(sql, args.toArray(), (rs, rowNum) -> rs.getString("open_id")));
    }

    private String buildMenuKeyPredicate(String menuKey, SegmentSqlParamBinder binder)
    {
        if (!WechatMpMenuUrlNormalizer.isUrlMenuKey(menuKey))
        {
            return FanAudienceSqlSupport.MENU_KEY_EXPR + " = " + binder.bind(menuKey);
        }
        String normalized = WechatMpMenuUrlNormalizer.normalize(menuKey);
        String sqlMatch = WechatMpMenuUrlNormalizer.normalizeForSqlMatch(menuKey);
        if (menuKey.equals(normalized))
        {
            return FanAudienceSqlSupport.MENU_KEY_EXPR + " = " + binder.bind(menuKey)
                    + " OR " + FanAudienceSqlSupport.SQL_NORMALIZE_MENU_KEY + " = " + binder.bind(sqlMatch);
        }
        return FanAudienceSqlSupport.MENU_KEY_EXPR + " IN (" + binder.bind(menuKey) + ", " + binder.bind(normalized) + ")"
                + " OR " + FanAudienceSqlSupport.SQL_NORMALIZE_MENU_KEY + " = " + binder.bind(sqlMatch);
    }
}
