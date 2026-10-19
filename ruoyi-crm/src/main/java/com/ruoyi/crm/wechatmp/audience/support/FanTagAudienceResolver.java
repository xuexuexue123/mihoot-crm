package com.ruoyi.crm.wechatmp.audience.support;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.service.support.SegmentOperator;
import com.ruoyi.crm.wechatmp.audience.FanAudienceRuleValidator;
import com.ruoyi.crm.wechatmp.audience.dto.FanWechatMpTagRef;

/**
 * 标签条件解析器：通过 tagId 过滤粉丝。
 * <p>
 * 数据源为本地粉丝表 {@code crm_wechat_mp_fan.tag_ids}（JSONB 数组），
 * 在粉丝同步（{@code user/info/batchget}）时由 {@code tagid_list} 写入。
 * </p>
 *
 * @author luohd
 * @date 2026-07-17
 */
@Component
public class FanTagAudienceResolver
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FanAudienceRuleValidator ruleValidator;

    @Autowired
    private FanAudienceScopeHelper scopeHelper;

    /**
     * @param appId    公众号 AppID
     * @param operator exists / not_exists
     * @param value    前端传递的 WechatMpFanTagRef 或其 JSON 形式
     * @return 命中或排除后的 openid 集合
     */
    public Set<String> resolve(String appId, String operator, Object value)
    {
        FanWechatMpTagRef ref = ruleValidator.parseTagRef(operator, value);
        Long tagId = ref.getTagId();

        Set<String> matched = queryOpenIdsByTag(appId, tagId);

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
        throw new ServiceException("非法算子: " + operator);
    }

    private Set<String> queryOpenIdsByTag(String appId, Long tagId)
    {
        String sql = "SELECT open_id FROM crm_wechat_mp_fan "
                + "WHERE app_id = ? AND tag_ids::jsonb @> ?::jsonb";
        List<String> rows = jdbcTemplate.query(
                sql,
                new Object[]{ appId, "[" + tagId + "]" },
                (rs, rowNum) -> rs.getString("open_id"));
        return new HashSet<>(rows);
    }
}
