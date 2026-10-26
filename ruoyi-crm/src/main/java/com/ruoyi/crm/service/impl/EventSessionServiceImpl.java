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
import com.ruoyi.crm.domain.CrmEventSession;
import com.ruoyi.crm.domain.dto.EventRegistrationSettingsSaveRequest;
import com.ruoyi.crm.domain.dto.EventSessionSaveRequest;
import com.ruoyi.crm.domain.vo.EventRegistrationSettingsVo;
import com.ruoyi.crm.domain.vo.EventSessionVo;
import com.ruoyi.crm.enums.EventRegistrationStatus;
import com.ruoyi.crm.mapper.CrmEventMapper;
import com.ruoyi.crm.mapper.CrmEventCheckInMapper;
import com.ruoyi.crm.mapper.CrmEventRegistrationMapper;
import com.ruoyi.crm.mapper.CrmEventSessionMapper;
import com.ruoyi.crm.service.IEventSessionService;
import com.ruoyi.crm.service.support.DataTableTenantSupport;

@Service
public class EventSessionServiceImpl implements IEventSessionService
{
    private static final String DEFAULT_SESSION_NAME = "默认场次";

    @Autowired
    private CrmEventSessionMapper sessionMapper;

    @Autowired
    private CrmEventMapper crmEventMapper;

    @Autowired
    private CrmEventRegistrationMapper registrationMapper;

    @Autowired
    private CrmEventCheckInMapper checkInMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void ensureDefaultSession(String tenantId, String eventId, String operator)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        CrmEvent event = requireEvent(eventId);
        if (StringUtils.isNotBlank(event.getDefaultSessionId()))
        {
            CrmEventSession existing = sessionMapper.selectBySessionId(tenantId, event.getDefaultSessionId());
            if (existing != null && !"deleted".equals(existing.getStatus()))
            {
                return;
            }
        }
        List<CrmEventSession> active = sessionMapper.selectByEventId(tenantId, eventId, "active");
        if (active != null && !active.isEmpty())
        {
            if (StringUtils.isBlank(event.getDefaultSessionId()))
            {
                CrmEvent update = new CrmEvent();
                update.setEventId(eventId);
                update.setDefaultSessionId(active.get(0).getSessionId());
                crmEventMapper.updateCrmEvent(update);
            }
            return;
        }
        String sessionId = UUID.randomUUID().toString();
        CrmEventSession session = new CrmEventSession();
        session.setSessionId(sessionId);
        session.setEventId(eventId);
        session.setTenantId(tenantId);
        session.setName(DEFAULT_SESSION_NAME);
        session.setStartTime(event.getStartTime());
        session.setEndTime(event.getEndTime());
        session.setLocation(event.getLocation());
        session.setSortOrder(0);
        session.setStatus("active");
        session.setCreateBy(operator);
        session.setCreateTime(DateUtils.getNowDate());
        sessionMapper.insert(session);

