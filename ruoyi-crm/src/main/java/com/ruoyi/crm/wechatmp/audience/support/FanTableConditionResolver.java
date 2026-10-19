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
import com.ruoyi.crm.wechatmp.audience.FanAudienceFieldRegistry;

@Component
public class FanTableConditionResolver
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FanAudienceScopeHelper scopeHelper;

    public Set<String> resolve(String appId, String field, String operator, Object value)
    {
        if (FanAudienceFieldRegistry.FIELD_SUBSCRIBED.equals(field))
        {
            return resolveSubscribed(appId, operator);
        }
        if (FanAudienceFieldRegistry.FIELD_HAS_CONTACT.equals(field))
        {
            return resolveHasContact(appId, operator);
        }
        if (FanAudienceFieldRegistry.FIELD_SUBSCRIBE_TIME.equals(field))
        {
            return resolveSubscribeTime(appId, operator, value);
        }
        throw new ServiceException("非法粉丝表字段");
    }

    private Set<String> resolveSubscribed(String appId, String operator)
    {
        if (SegmentOperator.EXISTS.equals(operator))
        {
            return new HashSet<>(queryOpenIds(
                    "SELECT open_id FROM crm_wechat_mp_fan WHERE app_id = ? AND subscribe = true", appId));
        }
        if (SegmentOperator.NOT_EXISTS.equals(operator))
        {
            return new HashSet<>(queryOpenIds(
                    "SELECT open_id FROM crm_wechat_mp_fan WHERE app_id = ? AND subscribe = false", appId));
        }
        throw new ServiceException("非法算子");
    }

    private Set<String> resolveHasContact(String appId, String operator)
    {
        if (SegmentOperator.EXISTS.equals(operator))
        {
            return new HashSet<>(queryOpenIds(
                    "SELECT open_id FROM crm_wechat_mp_fan WHERE app_id = ? AND contact_id IS NOT NULL", appId));
        }
        if (SegmentOperator.NOT_EXISTS.equals(operator))
        {
            Set<String> scoped = scopeHelper.loadScopedOpenIds(appId);
            Set<String> withContact = new HashSet<>(queryOpenIds(
                    "SELECT open_id FROM crm_wechat_mp_fan WHERE app_id = ? AND contact_id IS NOT NULL", appId));
            scoped.removeAll(withContact);
            return scoped;
        }
        throw new ServiceException("非法算子");
    }

    private Set<String> resolveSubscribeTime(String appId, String operator, Object value)
    {
        SegmentSqlParamBinder binder = new SegmentSqlParamBinder();
        String predicate = buildTimePredicate(operator, value, binder);
        String sql = "SELECT open_id FROM crm_wechat_mp_fan WHERE app_id = ? AND subscribe_time IS NOT NULL AND "
                + predicate;
        List<Object> args = new ArrayList<>();
        args.add(appId);
        args.addAll(binder.getParams());
        return new HashSet<>(jdbcTemplate.query(sql, args.toArray(), (rs, rowNum) -> rs.getString("open_id")));
    }

    private String buildTimePredicate(String operator, Object value, SegmentSqlParamBinder binder)
    {
        switch (operator)
        {
            case SegmentOperator.WITHIN_LAST_DAYS:
                return "subscribe_time >= now() - (" + binder.bind(asInteger(value)) + " || ' days')::interval";
            case SegmentOperator.BEFORE_DAYS_AGO:
                return "subscribe_time < now() - (" + binder.bind(asInteger(value)) + " || ' days')::interval";
            case SegmentOperator.BETWEEN:
                List<Object> range = asObjectList(value);
                return "subscribe_time BETWEEN " + binder.bind(range.get(0)) + " AND " + binder.bind(range.get(1));
            default:
                throw new ServiceException("非法算子");
        }
    }

    private Integer asInteger(Object value)
    {
        if (value instanceof Number)
        {
            return ((Number) value).intValue();
        }
        return Integer.valueOf(String.valueOf(value));
    }

    @SuppressWarnings("unchecked")
    private List<Object> asObjectList(Object value)
    {
        if (value instanceof List)
        {
            return new ArrayList<>((List<Object>) value);
        }
        if (value instanceof com.alibaba.fastjson2.JSONArray)
        {
            com.alibaba.fastjson2.JSONArray array = (com.alibaba.fastjson2.JSONArray) value;
            List<Object> items = new ArrayList<>();
            for (int i = 0; i < array.size(); i++)
            {
                items.add(array.get(i));
            }
            return items;
        }
        throw new ServiceException("between 需要 [from, to] 两个 ISO 日期");
    }

    private List<String> queryOpenIds(String sql, Object... args)
    {
        return jdbcTemplate.query(sql, args, (rs, rowNum) -> rs.getString("open_id"));
    }
}
