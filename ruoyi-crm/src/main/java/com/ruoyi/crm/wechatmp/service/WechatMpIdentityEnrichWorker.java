package com.ruoyi.crm.wechatmp.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.crm.wechatmp.domain.ActivityUnmatched;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpFan;
import com.ruoyi.crm.wechatmp.dto.WechatMpIdentityEnrichContext;
import com.ruoyi.crm.wechatmp.dto.WechatMpUserProfile;
import com.ruoyi.crm.wechatmp.mapper.ActivityUnmatchedMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpFanMapper;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.manager.AsyncManager;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;

/**
 * subscribe / SCAN 回调后立即异步拉取 user/info，完成 UnionID enrich。
 */
@Slf4j
@Component
public class WechatMpIdentityEnrichWorker
{
    @Autowired
    private WechatMpUserProfileResolver userProfileResolver;

    @Autowired
    private WechatMpIdentityResolutionSupport resolutionSupport;

    @Autowired
    private ActivityUnmatchedMapper unmatchedMapper;

    @Autowired
    private CrmWechatMpFanMapper fanMapper;

    public static boolean shouldEnrich(String eventType, WxMpXmlMessage wxMessage)
    {
        if (wxMessage == null || !StringUtils.hasText(eventType))
        {
            return false;
        }
        if ("channel.wechat_mp.follow".equals(eventType))
        {
            return true;
        }
        if ("channel.wechat_mp.scan".equals(eventType))
        {
            return true;
        }
        return false;
    }

    public void scheduleImmediately(WechatMpIdentityEnrichContext ctx)
    {
        if (ctx == null || !StringUtils.hasText(ctx.getTenantId()))
        {
            return;
        }
        final WechatMpIdentityEnrichContext copy = ctx;
        AsyncManager.me().execute(new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    TenantDataSourceSupport.bindTenantKey(copy.getTenantId());
                    enrich(copy);
                }
                catch (Exception e)
                {
                    log.error("WechatMpIdentityEnrich failed tenant={} openId={} channelEventId={}",
                            copy.getTenantId(), copy.getOpenId(), copy.getChannelEventId(), e);
                }
                finally
                {
                    DynamicDataSourceContextHolder.clearDataSourceKey();
                }
            }
        });
    }

    void enrich(WechatMpIdentityEnrichContext ctx)
    {
        String openId = ctx.getOpenId();
        String appId = ctx.getAppId();
        if (!StringUtils.hasText(openId) || !StringUtils.hasText(appId))
        {
            return;
        }

        boolean hadSubjectBefore = StringUtils.hasText(ctx.getSyncContactId());
        WechatMpUserProfile profile = userProfileResolver.resolve(appId, openId).orElse(null);

        String unionId = null;
        String unionIdSource = null;
        if (profile != null && StringUtils.hasText(profile.getUnionId()))
        {
            unionId = profile.getUnionId();
            unionIdSource = WechatMpUserProfileResolver.UNION_ID_SOURCE_API;
        }
        else if (StringUtils.hasText(ctx.getXmlUnionId()))
        {
            unionId = ctx.getXmlUnionId();
            unionIdSource = WechatMpUserProfileResolver.UNION_ID_SOURCE_XML;
        }

        String syncContactId = ctx.getSyncContactId();
        String finalContactId = resolutionSupport.resolveFinalContactId(openId, unionId, syncContactId);

        if (finalContactId != null && syncContactId == null)
        {
            resolutionSupport.bindOpenIdIfNeeded(openId, finalContactId, "wechat_mp_callback");
        }

        boolean subscribeLike = isSubscribeLikeEvent(ctx.getEventType());
        resolutionSupport.bindUnionIdIfNeeded(unionId, finalContactId, "wechat_mp_callback", subscribeLike);

        JSONObject enrichJson = resolutionSupport.buildEnrichContextJson(unionId, unionIdSource, profile);
        resolutionSupport.mergeActivityContext(ctx.getChannelEventId(), enrichJson, finalContactId, hadSubjectBefore);

        boolean metricsViaUnmatched = handleUnmatched(ctx, unionId, unionIdSource, finalContactId, hadSubjectBefore);

        if (!metricsViaUnmatched && finalContactId != null && !hadSubjectBefore)
        {
            resolutionSupport.incrementMetricsIfSubjectNewlySet(
                    ctx.getChannelEventId(), finalContactId, hadSubjectBefore);
        }

        if (profile != null)
        {
            upsertFan(appId, profile, finalContactId);
        }
    }

    private boolean handleUnmatched(WechatMpIdentityEnrichContext ctx, String unionId, String unionIdSource,
            String finalContactId, boolean hadSubjectBefore)
    {
        if (ctx.isSyncWroteUnmatched())
        {
            if (ctx.getUnmatchedId() != null && StringUtils.hasText(unionId))
            {
                resolutionSupport.updateUnmatchedUnionId(ctx.getUnmatchedId(), unionId, unionIdSource);
            }
            if (finalContactId != null)
            {
                ActivityUnmatched record = new ActivityUnmatched();
                record.setId(ctx.getUnmatchedId());
                record.setPulseId(ctx.getChannelEventId());
                record.setIdentityType("wechat_mp_openid");
                record.setIdentityValue(ctx.getOpenId());
                resolutionSupport.markUnmatchedMatched(record, finalContactId, ctx.getChannelEventId(), hadSubjectBefore);
                return true;
            }
            return false;
        }

        if (finalContactId == null)
        {
            saveUnmatchedAfterEnrich(ctx, unionId, unionIdSource);
        }
        return false;
    }

    private void saveUnmatchedAfterEnrich(WechatMpIdentityEnrichContext ctx, String unionId, String unionIdSource)
    {
        ActivityUnmatched unmatched = new ActivityUnmatched();
        unmatched.setPulseId(ctx.getChannelEventId());
        unmatched.setIdentityType("wechat_mp_openid");
        unmatched.setIdentityValue(ctx.getOpenId());
        unmatched.setUnionId(unionId);
        unmatched.setUnionIdSource(unionIdSource);
        unmatched.setAppId(ctx.getAppId());
        unmatched.setMaxRetries(12);
        unmatched.setNextRetryAt(LocalDateTime.now().plusSeconds(30));
        unmatched.setRawPayload(ctx.getRawXml());
        try
        {
            unmatchedMapper.insert(unmatched);
        }
        catch (Exception e)
        {
            log.warn("Failed to insert unmatched after enrich openId={}, pulseId={}",
                    ctx.getOpenId(), ctx.getChannelEventId(), e);
        }
    }

    private void upsertFan(String appId, WechatMpUserProfile profile, String contactId)
    {
        CrmWechatMpFan fan = new CrmWechatMpFan();
        fan.setAppId(appId);
        fan.setOpenId(profile.getOpenId());
        fan.setUnionId(profile.getUnionId());
        fan.setNickname(profile.getNickname());
        if (profile.getSubscribe() != null)
        {
            fan.setSubscribe(profile.getSubscribe());
        }
        if (profile.getSubscribeTime() != null)
        {
            fan.setSubscribeTime(new Date(profile.getSubscribeTime() * 1000L));
        }
        fan.setContactId(contactId);
        fan.setLastSyncAt(DateUtils.getNowDate());
        try
        {
            fanMapper.upsert(fan);
        }
        catch (Exception e)
        {
            log.warn("Failed to upsert fan appId={} openId={}: {}", appId, profile.getOpenId(), e.getMessage());
        }
    }

    private boolean isSubscribeLikeEvent(String eventType)
    {
        return "channel.wechat_mp.follow".equals(eventType);
    }
}
