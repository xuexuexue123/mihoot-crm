package com.ruoyi.crm.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.domain.CrmEventSpeaker;
import com.ruoyi.crm.domain.dto.PublicWebsiteEventRegisterRequest;
import com.ruoyi.crm.domain.vo.EventRegistrationSettingsVo;
import com.ruoyi.crm.domain.vo.EventSessionVo;
import com.ruoyi.crm.domain.vo.OutreachRegistrationSubmitResultVo;
import com.ruoyi.crm.domain.vo.PublicWebsiteEventCardVo;
import com.ruoyi.crm.domain.vo.PublicWebsiteEventDetailVo;
import com.ruoyi.crm.domain.vo.PublicWebsiteEventListVo;
import com.ruoyi.crm.domain.vo.PublicWebsiteEventRegistrationSettingsVo;
import com.ruoyi.crm.domain.vo.PublicWebsiteEventSessionVo;
import com.ruoyi.crm.domain.vo.PublicWebsiteEventSpeakerVo;
import com.ruoyi.crm.mapper.CrmEventMapper;
import com.ruoyi.crm.service.ICrmEventSpeakerService;
import com.ruoyi.crm.service.ICrmPublicTenantWebsiteEventService;
import com.ruoyi.crm.service.IEventRegistrationService;
import com.ruoyi.crm.service.IEventSessionService;
import com.ruoyi.crm.service.support.EventRegistrationFormSupport;
import com.ruoyi.crm.service.support.LeadDefaults;
import com.ruoyi.crm.service.support.OutreachLeadValidationSupport;
import com.ruoyi.crm.service.support.PublicTenantSiteResolver;
import com.ruoyi.crm.service.support.WaitResumeTenantBootstrap;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.tenant.domain.vo.PublicTenantSiteVo;

@Service
public class CrmPublicTenantWebsiteEventServiceImpl implements ICrmPublicTenantWebsiteEventService
{
    private static final String EXECUTION_STAGE = "Execution";
    private static final int DEFAULT_LIMIT = 50;
    private static final int MAX_LIMIT = 100;
    private static final int SHORT_DESCRIPTION_MAX = 120;

    @Autowired
    private PublicTenantSiteResolver publicTenantSiteResolver;

    @Autowired
    private WaitResumeTenantBootstrap waitResumeTenantBootstrap;

    @Autowired
    private CrmEventMapper crmEventMapper;

    @Autowired
    private ICrmEventSpeakerService speakerService;

    @Autowired
    private IEventSessionService eventSessionService;

    @Autowired
    private IEventRegistrationService eventRegistrationService;

