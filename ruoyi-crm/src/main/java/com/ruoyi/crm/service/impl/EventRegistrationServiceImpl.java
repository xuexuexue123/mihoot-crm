package com.ruoyi.crm.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.domain.CrmEventRegistration;
import com.ruoyi.crm.domain.CrmEventSession;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmOutreachlead;
import com.ruoyi.crm.domain.dto.AudienceKeyHints;
import com.ruoyi.crm.domain.dto.EventRegistrationCreateRequest;
import com.ruoyi.crm.domain.dto.EventRegistrationQuery;
import com.ruoyi.crm.domain.vo.EventRegistrationDetailVo;
import com.ruoyi.crm.domain.vo.EventRegistrationRowVo;
import com.ruoyi.crm.domain.vo.OutreachRegistrationSubmitResultVo;
import com.ruoyi.crm.enums.EventRegistrationSource;
import com.ruoyi.crm.enums.EventRegistrationStatus;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.mapper.CrmEventMapper;
import com.ruoyi.crm.mapper.CrmEventRegistrationMapper;
import com.ruoyi.crm.mapper.CrmEventSessionMapper;
import com.ruoyi.crm.service.ICrmContactService;
import com.ruoyi.crm.service.ICrmLeadService;
import com.ruoyi.crm.service.IEventRegistrationService;
import com.ruoyi.crm.service.support.AudienceStatusResolver;
import com.ruoyi.crm.service.support.DataTableTenantSupport;
import com.ruoyi.crm.service.support.EventAudienceKeyResolver;
import com.ruoyi.crm.service.support.LeadDefaults;
import com.ruoyi.crm.service.support.OutreachRegistrationContactHandler;
import com.ruoyi.crm.service.support.OutreachRegistrationIdentityResolution;
import com.ruoyi.crm.service.support.OutreachRegistrationIdentityResolver;
import com.ruoyi.crm.service.support.OutreachRegistrationLeadAttributionService;
import com.ruoyi.crm.service.support.OutreachTicketTokenService;
import com.ruoyi.crm.service.support.PhoneNormalizer;
import com.ruoyi.crm.service.support.RegistrationFormSnapshotSupport;
import com.ruoyi.crm.service.support.WebsiteVisitorSessionService;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.dto.PublicWebsiteEventRegisterRequest;

@Service
public class EventRegistrationServiceImpl implements IEventRegistrationService
{
    private static final Logger log = LoggerFactory.getLogger(EventRegistrationServiceImpl.class);

    @Autowired
    private CrmEventRegistrationMapper registrationMapper;

    @Autowired
    private CrmEventSessionMapper sessionMapper;

    @Autowired
    private CrmEventMapper crmEventMapper;

    @Autowired
    private EventAudienceKeyResolver audienceKeyResolver;

    @Autowired
    private OutreachTicketTokenService ticketTokenService;

    @Autowired
    private OutreachRegistrationLeadAttributionService leadAttributionService;

    @Autowired
    private OutreachRegistrationIdentityResolver identityResolver;

    @Autowired
    private OutreachRegistrationContactHandler contactHandler;

    @Autowired
    private ICrmLeadService crmLeadService;

    @Autowired
    private ICrmContactService crmContactService;

    @Autowired
    private ActivityEventProducer activityEventProducer;

    @Autowired
    private WebsiteVisitorSessionService visitorSessionService;

