package com.ruoyi.crm.wechatmp.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.wechatmp.dto.WechatMpIdentityEnrichContext;
import com.ruoyi.crm.wechatmp.dto.WechatMpUserProfile;
import com.ruoyi.crm.wechatmp.mapper.ActivityUnmatchedMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpFanMapper;

@ExtendWith(MockitoExtension.class)
class WechatMpIdentityEnrichWorkerTest
{
    @Mock
    private WechatMpUserProfileResolver userProfileResolver;

    @Mock
    private WechatMpIdentityResolutionSupport resolutionSupport;

    @Mock
    private ActivityUnmatchedMapper unmatchedMapper;

    @Mock
    private CrmWechatMpFanMapper fanMapper;

    @InjectMocks
    private WechatMpIdentityEnrichWorker worker;

    @Test
    void enrich_bindsUnionIdAndBackfillsActivityWhenOpenIdUnmatched()
    {
        WechatMpUserProfile profile = new WechatMpUserProfile();
        profile.setOpenId("o-new");
        profile.setUnionId("u-new");
        profile.setSubscribe(true);
        profile.setSubscribeScene("ADD_SCENE_SEARCH");
        when(userProfileResolver.resolve("wxApp", "o-new")).thenReturn(java.util.Optional.of(profile));
        when(resolutionSupport.resolveFinalContactId("o-new", "u-new", null)).thenReturn("C001");
        when(resolutionSupport.buildEnrichContextJson(
                eq("u-new"), eq(WechatMpUserProfileResolver.UNION_ID_SOURCE_API), eq(profile)))
                .thenReturn(new JSONObject()
                        .fluentPut("unionid", "u-new")
                        .fluentPut("unionid_source", "wechat_api")
                        .fluentPut("subscribe_scene", "ADD_SCENE_SEARCH"));

        WechatMpIdentityEnrichContext ctx = new WechatMpIdentityEnrichContext();
        ctx.setAppId("wxApp");
        ctx.setOpenId("o-new");
        ctx.setChannelEventId("mp_123_o-new");
        ctx.setEventType("channel.wechat_mp.follow");
        ctx.setSyncWroteUnmatched(true);
        ctx.setUnmatchedId(99L);

        worker.enrich(ctx);

        verify(resolutionSupport).bindOpenIdIfNeeded("o-new", "C001", "wechat_mp_callback");
        verify(resolutionSupport).bindUnionIdIfNeeded(
                "u-new", "C001", "wechat_mp_callback", true);
        verify(resolutionSupport).updateUnmatchedUnionId(
                99L, "u-new", WechatMpUserProfileResolver.UNION_ID_SOURCE_API);
        verify(fanMapper).upsert(any());
    }

    @Test
    void enrich_usesXmlUnionIdFallbackWhenApiHasNoUnionId()
    {
        WechatMpUserProfile profile = new WechatMpUserProfile();
        profile.setOpenId("o-sim");
        profile.setSubscribe(true);
        when(userProfileResolver.resolve("wxApp", "o-sim")).thenReturn(java.util.Optional.of(profile));
        when(resolutionSupport.resolveFinalContactId("o-sim", "u-xml", null)).thenReturn("C002");
        when(resolutionSupport.buildEnrichContextJson(
                eq("u-xml"), eq(WechatMpUserProfileResolver.UNION_ID_SOURCE_XML), eq(profile)))
                .thenReturn(new JSONObject()
                        .fluentPut("unionid", "u-xml")
                        .fluentPut("unionid_source", "xml_event"));

        WechatMpIdentityEnrichContext ctx = new WechatMpIdentityEnrichContext();
        ctx.setAppId("wxApp");
        ctx.setOpenId("o-sim");
        ctx.setChannelEventId("mp_456_o-sim");
        ctx.setEventType("channel.wechat_mp.follow");
        ctx.setXmlUnionId("u-xml");
        ctx.setSyncContactId(null);

        worker.enrich(ctx);

        verify(resolutionSupport).bindUnionIdIfNeeded(
                "u-xml", "C002", "wechat_mp_callback", true);
        verify(unmatchedMapper, never()).insert(any());
    }
}
