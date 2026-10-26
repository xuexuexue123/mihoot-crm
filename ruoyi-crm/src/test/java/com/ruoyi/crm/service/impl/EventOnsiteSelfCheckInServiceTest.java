package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.domain.CrmEventRegistration;
import com.ruoyi.crm.domain.CrmEventSession;
import com.ruoyi.crm.domain.dto.EventSelfCheckInRequest;
import com.ruoyi.crm.domain.vo.EventCheckInResultVo;
import com.ruoyi.crm.domain.vo.EventSelfCheckInResultVo;
import com.ruoyi.crm.enums.EventCheckInMethod;
import com.ruoyi.crm.enums.EventRegistrationStatus;
import com.ruoyi.crm.mapper.CrmEventMapper;
import com.ruoyi.crm.mapper.CrmEventRegistrationMapper;
import com.ruoyi.crm.mapper.CrmEventSessionMapper;
import com.ruoyi.crm.service.IEventCheckInService;
import com.ruoyi.crm.service.IEventRegistrationService;
import com.ruoyi.crm.service.support.EventAudienceKeyResolver;
import com.ruoyi.crm.service.support.OutreachCheckInTokenService;
import com.ruoyi.crm.service.support.OutreachCheckInTokenService.VerifiedCheckInPayload;
import com.ruoyi.crm.service.support.OutreachEventLeadResolveService;
import com.ruoyi.crm.wechatmp.oauth.OutreachMpOAuthService;

@ExtendWith(MockitoExtension.class)
class EventOnsiteSelfCheckInServiceTest
{
    private static final String TENANT = "tenant-1";
    private static final String EVENT = "event-1";
    private static final String SESSION = "sess-1";

    @Mock
    private OutreachCheckInTokenService checkInTokenService;
    @Mock
    private CrmEventMapper crmEventMapper;
    @Mock
    private CrmEventSessionMapper sessionMapper;
    @Mock
    private CrmEventRegistrationMapper registrationMapper;
    @Mock
    private IEventCheckInService eventCheckInService;
    @Mock
    private IEventRegistrationService eventRegistrationService;
    @Mock
    private EventAudienceKeyResolver audienceKeyResolver;
    @Mock
    private OutreachEventLeadResolveService leadResolveService;
    @Mock
    private OutreachMpOAuthService mpOAuthService;

    @InjectMocks
    private EventOnsiteSelfCheckInServiceImpl service;

    private VerifiedCheckInPayload payload;
    private CrmEvent event;
    private CrmEventSession session;

    @BeforeEach
    void setUp()
    {
        payload = new VerifiedCheckInPayload(TENANT, EVENT, SESSION, null, null,
                java.time.Instant.now().plusSeconds(3600).getEpochSecond());
        event = new CrmEvent();
        event.setEventId(EVENT);
        event.setCheckInEnabled(true);
        event.setAllowWalkIn(true);
        session = new CrmEventSession();
        session.setSessionId(SESSION);
        session.setEventId(EVENT);
        session.setStatus("active");
        session.setStartTime(new Date(System.currentTimeMillis() - 3600_000));
        session.setEndTime(new Date(System.currentTimeMillis() + 3600_000));
    }

    @Test
    void checkInOnly_confirmedRegistration_success()
    {
        when(crmEventMapper.selectCrmEventByEventId(EVENT)).thenReturn(event);
        when(sessionMapper.selectBySessionId(TENANT, SESSION)).thenReturn(session);

        CrmEventRegistration reg = new CrmEventRegistration();
        reg.setRegistrationId("reg-1");
        reg.setAudienceKey("mobile:13800138000");
        reg.setDisplayName("张三");
        reg.setRegistrationStatus(EventRegistrationStatus.CONFIRMED.getCode());
        when(registrationMapper.selectActiveBySlot(TENANT, EVENT, SESSION, "mobile:13800138000")).thenReturn(reg);

        EventCheckInResultVo checkInVo = new EventCheckInResultVo();
        checkInVo.setCheckInId("ci-1");
        checkInVo.setAudienceKey("mobile:13800138000");
        checkInVo.setAlreadyCheckedIn(false);
        when(eventCheckInService.insertCheckInIfAbsent(eq(TENANT), eq(EVENT), eq(SESSION),
                eq("mobile:13800138000"), eq("reg-1"), eq("张三"),
                eq(EventCheckInMethod.ONSITE_SELF.getCode()), eq("self"))).thenReturn(checkInVo);

        EventSelfCheckInRequest request = new EventSelfCheckInRequest();
        request.setMode("check_in_only");
        request.setMobilePhone("13800138000");

        EventSelfCheckInResultVo result = service.submitSelfCheckIn(payload, request, null);
        assertEquals("reg-1", result.getRegistrationId());
        assertTrue(!result.isAlreadyCheckedIn());
    }

