package com.ruoyi.crm.wechatmp.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.wechatmp.WechatMpTencentApiClient;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpFan;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessagePreset;
import com.ruoyi.crm.wechatmp.dto.WechatMpMessagePresetRenderResult;
import com.ruoyi.crm.wechatmp.dto.WechatMpMessageTemplateRequest;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpFanMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpMessageMapper;
import com.ruoyi.crm.wechatmp.service.EventMpAudienceOutreachService;
import com.ruoyi.crm.wechatmp.service.EventWechatMpAppIdResolver;
import com.ruoyi.crm.wechatmp.service.IWechatMpMessagePresetService;
import com.ruoyi.crm.wechatmp.service.WechatMpTokenService;

@ExtendWith(MockitoExtension.class)
class WechatMpMessageServiceImplTemplateAppIdTest
{
    @Mock
    private CrmWechatMpMessageMapper messageMapper;

    @Mock
    private WechatMpTokenService tokenService;

    @Mock
    private WechatMpTencentApiClient tencentApiClient;

    @Mock
    private CrmWechatMpFanMapper fanMapper;

    @Mock
    private EventWechatMpAppIdResolver eventWechatMpAppIdResolver;

    @Mock
    private IWechatMpMessagePresetService presetService;

    @Mock
    private EventMpAudienceOutreachService audienceOutreachService;

    @Mock
    private ActivityEventProducer activityEventProducer;

    @Mock
    private ICrmCustomerIdentityService identityService;

    @InjectMocks
    private WechatMpMessageServiceImpl messageService;

    @Test
    void sendTemplateUsesRowLevelWechatMpAppId()
    {
        WechatMpMessageTemplateRequest req = new WechatMpMessageTemplateRequest();
        req.setAppId("wx-wrong");
        req.setWechatMpAppId("wx-actual");
        req.setOpenId("openid-1");
        req.setTemplateId("tpl-1");

        CrmWechatMpFan fan = new CrmWechatMpFan();
        fan.setAppId("wx-actual");
        fan.setOpenId("openid-1");
        fan.setSubscribe(true);
        when(fanMapper.selectByOpenId("wx-actual", "openid-1")).thenReturn(fan);
        when(tokenService.getAccessToken("wx-actual")).thenReturn("token");
        JSONObject ok = new JSONObject();
        ok.put("errcode", 0);
        when(tencentApiClient.sendTemplateMessage(eq("token"), any())).thenReturn(ok);
        when(messageMapper.insert(any())).thenAnswer(inv -> {
            com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessage msg = inv.getArgument(0);
            msg.setId(1L);
            return 1;
        });

        messageService.sendTemplate(req);

        assertEquals("wx-actual", req.getAppId());
    }

    @Test
    void sendTemplateRejectsMismatchWithoutPresetId()
    {
        WechatMpMessageTemplateRequest req = new WechatMpMessageTemplateRequest();
        req.setAppId("wx-event-default");
        req.setOpenId("openid-1");
        req.setTemplateId("tpl-1");

        when(fanMapper.selectByOpenId("wx-event-default", "openid-1")).thenReturn(null);
        CrmWechatMpFan fan = new CrmWechatMpFan();
        fan.setAppId("wx-real");
        fan.setOpenId("openid-1");
        fan.setSubscribe(true);
        when(fanMapper.selectSubscribedByOpenId("openid-1")).thenReturn(Collections.singletonList(fan));

        ServiceException ex = assertThrows(ServiceException.class, () -> messageService.sendTemplate(req));

        assertEquals("wechat_mp_app_id_mismatch", ex.getMessage());
    }

    @Test
    void sendTemplateRerendersWhenPresetMatchesFanAppId()
    {
        WechatMpMessageTemplateRequest req = new WechatMpMessageTemplateRequest();
        req.setAppId("wx-event-default");
        req.setOpenId("openid-1");
        req.setEventId("evt-1");
        req.setPresetId(9L);
        req.setTemplateId("tpl-wrong");
        req.setUrl("http://old");

        when(fanMapper.selectByOpenId("wx-event-default", "openid-1")).thenReturn(null);
        CrmWechatMpFan fan = new CrmWechatMpFan();
        fan.setAppId("wx-real");
        fan.setOpenId("openid-1");
        fan.setSubscribe(true);
        when(fanMapper.selectSubscribedByOpenId("openid-1")).thenReturn(Collections.singletonList(fan));

        CrmWechatMpMessagePreset preset = new CrmWechatMpMessagePreset();
        preset.setId(9L);
        preset.setAppId("wx-real");
        preset.setStatus("0");
        when(presetService.getById(9L)).thenReturn(preset);

        WechatMpMessagePresetRenderResult rendered = new WechatMpMessagePresetRenderResult();
        rendered.setAppId("wx-real");
        rendered.setTemplateId("tpl-real");
        rendered.setUrl("http://new");
        rendered.setData(new JSONObject());
        when(presetService.render(eq(9L), any())).thenReturn(rendered);

        when(tokenService.getAccessToken("wx-real")).thenReturn("token");
        JSONObject ok = new JSONObject();
        ok.put("errcode", 0);
        when(tencentApiClient.sendTemplateMessage(eq("token"), any())).thenReturn(ok);
        when(messageMapper.insert(any())).thenAnswer(inv -> {
            com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessage msg = inv.getArgument(0);
            msg.setId(1L);
            return 1;
        });

        messageService.sendTemplate(req);

        assertEquals("wx-real", req.getAppId());
        assertEquals("tpl-real", req.getTemplateId());
        assertEquals("http://new", req.getUrl());
    }

    @Test
    void sendTemplateRejectsAmbiguousOpenId()
    {
        WechatMpMessageTemplateRequest req = new WechatMpMessageTemplateRequest();
        req.setAppId("wx-a");
        req.setOpenId("openid-1");
        req.setTemplateId("tpl-1");

        when(fanMapper.selectByOpenId("wx-a", "openid-1")).thenReturn(null);
        CrmWechatMpFan fanA = new CrmWechatMpFan();
        fanA.setAppId("wx-a");
        fanA.setSubscribe(true);
        CrmWechatMpFan fanB = new CrmWechatMpFan();
        fanB.setAppId("wx-b");
        fanB.setSubscribe(true);
        when(fanMapper.selectSubscribedByOpenId("openid-1")).thenReturn(Arrays.asList(fanA, fanB));

        ServiceException ex = assertThrows(ServiceException.class, () -> messageService.sendTemplate(req));

        assertEquals("wechat_mp_openid_ambiguous", ex.getMessage());
    }
}
