package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmCapture;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.dto.CaptureSubmitRequest;
import com.ruoyi.crm.domain.vo.CaptureSubmitResultVo;
import com.ruoyi.crm.domain.vo.PublicCaptureDefinitionVo;
import com.ruoyi.crm.mapper.CrmCaptureMapper;
import com.ruoyi.crm.service.support.CaptureLeadAttributionService;
import com.ruoyi.crm.service.support.CaptureSurveyDataMapper.MappedLeadFields;
import com.ruoyi.crm.service.support.OutreachRegistrationIdentityResolution;
import com.ruoyi.crm.service.support.PublicTenantSiteResolver;
import com.ruoyi.crm.service.support.WaitResumeTenantBootstrap;
import com.ruoyi.crm.service.support.WebsiteLeadIdentityResolver;
import com.ruoyi.crm.service.support.WebsiteVisitorSessionService;
import com.ruoyi.tenant.domain.vo.PublicTenantSiteVo;

@ExtendWith(MockitoExtension.class)
class CrmPublicCaptureServiceImplTest
{
    @Mock
    private PublicTenantSiteResolver publicTenantSiteResolver;
    @Mock
    private WaitResumeTenantBootstrap waitResumeTenantBootstrap;
    @Mock
    private CrmCaptureMapper captureMapper;
    @Mock
    private WebsiteLeadIdentityResolver identityResolver;
    @Mock
    private CaptureLeadAttributionService leadAttributionService;
    @Mock
    private WebsiteVisitorSessionService visitorSessionService;

    @InjectMocks
    private CrmPublicCaptureServiceImpl service;

    @Test
    void getPublished_returnsSnapshot()
    {
        when(publicTenantSiteResolver.resolveActiveSite("demo")).thenReturn(activeSite());
        when(captureMapper.selectByCaptureId("cap-1")).thenReturn(publishedCapture("cap-1", null));

        PublicCaptureDefinitionVo vo = service.getPublished("cap-1", "demo", null);

        assertEquals("cap-1", vo.getCaptureId());
        assertEquals("{\"pages\":[]}", vo.getSurveyJson());
        verify(waitResumeTenantBootstrap).bindTenantDataSource("tenant_demo");
    }

    @Test
    void getPublished_unpublished_returns404()
    {
        when(publicTenantSiteResolver.resolveActiveSite("demo")).thenReturn(activeSite());
        CrmCapture capture = publishedCapture("cap-1", null);
        capture.setStatus(CrmCapture.STATUS_UNPUBLISHED);
        when(captureMapper.selectByCaptureId("cap-1")).thenReturn(capture);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.getPublished("cap-1", "demo", null));
        assertEquals(Integer.valueOf(404), ex.getCode());
    }

    @Test
    void getPublished_rejectsDisallowedOrigin()
    {
        when(publicTenantSiteResolver.resolveActiveSite("demo")).thenReturn(activeSite());
        when(captureMapper.selectByCaptureId("cap-1"))
                .thenReturn(publishedCapture("cap-1", "[\"https://ok.example\"]"));

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.getPublished("cap-1", "demo", "https://bad.example"));
        assertEquals(Integer.valueOf(403), ex.getCode());
    }

    @Test
    void submit_createsLeadAndStitches()
    {
        CaptureSubmitRequest request = validSubmit();
        when(publicTenantSiteResolver.resolveActiveSite("demo")).thenReturn(activeSite());
        when(captureMapper.selectByCaptureId("cap-1")).thenReturn(publishedCapture("cap-1", null));
        when(identityResolver.resolve(eq("tenant_demo"), eq("13800138000")))
                .thenReturn(OutreachRegistrationIdentityResolution.createNewLead());
        when(leadAttributionService.submitNewLead(any(MappedLeadFields.class), any(), any()))
                .thenReturn("lead-new");

        CaptureSubmitResultVo result = service.submit("cap-1", request, null);

        assertEquals("created", result.getResolution());
        assertEquals("lead-new", result.getLeadId());
        verify(visitorSessionService).stitch(eq("sess-1"), isNull(), eq("Lead"), eq("lead-new"));
    }

    @Test
    void submit_updatesExistingLead()
    {
        CaptureSubmitRequest request = validSubmit();
        CrmLead existing = new CrmLead();
        existing.setLeadId("lead-old");
        when(publicTenantSiteResolver.resolveActiveSite("demo")).thenReturn(activeSite());
        when(captureMapper.selectByCaptureId("cap-1")).thenReturn(publishedCapture("cap-1", null));
        when(identityResolver.resolve(eq("tenant_demo"), eq("13800138000")))
                .thenReturn(OutreachRegistrationIdentityResolution.leadUpsert(existing));
        when(leadAttributionService.submitLeadUpdate(any(MappedLeadFields.class), any(), eq(existing)))
                .thenReturn("lead-old");

        CaptureSubmitResultVo result = service.submit("cap-1", request, null);

        assertEquals("updated", result.getResolution());
        assertEquals("lead-old", result.getLeadId());
        verify(visitorSessionService).stitch(eq("sess-1"), isNull(), eq("Lead"), eq("lead-old"));
    }

    private CaptureSubmitRequest validSubmit()
    {
        CaptureSubmitRequest request = new CaptureSubmitRequest();
        request.setTenantSlug("demo");
        request.set_sessionId("sess-1");
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("leadName", "张三");
        data.put("mobilePhone", "13800138000");
        data.put("remark", "hello");
        request.setSurveyData(data);
        return request;
    }

    private static PublicTenantSiteVo activeSite()
    {
        PublicTenantSiteVo site = new PublicTenantSiteVo();
        site.setTenantName("tenant_demo");
        site.setWebsiteStatus("active");
        return site;
    }

    private static CrmCapture publishedCapture(String id, String allowedOrigins)
    {
        CrmCapture capture = new CrmCapture();
        capture.setCaptureId(id);
        capture.setTemplateId("tpl-1");
        capture.setTitle("预约");
        capture.setStatus(CrmCapture.STATUS_PUBLISHED);
        capture.setContentSnapshot("{\"pages\":[]}");
        capture.setSuccessMessage("ok");
        capture.setAllowedOrigins(allowedOrigins);
        return capture;
    }
}
