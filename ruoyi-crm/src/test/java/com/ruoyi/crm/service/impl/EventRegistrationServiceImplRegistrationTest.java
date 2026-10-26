package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.domain.CrmEventRegistration;
import com.ruoyi.crm.domain.CrmEventSession;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmOutreachlead;
import com.ruoyi.crm.domain.vo.EventRegistrationDetailVo;
import com.ruoyi.crm.domain.vo.OutreachRegistrationSubmitResultVo;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.mapper.CrmEventMapper;
import com.ruoyi.crm.mapper.CrmEventRegistrationMapper;
import com.ruoyi.crm.mapper.CrmEventSessionMapper;
import com.ruoyi.crm.service.ICrmContactService;
import com.ruoyi.crm.service.ICrmLeadService;
import com.ruoyi.crm.service.support.AudienceStatusResolver;
import com.ruoyi.crm.service.support.EventAudienceKeyResolver;
import com.ruoyi.crm.service.support.OutreachRegistrationContactHandler;
import com.ruoyi.crm.service.support.OutreachRegistrationIdentityResolution;
import com.ruoyi.crm.service.support.OutreachRegistrationIdentityResolver;
import com.ruoyi.crm.service.support.OutreachRegistrationLeadAttributionService;
import com.ruoyi.crm.service.support.OutreachTicketTokenService;
import com.ruoyi.crm.service.support.WebsiteVisitorSessionService;

@ExtendWith(MockitoExtension.class)
class EventRegistrationServiceImplRegistrationTest
{
    private static final String TENANT_ID = "tenant-1";
    private static final String EVENT_ID = "event-1";
    private static final String SESSION_ID = "session-1";
    private static final String CONTACT_ID = "contact-aaa";
    private static final String OPEN_ID = "o-test";
    private static final String APP_ID = "wx-app";

    @Mock
    private CrmEventRegistrationMapper registrationMapper;
    @Mock
    private CrmEventSessionMapper sessionMapper;
    @Mock
    private CrmEventMapper crmEventMapper;
    @Mock
    private EventAudienceKeyResolver audienceKeyResolver;
    @Mock
    private OutreachTicketTokenService ticketTokenService;
    @Mock
    private OutreachRegistrationLeadAttributionService leadAttributionService;
    @Mock
    private OutreachRegistrationIdentityResolver identityResolver;
    @Mock
    private OutreachRegistrationContactHandler contactHandler;
    @Mock
    private ICrmLeadService crmLeadService;
    @Mock
    private ICrmContactService crmContactService;
    @Mock
    private ActivityEventProducer activityEventProducer;
    @Mock
    private WebsiteVisitorSessionService visitorSessionService;

    @InjectMocks
    private EventRegistrationServiceImpl service;

    private CrmEvent event;
    private CrmEventSession session;

    @BeforeEach
    void setUp()
    {
        event = new CrmEvent();
        event.setEventId(EVENT_ID);
        event.setSubject("春季沙龙");
        event.setRegistrationEnabled(true);
        event.setDefaultSessionId(SESSION_ID);

        session = new CrmEventSession();
        session.setSessionId(SESSION_ID);
        session.setEventId(EVENT_ID);
        session.setName("默认场次");
        session.setStatus("active");
    }

    @Test
    void registerFromH5_contactKnown_skipsLeadAndBindsContact()
    {
        when(identityResolver.resolve(TENANT_ID, EVENT_ID, "13922009933", OPEN_ID, APP_ID))
                .thenReturn(OutreachRegistrationIdentityResolution.contactKnown(CONTACT_ID, "activity_follow"));
        when(sessionMapper.selectBySessionId(TENANT_ID, SESSION_ID)).thenReturn(session);
        when(sessionMapper.selectBySessionIdForUpdate(TENANT_ID, SESSION_ID)).thenReturn(session);
        when(audienceKeyResolver.resolve(any())).thenReturn("contact:" + CONTACT_ID);
        when(registrationMapper.selectActiveBySlot(TENANT_ID, EVENT_ID, SESSION_ID, "contact:" + CONTACT_ID))
                .thenReturn(null);
        when(registrationMapper.countConfirmedBySession(TENANT_ID, EVENT_ID, SESSION_ID)).thenReturn(0);
        when(ticketTokenService.sign(eq(TENANT_ID), eq(EVENT_ID), any(), eq(SESSION_ID), eq(session)))
                .thenReturn("ticket-token");

        CrmOutreachlead submission = new CrmOutreachlead();
        submission.setMobilePhone("13922009933");
        submission.setLeadName("张三");

        OutreachRegistrationSubmitResultVo result = service.registerFromH5(
                TENANT_ID, submission, event, SESSION_ID, OPEN_ID, APP_ID);

        verify(leadAttributionService, never()).submitRegistration(any(), any(), any(), any());
        verify(contactHandler).enrichAndBindIdentity(submission, CONTACT_ID, OPEN_ID, APP_ID);
        verify(registrationMapper).insert(any(CrmEventRegistration.class));
        verify(activityEventProducer).produceEventRegistrationSubmitted(
                eq("Contact"), eq(CONTACT_ID), eq("website"),
                eq(EVENT_ID), eq("春季沙龙"), any(), eq(SESSION_ID),
                eq("h5"), eq("confirmed"), any(), eq(APP_ID), eq(OPEN_ID));

        assertEquals("contact_known", result.getResolution());
        assertEquals(CONTACT_ID, result.getContactId());
        assertNull(result.getLeadId());
        assertEquals(AudienceStatusResolver.CONTACT_KNOWN, result.getAudienceStatus());
        assertEquals("contact:" + CONTACT_ID, result.getAudienceKey());
    }