    @Override
    public PublicWebsiteEventListVo listEvents(String slug, Integer limit)
    {
        PublicTenantSiteVo site = publicTenantSiteResolver.resolveActiveSite(slug);
        int effectiveLimit = normalizeLimit(limit);
        try
        {
            waitResumeTenantBootstrap.bindTenantDataSource(site.getTenantName());
            List<CrmEvent> events = crmEventMapper.selectPublicExecutionEvents(effectiveLimit);
            PublicWebsiteEventListVo vo = new PublicWebsiteEventListVo();
            if (events == null || events.isEmpty())
            {
                vo.setItems(Collections.emptyList());
                return vo;
            }
            List<PublicWebsiteEventCardVo> items = new ArrayList<>(events.size());
            for (CrmEvent event : events)
            {
                items.add(toCardVo(event));
            }
            vo.setItems(items);
            return vo;
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    @Override
    public PublicWebsiteEventDetailVo getEvent(String slug, String eventId)
    {
        PublicTenantSiteVo site = publicTenantSiteResolver.resolveActiveSite(slug);
        try
        {
            waitResumeTenantBootstrap.bindTenantDataSource(site.getTenantName());
            CrmEvent event = requirePublicExecutionEvent(eventId);
            return toDetailVo(site.getTenantName(), event);
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    @Override
    public OutreachRegistrationSubmitResultVo register(String slug, String eventId,
            PublicWebsiteEventRegisterRequest request)
    {
        PublicTenantSiteVo site = publicTenantSiteResolver.resolveActiveSite(slug);
        try
        {
            waitResumeTenantBootstrap.bindTenantDataSource(site.getTenantName());
            CrmEvent event = requirePublicExecutionEvent(eventId);

            if (request == null)
            {
                throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
            }
            if (StringUtils.isBlank(request.getSessionId()))
            {
                throw new ServiceException("SESSION_REQUIRED", HttpStatus.BAD_REQUEST);
            }

            // 不信任客户端 leadId 作 PK
            request.setLeadId(null);
            request.setSid(eventId);
            request.setStage(LeadDefaults.DEFAULT_STAGE);
            if (StringUtils.isBlank(request.getLeadSource()))
            {
                request.setLeadSource(IEventRegistrationService.DEFAULT_WEBSITE_LEAD_SOURCE);
            }

            OutreachLeadValidationSupport.validateEventAcceptsRegistration(event);
            OutreachLeadValidationSupport.validateOutreachLeadSubmission(request, event);

            return eventRegistrationService.registerFromWebsite(
                    site.getTenantName(), request, event, request.getSessionId());
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    private CrmEvent requirePublicExecutionEvent(String eventId)
    {
        if (StringUtils.isBlank(eventId))
        {
            throw new ServiceException("Not found", HttpStatus.NOT_FOUND);
        }
        CrmEvent event = crmEventMapper.selectCrmEventByEventId(eventId);
        if (event == null || !EXECUTION_STAGE.equals(event.getStage()))
        {
            throw new ServiceException("Not found", HttpStatus.NOT_FOUND);
        }
        return event;
    }

    private PublicWebsiteEventDetailVo toDetailVo(String tenantId, CrmEvent event)
    {
        PublicWebsiteEventDetailVo vo = new PublicWebsiteEventDetailVo();
        vo.setEventId(event.getEventId());
        vo.setSubject(event.getSubject());
        vo.setAvatar(event.getAvatar());
        vo.setStartTime(event.getStartTime());
        vo.setEndTime(event.getEndTime());
        vo.setLocation(event.getLocation());
        vo.setDescription(event.getDescription());
        vo.setAgenda(event.getAgenda());
        vo.setSpeakers(toSpeakerVos(event.getEventId()));

        String formJson;
        if (StringUtils.isNotBlank(event.getRegistrationFormJson()))
        {
            if (!EventRegistrationFormSupport.isValidSurveyJson(event.getRegistrationFormJson()))
            {
                throw new ServiceException("registration form snapshot is corrupted", HttpStatus.ERROR);
            }
            formJson = event.getRegistrationFormJson();
        }
        else
        {
            formJson = EventRegistrationFormSupport.DEFAULT_REGISTRATION_FORM_JSON;
        }
        vo.setRegistrationFormJson(formJson);

        eventSessionService.ensureDefaultSession(tenantId, event.getEventId(), "system");
        EventRegistrationSettingsVo settings = eventSessionService.getRegistrationSettings(
                tenantId, event.getEventId());
        PublicWebsiteEventRegistrationSettingsVo settingsVo = new PublicWebsiteEventRegistrationSettingsVo();
        settingsVo.setRegistrationEnabled(settings.getRegistrationEnabled());
        settingsVo.setRegistrationDeadline(settings.getRegistrationDeadline());
        settingsVo.setWaitlistEnabled(settings.getWaitlistEnabled());
        vo.setRegistrationSettings(settingsVo);
        vo.setDefaultSessionId(settings.getDefaultSessionId());

        List<EventSessionVo> sessions = eventSessionService.listSessions(
                tenantId, event.getEventId(), "active");
        List<PublicWebsiteEventSessionVo> sessionVos = new ArrayList<>();
        if (sessions != null)
        {
            for (EventSessionVo s : sessions)
            {
                PublicWebsiteEventSessionVo brief = new PublicWebsiteEventSessionVo();
                brief.setSessionId(s.getSessionId());
                brief.setName(s.getName());
                brief.setStartTime(s.getStartTime());
                brief.setEndTime(s.getEndTime());
                brief.setLocation(s.getLocation());
                brief.setCapacity(s.getCapacity());
                brief.setRegistrationDeadline(s.getRegistrationDeadline());
                brief.setSortOrder(s.getSortOrder());
                brief.setStatus(s.getStatus());
                brief.setConfirmedCount(s.getConfirmedCount());
                brief.setWaitlistCount(s.getWaitlistCount());
                brief.setFull(s.isFull());
                sessionVos.add(brief);
            }
        }
        vo.setSessions(sessionVos);
        return vo;
    }

    private List<PublicWebsiteEventSpeakerVo> toSpeakerVos(String eventId)
    {
        List<CrmEventSpeaker> speakers = speakerService.selectSpeakerByEventId(eventId);
        if (speakers == null || speakers.isEmpty())
        {
            return Collections.emptyList();
        }
        List<PublicWebsiteEventSpeakerVo> vos = new ArrayList<>(speakers.size());
        List<CrmEventSpeaker> sorted = new ArrayList<>(speakers);
        sorted.sort(Comparator.comparing(CrmEventSpeaker::getSortOrder,
                Comparator.nullsLast(Integer::compareTo)));
        for (CrmEventSpeaker speaker : sorted)
        {
            PublicWebsiteEventSpeakerVo vo = new PublicWebsiteEventSpeakerVo();
            vo.setId(speaker.getId());
            vo.setName(speaker.getName());
            vo.setAvatar(speaker.getAvatar());
            vo.setCompany(speaker.getCompany());
            vo.setPosition(speaker.getPosition());
            vo.setDescription(speaker.getDescription());
            vo.setSortOrder(speaker.getSortOrder());
            vos.add(vo);
        }
        return vos;
    }

    private PublicWebsiteEventCardVo toCardVo(CrmEvent event)
    {
        PublicWebsiteEventCardVo vo = new PublicWebsiteEventCardVo();
        vo.setEventId(event.getEventId());
        vo.setSubject(event.getSubject());
        vo.setAvatar(event.getAvatar());
        vo.setStartTime(event.getStartTime());
        vo.setEndTime(event.getEndTime());
        vo.setLocation(event.getLocation());
        vo.setShortDescription(buildShortDescription(event.getDescription()));
        return vo;
    }

    static String buildShortDescription(String description)
    {
        if (StringUtils.isBlank(description))
        {
            return null;
        }
        String plain = description
                .replaceAll("(?s)```.*?```", " ")
                .replaceAll("!\\[[^\\]]*\\]\\([^)]*\\)", " ")
                .replaceAll("\\[[^\\]]*\\]\\([^)]*\\)", " ")
                .replaceAll("[#>*_`~\\-]+", " ")
                .replaceAll("\\s+", " ")
                .trim();
        if (plain.isEmpty())
        {
            return null;
        }
        if (plain.length() <= SHORT_DESCRIPTION_MAX)
        {
            return plain;
        }
        return plain.substring(0, SHORT_DESCRIPTION_MAX);
    }

    private int normalizeLimit(Integer limit)
    {
        if (limit == null || limit < 1)
        {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }
}
