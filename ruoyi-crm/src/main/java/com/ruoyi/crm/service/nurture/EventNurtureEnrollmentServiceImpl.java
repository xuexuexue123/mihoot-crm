package com.ruoyi.crm.service.nurture;

import java.util.Calendar;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmEventNurtureEnrollment;
import com.ruoyi.crm.domain.CrmEventNurtureSequence;
import com.ruoyi.crm.domain.CrmEventNurtureStep;
import com.ruoyi.crm.domain.CrmEventNurtureStepExecution;
import com.ruoyi.crm.domain.dto.EventNurtureAudienceSnapshot;
import com.ruoyi.crm.enums.EventNurtureEnrollmentStatus;
import com.ruoyi.crm.enums.EventNurtureTriggerType;
import com.ruoyi.crm.mapper.EventNurtureEnrollmentMapper;
import com.ruoyi.crm.mapper.EventNurtureSequenceMapper;
import com.ruoyi.crm.mapper.EventNurtureStepExecutionMapper;
import com.ruoyi.crm.mapper.EventNurtureStepMapper;
import com.ruoyi.crm.service.nurture.support.EventNurtureAudienceResolver;
import com.ruoyi.crm.service.nurture.support.EventNurtureStepResult;
import com.ruoyi.crm.wechatmp.automation.EventMpOutreachContext;

@Service
public class EventNurtureEnrollmentServiceImpl implements IEventNurtureEnrollmentService
{
    private static final Logger log = LoggerFactory.getLogger(EventNurtureEnrollmentServiceImpl.class);
    private static final int RETRY_BACKOFF_SECONDS = 300;

    @Autowired
    private EventNurtureSequenceMapper sequenceMapper;

    @Autowired
    private EventNurtureStepMapper stepMapper;

    @Autowired
    private EventNurtureEnrollmentMapper enrollmentMapper;

    @Autowired
    private EventNurtureStepExecutionMapper executionMapper;

    @Autowired
    private EventNurtureStepExecutor stepExecutor;

    @Autowired
    private EventNurtureAudienceResolver audienceResolver;

    @Override
    public void tryEnroll(String tenantId, String eventId, String triggerType,
            EventNurtureAudienceSnapshot audience)
    {
        if (StringUtils.isBlank(tenantId) || StringUtils.isBlank(eventId)
                || StringUtils.isBlank(triggerType) || audience == null
                || StringUtils.isBlank(audience.getAudienceKey()))
        {
            return;
        }
        CrmEventNurtureSequence seq = sequenceMapper.selectEnabledByEventAndTrigger(
                tenantId, eventId, triggerType);
        if (seq == null || !seq.isEnabledFlag())
        {
            return;
        }
        enrollIfAbsent(tenantId, seq, audience);
    }

    @Override
    public void tryEnrollFromMpContext(EventMpOutreachContext ctx)
    {
        if (ctx == null || StringUtils.isBlank(ctx.getTenantId())
                || StringUtils.isBlank(ctx.getCrmEventId()) || StringUtils.isBlank(ctx.getOpenId()))
        {
            return;
        }
        EventNurtureAudienceSnapshot snapshot = EventNurtureAudienceResolver.fromMpSubscribe(
                ctx.getOpenId(), ctx.getAppId());
        tryEnroll(ctx.getTenantId(), ctx.getCrmEventId(),
                EventNurtureTriggerType.MP_SUBSCRIBE_ATTRIBUTED.getCode(), snapshot);
    }

    @Override
    public void tryEnrollFromLeadRelease(String tenantId, String eventId, String leadId)
    {
        EventNurtureAudienceSnapshot snapshot = audienceResolver.resolveByLeadId(tenantId, eventId, leadId);
        if (snapshot == null)
        {
            snapshot = new EventNurtureAudienceSnapshot();
            snapshot.setAudienceKey("h5:" + leadId);
            snapshot.setLeadId(leadId);
        }
        tryEnroll(tenantId, eventId, EventNurtureTriggerType.LEAD_RELEASED.getCode(), snapshot);
    }

    @Override
    public boolean shouldExit(CrmEventNurtureSequence sequence, EventNurtureAudienceSnapshot audience)
    {
        if (sequence == null || !sequence.isExitOnContactFlag() || audience == null)
        {
            return false;
        }
        String status = audience.getAudienceStatus();
        return "converted_contact".equals(status) || "contact_known".equals(status);
    }

