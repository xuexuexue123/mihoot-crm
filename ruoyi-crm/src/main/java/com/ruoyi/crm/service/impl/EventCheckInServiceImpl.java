package com.ruoyi.crm.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.domain.CrmEventCheckIn;
import com.ruoyi.crm.domain.CrmEventRegistration;
import com.ruoyi.crm.domain.CrmEventSession;
import com.ruoyi.crm.domain.dto.EventCheckInQuery;
import com.ruoyi.crm.domain.dto.EventCheckInRequest;
import com.ruoyi.crm.domain.vo.EventAudienceRowVo;
import com.ruoyi.crm.domain.vo.EventCheckInLookupRowVo;
import com.ruoyi.crm.domain.vo.EventCheckInResultVo;
import com.ruoyi.crm.domain.vo.EventCheckInRowVo;
import com.ruoyi.crm.domain.vo.EventCheckInSummaryVo;
import com.ruoyi.crm.domain.vo.OutreachEventTicketVo;
import com.ruoyi.crm.enums.EventCheckInMethod;
import com.ruoyi.crm.enums.EventRegistrationStatus;
import com.ruoyi.crm.mapper.CrmEventCheckInMapper;
import com.ruoyi.crm.mapper.CrmEventMapper;
import com.ruoyi.crm.mapper.CrmEventRegistrationMapper;
import com.ruoyi.crm.mapper.CrmEventSessionMapper;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.domain.dto.LeadConvertEvaluateResult;
import com.ruoyi.crm.domain.dto.LeadConvertEvaluateSummary;
import com.ruoyi.crm.service.ICrmLeadConvertService;
import com.ruoyi.crm.service.IEventAudienceService;
import com.ruoyi.crm.service.IEventCheckInService;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.service.support.DataTableTenantSupport;
import com.ruoyi.crm.service.support.LeadConvertTriggerService;
import com.ruoyi.crm.service.support.EventCheckInWindowSupport;
import com.ruoyi.crm.service.support.EventPerformanceMetricsSupport;
import com.ruoyi.crm.service.support.OutreachTicketTokenService;
import com.ruoyi.crm.service.support.OutreachTicketTokenService.VerifiedTicketPayload;

@Service
public class EventCheckInServiceImpl implements IEventCheckInService
{
    @Autowired
    private CrmEventCheckInMapper checkInMapper;

    @Autowired
    private CrmEventRegistrationMapper registrationMapper;

    @Autowired
    private CrmEventSessionMapper sessionMapper;

    @Autowired
    private CrmEventMapper crmEventMapper;

    @Autowired
    private OutreachTicketTokenService ticketTokenService;

    @Autowired
    private IEventAudienceService eventAudienceService;

    @Autowired
    private ICrmLeadConvertService leadConvertService;

    @Autowired
    private LeadConvertTriggerService leadConvertTriggerService;

