package com.ruoyi.crm.wechatmp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.mapper.CrmEventMapper;
import com.ruoyi.crm.service.support.OutreachRegistrationTokenService;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.crm.wechatmp.domain.CrmEventMpOutreachLog;

/**
 * 活动受众 Tab 手动/批量发模板后的 outreach 日志
 */
@Service
public class EventMpAudienceOutreachService
{
    public static final String TRIGGER_AUDIENCE_MANUAL_TEMPLATE = "audience_manual_template";

    @Autowired
    private IEventMpOutreachLogService outreachLogService;

    @Autowired
    private OutreachRegistrationTokenService tokenService;

    @Autowired
    private CrmEventMapper crmEventMapper;

    public void recordSent(String tenantId, String eventId, String appId, String openId,
            Long presetId, Long messageId)
    {
        if (!StringUtils.hasText(eventId) || !StringUtils.hasText(openId) || !StringUtils.hasText(appId))
        {
            return;
        }
        String resolvedTenant = StringUtils.hasText(tenantId)
                ? tenantId
                : TenantDataSourceSupport.currentTenantKey();

        CrmEvent event = crmEventMapper.selectCrmEventByEventId(eventId);
        Long exp = event != null ? tokenService.computeExp(event) : null;

        CrmEventMpOutreachLog row = new CrmEventMpOutreachLog();
        row.setTenantId(resolvedTenant);
        row.setEventId(eventId);
        row.setAppId(appId);
        row.setOpenId(openId);
        row.setTriggerType(TRIGGER_AUDIENCE_MANUAL_TEMPLATE);
        row.setPresetId(presetId);
        row.setRegistrationTokenExp(exp);
        row.setMessageId(messageId);
        row.setStatus("sent");
        outreachLogService.recordOutcome(row);
    }
}
