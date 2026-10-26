package com.ruoyi.web.controller.crm;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.domain.CrmEventSession;
import com.ruoyi.crm.domain.dto.EventCheckInRequest;
import com.ruoyi.crm.domain.dto.EventRegistrationCreateRequest;
import com.ruoyi.crm.domain.dto.EventRegistrationQuery;
import com.ruoyi.crm.domain.dto.EventRegistrationSettingsSaveRequest;
import com.ruoyi.crm.domain.dto.EventSessionSaveRequest;
import com.ruoyi.crm.domain.dto.EventCheckInQuery;
import com.ruoyi.crm.domain.vo.EventCheckInLookupRowVo;
import com.ruoyi.crm.domain.vo.EventCheckInResultVo;
import com.ruoyi.crm.domain.vo.EventCheckInRowVo;
import com.ruoyi.crm.domain.vo.EventCheckInUrlVo;
import com.ruoyi.crm.domain.vo.EventRegistrationDetailVo;
import com.ruoyi.crm.domain.vo.EventRegistrationRowVo;
import com.ruoyi.crm.domain.vo.EventRegistrationSettingsVo;
import com.ruoyi.crm.domain.vo.EventSessionVo;
import com.ruoyi.crm.mapper.CrmEventSessionMapper;
import com.ruoyi.crm.service.ICrmEventService;
import com.ruoyi.crm.service.IEventCheckInService;
import com.ruoyi.crm.service.IEventRegistrationService;
import com.ruoyi.crm.service.IEventSessionService;
import com.ruoyi.crm.service.support.OutreachCheckInTokenService;

/**
 * 活动报名与现场运营
 */
@RestController
@RequestMapping("/crm/event")
public class CrmEventOnsiteController extends BaseController
{
    private static final DateTimeFormatter ISO_UTC = DateTimeFormatter.ISO_INSTANT;

    @Autowired
    private IEventSessionService eventSessionService;

    @Autowired
    private IEventRegistrationService eventRegistrationService;

    @Autowired
    private IEventCheckInService eventCheckInService;

    @Autowired
    private ICrmEventService crmEventService;

    @Autowired
    private CrmEventSessionMapper sessionMapper;

    @Autowired
    private OutreachCheckInTokenService outreachCheckInTokenService;

    @PreAuthorize("@ss.hasPermi('crm:event:query')")
    @GetMapping("/{eventId}/registration-settings")
    public AjaxResult getRegistrationSettings(@PathVariable("eventId") String eventId,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        EventRegistrationSettingsVo vo = eventSessionService.getRegistrationSettings(tenant, eventId);
        return success(vo);
    }

    @PreAuthorize("@ss.hasPermi('crm:event:edit')")
    @PutMapping("/{eventId}/registration-settings")
    public AjaxResult updateRegistrationSettings(@PathVariable("eventId") String eventId,
            @RequestBody(required = false) EventRegistrationSettingsSaveRequest request,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        EventRegistrationSettingsVo vo = eventSessionService.updateRegistrationSettings(
                tenant, eventId, request, SecurityUtils.getUsername());
        return success(vo);
    }

    @PreAuthorize("@ss.hasPermi('crm:event:query')")
    @GetMapping("/{eventId}/sessions")
    public AjaxResult listSessions(@PathVariable("eventId") String eventId,
            @RequestParam(value = "status", required = false) String status,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        List<EventSessionVo> rows = eventSessionService.listSessions(tenant, eventId, status);
        return success(rows);
    }

