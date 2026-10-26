package com.ruoyi.crm.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.domain.CrmEventRegistration;
import com.ruoyi.crm.domain.CrmEventSession;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmOutreachlead;
import com.ruoyi.crm.domain.dto.EventSelfCheckInRequest;
import com.ruoyi.crm.domain.vo.EventCheckInResultVo;
import com.ruoyi.crm.domain.vo.EventSelfCheckInResultVo;
import com.ruoyi.crm.domain.vo.OutreachEventCheckInVo;
import com.ruoyi.crm.enums.EventCheckInMethod;
import com.ruoyi.crm.enums.EventRegistrationStatus;
import com.ruoyi.crm.mapper.CrmEventMapper;
import com.ruoyi.crm.mapper.CrmEventRegistrationMapper;
import com.ruoyi.crm.mapper.CrmEventSessionMapper;
import com.ruoyi.crm.service.IEventCheckInService;
import com.ruoyi.crm.service.IEventOnsiteSelfCheckInService;
import com.ruoyi.crm.service.IEventRegistrationService;
import com.ruoyi.crm.service.support.EventAudienceKeyResolver;
import com.ruoyi.crm.service.support.EventCheckInWindowSupport;
import com.ruoyi.crm.service.support.EventRegistrationFormSupport;
import com.ruoyi.crm.service.support.LeadDefaults;
import com.ruoyi.crm.service.support.OutreachCheckInTokenService;
import com.ruoyi.crm.service.support.OutreachCheckInTokenService.VerifiedCheckInPayload;
import com.ruoyi.crm.service.support.OutreachEventLeadResolveService;
import com.ruoyi.crm.service.support.PhoneNormalizer;
import com.ruoyi.crm.wechatmp.oauth.OutreachMpOAuthService;
import com.ruoyi.crm.wechatmp.oauth.OutreachMpSessionPayload;
import com.ruoyi.crm.wechatmp.oauth.OutreachMpSessionSupport;

@Service
public class EventOnsiteSelfCheckInServiceImpl implements IEventOnsiteSelfCheckInService
{
    private static final String MODE_CHECK_IN_ONLY = "check_in_only";
    private static final String MODE_REGISTER_AND_CHECK_IN = "register_and_check_in";

    @Autowired
    private OutreachCheckInTokenService checkInTokenService;

    @Autowired
    private CrmEventMapper crmEventMapper;

    @Autowired
    private CrmEventSessionMapper sessionMapper;

    @Autowired
    private CrmEventRegistrationMapper registrationMapper;

    @Autowired
    private IEventCheckInService eventCheckInService;

    @Autowired
    private IEventRegistrationService eventRegistrationService;

    @Autowired
    private EventAudienceKeyResolver audienceKeyResolver;

    @Autowired
    private OutreachEventLeadResolveService leadResolveService;

    @Autowired
    private OutreachMpOAuthService mpOAuthService;

    @Override
    public VerifiedCheckInPayload verifyAndResolveSession(String checkInToken, String tenantIdForDefaultSession)
    {
        VerifiedCheckInPayload payload = checkInTokenService.verify(checkInToken);
        if (StringUtils.isBlank(payload.getSessionId()))
        {
            CrmEvent event = requireEvent(payload.getEventId());
            String defaultSessionId = event.getDefaultSessionId();
            if (StringUtils.isBlank(defaultSessionId))
            {
                throw new ServiceException("SESSION_REQUIRED", HttpStatus.BAD_REQUEST);
            }
            payload = new VerifiedCheckInPayload(
                    payload.getTenantId(), payload.getEventId(), defaultSessionId,
                    payload.getOpenId(), payload.getAppId(), payload.getExp());
        }
        return payload;
    }