    @Test
    void registerFromH5_persistsFormSnapshot()
    {
        when(identityResolver.resolve(TENANT_ID, EVENT_ID, "13922009933", OPEN_ID, APP_ID))
                .thenReturn(OutreachRegistrationIdentityResolution.contactKnown(CONTACT_ID, "activity_follow"));
        when(sessionMapper.selectBySessionId(TENANT_ID, SESSION_ID)).thenReturn(session);
        when(sessionMapper.selectBySessionIdForUpdate(TENANT_ID, SESSION_ID)).thenReturn(session);
        when(audienceKeyResolver.resolve(any())).thenReturn("contact:" + CONTACT_ID);
        when(registrationMapper.selectActiveBySlot(TENANT_ID, EVENT_ID, SESSION_ID, "contact:" + CONTACT_ID))
                .thenReturn(null);
        when(registrationMapper.countConfirmedBySession(TENANT_ID, EVENT_ID, SESSION_ID)).thenReturn(0);
        when(ticketTokenService.sign(eq(TENANT_ID), eq(EVENT_ID), any(), eq(SESSION_ID), eq(session)))
                .thenReturn("ticket-token");

        CrmOutreachlead submission = new CrmOutreachlead();
        submission.setMobilePhone("13922009933");
        submission.setLeadName("陈大发");
        submission.setCustomField("[{\"fieldName\":\"interest\",\"value\":\"种植\"}]");

        service.registerFromH5(TENANT_ID, submission, event, SESSION_ID, OPEN_ID, APP_ID);

        ArgumentCaptor<CrmEventRegistration> captor = ArgumentCaptor.forClass(CrmEventRegistration.class);
        verify(registrationMapper).insert(captor.capture());
        CrmEventRegistration saved = captor.getValue();
        assertNotNull(saved.getFormData());
        assertTrue(saved.getFormData().contains("陈大发"));
        assertEquals("[{\"fieldName\":\"interest\",\"value\":\"种植\"}]", saved.getCustomField());
        assertEquals(CONTACT_ID, saved.getContactId());
        assertNull(saved.getLeadId());
    }

    @Test
    void getRegistrationDetail_returnsPersistedFormData()
    {
        CrmEvent loadedEvent = new CrmEvent();
        loadedEvent.setEventId(EVENT_ID);
        when(crmEventMapper.selectCrmEventByEventId(EVENT_ID)).thenReturn(loadedEvent);

        EventRegistrationDetailVo row = new EventRegistrationDetailVo();
        row.setRegistrationId("reg-1");
        row.setEventId(EVENT_ID);
        row.setFormDataJson("{\"leadName\":\"陈大发\",\"mobilePhone\":\"13922009933\"}");
        row.setCustomField("[{\"fieldName\":\"interest\",\"value\":\"种植\"}]");
        when(registrationMapper.selectRegistrationDetail(TENANT_ID, EVENT_ID, "reg-1")).thenReturn(row);

        EventRegistrationDetailVo detail = service.getRegistrationDetail(TENANT_ID, EVENT_ID, "reg-1");

        assertEquals("陈大发", detail.getFormData().get("leadName"));
        assertEquals("[{\"fieldName\":\"interest\",\"value\":\"种植\"}]", detail.getCustomField());
    }

