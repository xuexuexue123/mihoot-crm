package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
import com.ruoyi.crm.domain.CrmEventRegistration;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmMarketEvent;
import com.ruoyi.crm.domain.dto.MiniappMarketEventRegisterRequest;
import com.ruoyi.crm.domain.vo.MiniappMarketEventRegisterResultVo;
import com.ruoyi.crm.domain.vo.SubjectRegistrationRowVo;
import com.ruoyi.crm.enums.EventRegistrationSource;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.mapper.CrmEventRegistrationMapper;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.ICrmLeadService;
import com.ruoyi.crm.service.ICrmMarketEventService;
import com.ruoyi.crm.service.ICrmMiniappMarketEventRegisterService;

@ExtendWith(MockitoExtension.class)
class CrmMiniappMarketEventRegisterServiceImplTest
{
    private static final String TENANT = "tenant3";
    private static final String EVENT_ID = "evt-001";
    private static final String MOBILE = "13900139000";

    @Mock
    private ICrmMarketEventService marketEventService;
    @Mock
    private CrmLeadMapper crmLeadMapper;
    @Mock
    private ICrmLeadService crmLeadService;
    @Mock
    private ICrmCustomerIdentityService identityService;
    @Mock
    private CrmEventRegistrationMapper registrationMapper;
    @Mock
    private ActivityEventProducer activityEventProducer;

    @InjectMocks
    private CrmMiniappMarketEventRegisterServiceImpl service;

    private CrmMarketEvent marketEvent;

    @BeforeEach
    void setUp()
    {
        marketEvent = new CrmMarketEvent();
        marketEvent.setId(EVENT_ID);
        marketEvent.setTitleCn("2026 越南国际建材展览会");
        marketEvent.setDelFlag("0");
    }

    @Test
    void register_newMobile_createsLeadRegistrationAndActivity()
    {
        when(marketEventService.selectCrmMarketEventById(EVENT_ID)).thenReturn(marketEvent);
        when(identityService.findContactByIdentity("mobile_phone", MOBILE)).thenReturn(null);
        when(crmLeadMapper.selectActiveUnconvertedByMobilePhone(MOBILE)).thenReturn(null);
        when(crmLeadService.insertCrmLead(any(CrmLead.class))).thenAnswer(inv -> {
            CrmLead lead = inv.getArgument(0);
            assertNotNull(lead.getLeadId());
            return 1;
        });
        when(registrationMapper.selectActiveBySlot(TENANT, EVENT_ID,
                ICrmMiniappMarketEventRegisterService.SESSION_ID_MARKET_EVENT, "mobile:" + MOBILE))
                .thenReturn(null);

        MiniappMarketEventRegisterResultVo result = service.register(TENANT, buildRequest());

        assertEquals(ICrmMiniappMarketEventRegisterService.RESOLUTION_LEAD_CREATED, result.getResolution());
        assertNotNull(result.getLeadId());
        assertNull(result.getContactId());
        assertNotNull(result.getRegistrationId());
        assertEquals(EVENT_ID, result.getEventId());
        assertEquals(EventRegistrationSource.MARKET_EVENT.getCode(), result.getRegistrationSource());
        assertEquals("mobile:" + MOBILE, result.getAudienceKey());

        ArgumentCaptor<CrmEventRegistration> regCaptor = ArgumentCaptor.forClass(CrmEventRegistration.class);
        verify(registrationMapper).insert(regCaptor.capture());
        CrmEventRegistration reg = regCaptor.getValue();
        assertEquals(EventRegistrationSource.MARKET_EVENT.getCode(), reg.getRegistrationSource());
        assertEquals(ICrmMiniappMarketEventRegisterService.SESSION_ID_MARKET_EVENT, reg.getSessionId());
        assertTrue(reg.getFormData().contains("company_name"));

        verify(activityEventProducer).produceEventRegistrationSubmitted(
                eq("Lead"), any(), eq(ICrmMiniappMarketEventRegisterService.CHANNEL_WECHAT_MINIAPP),
                eq(EVENT_ID), eq("2026 越南国际建材展览会"), any(),
                eq(ICrmMiniappMarketEventRegisterService.SESSION_ID_MARKET_EVENT),
                eq(EventRegistrationSource.MARKET_EVENT.getCode()), eq("confirmed"), any(),
                isNull(), isNull());
    }

