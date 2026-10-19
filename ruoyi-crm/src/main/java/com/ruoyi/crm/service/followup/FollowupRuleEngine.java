package com.ruoyi.crm.service.followup;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmFollowupRule;
import com.ruoyi.crm.domain.CrmFollowupRuleEnrollment;
import com.ruoyi.crm.domain.dto.FollowupRuleDispatchEvent;
import com.ruoyi.crm.mapper.CrmFollowupRuleEnrollmentMapper;
import com.ruoyi.crm.mapper.CrmFollowupRuleMapper;
import com.ruoyi.crm.service.followup.FollowupRuleTaskFactory.FollowupRuleSkipException;

/**
 * 匹配规则 → 去重 → 延迟入队或立即建任务
 */
@Service
public class FollowupRuleEngine
{
    private static final Logger log = LoggerFactory.getLogger(FollowupRuleEngine.class);

    @Autowired
    private CrmFollowupRuleMapper ruleMapper;

    @Autowired
    private CrmFollowupRuleEnrollmentMapper enrollmentMapper;

    @Autowired
    private FollowupRuleTaskFactory taskFactory;

    @Autowired
    private FollowupRuleSystemSeeder systemSeeder;

    public DispatchResult dispatch(FollowupRuleDispatchEvent event)
    {
        DispatchResult result = new DispatchResult();
        if (event == null || StringUtils.isBlank(event.getTenant()) || StringUtils.isBlank(event.getTriggerType()))
        {
            return result;
        }
        try
        {
            systemSeeder.ensure(event.getTenant());
        }
        catch (Exception ex)
        {
            log.debug("followup rule seeder skipped: {}", ex.getMessage());
        }
        List<CrmFollowupRule> rules = ruleMapper.selectEnabledByTrigger(event.getTenant(), event.getTriggerType());
        if (rules == null || rules.isEmpty())
        {
            return result;
        }
        result.matched = rules.size();
        for (CrmFollowupRule rule : rules)
        {
            try
            {
                processRule(event, rule, result);
            }
            catch (Exception ex)
            {
                log.warn("followup rule {} failed: {}", rule.getRuleId(), ex.getMessage());
            }
        }
        return result;
    }

    public void processDueEnrollment(CrmFollowupRuleEnrollment enrollment)
    {
        if (enrollment == null)
        {
            return;
        }
        CrmFollowupRule rule = ruleMapper.selectEntityById(enrollment.getTenantId(), enrollment.getRuleId());
        if (rule == null || !FollowupRuleConstants.ENABLED.equals(rule.getEnabled()))
        {
            enrollmentMapper.markSkipped(enrollment.getEnrollmentId(), FollowupRuleConstants.SKIP_RULE_DISABLED);
            return;
        }
        fireEnrollment(rule, enrollment);
    }