    @Test
    void checkInOnly_waitlist_rejected()
    {
        when(crmEventMapper.selectCrmEventByEventId(EVENT)).thenReturn(event);
        when(sessionMapper.selectBySessionId(TENANT, SESSION)).thenReturn(session);

        CrmEventRegistration reg = new CrmEventRegistration();
        reg.setRegistrationStatus(EventRegistrationStatus.WAITLIST.getCode());
        reg.setAudienceKey("mobile:13800138000");
        when(registrationMapper.selectActiveBySlot(TENANT, EVENT, SESSION, "mobile:13800138000")).thenReturn(reg);

        EventSelfCheckInRequest request = new EventSelfCheckInRequest();
        request.setMode("check_in_only");
        request.setMobilePhone("13800138000");

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.submitSelfCheckIn(payload, request, null));
        assertEquals(HttpStatus.CONFLICT, ex.getCode());
        assertEquals("WAITLIST_CANNOT_CHECK_IN", ex.getMessage());
    }

    @Test
    void checkInOnly_notRegistered_returns404()
    {
        when(crmEventMapper.selectCrmEventByEventId(EVENT)).thenReturn(event);
        when(sessionMapper.selectBySessionId(TENANT, SESSION)).thenReturn(session);
        when(registrationMapper.selectActiveBySlot(any(), any(), any(), any())).thenReturn(null);

        EventSelfCheckInRequest request = new EventSelfCheckInRequest();
        request.setMode("check_in_only");
        request.setMobilePhone("13800138000");

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.submitSelfCheckIn(payload, request, null));
        assertEquals(HttpStatus.NOT_FOUND, ex.getCode());
    }

    @Test
    void checkInDisabled_returns410()
    {
        event.setCheckInEnabled(false);
        when(crmEventMapper.selectCrmEventByEventId(EVENT)).thenReturn(event);
        when(sessionMapper.selectBySessionId(TENANT, SESSION)).thenReturn(session);

        EventSelfCheckInRequest request = new EventSelfCheckInRequest();
        request.setMode("check_in_only");
        request.setMobilePhone("13800138000");

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.submitSelfCheckIn(payload, request, null));
        assertEquals(HttpStatus.GONE, ex.getCode());
        assertEquals("CHECK_IN_DISABLED", ex.getMessage());
    }

    @Test
    void walkInDisabled_returns403()
    {
        event.setAllowWalkIn(false);
        when(crmEventMapper.selectCrmEventByEventId(EVENT)).thenReturn(event);
        when(sessionMapper.selectBySessionId(TENANT, SESSION)).thenReturn(session);

        EventSelfCheckInRequest request = new EventSelfCheckInRequest();
        request.setMode("register_and_check_in");
        request.setMobilePhone("13800138000");
        request.setDisplayName("李四");

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.submitSelfCheckIn(payload, request, null));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode());
        assertEquals("WALK_IN_DISABLED", ex.getMessage());
    }

    @Test
    void duplicateCheckIn_returnsAlreadyCheckedIn()
    {
        when(crmEventMapper.selectCrmEventByEventId(EVENT)).thenReturn(event);
        when(sessionMapper.selectBySessionId(TENANT, SESSION)).thenReturn(session);

        CrmEventRegistration reg = new CrmEventRegistration();
        reg.setRegistrationId("reg-1");
        reg.setAudienceKey("mobile:13800138000");
        reg.setDisplayName("张三");
        reg.setRegistrationStatus(EventRegistrationStatus.CONFIRMED.getCode());
        when(registrationMapper.selectActiveBySlot(TENANT, EVENT, SESSION, "mobile:13800138000")).thenReturn(reg);

        EventCheckInResultVo checkInVo = new EventCheckInResultVo();
        checkInVo.setAlreadyCheckedIn(true);
        when(eventCheckInService.insertCheckInIfAbsent(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(checkInVo);

        EventSelfCheckInRequest request = new EventSelfCheckInRequest();
        request.setMode("check_in_only");
        request.setMobilePhone("13800138000");

        EventSelfCheckInResultVo result = service.submitSelfCheckIn(payload, request, null);
        assertTrue(result.isAlreadyCheckedIn());
    }
}