    @Test
    void register_sameMobileSameEvent_updatesFormDataWithoutNewActivity()
    {
        when(marketEventService.selectCrmMarketEventById(EVENT_ID)).thenReturn(marketEvent);
        when(identityService.findContactByIdentity("mobile_phone", MOBILE)).thenReturn(null);

        CrmLead existingLead = new CrmLead();
        existingLead.setLeadId("lead-1");
        existingLead.setMobilePhone(MOBILE);
        existingLead.setLeadName("李娜");
        when(crmLeadMapper.selectActiveUnconvertedByMobilePhone(MOBILE)).thenReturn(existingLead);
        when(crmLeadService.updateCrmLead(any(CrmLead.class))).thenReturn(1);

        CrmEventRegistration existingReg = new CrmEventRegistration();
        existingReg.setRegistrationId("reg-1");
        existingReg.setEventId(EVENT_ID);
        existingReg.setAudienceKey("mobile:" + MOBILE);
        existingReg.setRegistrationStatus("confirmed");
        when(registrationMapper.selectActiveBySlot(TENANT, EVENT_ID,
                ICrmMiniappMarketEventRegisterService.SESSION_ID_MARKET_EVENT, "mobile:" + MOBILE))
                .thenReturn(existingReg);

        MiniappMarketEventRegisterRequest req = buildRequest();
        req.getSlots().put("company_name", "改名公司");

        MiniappMarketEventRegisterResultVo result = service.register(TENANT, req);

        assertEquals(ICrmMiniappMarketEventRegisterService.RESOLUTION_REGISTRATION_UPDATED,
                result.getResolution());
        assertEquals("reg-1", result.getRegistrationId());
        assertEquals("lead-1", result.getLeadId());

        ArgumentCaptor<CrmEventRegistration> patchCaptor = ArgumentCaptor.forClass(CrmEventRegistration.class);
        verify(registrationMapper).update(patchCaptor.capture());
        assertTrue(patchCaptor.getValue().getFormData().contains("改名公司"));
        verify(registrationMapper, never()).insert(any());
        verify(activityEventProducer, never()).produceEventRegistrationSubmitted(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void register_missingMarketEvent_returns404()
    {
        when(marketEventService.selectCrmMarketEventById(EVENT_ID)).thenReturn(null);
        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.register(TENANT, buildRequest()));
        assertEquals(HttpStatus.NOT_FOUND, ex.getCode());
        verify(registrationMapper, never()).insert(any());
    }

    @Test
    void register_contactKnown_skipsLeadAndWritesActivityOnContact()
    {
        when(marketEventService.selectCrmMarketEventById(EVENT_ID)).thenReturn(marketEvent);
        when(identityService.findContactByIdentity("mobile_phone", MOBILE)).thenReturn("contact-1");
        when(registrationMapper.selectActiveBySlot(TENANT, EVENT_ID,
                ICrmMiniappMarketEventRegisterService.SESSION_ID_MARKET_EVENT, "mobile:" + MOBILE))
                .thenReturn(null);

        MiniappMarketEventRegisterResultVo result = service.register(TENANT, buildRequest());

        assertEquals(ICrmMiniappMarketEventRegisterService.RESOLUTION_CONTACT_KNOWN, result.getResolution());
        assertEquals("contact-1", result.getContactId());
        assertNull(result.getLeadId());
        verify(crmLeadService, never()).insertCrmLead(any());
        verify(crmLeadService, never()).updateCrmLead(any());

        ArgumentCaptor<CrmEventRegistration> regCaptor = ArgumentCaptor.forClass(CrmEventRegistration.class);
        verify(registrationMapper).insert(regCaptor.capture());
        assertEquals("contact-1", regCaptor.getValue().getContactId());
        assertNull(regCaptor.getValue().getLeadId());

        verify(activityEventProducer).produceEventRegistrationSubmitted(
                eq("Contact"), eq("contact-1"), eq(ICrmMiniappMarketEventRegisterService.CHANNEL_WECHAT_MINIAPP),
                eq(EVENT_ID), any(), any(), any(), any(), any(), any(), isNull(), isNull());
    }

    @Test
    void listByLeadId_parsesFormDataAndClearsMarketEventSessionName()
    {
        SubjectRegistrationRowVo row = new SubjectRegistrationRowVo();
        row.setRegistrationId("reg-1");
        row.setRegistrationSource(EventRegistrationSource.MARKET_EVENT.getCode());
        row.setSessionId(ICrmMiniappMarketEventRegisterService.SESSION_ID_MARKET_EVENT);
        row.setSessionName("should-clear");
        row.setEventTitle("2026 越南国际建材展览会");
        row.setFormDataJson("{\"contact_name\":\"李娜\",\"email\":\"lina@example.com\"}");
        when(registrationMapper.selectByLeadId(TENANT, "lead-1", null))
                .thenReturn(Collections.singletonList(row));

        List<SubjectRegistrationRowVo> rows = service.listByLeadId(TENANT, "lead-1", null);
        assertEquals(1, rows.size());
        assertEquals("2026 越南国际建材展览会", rows.get(0).getEventTitle());
        assertNotNull(rows.get(0).getFormData());
        assertEquals("李娜", rows.get(0).getFormData().get("contact_name"));
        assertEquals("lina@example.com", rows.get(0).getWorkEmail());
        assertNull(rows.get(0).getSessionName());
    }

    @Test
    void register_missingSlots_badRequest()
    {
        MiniappMarketEventRegisterRequest req = buildRequest();
        req.setSlots(null);
        ServiceException ex = assertThrows(ServiceException.class, () -> service.register(TENANT, req));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getCode());
    }

    private MiniappMarketEventRegisterRequest buildRequest()
    {
        MiniappMarketEventRegisterRequest req = new MiniappMarketEventRegisterRequest();
        req.setMarketEventId(EVENT_ID);
        req.setMobilePhone(MOBILE);
        req.setContactName("李娜");
        req.setEmail("lina@example.com");
        req.setCompanyName("上海某某贸易有限公司");
        req.setBusinessField("跨境电商");
        req.setWebsite("https://www.example.com");
        Map<String, Object> slots = new HashMap<>();
        slots.put("company_name", "上海某某贸易有限公司");
        slots.put("contact_name", "李娜");
        slots.put("phone", MOBILE);
        slots.put("email", "lina@example.com");
        slots.put("event_name", "2026 越南国际建材展览会");
        req.setSlots(slots);
        return req;
    }
}
