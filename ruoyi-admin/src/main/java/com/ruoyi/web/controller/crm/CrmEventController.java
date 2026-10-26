package com.ruoyi.web.controller.crm;

import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.domain.CrmEventSpeaker;
import com.ruoyi.crm.domain.vo.EventRegistrationUrlVo;
import com.ruoyi.crm.service.ICrmEventService;
import com.ruoyi.crm.service.ICrmEventSpeakerService;
import com.ruoyi.crm.service.IEventSessionService;
import com.ruoyi.crm.service.support.OutreachRegistrationTokenService;
import com.ruoyi.crm.wechatmp.domain.CrmEventMpOutreachLog;
import com.ruoyi.crm.wechatmp.service.IEventMpOutreachLogService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.utils.StringUtils;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * 业务事件Controller
 * 
 * @author luohd
 * @date 2025-03-15
 */
@RestController
@RequestMapping("/crm/event")
public class CrmEventController extends BaseController
{
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ICrmEventService crmEventService;

    @Autowired
    private OutreachRegistrationTokenService outreachRegistrationTokenService;

    @Autowired
    private IEventMpOutreachLogService eventMpOutreachLogService;

    @Autowired
    private IEventSessionService eventSessionService;

    @Autowired
    private ICrmEventSpeakerService speakerService;