    @Override
    public void exitEnrollment(Long enrollmentId, String reason)
    {
        CrmEventNurtureEnrollment row = enrollmentMapper.selectById(enrollmentId);
        if (row == null || !EventNurtureEnrollmentStatus.ACTIVE.getCode().equals(row.getStatus()))
        {
            return;
        }
        row.setExitReason(reason);
        row.setExitedAt(DateUtils.getNowDate());
        enrollmentMapper.markExited(row);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processDueEnrollment(Long enrollmentId, String tenantId)
    {
        CrmEventNurtureEnrollment enrollment = enrollmentMapper.selectById(enrollmentId);
        if (enrollment == null
                || !EventNurtureEnrollmentStatus.ACTIVE.getCode().equals(enrollment.getStatus()))
        {
            return;
        }

        CrmEventNurtureSequence sequence = sequenceMapper.selectByEventId(
                tenantId, enrollment.getEventId());
        if (sequence == null || !sequence.isEnabledFlag())
        {
            exitEnrollment(enrollmentId, "sequence_disabled");
            return;
        }

        EventNurtureAudienceSnapshot audience = audienceResolver.resolve(
                tenantId, enrollment.getEventId(), enrollment.getAudienceKey());
        if (shouldExit(sequence, audience))
        {
            exitEnrollment(enrollmentId, "already_converted");
            return;
        }

        CrmEventNurtureStep step = stepMapper.selectBySequenceAndOrder(
                enrollment.getSequenceId(), enrollment.getCurrentStepOrder());
        if (step == null)
        {
            enrollmentMapper.markCompleted(enrollmentId);
            return;
        }

        CrmEventNurtureStepExecution existing = executionMapper.selectByEnrollmentAndStep(
                enrollmentId, step.getId());
        if (existing != null && "sent".equals(existing.getStatus()))
        {
            advanceEnrollment(enrollment, sequence);
            return;
        }
        if (existing != null && "skipped".equals(existing.getStatus()))
        {
            advanceEnrollment(enrollment, sequence);
            return;
        }

        EventNurtureStepResult result = stepExecutor.execute(tenantId, enrollment, step, audience);
        recordExecution(tenantId, enrollment, step, result);

        if (result.isSuccess() || result.isSkipped())
        {
            advanceEnrollment(enrollment, sequence);
        }
        else if (result.isRetryable())
        {
            reschedule(enrollment, RETRY_BACKOFF_SECONDS);
        }
    }

    private void enrollIfAbsent(String tenantId, CrmEventNurtureSequence seq,
            EventNurtureAudienceSnapshot audience)
    {
        CrmEventNurtureEnrollment existing = enrollmentMapper.selectBySequenceAndAudienceKey(
                tenantId, seq.getId(), audience.getAudienceKey());
        if (existing != null)
        {
            return;
        }

        CrmEventNurtureStep firstStep = stepMapper.selectBySequenceAndOrder(seq.getId(), 1);
        int initialDelay = firstStep != null && firstStep.getDelaySeconds() != null
                ? firstStep.getDelaySeconds() : 0;

        CrmEventNurtureEnrollment row = new CrmEventNurtureEnrollment();
        row.setTenantId(tenantId);
        row.setEventId(seq.getEventId());
        row.setSequenceId(seq.getId());
        row.setAudienceKey(audience.getAudienceKey());
        row.setLeadId(audience.getLeadId());
        row.setContactId(audience.getContactId());
        row.setWechatMpOpenid(audience.getWechatMpOpenid());
        row.setWechatMpAppId(audience.getWechatMpAppId());
        row.setWecomExternalUserid(audience.getWecomExternalUserid());
        row.setDisplayName(audience.getDisplayName());
        row.setMobilePhone(audience.getMobilePhone());
        row.setStatus(EventNurtureEnrollmentStatus.ACTIVE.getCode());
        row.setCurrentStepOrder(1);
        row.setNextRunAt(addSeconds(DateUtils.getNowDate(), initialDelay));
        row.setEnrolledAt(DateUtils.getNowDate());
        try
        {
            enrollmentMapper.insert(row);
        }
        catch (DuplicateKeyException ex)
        {
            log.debug("Nurture enrollment duplicate audienceKey={}", audience.getAudienceKey());
        }
    }

    private void advanceEnrollment(CrmEventNurtureEnrollment enrollment, CrmEventNurtureSequence sequence)
    {
        int nextOrder = enrollment.getCurrentStepOrder() + 1;
        CrmEventNurtureStep nextStep = stepMapper.selectBySequenceAndOrder(
                enrollment.getSequenceId(), nextOrder);
        if (nextStep == null)
        {
            enrollmentMapper.markCompleted(enrollment.getId());
            return;
        }
        int delay = nextStep.getDelaySeconds() != null ? nextStep.getDelaySeconds() : 0;
        enrollment.setCurrentStepOrder(nextOrder);
        enrollment.setNextRunAt(addSeconds(DateUtils.getNowDate(), delay));
        enrollmentMapper.updateProgress(enrollment);
    }

    private void reschedule(CrmEventNurtureEnrollment enrollment, int backoffSeconds)
    {
        enrollment.setNextRunAt(addSeconds(DateUtils.getNowDate(), backoffSeconds));
        enrollmentMapper.updateProgress(enrollment);
    }

    private void recordExecution(String tenantId, CrmEventNurtureEnrollment enrollment,
            CrmEventNurtureStep step, EventNurtureStepResult result)
    {
        CrmEventNurtureStepExecution existing = executionMapper.selectByEnrollmentAndStep(
                enrollment.getId(), step.getId());
        if (existing != null)
        {
            return;
        }
        CrmEventNurtureStepExecution row = new CrmEventNurtureStepExecution();
        row.setTenantId(tenantId);
        row.setEnrollmentId(enrollment.getId());
        row.setSequenceId(enrollment.getSequenceId());
        row.setStepId(step.getId());
        row.setStepOrder(step.getStepOrder());
        row.setActionType(step.getActionType());
        row.setStatus(result.getStatus());
        row.setSkipReason(result.getSkipReason());
        row.setExternalRef(result.getExternalRef());
        row.setErrorMsg(result.getErrorMsg());
        row.setCreateTime(DateUtils.getNowDate());
        try
        {
            executionMapper.insert(row);
        }
        catch (DuplicateKeyException ex)
        {
            log.debug("Duplicate step execution enrollment={} step={}", enrollment.getId(), step.getId());
        }
    }

    private static Date addSeconds(Date base, int seconds)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(base != null ? base : new Date());
        cal.add(Calendar.SECOND, seconds);
        return cal.getTime();
    }
}
