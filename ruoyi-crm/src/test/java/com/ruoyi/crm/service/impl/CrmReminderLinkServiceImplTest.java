package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmReminderLink;
import com.ruoyi.crm.domain.dto.ReminderLinkCreateRequest;
import com.ruoyi.crm.domain.vo.ReminderLinkCreateResultVo;
import com.ruoyi.crm.domain.vo.ReminderLinkPublicVo;
import com.ruoyi.crm.mapper.CrmReminderLinkMapper;
import com.ruoyi.crm.service.support.PublicTenantSiteResolver;
import com.ruoyi.crm.service.support.WaitResumeTenantBootstrap;
import com.ruoyi.crm.service.whatsapp.ReminderLinkTokenGenerator;
import com.ruoyi.tenant.config.TenantWebsiteProperties;
import com.ruoyi.tenant.domain.vo.PublicTenantSiteVo;

@ExtendWith(MockitoExtension.class)
class CrmReminderLinkServiceImplTest
{
    @Mock
    private CrmReminderLinkMapper reminderLinkMapper;
    @Mock
    private TenantWebsiteProperties tenantWebsiteProperties;
    @Mock
    private PublicTenantSiteResolver publicTenantSiteResolver;
    @Mock
    private WaitResumeTenantBootstrap waitResumeTenantBootstrap;

    @InjectMocks
    private CrmReminderLinkServiceImpl service;

    @BeforeEach
    void stubWebsiteProps()
    {
        lenient().when(tenantWebsiteProperties.getPublicBaseProtocol()).thenReturn("https");
        lenient().when(tenantWebsiteProperties.getEffectivePublicBaseDomain()).thenReturn("mihoot.com");
    }

    @Test
    void create_buildsPublicUrlFromRequestSlug()
    {
        when(reminderLinkMapper.insert(any(CrmReminderLink.class))).thenAnswer(inv -> {
            CrmReminderLink row = inv.getArgument(0);
            row.setId(42L);
            return 1;
        });

        ReminderLinkCreateRequest req = baseCreateRequest();
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "你的预约已取消");
        payload.put("customerName", "张三");
        req.setPayload(payload);
        req.setActions(Collections.singletonList("contact_clinic"));
        req.setExpiresInSeconds(3600L);

        ReminderLinkCreateResultVo vo = service.create("tenant3", "admin", req);

        assertEquals(42L, vo.getId());
        assertTrue(ReminderLinkTokenGenerator.isValidFormat(vo.getToken()));
        assertEquals("https://tenant3.mihoot.com/reminder/" + vo.getToken(), vo.getPublicUrl());
        assertNotNull(vo.getExpiresAt());

