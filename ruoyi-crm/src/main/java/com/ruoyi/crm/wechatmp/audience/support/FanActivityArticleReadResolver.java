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
import com.ruoyi.crm.wechatmp.audience.dto.FanWechatMpArticleRef;

@Component
public class FanActivityArticleReadResolver
{
    private static final String ARTICLE_ID_EXPR =
            "COALESCE(a.payload::jsonb->>'article_id', a.payload::jsonb->>'articleId')";

    private static final String ARTICLE_URL_EXPR =
            "COALESCE(a.payload::jsonb->>'url', a.payload::jsonb->>'article_url', a.payload::jsonb->>'articleUrl')";

    private static final String ARTICLE_TITLE_EXPR =
            "COALESCE(a.payload::jsonb->>'title', a.payload::jsonb->>'article_title', a.payload::jsonb->>'articleTitle')";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FanAudienceRuleValidator ruleValidator;

    @Autowired
    private FanAudienceScopeHelper scopeHelper;

    public Set<String> resolve(String appId, String operator, Object value)
    {
        FanWechatMpArticleRef ref = ruleValidator.parseArticleRef(operator, value);
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

    private Set<String> queryMatchedOpenIds(String appId, FanWechatMpArticleRef ref)
    {
        SegmentSqlParamBinder binder = new SegmentSqlParamBinder();
        List<String> predicates = new ArrayList<>();
        if (StringUtils.hasText(ref.getUrl()))
        {
            predicates.add(ARTICLE_URL_EXPR + " = " + binder.bind(ref.getUrl().trim()));
        }
        if (StringUtils.hasText(ref.getArticleId()))
        {
            predicates.add(ARTICLE_ID_EXPR + " = " + binder.bind(ref.getArticleId().trim()));
        }
        if (StringUtils.hasText(ref.getTitle()))
        {
            predicates.add(ARTICLE_TITLE_EXPR + " ILIKE " + binder.bind("%" + ref.getTitle().trim() + "%"));
        }
        if (predicates.isEmpty())
        {
            return new HashSet<>();
        }
        String sql = "SELECT DISTINCT " + FanAudienceSqlSupport.OPENID_EXPR + " AS open_id "
                + "FROM crm_activity a "
                + "WHERE a.channel = 'wechat_mp' "
                + "AND a.event_type = 'channel.wechat_mp.article.read' "
                + "AND " + FanAudienceSqlSupport.APP_ID_EXPR + " = ? "
                + "AND (" + String.join(" OR ", predicates) + ") "
                + "AND " + FanAudienceSqlSupport.OPENID_EXPR + " IS NOT NULL";
        List<Object> args = new ArrayList<>();
        args.add(appId);
        args.addAll(binder.getParams());
        return new HashSet<>(jdbcTemplate.query(sql, args.toArray(), (rs, rowNum) -> rs.getString("open_id")));
    }
}
