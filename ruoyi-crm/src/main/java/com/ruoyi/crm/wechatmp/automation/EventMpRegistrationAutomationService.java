package com.ruoyi.crm.wechatmp.automation;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.mapper.CrmEventMapper;
import com.ruoyi.crm.service.support.OutreachEventLeadResolveService;
import com.ruoyi.crm.service.support.OutreachRegistrationTokenService;
import com.ruoyi.crm.wechatmp.domain.CrmEventMpOutreachLog;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessagePreset;
import com.ruoyi.crm.wechatmp.dto.WechatMpMessagePresetRenderRequest;
import com.ruoyi.crm.wechatmp.dto.WechatMpMessagePresetRenderResult;
import com.ruoyi.crm.wechatmp.dto.WechatMpMessageTemplateRequest;
import com.ruoyi.crm.wechatmp.service.EventWechatMpAppIdResolver;
import com.ruoyi.crm.wechatmp.service.IEventMpOutreachLogService;
import com.ruoyi.crm.wechatmp.service.IWechatMpMessagePresetService;
import com.ruoyi.crm.wechatmp.service.IWechatMpMessageService;

@Service
public class EventMpRegistrationAutomationService
{
    private static final Logger log = LoggerFactory.getLogger(EventMpRegistrationAutomationService.class);

    @Autowired
    private CrmEventMapper crmEventMapper;

    @Autowired
    private IWechatMpMessagePresetService presetService;

    @Autowired
    private IWechatMpMessageService messageService;

    @Autowired
    private OutreachRegistrationTokenService tokenService;

    @Autowired
    private IEventMpOutreachLogService outreachLogService;

    @Autowired
    private OutreachEventLeadResolveService leadResolveService;

    @Autowired
    private EventWechatMpAppIdResolver eventWechatMpAppIdResolver;

