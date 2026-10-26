package com.ruoyi.crm.wechatmp.audience.support;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.service.support.SegmentOperator;

@Component
public class FanInteractionActivityResolver
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FanAudienceScopeHelper scopeHelper;

    public Set<String> resolve(String appId, String operator)
    {
        if (SegmentOperator.EXISTS.equals(operator))
        {
            return new HashSet<>(queryRecentInteractionOpenIds(appId));
        }
        if (SegmentOperator.NOT_EXISTS.equals(operator))
        {
            Set<String> scoped = scopeHelper.loadScopedOpenIds(appId);
            scoped.removeAll(new HashSet<>(queryRecentInteractionOpenIds(appId)));
            return scoped;
        }
        throw new ServiceException("非法算子: " + operator + "，lastInteractionWithin48h 仅支持 exists / not_exists");
    }

    private List<String> queryRecentInteractionOpenIds(String appId)
    {
        String sql = "SELECT DISTINCT " + FanAudienceSqlSupport.OPENID_EXPR + " AS open_id "
                + "FROM crm_activity a "
                + "WHERE a.channel = 'wechat_mp' "
                + "AND " + FanAudienceSqlSupport.APP_ID_EXPR + " = ? "
                + "AND " + FanAudienceSqlSupport.OPENID_EXPR + " IS NOT NULL "
                + "AND a.created_at >= NOW() - INTERVAL '48 hours'";
        return jdbcTemplate.query(sql, new Object[]{appId}, (rs, rowNum) -> rs.getString("open_id"));
    }
}
