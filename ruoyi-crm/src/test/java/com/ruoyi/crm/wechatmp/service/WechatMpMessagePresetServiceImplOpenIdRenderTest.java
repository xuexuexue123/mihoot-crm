package com.ruoyi.crm.wechatmp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.mapper.CrmEventMapper;
import com.ruoyi.crm.service.support.OutreachRegistrationTokenService;
import com.ruoyi.crm.service.support.SurveyOutreachUrlService;
import com.ruoyi.crm.service.support.TenantPublicAppUrlResolver;
import com.ruoyi.crm.domain.vo.SurveyOutreachUrlVo;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessagePreset;
import com.ruoyi.crm.wechatmp.dto.WechatMpMessagePresetRenderRequest;
import com.ruoyi.crm.wechatmp.dto.WechatMpMessagePresetRenderResult;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConfigMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpMessagePresetMapper;
import com.ruoyi.crm.wechatmp.service.impl.WechatMpMessagePresetServiceImpl;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class WechatMpMessagePresetServiceImplOpenIdRenderTest
{
    private static final String EVENT_ID = "evt-uuid-1";
    private static final String TENANT = "tenant-1";
    private static final String OPEN_ID = "oABC123";
    private static final String APP_ID = "wxAppId1";

    @Mock
    private CrmWechatMpMessagePresetMapper presetMapper;

    @Mock
    private CrmWechatMpConfigMapper wechatMpConfigMapper;

    @Mock
    private CrmEventMapper crmEventMapper;

    @Mock
    private OutreachRegistrationTokenService outreachRegistrationTokenService;

    @Mock
    private SurveyOutreachUrlService surveyOutreachUrlService;

    @Mock
    private TenantPublicAppUrlResolver tenantPublicAppUrlResolver;

    @InjectMocks
    private WechatMpMessagePresetServiceImpl presetService;

    @BeforeEach
    void setUp()
    {
        ReflectionTestUtils.setField(presetService, "nextAppUrl", "https://h5.example.com");
        lenient().when(tenantPublicAppUrlResolver.resolve(TENANT)).thenReturn("https://tenant1.mycrm.com");
    }

    @Test
    void render_extraSurveyUrlOverridesBuiltIn()
    {
        stubActivePresetForSurvey();
        CrmEvent event = new CrmEvent();
        event.setEventId(EVENT_ID);
        when(crmEventMapper.selectCrmEventByEventId(EVENT_ID)).thenReturn(event);
        when(outreachRegistrationTokenService.isSigningConfigured()).thenReturn(true);
        when(outreachRegistrationTokenService.computeExp(event)).thenReturn(9999999999L);
        when(outreachRegistrationTokenService.buildRegistrationUrl(
                eq(TENANT), eq(EVENT_ID), eq(9999999999L), eq(OPEN_ID), eq(APP_ID)))
                .thenReturn("https://tenant3.mycrm.com/event-registration?token=signed");

        WechatMpMessagePresetRenderRequest request = baseRequest();
        request.setOpenId(OPEN_ID);
        request.setAppId(APP_ID);
        Map<String, String> extra = new java.util.HashMap<>();
        extra.put("surveyUrl", "https://tenant3.mycrm.com/event-registration?token=from-extra");
        request.setExtra(extra);

        WechatMpMessagePresetRenderResult result = presetService.render(1L, request);
        assertEquals("https://tenant3.mycrm.com/event-registration?token=from-extra", result.getUrl());
    }

    @Test
    void render_surveyUrlAliasFromRegistrationUrlWhenTemplateUsesSurveyUrl()
    {
        stubActivePresetForSurvey();
        CrmEvent event = new CrmEvent();
        event.setEventId(EVENT_ID);
        when(crmEventMapper.selectCrmEventByEventId(EVENT_ID)).thenReturn(event);
        when(outreachRegistrationTokenService.isSigningConfigured()).thenReturn(true);
        when(outreachRegistrationTokenService.computeExp(event)).thenReturn(9999999999L);
        when(outreachRegistrationTokenService.buildRegistrationUrl(
                eq(TENANT), eq(EVENT_ID), eq(9999999999L), eq(OPEN_ID), eq(APP_ID)))
                .thenReturn("https://tenant3.mycrm.com/event-registration?token=signed");

        WechatMpMessagePresetRenderRequest request = baseRequest();
        request.setOpenId(OPEN_ID);
        request.setAppId(APP_ID);

        WechatMpMessagePresetRenderResult result = presetService.render(1L, request);
        assertEquals("https://tenant3.mycrm.com/event-registration?token=signed", result.getUrl());
    }

    @Test
    void render_rejectsOpenIdWithoutAppId()
    {
        stubActivePreset();
        WechatMpMessagePresetRenderRequest request = baseRequest();
        request.setOpenId(OPEN_ID);

        assertThrows(ServiceException.class, () -> presetService.render(1L, request));
    }

    @Test
    void render_withOpenIdAndAppId_usesPersonalizedRegistrationUrl()
    {
        stubActivePreset();
        CrmEvent event = new CrmEvent();
        event.setEventId(EVENT_ID);
        when(crmEventMapper.selectCrmEventByEventId(EVENT_ID)).thenReturn(event);
        when(outreachRegistrationTokenService.isSigningConfigured()).thenReturn(true);
        when(outreachRegistrationTokenService.computeExp(event)).thenReturn(9999999999L);
        when(outreachRegistrationTokenService.buildRegistrationUrl(
                eq(TENANT), eq(EVENT_ID), eq(9999999999L), eq(OPEN_ID), eq(APP_ID)))
                .thenReturn("https://h5.example.com/event-registration?token=signed");

        WechatMpMessagePresetRenderRequest request = baseRequest();
        request.setOpenId(OPEN_ID);
        request.setAppId(APP_ID);
        request.setTenantId(TENANT);

        WechatMpMessagePresetRenderResult result = presetService.render(1L, request);

        assertEquals("https://h5.example.com/event-registration?token=signed", result.getUrl());
    }

    @Test
    void render_withoutOpenId_usesGenericRegistrationUrl()
    {
        stubActivePreset();
        CrmEvent event = new CrmEvent();
        event.setEventId(EVENT_ID);
        when(crmEventMapper.selectCrmEventByEventId(EVENT_ID)).thenReturn(event);
        when(outreachRegistrationTokenService.isSigningConfigured()).thenReturn(true);
        when(outreachRegistrationTokenService.computeExp(event)).thenReturn(9999999999L);
        when(outreachRegistrationTokenService.buildRegistrationUrl(eq(TENANT), eq(EVENT_ID), eq(9999999999L)))
                .thenReturn("https://h5.example.com/event-registration?token=generic");

        WechatMpMessagePresetRenderResult result = presetService.render(1L, baseRequest());

        assertEquals("https://h5.example.com/event-registration?token=generic", result.getUrl());
    }

    @Test
    void render_withSurveyId_usesSurveyUrl()
    {
        stubActivePresetForSurvey();
        SurveyOutreachUrlVo vo = new SurveyOutreachUrlVo();
        vo.setSurveyUrl("https://h5.example.com/surveypage?token=survey-signed");
        when(surveyOutreachUrlService.issueUrlForPreset(
                eq(TENANT), eq("survey-1"), eq(null), eq(null), eq(null))).thenReturn(vo);

        WechatMpMessagePresetRenderRequest request = new WechatMpMessagePresetRenderRequest();
        request.setSurveyId("survey-1");
        request.setTenantId(TENANT);

        WechatMpMessagePresetRenderResult result = presetService.render(1L, request);
        assertEquals("https://h5.example.com/surveypage?token=survey-signed", result.getUrl());
    }

    @Test
    void render_rejectsEventIdAndSurveyIdTogether()
    {
        stubActivePreset();
        WechatMpMessagePresetRenderRequest request = baseRequest();
        request.setSurveyId("survey-1");
        assertThrows(ServiceException.class, () -> presetService.render(1L, request));
    }

    private WechatMpMessagePresetRenderRequest baseRequest()
    {
        WechatMpMessagePresetRenderRequest request = new WechatMpMessagePresetRenderRequest();
        request.setEventId(EVENT_ID);
        request.setTenantId(TENANT);
        return request;
    }

    private void stubActivePreset()
    {
        CrmWechatMpMessagePreset preset = new CrmWechatMpMessagePreset();
        preset.setId(1L);
        preset.setStatus("0");
        preset.setAppId(APP_ID);
        preset.setWechatTemplateId("tpl-1");
        preset.setUrlTemplate("{{registrationUrl}}");
        preset.setDataJsonRaw("{\"first\":{\"value\":\"hi\"}}");
        when(presetMapper.selectById(1L)).thenReturn(preset);
    }

    private void stubActivePresetForSurvey()
    {
        CrmWechatMpMessagePreset preset = new CrmWechatMpMessagePreset();
        preset.setId(1L);
        preset.setStatus("0");
        preset.setAppId(APP_ID);
        preset.setWechatTemplateId("tpl-1");
        preset.setUrlTemplate("{{surveyUrl}}");
        preset.setDataJsonRaw("{\"first\":{\"value\":\"hi\"}}");
        when(presetMapper.selectById(1L)).thenReturn(preset);
    }
}