        CrmEvent update = new CrmEvent();
        update.setEventId(eventId);
        update.setDefaultSessionId(sessionId);
        crmEventMapper.updateCrmEvent(update);
    }

    @Override
    public EventRegistrationSettingsVo getRegistrationSettings(String tenantId, String eventId)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        CrmEvent event = requireEvent(eventId);
        ensureDefaultSessionQuiet(tenantId, eventId);
        event = crmEventMapper.selectCrmEventByEventId(eventId);
        return toSettingsVo(event);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EventRegistrationSettingsVo updateRegistrationSettings(String tenantId, String eventId,
            EventRegistrationSettingsSaveRequest request, String operator)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        requireEvent(eventId);
        CrmEvent update = new CrmEvent();
        update.setEventId(eventId);
        update.setUpdateBy(operator);
        update.setUpdateTime(DateUtils.getNowDate());
        if (request != null)
        {
            if (request.getRegistrationEnabled() != null)
            {
                update.setRegistrationEnabled(request.getRegistrationEnabled());
            }
            if (request.getRegistrationDeadline() != null)
            {
                update.setRegistrationDeadline(request.getRegistrationDeadline());
            }
            if (request.getWaitlistEnabled() != null)
            {
                update.setWaitlistEnabled(request.getWaitlistEnabled());
            }
            if (request.getCheckInEnabled() != null)
            {
                update.setCheckInEnabled(request.getCheckInEnabled());
            }
            if (request.getAllowWalkIn() != null)
            {
                update.setAllowWalkIn(request.getAllowWalkIn());
            }
            if (request.getDefaultSessionId() != null)
            {
                update.setDefaultSessionId(request.getDefaultSessionId());
            }
        }
        crmEventMapper.updateCrmEvent(update);
        return toSettingsVo(crmEventMapper.selectCrmEventByEventId(eventId));
    }

    @Override
    public List<EventSessionVo> listSessions(String tenantId, String eventId, String status)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        requireEvent(eventId);
        ensureDefaultSessionQuiet(tenantId, eventId);
        String effectiveStatus = StringUtils.isNotBlank(status) ? status : "active";
        List<CrmEventSession> sessions = sessionMapper.selectByEventId(tenantId, eventId, effectiveStatus);
        List<EventSessionVo> result = new ArrayList<>();
        if (sessions == null)
        {
            return result;
        }
        for (CrmEventSession session : sessions)
        {
            result.add(toSessionVo(tenantId, eventId, session));
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EventSessionVo createSession(String tenantId, String eventId, EventSessionSaveRequest request,
            String operator)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        requireEvent(eventId);
        if (request == null || StringUtils.isBlank(request.getName()))
        {
            throw new ServiceException("场次名称不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmEventSession session = new CrmEventSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setEventId(eventId);
        session.setTenantId(tenantId);
        applySessionFields(session, request);
        session.setStatus(StringUtils.isNotBlank(request.getStatus()) ? request.getStatus() : "active");
        session.setCreateBy(operator);
        session.setCreateTime(DateUtils.getNowDate());
        sessionMapper.insert(session);
        return toSessionVo(tenantId, eventId, session);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EventSessionVo updateSession(String tenantId, String eventId, String sessionId,
            EventSessionSaveRequest request, String operator)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        requireEvent(eventId);
        CrmEventSession session = requireSession(tenantId, sessionId);
        if (!eventId.equals(session.getEventId()))
        {
            throw new ServiceException("场次不属于该活动", HttpStatus.BAD_REQUEST);
        }
        if (request != null && request.getCapacity() != null)
        {
            int confirmed = registrationMapper.countConfirmedBySession(tenantId, eventId, sessionId);
            if (request.getCapacity() < confirmed)
            {
                throw new ServiceException("CAPACITY_BELOW_CONFIRMED", HttpStatus.BAD_REQUEST);
            }
        }
        applySessionFields(session, request);
        session.setUpdateBy(operator);
        session.setUpdateTime(DateUtils.getNowDate());
        sessionMapper.update(session);
        return toSessionVo(tenantId, eventId, sessionMapper.selectBySessionId(tenantId, sessionId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSession(String tenantId, String eventId, String sessionId, String operator)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        requireEvent(eventId);
        CrmEventSession session = requireSession(tenantId, sessionId);
        if (!eventId.equals(session.getEventId()))
        {
            throw new ServiceException("场次不属于该活动", HttpStatus.BAD_REQUEST);
        }
        int activeCount = sessionMapper.countActiveByEventId(tenantId, eventId);
        if (activeCount <= 1)
        {
            throw new ServiceException("活动至少保留一个有效场次", HttpStatus.BAD_REQUEST);
        }
        int confirmed = registrationMapper.countConfirmedBySession(tenantId, eventId, sessionId);
        int checkedIn = checkInMapper.countDistinctBySession(tenantId, eventId, sessionId);
        if (confirmed > 0 || checkedIn > 0)
        {
            throw new ServiceException("SESSION_HAS_REGISTRATIONS", HttpStatus.CONFLICT);
        }
        sessionMapper.softDelete(tenantId, sessionId, operator);
    }

    private void ensureDefaultSessionQuiet(String tenantId, String eventId)
    {
        try
        {
            ensureDefaultSession(tenantId, eventId, "system");
        }
        catch (Exception ignored)
        {
        }
    }

    private void applySessionFields(CrmEventSession session, EventSessionSaveRequest request)
    {
        if (request == null)
        {
            return;
        }
        if (request.getName() != null)
        {
            session.setName(request.getName());
        }
        if (request.getStartTime() != null)
        {
            session.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null)
        {
            session.setEndTime(request.getEndTime());
        }
        if (request.getLocation() != null)
        {
            session.setLocation(request.getLocation());
        }
        if (request.getCapacity() != null)
        {
            session.setCapacity(request.getCapacity());
        }
        if (request.getRegistrationDeadline() != null)
        {
            session.setRegistrationDeadline(request.getRegistrationDeadline());
        }
        if (request.getCheckInOpenAt() != null)
        {
            session.setCheckInOpenAt(request.getCheckInOpenAt());
        }
        if (request.getCheckInCloseAt() != null)
        {
            session.setCheckInCloseAt(request.getCheckInCloseAt());
        }
        if (request.getSortOrder() != null)
        {
            session.setSortOrder(request.getSortOrder());
        }
        if (request.getStatus() != null)
        {
            session.setStatus(request.getStatus());
        }
    }

    private EventSessionVo toSessionVo(String tenantId, String eventId, CrmEventSession session)
    {
        EventSessionVo vo = new EventSessionVo();
        vo.setSessionId(session.getSessionId());
        vo.setEventId(session.getEventId());
        vo.setName(session.getName());
        vo.setStartTime(session.getStartTime());
        vo.setEndTime(session.getEndTime());
        vo.setLocation(session.getLocation());
        vo.setCapacity(session.getCapacity());
        vo.setRegistrationDeadline(session.getRegistrationDeadline());
        vo.setCheckInOpenAt(session.getCheckInOpenAt());
        vo.setCheckInCloseAt(session.getCheckInCloseAt());
        vo.setSortOrder(session.getSortOrder());
        vo.setStatus(session.getStatus());
        int confirmed = registrationMapper.countConfirmedBySession(tenantId, eventId, session.getSessionId());
        int waitlist = registrationMapper.countWaitlistBySession(tenantId, eventId, session.getSessionId());
        int checkedIn = checkInMapper.countDistinctBySession(tenantId, eventId, session.getSessionId());
        vo.setConfirmedCount(confirmed);
        vo.setWaitlistCount(waitlist);
        vo.setCheckedInCount(checkedIn);
        vo.setFull(session.getCapacity() != null && confirmed >= session.getCapacity());
        return vo;
    }

    private EventRegistrationSettingsVo toSettingsVo(CrmEvent event)
    {
        EventRegistrationSettingsVo vo = new EventRegistrationSettingsVo();
        vo.setEventId(event.getEventId());
        vo.setRegistrationEnabled(event.getRegistrationEnabled());
        vo.setRegistrationDeadline(event.getRegistrationDeadline());
        vo.setWaitlistEnabled(event.getWaitlistEnabled());
        vo.setCheckInEnabled(event.getCheckInEnabled());
        vo.setAllowWalkIn(event.getAllowWalkIn());
        vo.setDefaultSessionId(event.getDefaultSessionId());
        return vo;
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

    private CrmEventSession requireSession(String tenantId, String sessionId)
    {
        CrmEventSession session = sessionMapper.selectBySessionId(tenantId, sessionId);
        if (session == null || "deleted".equals(session.getStatus()))
        {
            throw new ServiceException("场次不存在", HttpStatus.NOT_FOUND);
        }
        return session;
    }
}