    @PreAuthorize("@ss.hasPermi('crm:event:edit')")
    @PostMapping("/{eventId}/sessions")
    public AjaxResult createSession(@PathVariable("eventId") String eventId,
            @RequestBody(required = false) EventSessionSaveRequest request,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (request == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        EventSessionVo vo = eventSessionService.createSession(tenant, eventId, request, SecurityUtils.getUsername());
        return success(vo);
    }

    @PreAuthorize("@ss.hasPermi('crm:event:edit')")
    @PutMapping("/{eventId}/sessions/{sessionId}")
    public AjaxResult updateSession(@PathVariable("eventId") String eventId,
            @PathVariable("sessionId") String sessionId,
            @RequestBody(required = false) EventSessionSaveRequest request,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        EventSessionVo vo = eventSessionService.updateSession(tenant, eventId, sessionId, request,
                SecurityUtils.getUsername());
        return success(vo);
    }

    @PreAuthorize("@ss.hasPermi('crm:event:edit')")
    @DeleteMapping("/{eventId}/sessions/{sessionId}")
    public AjaxResult deleteSession(@PathVariable("eventId") String eventId,
            @PathVariable("sessionId") String sessionId,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        eventSessionService.deleteSession(tenant, eventId, sessionId, SecurityUtils.getUsername());
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:event:query')")
    @GetMapping("/{eventId}/registrations")
    public TableDataInfo listRegistrations(@PathVariable("eventId") String eventId,
            EventRegistrationQuery query,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        startPage();
        List<EventRegistrationRowVo> rows = eventRegistrationService.listRegistrations(tenant, eventId, query);
        return getDataTable(rows);
    }

    @PreAuthorize("@ss.hasPermi('crm:event:query')")
    @GetMapping("/{eventId}/registrations/{registrationId}")
    public AjaxResult getRegistration(@PathVariable("eventId") String eventId,
            @PathVariable("registrationId") String registrationId,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        EventRegistrationDetailVo vo = eventRegistrationService.getRegistrationDetail(
                tenant, eventId, registrationId);
        return success(vo);
    }

    @PreAuthorize("@ss.hasPermi('crm:event:edit')")
    @PostMapping("/{eventId}/registrations")
    public AjaxResult createRegistration(@PathVariable("eventId") String eventId,
            @RequestBody(required = false) EventRegistrationCreateRequest request,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (request == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        EventRegistrationRowVo vo = eventRegistrationService.createManualRegistration(
                tenant, eventId, request, SecurityUtils.getUsername());
        return success(vo);
    }

    @PreAuthorize("@ss.hasPermi('crm:event:edit')")
    @PutMapping("/{eventId}/registrations/{registrationId}/cancel")
    public AjaxResult cancelRegistration(@PathVariable("eventId") String eventId,
            @PathVariable("registrationId") String registrationId,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        eventRegistrationService.cancelRegistration(tenant, eventId, registrationId, SecurityUtils.getUsername());
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:event:query')")
    @GetMapping("/{eventId}/check-in-url")
    public AjaxResult getCheckInUrl(@PathVariable("eventId") String eventId,
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (!outreachCheckInTokenService.isSigningConfigured())
        {
            return AjaxResult.error(HttpStatus.ERROR, "signing secret not configured");
        }
        if (StringUtils.isBlank(outreachCheckInTokenService.getPublicAppUrl()))
        {
            return AjaxResult.error(HttpStatus.ERROR, "public app url not configured");
        }

        CrmEvent event = crmEventService.selectCrmEventByEventId(eventId);
        if (event == null)
        {
            return AjaxResult.error(HttpStatus.NOT_FOUND, "Event not found");
        }

        String tenantId = getLoginUser().getTenant();
        if (StringUtils.isBlank(sessionId))
        {
            sessionId = event.getDefaultSessionId();
        }
        if (StringUtils.isBlank(sessionId))
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "SESSION_REQUIRED");
        }

        CrmEventSession session = sessionMapper.selectBySessionId(tenantId, sessionId);
        if (session == null || !eventId.equals(session.getEventId()) || "deleted".equals(session.getStatus()))
        {
            return AjaxResult.error(HttpStatus.NOT_FOUND, "Session not found");
        }

        long exp = outreachCheckInTokenService.computeExp(session, event);
        String checkInUrl = outreachCheckInTokenService.buildCheckInUrl(tenantId, eventId, sessionId, exp);

        EventCheckInUrlVo vo = new EventCheckInUrlVo();
        vo.setEventId(eventId);
        vo.setSessionId(sessionId);
        vo.setSessionName(session.getName());
        vo.setCheckInUrl(checkInUrl);
        vo.setExpiresAt(ISO_UTC.format(Instant.ofEpochSecond(exp)));
        return success(vo);
    }

    @PreAuthorize("@ss.hasPermi('crm:event:query')")
    @GetMapping("/{eventId}/check-in/summary")
    public AjaxResult checkInSummary(@PathVariable("eventId") String eventId,
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        return success(eventCheckInService.getSummary(tenant, eventId, sessionId));
    }

    @PreAuthorize("@ss.hasPermi('crm:event:query')")
    @GetMapping("/{eventId}/check-in")
    public TableDataInfo listCheckIns(@PathVariable("eventId") String eventId,
            EventCheckInQuery query,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        startPage();
        List<EventCheckInRowVo> rows = eventCheckInService.listCheckIns(tenant, eventId, query);
        return getDataTable(rows);
    }

    @PreAuthorize("@ss.hasPermi('crm:event:edit')")
    @GetMapping("/{eventId}/check-in/lookup")
    public AjaxResult checkInLookup(@PathVariable("eventId") String eventId,
            @RequestParam("sessionId") String sessionId,
            @RequestParam("keyword") String keyword,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        List<EventCheckInLookupRowVo> rows = eventCheckInService.lookup(tenant, eventId, sessionId, keyword);
        return success(rows);
    }

    @PreAuthorize("@ss.hasPermi('crm:event:edit')")
    @PostMapping("/{eventId}/check-in")
    public AjaxResult checkIn(@PathVariable("eventId") String eventId,
            @RequestBody(required = false) EventCheckInRequest request,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (request == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        EventCheckInResultVo vo = eventCheckInService.checkIn(tenant, eventId, request, SecurityUtils.getUsername());
        if (vo.isAlreadyCheckedIn())
        {
            return new AjaxResult(HttpStatus.CONFLICT, "ALREADY_CHECKED_IN", vo);
        }
        return success(vo);
    }
}