    @Test
    void getRegistrationDetail_fallsBackToLeadWhenNoSnapshot()
    {
        CrmEvent loadedEvent = new CrmEvent();
        loadedEvent.setEventId(EVENT_ID);
        when(crmEventMapper.selectCrmEventByEventId(EVENT_ID)).thenReturn(loadedEvent);

        EventRegistrationDetailVo row = new EventRegistrationDetailVo();
        row.setRegistrationId("reg-1");
        row.setEventId(EVENT_ID);
        row.setLeadId("lead-1");
        when(registrationMapper.selectRegistrationDetail(TENANT_ID, EVENT_ID, "reg-1")).thenReturn(row);

        CrmLead lead = new CrmLead();
        lead.setLeadName("旧数据");
        lead.setMobilePhone("13900001111");
        when(crmLeadService.selectCrmLeadByLeadId("lead-1")).thenReturn(lead);

        EventRegistrationDetailVo detail = service.getRegistrationDetail(TENANT_ID, EVENT_ID, "reg-1");

        assertEquals("旧数据", detail.getFormData().get("leadName"));
    }

    @Test
    void getRegistrationDetail_notFound()
    {
        CrmEvent loadedEvent = new CrmEvent();
        loadedEvent.setEventId(EVENT_ID);
        when(crmEventMapper.selectCrmEventByEventId(EVENT_ID)).thenReturn(loadedEvent);
        when(registrationMapper.selectRegistrationDetail(TENANT_ID, EVENT_ID, "missing")).thenReturn(null);

        assertThrows(ServiceException.class,
                () -> service.getRegistrationDetail(TENANT_ID, EVENT_ID, "missing"));
    }

    @Test
    void registerFromH5_leadBranch_createsLeadAndRegistration()
    {
        when(identityResolver.resolve(TENANT_ID, EVENT_ID, "13800138000", OPEN_ID, APP_ID))
                .thenReturn(OutreachRegistrationIdentityResolution.createNewLead());
        when(leadAttributionService.submitRegistration(any(), eq(event), eq(OPEN_ID), eq(APP_ID)))
                .thenAnswer(invocation -> {
                    CrmOutreachlead sub = invocation.getArgument(0);
                    sub.setLeadId("lead-new");
                    return 1;
                });
        when(crmLeadService.selectCrmLeadByLeadId("lead-new")).thenReturn(new CrmLead());
        when(sessionMapper.selectBySessionId(TENANT_ID, SESSION_ID)).thenReturn(session);
        when(sessionMapper.selectBySessionIdForUpdate(TENANT_ID, SESSION_ID)).thenReturn(session);
        when(audienceKeyResolver.resolve(any())).thenReturn("mobile:13800138000");
        when(registrationMapper.selectActiveBySlot(TENANT_ID, EVENT_ID, SESSION_ID, "mobile:13800138000"))
                .thenReturn(null);
        when(registrationMapper.countConfirmedBySession(TENANT_ID, EVENT_ID, SESSION_ID)).thenReturn(0);
        when(ticketTokenService.sign(eq(TENANT_ID), eq(EVENT_ID), any(), eq(SESSION_ID), eq(session)))
                .thenReturn("ticket-token");

        CrmOutreachlead submission = new CrmOutreachlead();
        submission.setMobilePhone("13800138000");

        OutreachRegistrationSubmitResultVo result = service.registerFromH5(
                TENANT_ID, submission, event, SESSION_ID, OPEN_ID, APP_ID);

        assertEquals("lead_created", result.getResolution());
        assertEquals("lead-new", result.getLeadId());
        assertEquals(AudienceStatusResolver.REGISTRATION_PENDING, result.getAudienceStatus());
        verify(leadAttributionService).submitRegistration(any(), eq(event), eq(OPEN_ID), eq(APP_ID));
        verify(activityEventProducer).produceEventRegistrationSubmitted(
                eq("Lead"), eq("lead-new"), eq("website"),
                eq(EVENT_ID), eq("春季沙龙"), any(), eq(SESSION_ID),
                eq("h5"), eq("confirmed"), any(), eq(APP_ID), eq(OPEN_ID));
    }

