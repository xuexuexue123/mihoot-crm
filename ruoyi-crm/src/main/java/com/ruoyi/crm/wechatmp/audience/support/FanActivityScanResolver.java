package com.ruoyi.crm.wechatmp.audience.support;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.service.support.SegmentOperator;
import com.ruoyi.crm.service.support.SegmentSqlParamBinder;
import com.ruoyi.crm.wechatmp.audience.FanAudienceRuleValidator;
import com.ruoyi.crm.wechatmp.audience.dto.FanWechatMpQrcodeRef;

@Component
public class FanActivityScanResolver
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FanAudienceRuleValidator ruleValidator;

    @Autowired
    private FanAudienceScopeHelper scopeHelper;

    public Set<String> resolve(String appId, String operator, Object value)
    {
        FanWechatMpQrcodeRef ref = ruleValidator.parseQrcodeRef(operator, value);
        Set<String> matched = queryMatchedOpenIds(appId, ref);
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

    private Set<String> queryMatchedOpenIds(String appId, FanWechatMpQrcodeRef ref)
    {
        SegmentSqlParamBinder binder = new SegmentSqlParamBinder();
        List<String> predicates = new ArrayList<>();
        if (ref.getQrcodeId() != null)
        {
            String qrcodeId = String.valueOf(ref.getQrcodeId());
            predicates.add("a.context::jsonb->>'qr_code_id' = " + binder.bind(qrcodeId));
            predicates.add("a.payload::jsonb->>'qrcode_id' = " + binder.bind(qrcodeId));
            predicates.add("a.payload::jsonb->>'qrcodeId' = " + binder.bind(qrcodeId));
        }
        if (StringUtils.hasText(ref.getSceneStr()))
        {
            String sceneStr = ref.getSceneStr().trim();
            predicates.add("a.context::jsonb->>'scene_str' = " + binder.bind(sceneStr));
            predicates.add("a.payload::jsonb->>'scene_str' = " + binder.bind(sceneStr));
            predicates.add("a.payload::jsonb->>'event_key' = " + binder.bind(sceneStr));
            predicates.add("a.payload::jsonb->>'event_key' = " + binder.bind("qrscene_" + sceneStr));
        }
        if (predicates.isEmpty())
        {
            return new HashSet<>();
        }
        String sql = "SELECT DISTINCT " + FanAudienceSqlSupport.OPENID_EXPR + " AS open_id "
                + "FROM crm_activity a "
                + "WHERE a.channel = 'wechat_mp' "
                + "AND a.event_type = 'channel.wechat_mp.scan' "
                + "AND " + FanAudienceSqlSupport.APP_ID_EXPR + " = ? "
                + "AND (" + String.join(" OR ", predicates) + ") "
                + "AND " + FanAudienceSqlSupport.OPENID_EXPR + " IS NOT NULL";
        List<Object> args = new ArrayList<>();
        args.add(appId);
        args.addAll(binder.getParams());
        return new HashSet<>(jdbcTemplate.query(sql, args.toArray(), (rs, rowNum) -> rs.getString("open_id")));
    }
}