    public static final String DEFAULT_WEBSITE_LEAD_SOURCE = IEventRegistrationService.DEFAULT_WEBSITE_LEAD_SOURCE;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OutreachRegistrationSubmitResultVo registerFromH5(String tenantId, CrmOutreachlead submission,
            CrmEvent event, String sessionId, String tokenOpenId, String tokenAppId)
    {
        return registerFromPublicChannel(tenantId, submission, event, sessionId,
                EventRegistrationSource.H5.getCode(), tokenOpenId, tokenAppId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OutreachRegistrationSubmitResultVo registerFromWebsite(String tenantId, CrmOutreachlead submission,
            CrmEvent event, String sessionId)
    {
        if (submission != null && StringUtils.isBlank(submission.getLeadSource()))
        {
            submission.setLeadSource(DEFAULT_WEBSITE_LEAD_SOURCE);
        }
        return registerFromPublicChannel(tenantId, submission, event, sessionId,
                EventRegistrationSource.WEBSITE.getCode(), null, null);
    }

    private OutreachRegistrationSubmitResultVo registerFromPublicChannel(String tenantId,
            CrmOutreachlead submission, CrmEvent event, String sessionId, String registrationSource,
            String tokenOpenId, String tokenAppId)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        if (StringUtils.isBlank(sessionId))
        {
            sessionId = event.getDefaultSessionId();
        }
        if (StringUtils.isBlank(sessionId))
        {
            throw new ServiceException("SESSION_REQUIRED", HttpStatus.BAD_REQUEST);
        }
        validateRegistrationOpen(event, sessionId, tenantId);

        String displayName = resolveLeadDisplayName(submission.getLeadName(), submission.getMobilePhone());
        OutreachRegistrationIdentityResolution identity = identityResolver.resolve(
                tenantId, event.getEventId(), submission.getMobilePhone(), tokenOpenId, tokenAppId);

        String leadId = null;
        String contactId = null;
        String resolution;
        String audienceStatus;
        AudienceKeyHints audienceHints;

        if (identity.isContactKnown())
        {
            contactId = identity.getContactId();
            contactHandler.enrichAndBindIdentity(submission, contactId, tokenOpenId, tokenAppId);
            resolution = "contact_known";
            audienceStatus = AudienceStatusResolver.CONTACT_KNOWN;
            audienceHints = AudienceKeyHints.create()
                    .contactId(contactId)
                    .mobilePhone(submission.getMobilePhone())
                    .wechatMpOpenid(tokenOpenId);
        }
        else
        {
            boolean leadExisted = identity.getKind() == OutreachRegistrationIdentityResolution.Kind.LEAD_UPSERT;
            int leadRows = leadAttributionService.submitRegistration(submission, event, tokenOpenId, tokenAppId);
            if (leadRows <= 0)
            {
                throw new ServiceException("报名失败", HttpStatus.ERROR);
            }
            leadId = submission.getLeadId();
            resolution = leadExisted ? "lead_updated" : "lead_created";
            audienceStatus = AudienceStatusResolver.REGISTRATION_PENDING;
            CrmLead lead = crmLeadService.selectCrmLeadByLeadId(leadId);
            audienceHints = AudienceKeyHints.create()
                    .contactId(lead != null ? lead.getContactId() : null)
                    .wechatMpOpenid(tokenOpenId)
                    .mobilePhone(submission.getMobilePhone())
                    .leadId(leadId);
        }

        RegistrationFormSnapshotSupport.Snapshot snapshot =
                RegistrationFormSnapshotSupport.fromSubmission(submission);
        RegistrationOutcome outcome = registerInternal(tenantId, event, sessionId,
                leadId, contactId, displayName, submission.getMobilePhone(),
                registrationSource, null, true, audienceHints, false, snapshot,
                tokenOpenId, tokenAppId);

        stitchVisitorSession(submission, contactId, leadId);

        OutreachRegistrationSubmitResultVo result = new OutreachRegistrationSubmitResultVo();
        result.setResolution(resolution);
        result.setContactId(contactId);
        result.setLeadId(leadId);
        result.setRegistrationId(outcome.registration.getRegistrationId());
        result.setRegistrationStatus(outcome.registration.getRegistrationStatus());
        result.setAudienceStatus(audienceStatus);
        result.setAudienceKey(outcome.registration.getAudienceKey());
        result.setSessionId(sessionId);
        result.setSessionName(outcome.sessionName);
        result.setTicketToken(outcome.ticketToken);
        return result;
    }

    /**
     * 将浏览器 Cookie _sess 对应的 visitor session 缝合到报名主体（与场次 sessionId 无关）。
     * 缝合失败只记日志，不回滚报名。
     */
    private void stitchVisitorSession(CrmOutreachlead submission, String contactId, String leadId)
    {
        if (submission == null)
        {
            return;
        }
        String visitorSessionId = resolveVisitorSessionId(submission);
        if (StringUtils.isBlank(visitorSessionId))
        {
            return;
        }
        String browseHistory = resolveBrowseHistory(submission);
        try
        {
            if (StringUtils.isNotBlank(contactId))
            {
                visitorSessionService.stitch(visitorSessionId, browseHistory, "Contact", contactId);
                return;
            }
            if (StringUtils.isNotBlank(leadId))
            {
                visitorSessionService.stitch(visitorSessionId, browseHistory, "Lead", leadId);
            }
        }
        catch (Exception e)
        {
            log.warn("Visitor session stitch failed after event registration: visitorSessionId={}, contactId={}, leadId={}",
                    visitorSessionId, contactId, leadId, e);
        }
    }

    private static String resolveVisitorSessionId(CrmOutreachlead submission)
    {
        if (StringUtils.isNotBlank(submission.get_sessionId()))
        {
            return submission.get_sessionId().trim();
        }
        return stringFromFormData(submission, "_sessionId");
    }

    private static String resolveBrowseHistory(CrmOutreachlead submission)
    {
        if (StringUtils.isNotBlank(submission.get_browseHistory()))
        {
            return submission.get_browseHistory();
        }
        return stringFromFormData(submission, "_browseHistory");
    }

    private static String stringFromFormData(CrmOutreachlead submission, String key)
    {
        if (!(submission instanceof PublicWebsiteEventRegisterRequest))
        {
            return null;
        }
        Map<String, Object> formData = ((PublicWebsiteEventRegisterRequest) submission).getFormData();
        if (formData == null || formData.get(key) == null)
        {
            return null;
        }
        String value = String.valueOf(formData.get(key)).trim();
        return StringUtils.isNotBlank(value) ? value : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CrmEventRegistration registerWalkInForSelfCheckIn(String tenantId, CrmEvent event, String sessionId,
            CrmOutreachlead submission, String tokenOpenId, String tokenAppId)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        if (StringUtils.isBlank(sessionId))
        {
            sessionId = event.getDefaultSessionId();
        }
        if (StringUtils.isBlank(sessionId))
        {
            throw new ServiceException("SESSION_REQUIRED", HttpStatus.BAD_REQUEST);
        }
        int leadRows = leadAttributionService.submitRegistration(submission, event, tokenOpenId, tokenAppId);
        if (leadRows <= 0)
        {
            throw new ServiceException("报名失败", HttpStatus.ERROR);
        }
        AudienceKeyHints hints = AudienceKeyHints.create()
                .wechatMpOpenid(tokenOpenId)
                .mobilePhone(submission.getMobilePhone())
                .leadId(submission.getLeadId());
        RegistrationFormSnapshotSupport.Snapshot snapshot =
                RegistrationFormSnapshotSupport.fromSubmission(submission);
        RegistrationOutcome outcome = registerInternal(tenantId, event, sessionId,
                submission.getLeadId(), null,
                resolveLeadDisplayName(submission.getLeadName(), submission.getMobilePhone()),
                submission.getMobilePhone(), EventRegistrationSource.WALK_IN.getCode(), "self",
                false, hints, true, snapshot, tokenOpenId, tokenAppId);
        return outcome.registration;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EventRegistrationRowVo createManualRegistration(String tenantId, String eventId,
            EventRegistrationCreateRequest request, String operator)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        CrmEvent event = requireEvent(eventId);
        if (request == null || StringUtils.isBlank(request.getSessionId()))
        {
            throw new ServiceException("SESSION_REQUIRED", HttpStatus.BAD_REQUEST);
        }
        String source = StringUtils.isNotBlank(request.getRegistrationSource())
                ? request.getRegistrationSource()
                : EventRegistrationSource.CRM_MANUAL.getCode();
        String leadId = request.getLeadId();
        if (StringUtils.isBlank(leadId) && StringUtils.isNotBlank(request.getMobilePhone()))
        {
            leadId = createInitialLead(eventId, request, operator);
        }
        RegistrationFormSnapshotSupport.Snapshot snapshot =
                RegistrationFormSnapshotSupport.fromManualRequest(request);
        RegistrationOutcome outcome = registerInternal(tenantId, event, request.getSessionId(),
                leadId, null, request.getDisplayName(), request.getMobilePhone(), source, operator,
                true, AudienceKeyHints.create().leadId(leadId).mobilePhone(request.getMobilePhone()), false,
                snapshot, null, null);
        EventRegistrationRowVo row = new EventRegistrationRowVo();
        row.setRegistrationId(outcome.registration.getRegistrationId());
        row.setEventId(eventId);
        row.setSessionId(request.getSessionId());
        row.setSessionName(outcome.sessionName);
        row.setLeadId(leadId);
        row.setAudienceKey(outcome.registration.getAudienceKey());
        row.setDisplayName(outcome.registration.getDisplayName());
        row.setMobilePhone(outcome.registration.getMobilePhone());
        row.setRegistrationStatus(outcome.registration.getRegistrationStatus());
        row.setRegistrationSource(outcome.registration.getRegistrationSource());
        row.setRegisteredAt(outcome.registration.getRegisteredAt());
        row.setCheckInStatus("not_checked_in");
        return row;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelRegistration(String tenantId, String eventId, String registrationId, String operator)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        CrmEventRegistration reg = registrationMapper.selectByRegistrationId(tenantId, registrationId);
        if (reg == null || !eventId.equals(reg.getEventId()))
        {
            throw new ServiceException("报名记录不存在", HttpStatus.NOT_FOUND);
        }
        if (EventRegistrationStatus.CANCELLED.getCode().equals(reg.getRegistrationStatus()))
        {
            return;
        }
        CrmEventRegistration update = new CrmEventRegistration();
        update.setTenantId(tenantId);
        update.setRegistrationId(registrationId);
        update.setRegistrationStatus(EventRegistrationStatus.CANCELLED.getCode());
        update.setCancelledAt(DateUtils.getNowDate());
        update.setUpdateBy(operator);
        update.setUpdateTime(DateUtils.getNowDate());
        registrationMapper.update(update);
    }

    @Override
    public List<EventRegistrationRowVo> listRegistrations(String tenantId, String eventId,
            EventRegistrationQuery query)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        requireEvent(eventId);
        return registrationMapper.selectRegistrationRows(tenantId, eventId, query);
    }