    public void processAttributedSubscribe(EventMpOutreachContext ctx)
    {
        if (ctx == null || !StringUtils.hasText(ctx.getCrmEventId())
                || !StringUtils.hasText(ctx.getOpenId()) || !StringUtils.hasText(ctx.getAppId()))
        {
            return;
        }

        String eventId = ctx.getCrmEventId();
        CrmEvent event = crmEventMapper.selectCrmEventByEventId(eventId);
        if (event == null)
        {
            return;
        }

        if (!"1".equals(event.getMpRegistrationAutomationEnabledRaw()))
        {
            recordSkip(ctx, null, "skipped", "automation_disabled");
            return;
        }

        String resolvedAppId = eventWechatMpAppIdResolver.resolve(event);
        if (StringUtils.hasText(resolvedAppId) && !resolvedAppId.equals(ctx.getAppId()))
        {
            recordSkip(ctx, event.getMpRegistrationPresetId(), "skipped", "app_id_mismatch");
            return;
        }

        CrmEventMpOutreachLog existing = outreachLogService.findByEventOpenId(
                eventId, ctx.getOpenId(), EventMpOutreachContext.TRIGGER_MP_SUBSCRIBE_ATTRIBUTED);
        if (existing != null && "sent".equals(existing.getStatus()))
        {
            return;
        }

        if ("1".equals(event.getMpRegistrationSkipIfLeadExistsRaw()))
        {
            CrmLead lead = leadResolveService.resolveLeadForEvent(eventId, ctx.getOpenId(), ctx.getAppId());
            if (lead != null)
            {
                recordSkip(ctx, event.getMpRegistrationPresetId(), "skipped", "lead_exists");
                return;
            }
        }

        Long presetId = event.getMpRegistrationPresetId();
        if (presetId == null)
        {
            recordSkip(ctx, null, "failed", "preset_invalid");
            return;
        }

        CrmWechatMpMessagePreset preset;
        try
        {
            preset = presetService.getById(presetId);
        }
        catch (Exception ex)
        {
            recordSkip(ctx, presetId, "failed", "preset_invalid");
            return;
        }

        if (!"0".equals(preset.getStatus()))
        {
            recordSkip(ctx, presetId, "skipped", "preset_invalid");
            return;
        }
        if (!ctx.getAppId().equals(preset.getAppId()))
        {
            recordSkip(ctx, presetId, "skipped", "preset_app_mismatch");
            return;
        }

        int delaySeconds = event.getMpRegistrationDelaySeconds() != null
                ? event.getMpRegistrationDelaySeconds() : 0;
        if (delaySeconds > 0)
        {
            try
            {
                Thread.sleep(delaySeconds * 1000L);
            }
            catch (InterruptedException ex)
            {
                Thread.currentThread().interrupt();
                return;
            }
        }

        long exp = tokenService.computeExp(event);
        String registrationUrl = tokenService.buildRegistrationUrl(
                ctx.getTenantId(), eventId, exp, ctx.getOpenId(), ctx.getAppId());
        if (!StringUtils.hasText(registrationUrl))
        {
            recordFailure(ctx, presetId, exp, "registration url not configured");
            return;
        }

        WechatMpMessagePresetRenderRequest renderRequest = new WechatMpMessagePresetRenderRequest();
        renderRequest.setEventId(eventId);
        renderRequest.setTenantId(ctx.getTenantId());
        renderRequest.setOpenId(ctx.getOpenId());
        renderRequest.setAppId(ctx.getAppId());
        Map<String, String> extra = new HashMap<>();
        extra.put("surveyUrl", registrationUrl);
        extra.put("registrationUrl", registrationUrl);
        renderRequest.setExtra(extra);

        WechatMpMessagePresetRenderResult rendered;
        try
        {
            rendered = presetService.render(presetId, renderRequest);
        }
        catch (Exception ex)
        {
            recordFailure(ctx, presetId, exp, ex.getMessage());
            return;
        }

        if (!StringUtils.hasText(rendered.getUrl()))
        {
            recordFailure(ctx, presetId, exp, "empty_url");
            return;
        }

        WechatMpMessageTemplateRequest sendReq = new WechatMpMessageTemplateRequest();
        sendReq.setAppId(ctx.getAppId());
        sendReq.setOpenId(ctx.getOpenId());
        sendReq.setTemplateId(rendered.getTemplateId());
        sendReq.setUrl(rendered.getUrl());
        sendReq.setData(rendered.getData());

        try
        {
            Long messageId = messageService.sendTemplate(sendReq);
            CrmEventMpOutreachLog row = baseLog(ctx, presetId, exp);
            row.setStatus("sent");
            row.setMessageId(messageId);
            outreachLogService.recordOutcome(row);
        }
        catch (Exception ex)
        {
            log.warn("MP registration automation send failed eventId={} openId={}: {}",
                    eventId, ctx.getOpenId(), ex.getMessage());
            recordFailure(ctx, presetId, exp, ex.getMessage());
        }
    }

    private void recordSkip(EventMpOutreachContext ctx, Long presetId, String status, String skipReason)
    {
        CrmEventMpOutreachLog row = baseLog(ctx, presetId, null);
        row.setStatus(status);
        row.setSkipReason(skipReason);
        outreachLogService.recordOutcome(row);
    }

    private void recordFailure(EventMpOutreachContext ctx, Long presetId, Long exp, String errmsg)
    {
        CrmEventMpOutreachLog row = baseLog(ctx, presetId, exp);
        row.setStatus("failed");
        row.setErrmsg(errmsg);
        outreachLogService.recordOutcome(row);
    }

    private CrmEventMpOutreachLog baseLog(EventMpOutreachContext ctx, Long presetId, Long exp)
    {
        CrmEventMpOutreachLog row = new CrmEventMpOutreachLog();
        row.setTenantId(ctx.getTenantId());
        row.setEventId(ctx.getCrmEventId());
        row.setAppId(ctx.getAppId());
        row.setOpenId(ctx.getOpenId());
        row.setTriggerType(EventMpOutreachContext.TRIGGER_MP_SUBSCRIBE_ATTRIBUTED);
        row.setPresetId(presetId);
        row.setRegistrationTokenExp(exp);
        return row;
    }
}
