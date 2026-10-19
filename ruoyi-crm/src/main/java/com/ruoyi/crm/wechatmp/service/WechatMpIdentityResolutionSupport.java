package com.ruoyi.crm.wechatmp.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.ICustomerMetricsService;
import com.ruoyi.crm.wechatmp.domain.ActivityUnmatched;
import com.ruoyi.crm.wechatmp.dto.WechatMpUserProfile;
import com.ruoyi.crm.wechatmp.mapper.ActivityUnmatchedMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 公众号 openid/unionid Identity Resolution 与 activity 回填共用逻辑。
 */
@Slf4j
@Component
public class WechatMpIdentityResolutionSupport
{
    public static final String SOURCE_LEAD_CONVERT = "lead_convert";

    @Autowired
    private ICrmCustomerIdentityService identityService;

    @Autowired
    private ICustomerMetricsService customerMetricsService;

    @Autowired
    private ActivityUnmatchedMapper unmatchedMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 3a/3b：在已知 openid 同步结果与 unionId 下解析最终 contactId。
     */
    public String resolveFinalContactId(String openId, String unionId, String syncContactId)
    {
        if (StringUtils.hasText(syncContactId))
        {
            return syncContactId;
        }
        if (StringUtils.hasText(unionId))
        {
            String viaUnion = identityService.findContactByIdentity("wechat_unionid", unionId);
            if (viaUnion != null)
            {
                return viaUnion;
            }
        }
        return identityService.findContactByIdentity("wechat_mp_openid", openId);
    }

    public void bindOpenIdIfNeeded(String openId, String contactId, String source)
    {
        if (!StringUtils.hasText(openId) || !StringUtils.hasText(contactId))
        {
            return;
        }
        if (identityService.findContactByIdentity("wechat_mp_openid", openId) != null)
        {
            return;
        }
        try
        {
            identityService.bindIdentity(contactId, "wechat_mp_openid", openId, source);
        }
        catch (Exception e)
        {
            log.warn("Failed to bind openid {} to contact {}: {}", openId, contactId, e.getMessage());
        }
    }

    public void bindUnionIdIfNeeded(String unionId, String contactId, String source)
    {
        if (!StringUtils.hasText(unionId) || !StringUtils.hasText(contactId))
        {
            return;
        }
        String effectiveSource = StringUtils.hasText(source) ? source : SOURCE_LEAD_CONVERT;
        try
        {
            identityService.bindIdentity(contactId, "wechat_unionid", unionId.trim(), effectiveSource);
        }
        catch (Exception e)
        {
            log.warn("UnionID {} already bound to another contact, skip: {}", unionId, e.getMessage());
            identityService.recordIdentityConflict("wechat_unionid", unionId.trim(),
                    contactId, effectiveSource);
        }
    }

    public void bindUnionIdIfNeeded(String unionId, String contactId, String source, boolean subscribeLikeEvent)
    {
        if (!subscribeLikeEvent)
        {
            return;
        }
        bindUnionIdIfNeeded(unionId, contactId, source);
    }

    public JSONObject buildEnrichContextJson(String unionId, String unionIdSource, WechatMpUserProfile profile)
    {
        JSONObject enrich = new JSONObject();
        if (StringUtils.hasText(unionId))
        {
            enrich.put("unionid", unionId);
        }
        if (StringUtils.hasText(unionIdSource))
        {
            enrich.put("unionid_source", unionIdSource);
        }
        if (profile != null && StringUtils.hasText(profile.getSubscribeScene()))
        {
            enrich.put("subscribe_scene", profile.getSubscribeScene());
        }
        return enrich;
    }

    public void mergeActivityContext(String channelEventId, JSONObject enrichJson, String contactId,
            boolean hadSubjectBefore)
    {
        if (!StringUtils.hasText(channelEventId) || enrichJson == null || enrichJson.isEmpty())
        {
            if (StringUtils.hasText(contactId) && !hadSubjectBefore)
            {
                updateActivitySubject(channelEventId, contactId);
            }
            return;
        }
        String enrichStr = enrichJson.toJSONString();
        if (StringUtils.hasText(contactId) && !hadSubjectBefore)
        {
            jdbcTemplate.update(
                    "UPDATE crm_activity SET "
                            + "context = COALESCE(context, '{}'::jsonb) || ?::jsonb, "
                            + "subject_type = 'Contact', subject_id = ? "
                            + "WHERE channel = 'wechat_mp' AND channel_event_id = ? AND subject_id IS NULL",
                    enrichStr, contactId, channelEventId);
        }
        else
        {
            jdbcTemplate.update(
                    "UPDATE crm_activity SET context = COALESCE(context, '{}'::jsonb) || ?::jsonb "
                            + "WHERE channel = 'wechat_mp' AND channel_event_id = ?",
                    enrichStr, channelEventId);
        }
    }

    public void updateActivitySubject(String channelEventId, String contactId)
    {
        jdbcTemplate.update(
                "UPDATE crm_activity SET subject_type = 'Contact', subject_id = ? "
                        + "WHERE channel = 'wechat_mp' AND channel_event_id = ? AND subject_id IS NULL",
                contactId, channelEventId);
    }

    public void incrementMetricsIfSubjectNewlySet(String channelEventId, String contactId, boolean hadSubjectBefore)
    {
        if (hadSubjectBefore || !StringUtils.hasText(contactId) || !StringUtils.hasText(channelEventId))
        {
            return;
        }
        try
        {
            Map<String, Object> row = jdbcTemplate.queryForMap(
                    "SELECT event_type, channel FROM crm_activity WHERE channel_event_id = ? LIMIT 1",
                    channelEventId);
            String eventType = row.get("event_type") != null ? row.get("event_type").toString() : null;
            String channel = row.get("channel") != null ? row.get("channel").toString() : "wechat_mp";
            if (eventType != null)
            {
                customerMetricsService.incrementMetric(contactId, eventType, channel, null);
            }
        }
        catch (Exception e)
        {
            log.warn("Failed to increment metrics after enrich channelEventId={}: {}", channelEventId, e.getMessage());
        }
    }

    public void markUnmatchedMatched(ActivityUnmatched record, String contactId, String channelEventId,
            boolean hadSubjectBefore)
    {
        if (record == null || record.getId() == null)
        {
            return;
        }
        unmatchedMapper.markMatched(record.getId(), contactId);
        updateActivitySubject(channelEventId, contactId);
        incrementMetricsIfSubjectNewlySet(channelEventId, contactId, hadSubjectBefore);
    }

    public void updateUnmatchedUnionId(Long unmatchedId, String unionId, String unionIdSource)
    {
        if (unmatchedId == null || !StringUtils.hasText(unionId))
        {
            return;
        }
        unmatchedMapper.updateUnionId(unmatchedId, unionId, unionIdSource);
    }
}