    private void processRule(FollowupRuleDispatchEvent event, CrmFollowupRule rule, DispatchResult result)
    {
        if (!matchesTriggerConfig(rule, event))
        {
            result.skipped++;
            return;
        }
        SubjectRef subject = resolveSubject(event);
        if (subject == null || StringUtils.isBlank(subject.id))
        {
            result.skipped++;
            return;
        }
        String idempotencyKey = FollowupRuleIdempotency.compute(event, rule.getRuleId(), subject.type, subject.id);
        if (enrollmentMapper.selectByIdempotency(event.getTenant(), idempotencyKey) != null)
        {
            result.skipped++;
            return;
        }
        String dedupeSkip = applyDedupe(event.getTenant(), rule, subject);
        if (FollowupRuleConstants.DEDUPE_REPLACE_PENDING.equals(dedupeSkip))
        {
            Instant nextRun = FollowupRuleDelayCalculator.calculate(rule, event);
            CrmFollowupRuleEnrollment pending = enrollmentMapper.selectPendingBySubjectAndRule(
                    event.getTenant(), rule.getRuleId(), subject.type, subject.id);
            if (pending != null)
            {
                enrollmentMapper.updateNextRunAt(pending.getEnrollmentId(), FollowupRuleDelayCalculator.toDate(nextRun));
                result.enrolled++;
                return;
            }
        }
        else if (dedupeSkip != null)
        {
            result.skipped++;
            return;
        }

        Instant nextRun = FollowupRuleDelayCalculator.calculate(rule, event);
        Date now = DateUtils.getNowDate();
        CrmFollowupRuleEnrollment enrollment = new CrmFollowupRuleEnrollment();
        enrollment.setEnrollmentId(IdUtils.fastUUID());
        enrollment.setTenantId(event.getTenant());
        enrollment.setRuleId(rule.getRuleId());
        enrollment.setTriggerType(event.getTriggerType());
        enrollment.setSubjectType(subject.type);
        enrollment.setSubjectId(subject.id);
        enrollment.setStatus(FollowupRuleConstants.STATUS_PENDING);
        enrollment.setNextRunAt(FollowupRuleDelayCalculator.toDate(nextRun));
        enrollment.setIdempotencyKey(idempotencyKey);
        enrollment.setTriggerPayload(event.getPayload() == null ? null : event.getPayload().toJSONString());
        enrollment.setRetryCount(0);
        enrollment.setCreateTime(now);
        enrollment.setUpdateTime(now);
        int inserted = enrollmentMapper.insertIgnoreDuplicate(enrollment);
        if (inserted == 0)
        {
            result.skipped++;
            return;
        }
        if (!nextRun.isAfter(now.toInstant()))
        {
            fireEnrollment(rule, enrollment);
            if (FollowupRuleConstants.STATUS_FIRED.equals(enrollment.getStatus()))
            {
                result.fired++;
            }
            else
            {
                result.skipped++;
            }
        }
        else
        {
            result.enrolled++;
        }
    }

    private void fireEnrollment(CrmFollowupRule rule, CrmFollowupRuleEnrollment enrollment)
    {
        try
        {
            String taskId = taskFactory.createTask(rule, enrollment);
            enrollmentMapper.markFired(enrollment.getEnrollmentId(), taskId);
            enrollment.setStatus(FollowupRuleConstants.STATUS_FIRED);
            enrollment.setTaskId(taskId);
        }
        catch (FollowupRuleSkipException skip)
        {
            enrollmentMapper.markSkipped(enrollment.getEnrollmentId(), skip.getSkipReason());
            enrollment.setStatus(FollowupRuleConstants.STATUS_SKIPPED);
            enrollment.setSkipReason(skip.getSkipReason());
        }
        catch (Exception ex)
        {
            log.warn("followup fire failed enrollment={}: {}", enrollment.getEnrollmentId(), ex.getMessage());
            enrollmentMapper.incrementRetry(enrollment.getEnrollmentId());
            Integer retry = enrollment.getRetryCount() == null ? 0 : enrollment.getRetryCount();
            if (retry + 1 >= 3)
            {
                enrollmentMapper.markSkipped(enrollment.getEnrollmentId(), FollowupRuleConstants.SKIP_RETRY_EXHAUSTED);
                enrollment.setStatus(FollowupRuleConstants.STATUS_SKIPPED);
            }
        }
    }

    boolean matchesTriggerConfig(CrmFollowupRule rule, FollowupRuleDispatchEvent event)
    {
        if (StringUtils.isBlank(rule.getTriggerConfig()))
        {
            return true;
        }
        try
        {
            JSONObject config = JSON.parseObject(rule.getTriggerConfig());
            if (config == null || config.isEmpty())
            {
                return true;
            }
            JSONObject payload = event.getPayload();
            for (String key : config.keySet())
            {
                Object expected = config.get(key);
                Object actual = FollowupRuleTemplateRenderer.lookup(payload, key);
                if (expected != null && (actual == null || !String.valueOf(expected).equals(String.valueOf(actual))))
                {
                    return false;
                }
            }
            return true;
        }
        catch (Exception ex)
        {
            return true;
        }
    }