    private static final DateTimeFormatter ISO_UTC = DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);

    /**
     * 查询业务事件列表
     */
    @PreAuthorize("@ss.hasPermi('crm:event:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmEvent crmEvent)
    {
        startPage();
        List<CrmEvent> list = crmEventService.selectCrmEventList(crmEvent);
        return getDataTable(list);
    }

    /**
     * 导出业务事件列表
     */
    @PreAuthorize("@ss.hasPermi('crm:event:export')")
    @Log(title = "业务事件", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmEvent crmEvent)
    {
        List<CrmEvent> list = crmEventService.selectCrmEventList(crmEvent);
        ExcelUtil<CrmEvent> util = new ExcelUtil<CrmEvent>(CrmEvent.class);
        util.exportExcel(response, list, "业务事件数据");
    }

    @PreAuthorize("@ss.hasPermi('crm:event:query')")
    @GetMapping(value = "/{eventId}/registration-url")
    public AjaxResult getRegistrationUrl(@PathVariable("eventId") String eventId)
    {
        if (!outreachRegistrationTokenService.isSigningConfigured())
        {
            return AjaxResult.error(HttpStatus.ERROR, "signing secret not configured");
        }

        CrmEvent event = crmEventService.selectCrmEventByEventId(eventId);
        if (event == null)
        {
            return AjaxResult.error(HttpStatus.NOT_FOUND, "Event not found");
        }

        String tenantId = getLoginUser().getTenant();
        long exp = outreachRegistrationTokenService.computeExp(event);
        String registrationUrl = outreachRegistrationTokenService.buildRegistrationUrl(tenantId, eventId, exp);
        if (StringUtils.isBlank(registrationUrl))
        {
            return AjaxResult.error(HttpStatus.ERROR, "public app url not configured");
        }

        EventRegistrationUrlVo vo = new EventRegistrationUrlVo();
        vo.setRegistrationUrl(registrationUrl);
        vo.setExpiresAt(ISO_UTC.format(Instant.ofEpochSecond(exp)));
        return success(vo);
    }

    @PreAuthorize("@ss.hasPermi('crm:event:query')")
    @GetMapping(value = "/{eventId}/mp-outreach/log")
    public TableDataInfo listMpOutreachLog(@PathVariable("eventId") String eventId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "openId", required = false) String openId)
    {
        if (crmEventService.selectCrmEventByEventId(eventId) == null)
        {
            return getDataTable(Collections.emptyList());
        }
        CrmEventMpOutreachLog query = new CrmEventMpOutreachLog();
        query.setEventId(eventId);
        query.setStatusFilter(status);
        query.setOpenId(openId);
        startPage();
        List<CrmEventMpOutreachLog> list = eventMpOutreachLogService.selectList(query);
        return getDataTable(list);
    }

    /**
     * 获取业务事件详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:event:query')")
    @GetMapping(value = "/{eventId}")
    public AjaxResult getInfo(@PathVariable("eventId") String eventId)
    {
        CrmEvent event = crmEventService.selectCrmEventByEventId(eventId);
        if (event == null)
        {
            return AjaxResult.error(HttpStatus.NOT_FOUND, "Event not found");
        }
        AjaxResult result = success(event);
        result.put("speakers", speakerService.selectSpeakerByEventId(eventId));
        return result;
    }

    /**
     * 新增业务事件
     */
    @PreAuthorize("@ss.hasPermi('crm:event:add')")
    @Log(title = "业务事件", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JsonNode body)
    {
        CrmEvent crmEvent = objectMapper.convertValue(body, CrmEvent.class);
        applyRegistrationFormTouched(body, crmEvent);
        applyMpRegistrationFields(body, crmEvent);
        applyWechatMpAppIdFields(body, crmEvent);
        List<CrmEventSpeaker> speakers = extractSpeakers(body);
        int rows = crmEventService.insertCrmEvent(crmEvent);
        if (rows > 0 && crmEvent.getEventId() != null)
        {
            String tenantId = getLoginUser().getTenant();
            eventSessionService.ensureDefaultSession(tenantId, crmEvent.getEventId(), getUsername());
            speakerService.replaceSpeakers(crmEvent.getEventId(), speakers);
        }
        return toAjax(rows);
    }

    /**
     * 修改业务事件
     */
    @PreAuthorize("@ss.hasPermi('crm:event:edit')")
    @Log(title = "业务事件", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JsonNode body)
    {
        CrmEvent crmEvent = objectMapper.convertValue(body, CrmEvent.class);
        applyRegistrationFormTouched(body, crmEvent);
        applyMpRegistrationFields(body, crmEvent);
        applyWechatMpAppIdFields(body, crmEvent);
        List<CrmEventSpeaker> speakers = extractSpeakers(body);
        int rows = crmEventService.updateCrmEvent(crmEvent);
        if (crmEvent.getEventId() != null && speakers != null)
        {
            speakerService.replaceSpeakers(crmEvent.getEventId(), speakers);
        }
        return toAjax(rows);
    }

    private static void applyRegistrationFormTouched(JsonNode body, CrmEvent crmEvent)
    {
        if (body != null && (body.has("registrationTemplateId") || body.has("registrationFormJson")))
        {
            crmEvent.setRegistrationFormTouched(true);
        }
    }

    /** 从 JSON 显式绑定 mp 自动化字段（含 delaySeconds=0） */
    private static void applyMpRegistrationFields(JsonNode body, CrmEvent crmEvent)
    {
        if (body == null || crmEvent == null)
        {
            return;
        }
        if (body.has("mpRegistrationDelaySeconds") && !body.get("mpRegistrationDelaySeconds").isNull())
        {
            crmEvent.setMpRegistrationDelaySeconds(body.get("mpRegistrationDelaySeconds").asInt(0));
        }
    }

    /** 从 JSON 显式绑定 wechatMpAppId（含清空为 null） */
    private static void applyWechatMpAppIdFields(JsonNode body, CrmEvent crmEvent)
    {
        if (body == null || crmEvent == null)
        {
            return;
        }
        if (!body.has("wechatMpAppId") && !body.has("wechat_mp_app_id"))
        {
            return;
        }
        crmEvent.setWechatMpAppIdTouched(true);
        JsonNode node = body.has("wechatMpAppId") ? body.get("wechatMpAppId") : body.get("wechat_mp_app_id");
        if (node == null || node.isNull())
        {
            crmEvent.setWechatMpAppId(null);
            return;
        }
        String value = node.asText(null);
        if (value == null || value.trim().isEmpty())
        {
            crmEvent.setWechatMpAppId(null);
        }
        else
        {
            crmEvent.setWechatMpAppId(value.trim());
        }
    }

    /** 从 JSON 请求体提取演讲者列表 */
    private static List<CrmEventSpeaker> extractSpeakers(JsonNode body)
    {
        if (body == null || !body.has("speakers") || body.get("speakers").isNull())
        {
            return null;
        }
        try
        {
            return objectMapper.readValue(
                body.get("speakers").traverse(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, CrmEventSpeaker.class));
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * 删除业务事件
     */
    @PreAuthorize("@ss.hasPermi('crm:event:remove')")
    @Log(title = "业务事件", businessType = BusinessType.DELETE)
	@DeleteMapping("/{eventIds}")
    public AjaxResult remove(@PathVariable String[] eventIds)
    {
        return toAjax(crmEventService.deleteCrmEventByEventIds(eventIds));
    }
}
