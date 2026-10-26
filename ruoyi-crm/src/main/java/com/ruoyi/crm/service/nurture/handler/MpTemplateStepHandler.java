package com.ruoyi.crm.service.nurture.handler;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.domain.CrmEventNurtureEnrollment;
import com.ruoyi.crm.domain.CrmEventNurtureStep;
import com.ruoyi.crm.domain.dto.EventNurtureAudienceSnapshot;
import com.ruoyi.crm.mapper.CrmEventMapper;
import com.ruoyi.crm.mapper.EventNurtureStepExecutionMapper;
import com.ruoyi.crm.service.support.OutreachRegistrationTokenService;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessagePreset;
import com.ruoyi.crm.wechatmp.dto.WechatMpMessagePresetRenderRequest;
import com.ruoyi.crm.wechatmp.dto.WechatMpMessagePresetRenderResult;
import com.ruoyi.crm.wechatmp.dto.WechatMpMessageTemplateRequest;
import com.ruoyi.crm.wechatmp.service.EventWechatMpAppIdResolver;
import com.ruoyi.crm.wechatmp.service.IWechatMpMessagePresetService;
import com.ruoyi.crm.wechatmp.service.IWechatMpMessageService;
import com.ruoyi.crm.service.nurture.support.EventNurtureJsonSupport;
import com.ruoyi.crm.service.nurture.support.EventNurtureStepResult;
import com.ruoyi.crm.service.nurture.support.EventNurtureTemplateSupport;

@Component
public class MpTemplateStepHandler
{
    private static final int RATE_LIMIT_HOURS = 24;

    @Autowired
    private CrmEventMapper crmEventMapper;

    @Autowired
    private IWechatMpMessagePresetService presetService;

    @Autowired
    private IWechatMpMessageService messageService;

    @Autowired
    private OutreachRegistrationTokenService tokenService;

    @Autowired
    private EventNurtureStepExecutionMapper executionMapper;

    @Autowired
    private EventWechatMpAppIdResolver eventWechatMpAppIdResolver;

    public EventNurtureStepResult execute(String tenantId, CrmEventNurtureEnrollment enrollment,
            CrmEventNurtureStep step, EventNurtureAudienceSnapshot audience)
    {
        JSONObject action = EventNurtureJsonSupport.parseJsonObject(step.getActionJson());
        Long presetId = action.getLong("presetId");
        if (presetId == null)
        {
            return EventNurtureStepResult.failed("presetId required", false);
        }

        String openId = enrollment.getWechatMpOpenid();
        String appId = enrollment.getWechatMpAppId();
        boolean requireOpenId = action.getBooleanValue("requireOpenId", true);
        if (requireOpenId && StringUtils.isBlank(openId))
        {
            return EventNurtureStepResult.skipped("no_openid");
        }

        Calendar since = Calendar.getInstance();
        since.add(Calendar.HOUR_OF_DAY, -RATE_LIMIT_HOURS);
        if (StringUtils.isNotEmpty(openId)
                && executionMapper.countRecentSentByOpenIdAndPreset(openId, presetId, since.getTime()) > 0)
        {
            return EventNurtureStepResult.skipped("duplicate");
        }

        CrmWechatMpMessagePreset preset;
        try
        {
            preset = presetService.getById(presetId);
        }
        catch (Exception ex)
        {
            return EventNurtureStepResult.failed("preset_invalid", false);
        }
        if (preset == null || !"0".equals(preset.getStatus()))
        {
            return EventNurtureStepResult.skipped("preset_invalid");
        }
        if (StringUtils.isNotEmpty(appId) && !appId.equals(preset.getAppId()))
        {
            return EventNurtureStepResult.skipped("preset_app_mismatch");
        }
        if (StringUtils.isEmpty(appId))
        {
            appId = eventWechatMpAppIdResolver.resolve(enrollment.getEventId());
        }
        if (StringUtils.isEmpty(appId))
        {
            appId = preset.getAppId();
        }

        CrmEvent event = crmEventMapper.selectCrmEventByEventId(enrollment.getEventId());
        WechatMpMessagePresetRenderRequest renderRequest = new WechatMpMessagePresetRenderRequest();
        renderRequest.setEventId(enrollment.getEventId());
        renderRequest.setTenantId(tenantId);
        renderRequest.setOpenId(openId);
        renderRequest.setAppId(appId);
        Map<String, String> extra = new HashMap<>();
        Map<String, String> vars = EventNurtureTemplateSupport.buildVars(event, enrollment, audience);
        for (Map.Entry<String, String> entry : vars.entrySet())
        {
            extra.put(entry.getKey(), entry.getValue());
        }

        boolean urlUsesToken = action.getBooleanValue("urlUsesSignedRegistrationToken", true);
        if (urlUsesToken && StringUtils.isNotEmpty(openId) && StringUtils.isNotEmpty(appId))
        {
            long exp = tokenService.computeExp(event);
            String registrationUrl = tokenService.buildRegistrationUrl(
                    tenantId, enrollment.getEventId(), exp, openId, appId);
            if (StringUtils.isNotEmpty(registrationUrl))
            {
                extra.put("surveyUrl", registrationUrl);
                extra.put("registrationUrl", registrationUrl);
            }
        }
        renderRequest.setExtra(extra);

        WechatMpMessagePresetRenderResult rendered;
        try
        {
            rendered = presetService.render(presetId, renderRequest);
        }
        catch (Exception ex)
        {
            return EventNurtureStepResult.failed(ex.getMessage(), true);
        }

        if (!StringUtils.isNotEmpty(rendered.getUrl()))
        {
            return EventNurtureStepResult.failed("empty_url", false);
        }

        WechatMpMessageTemplateRequest sendReq = new WechatMpMessageTemplateRequest();
        sendReq.setAppId(appId);
        sendReq.setOpenId(openId);
        sendReq.setTemplateId(rendered.getTemplateId());
        sendReq.setUrl(rendered.getUrl());
        sendReq.setData(rendered.getData());

        try
        {
            Long messageId = messageService.sendTemplate(sendReq);
            return EventNurtureStepResult.sent("preset:" + presetId + ":msg:" + messageId);
        }
        catch (Exception ex)
        {
            return EventNurtureStepResult.failed(ex.getMessage(), true);
        }
    }
}
