package com.ruoyi.crm.wechatmp.audience.support;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class FanAudienceScopeHelper
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Set<String> loadScopedOpenIds(String appId)
    {
        Set<String> openIds = new HashSet<>();
        openIds.addAll(queryOpenIds(
                "SELECT open_id FROM crm_wechat_mp_fan WHERE app_id = ? AND open_id IS NOT NULL",
                appId));
        openIds.addAll(queryOpenIds(
                "SELECT DISTINCT " + FanAudienceSqlSupport.OPENID_EXPR + " AS open_id "
                        + "FROM crm_activity a WHERE a.channel = 'wechat_mp' "
                        + "AND " + FanAudienceSqlSupport.APP_ID_EXPR + " = ? "
                        + "AND " + FanAudienceSqlSupport.OPENID_EXPR + " IS NOT NULL",
                appId));
        return openIds;
    }

    public Set<String> loadSubscribedOpenIds(String appId)
    {
        return new HashSet<>(queryOpenIds(
                "SELECT open_id FROM crm_wechat_mp_fan WHERE app_id = ? AND subscribe = true AND open_id IS NOT NULL",
                appId));
    }

    private List<String> queryOpenIds(String sql, Object... args)
    {
        return jdbcTemplate.query(sql, args, (rs, rowNum) -> rs.getString("open_id"));
    }
}
