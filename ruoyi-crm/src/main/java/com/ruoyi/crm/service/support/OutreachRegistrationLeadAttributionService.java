package com.ruoyi.crm.service.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmOutreachlead;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.ICrmLeadService;
import com.ruoyi.crm.service.nurture.support.EventNurtureHookSupport;

/**
 * 活动 H5 报名：token openid 可信落库与 upsert
 */
@Service
public class OutreachRegistrationLeadAttributionService
{
    private static final Logger log = LoggerFactory.getLogger(OutreachRegistrationLeadAttributionService.class);

    @Autowired
    private ICrmLeadService crmLeadService;

    @Autowired
    private OutreachEventLeadResolveService outreachEventLeadResolveService;

    @Autowired
    private ICrmCustomerIdentityService customerIdentityService;

    @Autowired
    private EventNurtureHookSupport nurtureHookSupport;

    @Autowired
    private ActivityEventProducer activityEventProducer;

    /**
     * 验签与业务校验完成后调用；openid 仅来自 token，body 中 openid 字段会被忽略。
     */
    public int submitRegistration(CrmOutreachlead submission, CrmEvent event,
            String tokenOpenId, String tokenAppId)
    {
        warnAndStripBodyOpenId(submission);

        if (StringUtils.isNotBlank(tokenOpenId) != StringUtils.isNotBlank(tokenAppId))
        {
            throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
        }

        CrmLead existing = outreachEventLeadResolveService.resolveExistingLead(
                event.getEventId(), submission.getMobilePhone(), tokenOpenId, tokenAppId);

        if (existing == null)
        {
            submission.setStatus("Initial");
            submission.setLeadId(UUID.randomUUID().toString());
            submission.setCreateTime(DateUtils.getNowDate());
            applyTokenMpAttribution(submission, tokenOpenId, tokenAppId);
            int rows = crmLeadService.insertCrmLead(submission);
            if (rows > 0)
            {
                nurtureHookSupport.onH5RegistrationCreated(submission, event.getEventId());
                activityEventProducer.produceOutreachLeadCreated(submission.getLeadId(),
                        submission.getLeadName(), event.getEventId(), event.getSubject());
            }
            return rows;
        }

        if (!OutreachLeadResolveSupport.isInitialLeadStatus(existing.getStatus()))
        {
            throw new ServiceException("该报名已处理，请联系工作人员", HttpStatus.FORBIDDEN);
        }

        submission.setLeadId(existing.getLeadId());
        submission.setStatus(existing.getStatus());
        submission.setUpdateTime(DateUtils.getNowDate());
        MpLeadIdentitySupport.mergeCustomField(submission, existing.getCustomField());
        applyTokenMpAttribution(submission, tokenOpenId, tokenAppId);
        int rows = crmLeadService.updateCrmLead(submission);
        if (rows > 0)
        {
            nurtureHookSupport.onH5RegistrationCreated(submission, event.getEventId());
            activityEventProducer.produceLeadDuplicated(existing.getLeadId(), "mobilePhone", "outreach");
        }
        return rows;
    }

    private void applyTokenMpAttribution(CrmLead lead, String tokenOpenId, String tokenAppId)
    {
        if (StringUtils.isBlank(tokenOpenId) || StringUtils.isBlank(tokenAppId))
        {
            return;
        }
        MpLeadIdentitySupport.applyMpOpenIdColumns(lead, tokenOpenId, tokenAppId);
        MpLeadIdentitySupport.bindMpIdentity(customerIdentityService, lead, tokenOpenId, tokenAppId);
    }

    private void warnAndStripBodyOpenId(CrmOutreachlead submission)
    {
        if (submission == null)
        {
            return;
        }
        boolean stripped = false;
        if (StringUtils.isNotBlank(submission.getWechatMpOpenid()))
        {
            stripped = true;
            submission.setWechatMpOpenid(null);
        }
        if (StringUtils.isNotBlank(submission.getWechatMpAppId()))
        {
            stripped = true;
            submission.setWechatMpAppId(null);
        }
        if (stripped)
        {
            log.warn("Ignoring client-supplied wechat_mp openid fields on outreachlead; token is authoritative");
        }
    }
}