    @Override
    public EventRegistrationDetailVo getRegistrationDetail(String tenantId, String eventId,
            String registrationId)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        requireEvent(eventId);
        EventRegistrationDetailVo detail = registrationMapper.selectRegistrationDetail(
                tenantId, eventId, registrationId);
        if (detail == null)
        {
            throw new ServiceException("报名记录不存在", HttpStatus.NOT_FOUND);
        }
        populateDetailFormData(detail);
        RegistrationFormSnapshotSupport.applyRedundantFields(detail);
        return detail;
    }

    private void populateDetailFormData(EventRegistrationDetailVo detail)
    {
        Map<String, Object> formData = RegistrationFormSnapshotSupport.parseFormDataJson(
                detail.getFormDataJson());
        if (formData != null && !formData.isEmpty())
        {
            detail.setFormData(formData);
            return;
        }
        if (StringUtils.isNotBlank(detail.getLeadId()))
        {
            CrmLead lead = crmLeadService.selectCrmLeadByLeadId(detail.getLeadId());
            formData = RegistrationFormSnapshotSupport.assembleFromLead(lead);
            if (formData != null)
            {
                detail.setFormData(formData);
            }
            if (StringUtils.isBlank(detail.getCustomField()) && lead != null)
            {
                detail.setCustomField(lead.getCustomField());
            }
            return;
        }
        if (StringUtils.isNotBlank(detail.getContactId()))
        {
            CrmContact contact = crmContactService.selectCrmContactByContactId(detail.getContactId());
            formData = RegistrationFormSnapshotSupport.assembleFromContact(contact);
            if (formData != null)
            {
                detail.setFormData(formData);
            }
            if (StringUtils.isBlank(detail.getCustomField()) && contact != null)
            {
                detail.setCustomField(contact.getCustomField());
            }
        }
    }

    private RegistrationOutcome registerInternal(String tenantId, CrmEvent event, String sessionId,
            String leadId, String contactId, String displayName, String mobilePhone, String source, String operator,
            boolean issueTicket, AudienceKeyHints audienceHints, boolean selfCheckInPriority,
            RegistrationFormSnapshotSupport.Snapshot snapshot, String tokenOpenId, String tokenAppId)
    {
        if (StringUtils.isNotBlank(leadId) && StringUtils.isNotBlank(contactId))
        {
            throw new ServiceException("报名主体冲突", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(leadId) && StringUtils.isBlank(contactId))
        {
            throw new ServiceException("报名主体缺失", HttpStatus.BAD_REQUEST);
        }

        CrmEventSession session = sessionMapper.selectBySessionIdForUpdate(tenantId, sessionId);
        if (session == null || !event.getEventId().equals(session.getEventId()) || "deleted".equals(session.getStatus()))
        {
            throw new ServiceException("场次不存在", HttpStatus.NOT_FOUND);
        }
        String audienceKey = resolveAudienceKey(audienceHints, selfCheckInPriority);
        if (StringUtils.isBlank(audienceKey))
        {
            audienceKey = StringUtils.isNotBlank(leadId) ? "h5:" + leadId : "contact:" + contactId;
        }
        String normalizedMobile = PhoneNormalizer.normalize(mobilePhone);

        CrmEventRegistration existing = registrationMapper.selectActiveBySlot(
                tenantId, event.getEventId(), sessionId, audienceKey);
        if (existing != null)
        {
            return handleExistingRegistration(tenantId, existing, displayName, normalizedMobile, operator, session,
                    snapshot);
        }

        int confirmedCount = registrationMapper.countConfirmedBySession(tenantId, event.getEventId(), sessionId);
        boolean isFull = session.getCapacity() != null && confirmedCount >= session.getCapacity();
        String status;
        if (isFull)
        {
            if (Boolean.TRUE.equals(event.getWaitlistEnabled()))
            {
                status = EventRegistrationStatus.WAITLIST.getCode();
            }
            else
            {
                throw new ServiceException("REGISTRATION_FULL", HttpStatus.CONFLICT);
            }
        }
        else
        {
            status = EventRegistrationStatus.CONFIRMED.getCode();
        }
        String registrationId = UUID.randomUUID().toString();
        CrmEventRegistration registration = new CrmEventRegistration();
        registration.setRegistrationId(registrationId);
        registration.setEventId(event.getEventId());
        registration.setSessionId(sessionId);
        registration.setTenantId(tenantId);
        registration.setLeadId(leadId);
        registration.setContactId(contactId);
        registration.setAudienceKey(audienceKey);
        registration.setDisplayName(displayName);
        registration.setMobilePhone(normalizedMobile);
        registration.setRegistrationStatus(status);
        registration.setRegistrationSource(source);
        registration.setRegisteredAt(DateUtils.getNowDate());
        applySnapshot(registration, snapshot);
        registration.setCreateBy(operator);
        registration.setCreateTime(DateUtils.getNowDate());

        String ticketToken = null;
        if (issueTicket && EventRegistrationStatus.CONFIRMED.getCode().equals(status))
        {
            ticketToken = ticketTokenService.sign(tenantId, event.getEventId(), registrationId, sessionId, session);
            registration.setTicketTokenHash(OutreachTicketTokenService.hashToken(ticketToken));
        }
        registrationMapper.insert(registration);
        writeRegistrationSubmittedActivity(event, registration, tokenOpenId, tokenAppId);

        RegistrationOutcome outcome = new RegistrationOutcome();
        outcome.registration = registration;
        outcome.sessionName = session.getName();
        outcome.ticketToken = ticketToken;
        outcome.newlyCreated = true;
        return outcome;
    }

    private void writeRegistrationSubmittedActivity(CrmEvent event, CrmEventRegistration registration,
            String tokenOpenId, String tokenAppId)
    {
        if (event == null || registration == null)
        {
            return;
        }
        String contactId = registration.getContactId();
        String leadId = registration.getLeadId();
        String subjectType;
        String subjectId;
        if (StringUtils.isNotBlank(contactId))
        {
            subjectType = "Contact";
            subjectId = contactId;
        }
        else if (StringUtils.isNotBlank(leadId))
        {
            subjectType = "Lead";
            subjectId = leadId;
        }
        else
        {
            return;
        }
        String source = registration.getRegistrationSource();
        String channel = (EventRegistrationSource.H5.getCode().equals(source)
                || EventRegistrationSource.WEBSITE.getCode().equals(source)) ? "website" : "manual";
        activityEventProducer.produceEventRegistrationSubmitted(
                subjectType, subjectId, channel,
                event.getEventId(), event.getSubject(),
                registration.getRegistrationId(), registration.getSessionId(),
                source, registration.getRegistrationStatus(), registration.getRegisteredAt(),
                tokenAppId, tokenOpenId);
    }

    private RegistrationOutcome handleExistingRegistration(String tenantId, CrmEventRegistration existing,
            String displayName, String normalizedMobile, String operator, CrmEventSession session,
            RegistrationFormSnapshotSupport.Snapshot snapshot)
    {
        boolean needsUpdate = false;
        CrmEventRegistration patch = new CrmEventRegistration();
        patch.setTenantId(tenantId);
        patch.setRegistrationId(existing.getRegistrationId());

        if (StringUtils.isNotBlank(displayName) && !displayName.equals(existing.getDisplayName()))
        {
            patch.setDisplayName(displayName);
            needsUpdate = true;
        }
        if (StringUtils.isNotBlank(normalizedMobile) && !normalizedMobile.equals(existing.getMobilePhone()))
        {
            patch.setMobilePhone(normalizedMobile);
            needsUpdate = true;
        }
        if (snapshot != null)
        {
            if (StringUtils.isNotBlank(snapshot.getFormDataJson()))
            {
                patch.setFormData(snapshot.getFormDataJson());
                needsUpdate = true;
            }
            if (snapshot.getCustomField() != null)
            {
                patch.setCustomField(snapshot.getCustomField());
                needsUpdate = true;
            }
        }
        if (needsUpdate)
        {
            patch.setUpdateBy(operator);
            patch.setUpdateTime(DateUtils.getNowDate());
            registrationMapper.update(patch);
            if (StringUtils.isNotBlank(patch.getDisplayName()))
            {
                existing.setDisplayName(patch.getDisplayName());
            }
            if (StringUtils.isNotBlank(patch.getMobilePhone()))
            {
                existing.setMobilePhone(patch.getMobilePhone());
            }
            if (StringUtils.isNotBlank(patch.getFormData()))
            {
                existing.setFormData(patch.getFormData());
            }
            if (patch.getCustomField() != null)
            {
                existing.setCustomField(patch.getCustomField());
            }
        }

        RegistrationOutcome outcome = new RegistrationOutcome();
        outcome.registration = existing;
        outcome.sessionName = session != null ? session.getName() : null;
        outcome.ticketToken = null;
        outcome.newlyCreated = false;
        return outcome;
    }

    private void validateRegistrationOpen(CrmEvent event, String sessionId, String tenantId)
    {
        if (!Boolean.TRUE.equals(event.getRegistrationEnabled()))
        {
            throw new ServiceException("REGISTRATION_CLOSED", HttpStatus.GONE);
        }
        Date now = new Date();
        CrmEventSession session = sessionMapper.selectBySessionId(tenantId, sessionId);
        Date deadline = session != null && session.getRegistrationDeadline() != null
                ? session.getRegistrationDeadline()
                : event.getRegistrationDeadline();
        if (deadline != null && now.after(deadline))
        {
            throw new ServiceException("REGISTRATION_CLOSED", HttpStatus.GONE);
        }
    }

    private String createInitialLead(String eventId, EventRegistrationCreateRequest request, String operator)
    {
        String displayName = resolveLeadDisplayName(request.getDisplayName(), request.getMobilePhone());
        CrmLead lead = new CrmLead();
        lead.setLeadId(UUID.randomUUID().toString());
        lead.setSid(eventId);
        lead.setStatus("Initial");
        lead.setLeadSource("Marketing Event");
        lead.setStage(LeadDefaults.DEFAULT_STAGE);
        lead.setLeadName(displayName);
        lead.setFullName(displayName);
        lead.setMobilePhone(request.getMobilePhone());
        lead.setCreateBy(operator);
        lead.setCreateTime(DateUtils.getNowDate());
        crmLeadService.insertCrmLead(lead);
        return lead.getLeadId();
    }

    static String resolveLeadDisplayName(String displayName, String mobilePhone)
    {
        if (StringUtils.isNotBlank(displayName))
        {
            return displayName.trim();
        }
        String mobile = PhoneNormalizer.normalize(mobilePhone);
        if (StringUtils.isNotBlank(mobile))
        {
            return mobile;
        }
        return "活动现场客户";
    }

    private CrmEvent requireEvent(String eventId)
    {
        CrmEvent event = crmEventMapper.selectCrmEventByEventId(eventId);
        if (event == null)
        {
            throw new ServiceException("活动不存在", HttpStatus.NOT_FOUND);
        }
        return event;
    }

    /**
     * 自助签到 walk-in 身份键：mp openid 优先，其次 mobile，再 leadId
     */
    static String resolveSelfCheckInAudienceKey(AudienceKeyHints hints)
    {
        if (hints == null)
        {
            return null;
        }
        if (StringUtils.isNotEmpty(hints.getWechatMpOpenid()))
        {
            return "mp:" + hints.getWechatMpOpenid();
        }
        String mobile = PhoneNormalizer.normalize(hints.getMobilePhone());
        if (StringUtils.isNotEmpty(mobile))
        {
            return "mobile:" + mobile;
        }
        if (StringUtils.isNotEmpty(hints.getLeadId()))
        {
            return "h5:" + hints.getLeadId();
        }
        return null;
    }

    private String resolveAudienceKey(AudienceKeyHints hints, boolean selfCheckInPriority)
    {
        if (selfCheckInPriority)
        {
            String key = resolveSelfCheckInAudienceKey(hints);
            if (StringUtils.isNotBlank(key))
            {
                return key;
            }
        }
        return audienceKeyResolver.resolve(hints);
    }

    private static void applySnapshot(CrmEventRegistration registration,
            RegistrationFormSnapshotSupport.Snapshot snapshot)
    {
        if (registration == null || snapshot == null)
        {
            return;
        }
        registration.setFormData(snapshot.getFormDataJson());
        registration.setCustomField(snapshot.getCustomField());
    }

    private static final class RegistrationOutcome
    {
        private CrmEventRegistration registration;
        private String sessionName;
        private String ticketToken;
        private boolean newlyCreated;
    }
}