        ArgumentCaptor<CrmReminderLink> captor = ArgumentCaptor.forClass(CrmReminderLink.class);
        verify(reminderLinkMapper).insert(captor.capture());
        assertEquals("appointment_cancel", captor.getValue().getType());
        assertEquals("admin", captor.getValue().getCreatedBy());
        assertTrue(captor.getValue().getPayload().contains("张三"));
    }

    @Test
    void create_missingWebsiteSlug_fails()
    {
        ReminderLinkCreateRequest req = new ReminderLinkCreateRequest();
        req.setType("appointment_cancel");

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.create("tenant3", "admin", req));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getCode());
        verify(reminderLinkMapper, never()).insert(any());
    }

    @Test
    void create_revokeSameRef_requiresRef()
    {
        ReminderLinkCreateRequest req = baseCreateRequest();
        req.setRevokeSameRef(true);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.create("tenant3", "admin", req));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getCode());
        verify(reminderLinkMapper, never()).revokeActiveByRef(any(), any(), any());
    }

    @Test
    void create_revokeSameRef_callsMapper()
    {
        when(reminderLinkMapper.insert(any(CrmReminderLink.class))).thenAnswer(inv -> {
            ((CrmReminderLink) inv.getArgument(0)).setId(1L);
            return 1;
        });

        ReminderLinkCreateRequest req = baseCreateRequest();
        req.setRefType("appointment");
        req.setRefId("A-1");
        req.setRevokeSameRef(true);

        service.create("tenant3", "admin", req);

        verify(reminderLinkMapper).revokeActiveByRef("tenant3", "appointment", "A-1");
    }

    @Test
    void create_missingPublicDomain_fails()
    {
        when(tenantWebsiteProperties.getEffectivePublicBaseDomain()).thenReturn("");
        when(reminderLinkMapper.insert(any(CrmReminderLink.class))).thenAnswer(inv -> {
            ((CrmReminderLink) inv.getArgument(0)).setId(1L);
            return 1;
        });

        ReminderLinkCreateRequest req = baseCreateRequest();

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.create("tenant3", "admin", req));
        assertEquals(HttpStatus.ERROR, ex.getCode());
    }

    @Test
    void getPublic_active_marksOpenedAndReturnsBranding()
    {
        when(publicTenantSiteResolver.resolveActiveSite("tenant3")).thenReturn(activeSite());
        CrmReminderLink link = activeLink("x7k2m9pqab");
        when(reminderLinkMapper.selectByTenantAndToken("tenant3", "x7k2m9pqab")).thenReturn(link);

        ReminderLinkPublicVo vo = service.getPublicByToken("x7k2m9pqab", "tenant3");

        assertEquals("appointment_cancel", vo.getType());
        assertEquals("active", vo.getStatus());
        assertEquals("张三", vo.getPayload().get("customerName"));
        assertEquals("tenant3", vo.getBranding().getTenantSlug());
        assertEquals("Tenant3 Clinic", vo.getBranding().getDisplayName());
        verify(waitResumeTenantBootstrap).bindTenantDataSource("tenant3");
        verify(reminderLinkMapper).markOpened(10L, "tenant3");
    }

    @Test
    void getPublic_missingToken_unified404()
    {
        when(publicTenantSiteResolver.resolveActiveSite("tenant3")).thenReturn(activeSite());
        when(reminderLinkMapper.selectByTenantAndToken("tenant3", "x7k2m9pqab")).thenReturn(null);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.getPublicByToken("x7k2m9pqab", "tenant3"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getCode());
        assertEquals("链接无效", ex.getMessage());
        verify(reminderLinkMapper, never()).markOpened(any(), any());
    }

    @Test
    void getPublic_expired_lazyMarksAndNoOpen()
    {
        when(publicTenantSiteResolver.resolveActiveSite("tenant3")).thenReturn(activeSite());
        CrmReminderLink link = activeLink("x7k2m9pqab");
        link.setExpiresAt(new Date(System.currentTimeMillis() - 60_000L));
        when(reminderLinkMapper.selectByTenantAndToken("tenant3", "x7k2m9pqab")).thenReturn(link);

        ReminderLinkPublicVo vo = service.getPublicByToken("x7k2m9pqab", "tenant3");

        assertEquals("expired", vo.getStatus());
        verify(reminderLinkMapper).markExpired(10L, "tenant3");
        verify(reminderLinkMapper, never()).markOpened(any(), any());
    }

    @Test
    void getPublic_revoked_returnsStatusWithoutOpen()
    {
        when(publicTenantSiteResolver.resolveActiveSite("tenant3")).thenReturn(activeSite());
        CrmReminderLink link = activeLink("x7k2m9pqab");
        link.setStatus("revoked");
        when(reminderLinkMapper.selectByTenantAndToken("tenant3", "x7k2m9pqab")).thenReturn(link);

        ReminderLinkPublicVo vo = service.getPublicByToken("x7k2m9pqab", "tenant3");

        assertEquals("revoked", vo.getStatus());
        verify(reminderLinkMapper, never()).markOpened(any(), any());
    }

    @Test
    void getPublic_invalidTokenFormat_404()
    {
        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.getPublicByToken("not-a-token", "tenant3"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getCode());
        verify(publicTenantSiteResolver, never()).resolveActiveSite(any());
    }

    @Test
    void buildPublicUrl_joinsOriginAndPath()
    {
        assertEquals("https://tenant3.mihoot.com/reminder/abc1234567",
                service.buildPublicUrl("tenant3", "abc1234567"));
    }

    private static ReminderLinkCreateRequest baseCreateRequest()
    {
        ReminderLinkCreateRequest req = new ReminderLinkCreateRequest();
        req.setType("appointment_cancel");
        req.setWebsiteSlug("tenant3");
        return req;
    }

    private static PublicTenantSiteVo activeSite()
    {
        PublicTenantSiteVo site = new PublicTenantSiteVo();
        site.setTenantName("tenant3");
        site.setWebsiteSlug("tenant3");
        site.setCompanyName("Tenant3 Clinic");
        site.setWebsiteStatus("active");
        return site;
    }

    private static CrmReminderLink activeLink(String token)
    {
        CrmReminderLink link = new CrmReminderLink();
        link.setId(10L);
        link.setTenantId("tenant3");
        link.setToken(token);
        link.setType("appointment_cancel");
        link.setStatus("active");
        link.setExpiresAt(new Date(System.currentTimeMillis() + 86_400_000L));
        link.setPayload("{\"title\":\"你的预约已取消\",\"customerName\":\"张三\"}");
        link.setActions("[\"contact_clinic\"]");
        link.setOpenCount(0);
        return link;
    }
}