    @Autowired
    private CrmLeadMapper crmLeadMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EventCheckInResultVo checkIn(String tenantId, String eventId, EventCheckInRequest request,
            String operator)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        CrmEvent event = requireEvent(eventId);
        if (!Boolean.TRUE.equals(event.getCheckInEnabled()))
        {
            throw new ServiceException("签到未启用", HttpStatus.FORBIDDEN);
        }
        if (request == null || StringUtils.isBlank(request.getSessionId()))
        {
            throw new ServiceException("场次不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmEventSession session = requireSession(tenantId, request.getSessionId(), eventId);
        if (!EventCheckInWindowSupport.isWithinWindow(session, new Date()))
        {
            throw new ServiceException("CHECK_IN_WINDOW_CLOSED", HttpStatus.FORBIDDEN);
        }

        String audienceKey;
        String registrationId = null;
        String displayName = null;
        String method = request.getMethod();

        if (StringUtils.isNotBlank(request.getTicketToken()))
        {
            VerifiedTicketPayload payload = ticketTokenService.verify(request.getTicketToken(), eventId);
            if (!request.getSessionId().equals(payload.getSessionId()))
            {
                throw new ServiceException("场次不匹配", HttpStatus.BAD_REQUEST);
            }
            CrmEventRegistration reg = registrationMapper.selectByRegistrationId(tenantId, payload.getRegistrationId());
            if (reg == null || !EventRegistrationStatus.CONFIRMED.getCode().equals(reg.getRegistrationStatus()))
            {
                throw new ServiceException("报名未确认", HttpStatus.FORBIDDEN);
            }
            if (!OutreachTicketTokenService.hashToken(request.getTicketToken()).equals(reg.getTicketTokenHash()))
            {
                throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
            }
            audienceKey = reg.getAudienceKey();
            registrationId = reg.getRegistrationId();
            displayName = reg.getDisplayName();
            method = EventCheckInMethod.QR_TICKET.getCode();
        }
        else if (StringUtils.isNotBlank(request.getRegistrationId()))
        {
            CrmEventRegistration reg = registrationMapper.selectByRegistrationId(tenantId, request.getRegistrationId());
            if (reg == null || !eventId.equals(reg.getEventId()))
            {
                throw new ServiceException("报名记录不存在", HttpStatus.NOT_FOUND);
            }
            if (!EventRegistrationStatus.CONFIRMED.getCode().equals(reg.getRegistrationStatus()))
            {
                throw new ServiceException("报名未确认", HttpStatus.FORBIDDEN);
            }
            audienceKey = reg.getAudienceKey();
            registrationId = reg.getRegistrationId();
            displayName = reg.getDisplayName();
            if (StringUtils.isBlank(method))
            {
                method = EventCheckInMethod.MANUAL.getCode();
            }
        }
        else if (StringUtils.isNotBlank(request.getAudienceKey()))
        {
            audienceKey = request.getAudienceKey();
            if (StringUtils.isBlank(method))
            {
                method = EventCheckInMethod.AUDIENCE_LOOKUP.getCode();
            }
            EventAudienceRowVo audience = eventAudienceService.findByAudienceKey(tenantId, eventId, audienceKey);
            if (audience != null)
            {
                displayName = audience.getDisplayName();
            }
        }
        else
        {
            throw new ServiceException("缺少签到定位信息", HttpStatus.BAD_REQUEST);
        }

        CrmEventCheckIn existing = checkInMapper.selectBySlot(tenantId, eventId, request.getSessionId(), audienceKey);
        if (existing != null)
        {
            return enrichCheckInResult(buildDuplicateResult(existing, audienceKey, displayName),
                    eventId, registrationId, audienceKey);
        }

        return enrichCheckInResult(
                insertNewCheckIn(tenantId, eventId, request.getSessionId(), audienceKey, registrationId,
                        displayName, method, operator, request.getNotes()),
                eventId, registrationId, audienceKey);
    }

    @Override
    public EventCheckInResultVo insertCheckInIfAbsent(String tenantId, String eventId, String sessionId,
            String audienceKey, String registrationId, String displayName, String method, String operator)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        CrmEventCheckIn existing = checkInMapper.selectBySlot(tenantId, eventId, sessionId, audienceKey);
        if (existing != null)
        {
            return enrichCheckInResult(buildDuplicateResult(existing, audienceKey, displayName),
                    eventId, sessionId, audienceKey);
        }
        return enrichCheckInResult(
                insertNewCheckIn(tenantId, eventId, sessionId, audienceKey, registrationId,
                        displayName, method, operator, null),
                eventId, registrationId, audienceKey);
    }

    private EventCheckInResultVo buildDuplicateResult(CrmEventCheckIn existing, String audienceKey, String displayName)
    {
        EventCheckInResultVo dup = new EventCheckInResultVo();
        dup.setCheckInId(existing.getCheckInId());
        dup.setAudienceKey(audienceKey);
        dup.setDisplayName(displayName);
        dup.setCheckedInAt(existing.getCheckedInAt());
        dup.setAlreadyCheckedIn(true);
        return dup;
    }