    @Override
    public OutreachEventCheckInVo getCheckInPageInfo(String checkInToken, VerifiedCheckInPayload payload,
            String userAgent, OutreachMpSessionPayload mpSession)
    {
        CrmEvent event = requireEvent(payload.getEventId());
        CrmEventSession session = requireActiveSession(payload.getTenantId(), payload.getSessionId(), payload.getEventId());
        validateCheckInOpen(event, session);

        OutreachEventCheckInVo vo = new OutreachEventCheckInVo();
        vo.setEventId(event.getEventId());
        vo.setEventSubject(event.getSubject());
        vo.setSessionId(session.getSessionId());
        vo.setSessionName(session.getName());
        vo.setStartTime(session.getStartTime());
        vo.setEndTime(session.getEndTime());
        vo.setLocation(session.getLocation());
        vo.setCheckInEnabled(event.getCheckInEnabled());
        vo.setAllowWalkIn(event.getAllowWalkIn());
        vo.setRegistrationEnabled(event.getRegistrationEnabled());
        vo.setCheckInOpenAt(EventCheckInWindowSupport.resolveOpenAt(session));
        vo.setCheckInCloseAt(EventCheckInWindowSupport.resolveCloseAt(session));
        vo.setRegistrationFormJson(resolveRegistrationFormJson(event));

        OutreachEventCheckInVo.IdentityState identityState = new OutreachEventCheckInVo.IdentityState();
        boolean hasSession = mpSession != null && mpSession.isValidFor(payload.getTenantId(), payload.getEventId());
        identityState.setHasOpenId(hasSession);
        if (hasSession)
        {
            identityState.setMaskedOpenId(OutreachMpSessionSupport.maskOpenId(mpSession.getOpenId()));
        }

        boolean weChatUa = mpOAuthService.isWeChatUserAgent(userAgent);
        boolean mpConfigured = mpOAuthService.resolveConnectedConfig() != null;
        boolean oauthRequired = weChatUa && mpConfigured && !hasSession;
        vo.setOauthRequired(oauthRequired);
        if (oauthRequired)
        {
            String redirectUri = checkInTokenService.buildCheckInUrl(
                    payload.getTenantId(), payload.getEventId(), payload.getSessionId(), payload.getExp());
            vo.setOauthAuthorizeUrl(mpOAuthService.buildOAuthAuthorizeUrl(checkInToken, redirectUri));
        }
        vo.setIdentityState(identityState);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EventSelfCheckInResultVo submitSelfCheckIn(VerifiedCheckInPayload payload, EventSelfCheckInRequest request,
            OutreachMpSessionPayload mpSession)
    {
        if (request == null || StringUtils.isBlank(request.getMode()))
        {
            throw new ServiceException("mode required", HttpStatus.BAD_REQUEST);
        }
        CrmEvent event = requireEvent(payload.getEventId());
        CrmEventSession session = requireActiveSession(payload.getTenantId(), payload.getSessionId(), payload.getEventId());
        validateCheckInOpen(event, session);

        String mode = request.getMode();
        if (MODE_CHECK_IN_ONLY.equals(mode))
        {
            return checkInOnly(payload, request, mpSession, event, session);
        }
        if (MODE_REGISTER_AND_CHECK_IN.equals(mode))
        {
            return registerAndCheckIn(payload, request, mpSession, event, session);
        }
        throw new ServiceException("Invalid mode", HttpStatus.BAD_REQUEST);
    }

    private EventSelfCheckInResultVo checkInOnly(VerifiedCheckInPayload payload, EventSelfCheckInRequest request,
            OutreachMpSessionPayload mpSession, CrmEvent event, CrmEventSession session)
    {
        ResolvedIdentity identity = resolveIdentity(payload, request, mpSession, event.getEventId());
        if (StringUtils.isBlank(identity.audienceKey))
        {
            throw new ServiceException("IDENTITY_REQUIRED", HttpStatus.BAD_REQUEST);
        }
        CrmEventRegistration registration = registrationMapper.selectActiveBySlot(
                payload.getTenantId(), payload.getEventId(), payload.getSessionId(), identity.audienceKey);
        if (registration == null)
        {
            throw new ServiceException("NOT_REGISTERED", HttpStatus.NOT_FOUND);
        }
        if (EventRegistrationStatus.WAITLIST.getCode().equals(registration.getRegistrationStatus()))
        {
            throw new ServiceException("WAITLIST_CANNOT_CHECK_IN", HttpStatus.CONFLICT);
        }
        if (!EventRegistrationStatus.CONFIRMED.getCode().equals(registration.getRegistrationStatus()))
        {
            throw new ServiceException("NOT_REGISTERED", HttpStatus.NOT_FOUND);
        }
        return toSelfCheckInResult(
                insertOnsiteSelfCheckIn(payload, registration, identity.displayName), registration);
    }

    private EventSelfCheckInResultVo registerAndCheckIn(VerifiedCheckInPayload payload, EventSelfCheckInRequest request,
            OutreachMpSessionPayload mpSession, CrmEvent event, CrmEventSession session)
    {
        if (!Boolean.TRUE.equals(event.getAllowWalkIn()))
        {
            throw new ServiceException("WALK_IN_DISABLED", HttpStatus.FORBIDDEN);
        }
        if (StringUtils.isBlank(request.getMobilePhone()))
        {
            throw new ServiceException("IDENTITY_REQUIRED", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getDisplayName()))
        {
            throw new ServiceException("displayName required", HttpStatus.BAD_REQUEST);
        }

        ResolvedIdentity identity = resolveIdentity(payload, request, mpSession, event.getEventId());
        if (StringUtils.isNotBlank(identity.audienceKey))
        {
            CrmEventRegistration existing = registrationMapper.selectActiveBySlot(
                    payload.getTenantId(), payload.getEventId(), payload.getSessionId(), identity.audienceKey);
            if (existing != null)
            {
                if (EventRegistrationStatus.CONFIRMED.getCode().equals(existing.getRegistrationStatus()))
                {
                    return toSelfCheckInResult(
                            insertOnsiteSelfCheckIn(payload, existing, existing.getDisplayName()), existing);
                }
                if (EventRegistrationStatus.WAITLIST.getCode().equals(existing.getRegistrationStatus()))
                {
                    throw new ServiceException("WAITLIST_CANNOT_CHECK_IN", HttpStatus.CONFLICT);
                }
                throw new ServiceException("REGISTRATION_DUPLICATE", HttpStatus.CONFLICT);
            }
        }

        String tokenOpenId = mpSession != null && mpSession.isValidFor(payload.getTenantId(), payload.getEventId())
                ? mpSession.getOpenId() : payload.getOpenId();
        String tokenAppId = mpSession != null && mpSession.isValidFor(payload.getTenantId(), payload.getEventId())
                ? mpSession.getAppId() : payload.getAppId();

        CrmOutreachlead submission = new CrmOutreachlead();
        submission.setSid(event.getEventId());
        submission.setMobilePhone(request.getMobilePhone());
        submission.setFullName(request.getDisplayName());
        submission.setLeadName(request.getDisplayName());
        submission.setLeadSource("Marketing Event");
        submission.setStage(LeadDefaults.DEFAULT_STAGE);

        CrmEventRegistration registration = eventRegistrationService.registerWalkInForSelfCheckIn(
                payload.getTenantId(), event, payload.getSessionId(), submission, tokenOpenId, tokenAppId);
        return toSelfCheckInResult(
                insertOnsiteSelfCheckIn(payload, registration, registration.getDisplayName()), registration);
    }

    private EventCheckInResultVo insertOnsiteSelfCheckIn(VerifiedCheckInPayload payload,
            CrmEventRegistration registration, String displayName)
    {
        return eventCheckInService.insertCheckInIfAbsent(
                payload.getTenantId(),
                payload.getEventId(),
                payload.getSessionId(),
                registration.getAudienceKey(),
                registration.getRegistrationId(),
                displayName != null ? displayName : registration.getDisplayName(),
                EventCheckInMethod.ONSITE_SELF.getCode(),
                "self");
    }

    private ResolvedIdentity resolveIdentity(VerifiedCheckInPayload payload, EventSelfCheckInRequest request,
            OutreachMpSessionPayload mpSession, String eventId)
    {
        String openId = null;
        String appId = null;
        if (mpSession != null && mpSession.isValidFor(payload.getTenantId(), payload.getEventId()))
        {
            openId = mpSession.getOpenId();
            appId = mpSession.getAppId();
        }
        else if (StringUtils.isNotBlank(payload.getOpenId()) && StringUtils.isNotBlank(payload.getAppId()))
        {
            openId = payload.getOpenId();
            appId = payload.getAppId();
        }

        String mobilePhone = request != null ? request.getMobilePhone() : null;
        String displayName = request != null ? request.getDisplayName() : null;

        if (StringUtils.isNotBlank(openId))
        {
            CrmLead lead = leadResolveService.resolveExistingLead(eventId, mobilePhone, openId, appId);
            if (lead != null)
            {
                String key = audienceKeyResolver.resolveFromMergedGroup(
                        lead.getContactId(), lead.getMobilePhone(), null, lead.getLeadId(),
                        null, lead.getWechatMpOpenid());
                if (StringUtils.isNotBlank(key))
                {
                    return new ResolvedIdentity(key, displayName != null ? displayName : lead.getFullName());
                }
            }
            return new ResolvedIdentity("mp:" + openId, displayName);
        }

        if (StringUtils.isNotBlank(mobilePhone))
        {
            String normalized = PhoneNormalizer.normalize(mobilePhone);
            CrmLead lead = leadResolveService.resolveExistingLead(eventId, mobilePhone, null, null);
            if (lead != null)
            {
                String key = audienceKeyResolver.resolveFromMergedGroup(
                        lead.getContactId(), lead.getMobilePhone(), null, lead.getLeadId(),
                        null, lead.getWechatMpOpenid());
                if (StringUtils.isNotBlank(key))
                {
                    return new ResolvedIdentity(key, displayName != null ? displayName : lead.getFullName());
                }
            }
            if (StringUtils.isNotBlank(normalized))
            {
                return new ResolvedIdentity("mobile:" + normalized, displayName);
            }
        }
        return new ResolvedIdentity(null, displayName);
    }

    private EventSelfCheckInResultVo toSelfCheckInResult(EventCheckInResultVo checkInResult)
    {
        EventSelfCheckInResultVo vo = new EventSelfCheckInResultVo();
        vo.setCheckInId(checkInResult.getCheckInId());
        vo.setAudienceKey(checkInResult.getAudienceKey());
        vo.setDisplayName(checkInResult.getDisplayName());
        vo.setCheckedInAt(checkInResult.getCheckedInAt());
        vo.setAlreadyCheckedIn(checkInResult.isAlreadyCheckedIn());
        return vo;
    }

    private EventSelfCheckInResultVo toSelfCheckInResult(EventCheckInResultVo checkInResult,
            CrmEventRegistration registration)
    {
        EventSelfCheckInResultVo vo = toSelfCheckInResult(checkInResult);
        if (registration != null)
        {
            vo.setRegistrationId(registration.getRegistrationId());
            vo.setRegistrationStatus(registration.getRegistrationStatus());
        }
        return vo;
    }

    private void validateCheckInOpen(CrmEvent event, CrmEventSession session)
    {
        if (!Boolean.TRUE.equals(event.getCheckInEnabled()))
        {
            throw new ServiceException("CHECK_IN_DISABLED", HttpStatus.GONE);
        }
        if (!EventCheckInWindowSupport.isWithinWindow(session, new Date()))
        {
            throw new ServiceException("CHECK_IN_WINDOW_CLOSED", HttpStatus.GONE);
        }
    }

    private CrmEvent requireEvent(String eventId)
    {
        CrmEvent event = crmEventMapper.selectCrmEventByEventId(eventId);
        if (event == null)
        {
            throw new ServiceException("Event not found", HttpStatus.NOT_FOUND);
        }
        return event;
    }

    private CrmEventSession requireActiveSession(String tenantId, String sessionId, String eventId)
    {
        CrmEventSession session = sessionMapper.selectBySessionId(tenantId, sessionId);
        if (session == null || !eventId.equals(session.getEventId()) || "deleted".equals(session.getStatus()))
        {
            throw new ServiceException("场次不存在", HttpStatus.NOT_FOUND);
        }
        return session;
    }

    private String resolveRegistrationFormJson(CrmEvent event)
    {
        if (StringUtils.isNotBlank(event.getRegistrationFormJson()))
        {
            if (!EventRegistrationFormSupport.isValidSurveyJson(event.getRegistrationFormJson()))
            {
                throw new ServiceException("registration form snapshot is corrupted", HttpStatus.ERROR);
            }
            return event.getRegistrationFormJson();
        }
        return EventRegistrationFormSupport.DEFAULT_REGISTRATION_FORM_JSON;
    }

    private static final class ResolvedIdentity
    {
        private final String audienceKey;
        private final String displayName;

        private ResolvedIdentity(String audienceKey, String displayName)
        {
            this.audienceKey = audienceKey;
            this.displayName = displayName;
        }
    }
}