    String applyDedupe(String tenantId, CrmFollowupRule rule, SubjectRef subject)
    {
        JSONObject config = StringUtils.isBlank(rule.getDedupeConfig())
                ? new JSONObject()
                : JSON.parseObject(rule.getDedupeConfig());
        String strategy = config == null ? FollowupRuleConstants.DEDUPE_ONCE_PER_RECORD
                : config.getString("strategy");
        if (StringUtils.isBlank(strategy))
        {
            strategy = FollowupRuleConstants.DEDUPE_ONCE_PER_RECORD;
        }
        if (FollowupRuleConstants.DEDUPE_NONE.equals(strategy))
        {
            return null;
        }
        if (FollowupRuleConstants.DEDUPE_REPLACE_PENDING.equals(strategy))
        {
            return FollowupRuleConstants.DEDUPE_REPLACE_PENDING;
        }
        if (FollowupRuleConstants.DEDUPE_WINDOW_DAYS.equals(strategy))
        {
            int days = config.getIntValue("windowDays", 7);
            Date since = Date.from(Instant.now().minus(days, ChronoUnit.DAYS));
            if (enrollmentMapper.countFiredSince(tenantId, rule.getRuleId(), subject.type, subject.id, since) > 0)
            {
                return FollowupRuleConstants.SKIP_DUPLICATE;
            }
            return null;
        }
        if (enrollmentMapper.countFiredBySubject(tenantId, rule.getRuleId(), subject.type, subject.id) > 0)
        {
            return FollowupRuleConstants.SKIP_DUPLICATE;
        }
        return null;
    }

    static SubjectRef resolveSubject(FollowupRuleDispatchEvent event)
    {
        JSONObject payload = event.getPayload();
        String trigger = event.getTriggerType();
        if (FollowupRuleConstants.TRIGGER_OPPORTUNITY_WON.equals(trigger))
        {
            return new SubjectRef(FollowupRuleConstants.SUBJECT_OPPORTUNITY,
                    FollowupRuleIdempotency.nestedString(payload, "opportunity", "optyId"));
        }
        if (FollowupRuleConstants.TRIGGER_LEAD_ASSIGNED.equals(trigger))
        {
            return new SubjectRef(FollowupRuleConstants.SUBJECT_LEAD,
                    FollowupRuleIdempotency.nestedString(payload, "lead", "leadId"));
        }
        if (FollowupRuleConstants.TRIGGER_PATIENT_RECALL_DUE.equals(trigger))
        {
            String contactId = FollowupRuleIdempotency.nestedString(payload, "contact", "contactId");
            if (StringUtils.isBlank(contactId))
            {
                contactId = FollowupRuleIdempotency.nestedString(payload, "patientValue", "contactId");
            }
            return new SubjectRef(FollowupRuleConstants.SUBJECT_CONTACT, contactId);
        }
        if (FollowupRuleConstants.TRIGGER_APPOINTMENT_NOSHOW.equals(trigger))
        {
            String leadId = FollowupRuleIdempotency.nestedString(payload, "lead", "leadId");
            if (StringUtils.isNotBlank(leadId))
            {
                return new SubjectRef(FollowupRuleConstants.SUBJECT_LEAD, leadId);
            }
            return new SubjectRef(FollowupRuleConstants.SUBJECT_CONTACT,
                    FollowupRuleIdempotency.nestedString(payload, "contact", "contactId"));
        }
        return null;
    }

    static final class SubjectRef
    {
        final String type;
        final String id;

        SubjectRef(String type, String id)
        {
            this.type = type;
            this.id = id;
        }
    }

    public static class DispatchResult
    {
        public int matched;
        public int enrolled;
        public int fired;
        public int skipped;
        public List<String> results = new ArrayList<>();
    }
}