    private EventCheckInResultVo insertNewCheckIn(String tenantId, String eventId, String sessionId,
            String audienceKey, String registrationId, String displayName, String method, String operator,
            String notes)
    {
        CrmEventCheckIn checkIn = new CrmEventCheckIn();
        checkIn.setCheckInId(UUID.randomUUID().toString());
        checkIn.setEventId(eventId);
        checkIn.setSessionId(sessionId);
        checkIn.setTenantId(tenantId);
        checkIn.setAudienceKey(audienceKey);
        checkIn.setRegistrationId(registrationId);
        checkIn.setCheckInMethod(method);
        checkIn.setCheckedInAt(DateUtils.getNowDate());
        checkIn.setCheckedInBy(operator);
        checkIn.setNotes(notes);
        checkIn.setCreateTime(DateUtils.getNowDate());
        checkInMapper.insert(checkIn);

        EventCheckInResultVo result = new EventCheckInResultVo();
        result.setCheckInId(checkIn.getCheckInId());
        result.setAudienceKey(audienceKey);
        result.setDisplayName(displayName);
        result.setCheckedInAt(checkIn.getCheckedInAt());
        result.setAlreadyCheckedIn(false);
        return result;
    }

    private EventCheckInResultVo enrichCheckInResult(EventCheckInResultVo result, String eventId,
            String registrationId, String audienceKey)
    {
        if (result == null)
        {
            return null;
        }
        String leadId = leadConvertService.resolveLeadIdForCheckIn(eventId, registrationId, audienceKey);
        if (StringUtils.isNotBlank(leadId))
        {
            result.setLeadId(leadId);
            LeadConvertEvaluateResult evaluate = leadConvertService.evaluateEventCheckIn(
                    eventId, registrationId, audienceKey);
            result.setConvertEvaluate(LeadConvertEvaluateSummary.from(evaluate));
            CrmLead leadHint = crmLeadMapper.selectCrmLeadByLeadId(leadId);
            leadConvertTriggerService.registerEventCheckInAfterCommit(
                    eventId, registrationId, audienceKey, leadHint);
        }
        return result;
    }

