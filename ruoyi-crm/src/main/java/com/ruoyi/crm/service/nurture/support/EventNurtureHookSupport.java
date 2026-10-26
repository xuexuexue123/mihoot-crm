package com.ruoyi.crm.service.nurture.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmConversionHistory;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.dto.EventNurtureAudienceSnapshot;
import com.ruoyi.crm.enums.EventNurtureTriggerType;
import com.ruoyi.crm.service.nurture.IEventNurtureEnrollmentService;
import com.ruoyi.crm.service.nurture.IEventNurtureSequenceService;
import com.ruoyi.crm.service.support.OutreachLeadResolveSupport;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.crm.wechatmp.automation.EventMpOutreachContext;

/**
 * 培育入组钩子统一入口
 */
@Component
public class EventNurtureHookSupport
{
    @Autowired
    private IEventNurtureEnrollmentService enrollmentService;

    @Autowired
    private IEventNurtureSequenceService sequenceService;

    @Autowired
    private EventNurtureAudienceResolver audienceResolver;

    public void onMpSubscribeAttributed(EventMpOutreachContext ctx)
    {
        if (ctx == null)
        {
            return;
        }
        enrollmentService.tryEnrollFromMpContext(ctx);
    }

    public boolean shouldSkipLegacyMpAutomation(String tenantId, String eventId)
    {
        return sequenceService.hasActiveMpSubscribeSequence(tenantId, eventId);
    }

    public void onH5RegistrationCreated(CrmLead lead, String eventId)
    {
        if (lead == null || StringUtils.isBlank(eventId) || StringUtils.isBlank(lead.getLeadId()))
        {
            return;
        }
        String tenantId = TenantDataSourceSupport.currentTenantKey();
        if (StringUtils.isBlank(tenantId))
        {
            return;
        }
        EventNurtureAudienceSnapshot snapshot = audienceResolver.resolveByLeadId(
                tenantId, eventId, lead.getLeadId());
        if (snapshot == null)
        {
            snapshot = new EventNurtureAudienceSnapshot();
            if (StringUtils.isNotBlank(lead.getWechatMpOpenid()))
            {
                snapshot.setAudienceKey("mp:" + lead.getWechatMpOpenid());
            }
            else if (StringUtils.isNotBlank(lead.getMobilePhone()))
            {
                snapshot.setAudienceKey("mobile:" + lead.getMobilePhone());
            }
            else
            {
                snapshot.setAudienceKey("h5:" + lead.getLeadId());
            }
            snapshot.setLeadId(lead.getLeadId());
            snapshot.setDisplayName(lead.getLeadName());
            snapshot.setMobilePhone(lead.getMobilePhone());
            snapshot.setWechatMpOpenid(lead.getWechatMpOpenid());
            snapshot.setWechatMpAppId(lead.getWechatMpAppId());
            snapshot.setAudienceStatus("registration_pending");
        }
        enrollmentService.tryEnroll(tenantId, eventId,
                EventNurtureTriggerType.H5_REGISTRATION_CREATED.getCode(), snapshot);
    }

    public void onLeadUpdated(CrmLead before, CrmLead after)
    {
        if (before == null || after == null || StringUtils.isBlank(after.getSid()))
        {
            return;
        }
        boolean wasInitial = OutreachLeadResolveSupport.isInitialLeadStatus(before.getStatus());
        boolean nowReleased = !OutreachLeadResolveSupport.isInitialLeadStatus(after.getStatus());
        if (!wasInitial || !nowReleased)
        {
            return;
        }
        String tenantId = TenantDataSourceSupport.currentTenantKey();
        if (StringUtils.isBlank(tenantId))
        {
            return;
        }
        enrollmentService.tryEnrollFromLeadRelease(tenantId, after.getSid(), after.getLeadId());
    }

    public void onConversionHistoryCreated(CrmConversionHistory history)
    {
        if (history == null || !"Marketing Event".equals(history.getConversionType()))
        {
            return;
        }
        if (StringUtils.isBlank(history.getLeadId()) || StringUtils.isBlank(history.getTargetId()))
        {
            return;
        }
        String tenantId = TenantDataSourceSupport.currentTenantKey();
        if (StringUtils.isBlank(tenantId))
        {
            return;
        }
        enrollmentService.tryEnrollFromLeadRelease(tenantId, history.getTargetId(), history.getLeadId());
    }
}