    @Test
    void registerFromH5_duplicateSlot_returnsExistingRegistration()
    {
        when(identityResolver.resolve(TENANT_ID, EVENT_ID, "13922009933", OPEN_ID, APP_ID))
                .thenReturn(OutreachRegistrationIdentityResolution.contactKnown(CONTACT_ID, "activity_follow"));
        when(sessionMapper.selectBySessionId(TENANT_ID, SESSION_ID)).thenReturn(session);
        when(sessionMapper.selectBySessionIdForUpdate(TENANT_ID, SESSION_ID)).thenReturn(session);
        when(audienceKeyResolver.resolve(any())).thenReturn("contact:" + CONTACT_ID);

        CrmEventRegistration existing = new CrmEventRegistration();
        existing.setRegistrationId("reg-existing");
        existing.setAudienceKey("contact:" + CONTACT_ID);
        existing.setRegistrationStatus("confirmed");
        existing.setDisplayName("张三");
        existing.setMobilePhone("13922009933");
        existing.setRegisteredAt(new Date());
        when(registrationMapper.selectActiveBySlot(TENANT_ID, EVENT_ID, SESSION_ID, "contact:" + CONTACT_ID))
                .thenReturn(existing);

        CrmOutreachlead submission = new CrmOutreachlead();
        submission.setMobilePhone("13922009933");
        submission.setLeadName("张三");

        OutreachRegistrationSubmitResultVo result = service.registerFromH5(
                TENANT_ID, submission, event, SESSION_ID, OPEN_ID, APP_ID);

        verify(registrationMapper, never()).insert(any());
        verify(activityEventProducer, never()).produceEventRegistrationSubmitted(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
        assertEquals("reg-existing", result.getRegistrationId());
        assertEquals("confirmed", result.getRegistrationStatus());
    }

    @Test
    void registerFromH5_fullWithoutWaitlist_doesNotWriteActivity()
    {
        session.setCapacity(1);
        event.setWaitlistEnabled(false);
        when(identityResolver.resolve(TENANT_ID, EVENT_ID, "13922009933", OPEN_ID, APP_ID))
                .thenReturn(OutreachRegistrationIdentityResolution.contactKnown(CONTACT_ID, "activity_follow"));
        when(sessionMapper.selectBySessionId(TENANT_ID, SESSION_ID)).thenReturn(session);
        when(sessionMapper.selectBySessionIdForUpdate(TENANT_ID, SESSION_ID)).thenReturn(session);
        when(audienceKeyResolver.resolve(any())).thenReturn("contact:" + CONTACT_ID);
        when(registrationMapper.selectActiveBySlot(TENANT_ID, EVENT_ID, SESSION_ID, "contact:" + CONTACT_ID))
                .thenReturn(null);
        when(registrationMapper.countConfirmedBySession(TENANT_ID, EVENT_ID, SESSION_ID)).thenReturn(1);

        CrmOutreachlead submission = new CrmOutreachlead();
        submission.setMobilePhone("13922009933");
        submission.setLeadName("张三");

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.registerFromH5(TENANT_ID, submission, event, SESSION_ID, OPEN_ID, APP_ID));
        assertEquals(Integer.valueOf(409), ex.getCode());
        verify(registrationMapper, never()).insert(any());
        verify(activityEventProducer, never()).produceEventRegistrationSubmitted(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void registerFromWebsite_setsWebsiteSourceAndDefaultLeadSource()
    {
        when(identityResolver.resolve(TENANT_ID, EVENT_ID, "13800138000", null, null))
                .thenReturn(OutreachRegistrationIdentityResolution.createNewLead());
        when(leadAttributionService.submitRegistration(any(), eq(event), eq(null), eq(null)))
                .thenAnswer(invocation -> {
                    CrmOutreachlead sub = invocation.getArgument(0);
                    sub.setLeadId("lead-web");
                    return 1;
                });
        when(crmLeadService.selectCrmLeadByLeadId("lead-web")).thenReturn(new CrmLead());
        when(sessionMapper.selectBySessionId(TENANT_ID, SESSION_ID)).thenReturn(session);
        when(sessionMapper.selectBySessionIdForUpdate(TENANT_ID, SESSION_ID)).thenReturn(session);
        when(audienceKeyResolver.resolve(any())).thenReturn("mobile:13800138000");
        when(registrationMapper.selectActiveBySlot(TENANT_ID, EVENT_ID, SESSION_ID, "mobile:13800138000"))
                .thenReturn(null);
        when(registrationMapper.countConfirmedBySession(TENANT_ID, EVENT_ID, SESSION_ID)).thenReturn(0);
        when(ticketTokenService.sign(eq(TENANT_ID), eq(EVENT_ID), any(), eq(SESSION_ID), eq(session)))
                .thenReturn("ticket-token");

        CrmOutreachlead submission = new CrmOutreachlead();
        submission.setMobilePhone("13800138000");
        submission.setLeadName("官网用户");

        OutreachRegistrationSubmitResultVo result = service.registerFromWebsite(
                TENANT_ID, submission, event, SESSION_ID);

        assertEquals("lead_created", result.getResolution());
        assertEquals("lead-web", result.getLeadId());
        assertEquals("Website Event Registration", submission.getLeadSource());

        ArgumentCaptor<CrmEventRegistration> captor = ArgumentCaptor.forClass(CrmEventRegistration.class);
        verify(registrationMapper).insert(captor.capture());
        assertEquals("website", captor.getValue().getRegistrationSource());
        verify(activityEventProducer).produceEventRegistrationSubmitted(
                eq("Lead"), eq("lead-web"), eq("website"),
                eq(EVENT_ID), eq("春季沙龙"), any(), eq(SESSION_ID),
                eq("website"), eq("confirmed"), any(), eq(null), eq(null));
        verify(visitorSessionService, never()).stitch(any(), any(), any(), any());
    }

    @Test
    void registerFromWebsite_withVisitorSessionId_stitchesContact()
    {
        when(identityResolver.resolve(TENANT_ID, EVENT_ID, "13922009933", null, null))
                .thenReturn(OutreachRegistrationIdentityResolution.contactKnown(CONTACT_ID, "mobile"));
        when(sessionMapper.selectBySessionId(TENANT_ID, SESSION_ID)).thenReturn(session);
        when(sessionMapper.selectBySessionIdForUpdate(TENANT_ID, SESSION_ID)).thenReturn(session);
        when(audienceKeyResolver.resolve(any())).thenReturn("contact:" + CONTACT_ID);
        when(registrationMapper.selectActiveBySlot(TENANT_ID, EVENT_ID, SESSION_ID, "contact:" + CONTACT_ID))
                .thenReturn(null);
        when(registrationMapper.countConfirmedBySession(TENANT_ID, EVENT_ID, SESSION_ID)).thenReturn(0);
        when(ticketTokenService.sign(eq(TENANT_ID), eq(EVENT_ID), any(), eq(SESSION_ID), eq(session)))
                .thenReturn("ticket-token");

        CrmOutreachlead submission = new CrmOutreachlead();
        submission.setMobilePhone("13922009933");
        submission.setLeadName("张三");
        submission.set_sessionId("visitor-sess-cookie");
        submission.set_browseHistory("[{\"u\":\"/events\",\"t\":1}]");

        service.registerFromWebsite(TENANT_ID, submission, event, SESSION_ID);

        verify(visitorSessionService).stitch(eq("visitor-sess-cookie"),
                eq("[{\"u\":\"/events\",\"t\":1}]"), eq("Contact"), eq(CONTACT_ID));
    }

    @Test
    void registerFromWebsite_fullWithWaitlist_returnsWaitlist()
    {
        session.setCapacity(1);
        event.setWaitlistEnabled(true);
        when(identityResolver.resolve(TENANT_ID, EVENT_ID, "13922009933", null, null))
                .thenReturn(OutreachRegistrationIdentityResolution.contactKnown(CONTACT_ID, "activity_follow"));
        when(sessionMapper.selectBySessionId(TENANT_ID, SESSION_ID)).thenReturn(session);
        when(sessionMapper.selectBySessionIdForUpdate(TENANT_ID, SESSION_ID)).thenReturn(session);
        when(audienceKeyResolver.resolve(any())).thenReturn("contact:" + CONTACT_ID);
        when(registrationMapper.selectActiveBySlot(TENANT_ID, EVENT_ID, SESSION_ID, "contact:" + CONTACT_ID))
                .thenReturn(null);
        when(registrationMapper.countConfirmedBySession(TENANT_ID, EVENT_ID, SESSION_ID)).thenReturn(1);

        CrmOutreachlead submission = new CrmOutreachlead();
        submission.setMobilePhone("13922009933");
        submission.setLeadName("候补用户");

        OutreachRegistrationSubmitResultVo result = service.registerFromWebsite(
                TENANT_ID, submission, event, SESSION_ID);

        assertEquals("waitlist", result.getRegistrationStatus());
        ArgumentCaptor<CrmEventRegistration> captor = ArgumentCaptor.forClass(CrmEventRegistration.class);
        verify(registrationMapper).insert(captor.capture());
        assertEquals("website", captor.getValue().getRegistrationSource());
        assertEquals("waitlist", captor.getValue().getRegistrationStatus());
        assertNull(result.getTicketToken());
    }
}