    @Override
    public EventCheckInSummaryVo getSummary(String tenantId, String eventId, String sessionId)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        requireEvent(eventId);
        EventCheckInSummaryVo vo = new EventCheckInSummaryVo();
        vo.setEventId(eventId);
        vo.setSessionId(sessionId);
        if (StringUtils.isNotBlank(sessionId))
        {
            CrmEventSession session = sessionMapper.selectBySessionId(tenantId, sessionId);
            if (session != null)
            {
                vo.setSessionName(session.getName());
                vo.setCapacity(session.getCapacity());
            }
            vo.setConfirmedCount(registrationMapper.countConfirmedBySession(tenantId, eventId, sessionId));
            vo.setWaitlistCount(registrationMapper.countWaitlistBySession(tenantId, eventId, sessionId));
            vo.setCheckedInCount(checkInMapper.countDistinctBySession(tenantId, eventId, sessionId));
            vo.setWalkInCheckInCount(checkInMapper.countByMethod(tenantId, eventId, sessionId,
                    EventCheckInMethod.WALK_IN.getCode()));
            vo.setAudienceOnlyCheckInCount(checkInMapper.countAudienceOnly(tenantId, eventId, sessionId));
        }
        else
        {
            vo.setConfirmedCount(registrationMapper.countDistinctConfirmedByEvent(tenantId, eventId, null));
            vo.setWaitlistCount(registrationMapper.countWaitlistByEvent(tenantId, eventId, null));
            vo.setCheckedInCount(checkInMapper.countDistinctByEvent(tenantId, eventId));
            vo.setWalkInCheckInCount(checkInMapper.countByMethod(tenantId, eventId, null,
                    EventCheckInMethod.WALK_IN.getCode()));
            vo.setAudienceOnlyCheckInCount(checkInMapper.countAudienceOnly(tenantId, eventId, null));
        }
        vo.setAttendanceRate(EventPerformanceMetricsSupport.rate(
                vo.getCheckedInCount(), vo.getConfirmedCount()));
        return vo;
    }

    @Override
    public List<EventCheckInLookupRowVo> lookup(String tenantId, String eventId, String sessionId, String keyword)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        if (StringUtils.isBlank(sessionId))
        {
            throw new ServiceException("场次不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(keyword) || keyword.trim().length() < 2)
        {
            throw new ServiceException("关键词至少2个字符", HttpStatus.BAD_REQUEST);
        }
        com.ruoyi.crm.domain.dto.EventAudienceQuery query = new com.ruoyi.crm.domain.dto.EventAudienceQuery();
        query.setKeyword(keyword.trim());
        List<EventAudienceRowVo> audience = eventAudienceService.listAudience(tenantId, eventId, query);
        List<EventCheckInLookupRowVo> rows = new ArrayList<>();
        for (EventAudienceRowVo row : audience)
        {
            EventCheckInLookupRowVo vo = new EventCheckInLookupRowVo();
            vo.setAudienceKey(row.getAudienceKey());
            vo.setDisplayName(row.getDisplayName());
            vo.setMobilePhone(row.getMobilePhone());
            vo.setChannels(row.getChannels());
            CrmEventRegistration reg = findBestRegistration(tenantId, eventId, sessionId, row.getAudienceKey());
            if (reg != null)
            {
                vo.setRegistrationStatus(reg.getRegistrationStatus());
                vo.setRegistrationId(reg.getRegistrationId());
            }
            CrmEventCheckIn ci = checkInMapper.selectBySlot(tenantId, eventId, sessionId, row.getAudienceKey());
            if (ci != null)
            {
                vo.setCheckInStatus("checked_in");
                vo.setCheckedInAt(ci.getCheckedInAt());
            }
            else
            {
                vo.setCheckInStatus("not_checked_in");
            }
            rows.add(vo);
        }
        return rows;
    }

    @Override
    public List<EventCheckInRowVo> listCheckIns(String tenantId, String eventId, EventCheckInQuery query)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        requireEvent(eventId);
        return checkInMapper.selectCheckInRows(tenantId, eventId, query);
    }

    @Override
    public OutreachEventTicketVo getTicketInfo(String ticketToken)
    {
        VerifiedTicketPayload payload = ticketTokenService.verify(ticketToken, null);
        CrmEvent event = crmEventMapper.selectCrmEventByEventId(payload.getEventId());
        CrmEventRegistration reg = registrationMapper.selectByRegistrationId(payload.getTenantId(),
                payload.getRegistrationId());
        CrmEventSession session = sessionMapper.selectBySessionId(payload.getTenantId(), payload.getSessionId());
        OutreachEventTicketVo vo = new OutreachEventTicketVo();
        if (event != null)
        {
            vo.setEventSubject(event.getSubject());
        }
        if (session != null)
        {
            vo.setSessionName(session.getName());
            vo.setStartTime(session.getStartTime());
            vo.setLocation(session.getLocation());
        }
        if (reg != null)
        {
            vo.setDisplayName(maskName(reg.getDisplayName()));
            vo.setRegistrationStatus(reg.getRegistrationStatus());
        }
        vo.setQrPayload("ticket:" + ticketToken);
        return vo;
    }

    private CrmEventRegistration findBestRegistration(String tenantId, String eventId, String sessionId,
            String audienceKey)
    {
        return registrationMapper.selectActiveBySlot(tenantId, eventId, sessionId, audienceKey);
    }

    private static String maskName(String name)
    {
        if (StringUtils.isBlank(name))
        {
            return name;
        }
        if (name.length() <= 1)
        {
            return name;
        }
        return name.charAt(0) + "*";
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

    private CrmEventSession requireSession(String tenantId, String sessionId, String eventId)
    {
        CrmEventSession session = sessionMapper.selectBySessionId(tenantId, sessionId);
        if (session == null || !eventId.equals(session.getEventId()) || "deleted".equals(session.getStatus()))
        {
            throw new ServiceException("场次不存在", HttpStatus.NOT_FOUND);
        }
        return session;
    }
}
