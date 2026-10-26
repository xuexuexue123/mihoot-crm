package com.ruoyi.crm.service.recall;

import java.time.Instant;
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
import com.ruoyi.crm.domain.CrmFollowupRuleEnrollment;
import com.ruoyi.crm.domain.CrmOpty;
import com.ruoyi.crm.domain.CrmRecallPolicy;
import com.ruoyi.crm.domain.CrmRecallSchedule;
import com.ruoyi.crm.mapper.CrmFollowupRuleEnrollmentMapper;
import com.ruoyi.crm.mapper.CrmRecallPolicyMapper;
import com.ruoyi.crm.mapper.CrmRecallScheduleMapper;
import com.ruoyi.crm.service.followup.FollowupRuleConstants;
import com.ruoyi.crm.service.followup.FollowupRuleDelayCalculator;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;

/**
 * 赢单后匹配复诊策略并 upsert 日程；同策略再赢单改期 pending enrollment。
 */
@Service
public class RecallScheduleService
{
    private static final Logger log = LoggerFactory.getLogger(RecallScheduleService.class);

    @Autowired
    private CrmRecallPolicyMapper policyMapper;

    @Autowired
    private CrmRecallScheduleMapper scheduleMapper;

    @Autowired
    private CrmFollowupRuleEnrollmentMapper enrollmentMapper;

    @Autowired
    private RecallPolicySystemSeeder policySeeder;

    @Autowired
    private com.ruoyi.crm.service.wecom.contactgroup.WecomGroupReminderMaterializer wecomGroupReminderMaterializer;

    public void onOpportunityWon(CrmOpty opty)
    {
        if (opty == null)
        {
            return;
        }
        String tenantId = TenantDataSourceSupport.currentTenantKey();
        if (StringUtils.isBlank(tenantId))
        {
            return;
        }
        if (StringUtils.isBlank(opty.getContactId()))
        {
            log.warn("recall schedule skipped: won opty {} has no contactId", opty.getOptyId());
            return;
        }
        try
        {
            policySeeder.ensure(tenantId);
        }
        catch (Exception ex)
        {
            log.debug("recall policy seeder skipped: {}", ex.getMessage());
        }
        try
        {
            List<CrmRecallPolicy> policies = policyMapper.selectEnabledOrdered(tenantId);
            CrmRecallPolicy policy = RecallPolicyMatcher.match(policies, opty.getTreatmentItemCode());
            if (policy == null)
            {
                log.warn("recall schedule skipped: no enabled policy tenant={}", tenantId);
                return;
            }
            Date nextDueAt = RecallIntervalCalculator.addToDate(opty.getCloseDate(),
                    policy.getIntervalValue() == null ? 0 : policy.getIntervalValue(),
                    policy.getIntervalUnit());
            Date now = DateUtils.getNowDate();
            CrmRecallSchedule row = new CrmRecallSchedule();
            row.setScheduleId(IdUtils.fastUUID());
            row.setTenantId(tenantId);
            row.setContactId(opty.getContactId());
            row.setPolicyId(policy.getPolicyId());
            row.setAnchorType(RecallPolicyConstants.ANCHOR_OPPORTUNITY_WON);
            row.setAnchorId(opty.getOptyId());
            row.setTreatmentItemCode(opty.getTreatmentItemCode());
            row.setNextDueAt(nextDueAt);
            row.setStatus(RecallPolicyConstants.STATUS_SCHEDULED);
            row.setCreateTime(now);
            row.setUpdateTime(now);
            scheduleMapper.upsertCrmRecallSchedule(row);
            reschedulePendingEnrollments(tenantId, opty.getContactId(), policy.getPolicyId(), nextDueAt);
            log.info("recall schedule upserted contact={} policy={} due={}", opty.getContactId(),
                    policy.getPolicyId(), nextDueAt);
            try
            {
                // reload by contact+policy so scheduleId matches persisted row if upsert kept old id
                CrmRecallSchedule persisted = scheduleMapper.selectByContactAndPolicy(tenantId, opty.getContactId(),
                        policy.getPolicyId());
                if (persisted != null)
                {
                    wecomGroupReminderMaterializer.onRecallScheduleUpsert(persisted);
                }
                else
                {
                    wecomGroupReminderMaterializer.onRecallScheduleUpsert(row);
                }
            }
            catch (Exception remEx)
            {
                log.warn("group reminder recall materialize failed: {}", remEx.getMessage());
            }
        }
        catch (Exception ex)
        {
            log.warn("recall schedule failed opty={}: {}", opty.getOptyId(), ex.getMessage());
        }
    }

    public int markFired(String scheduleId)
    {
        if (StringUtils.isBlank(scheduleId))
        {
            return 0;
        }
        int rows = scheduleMapper.markFired(scheduleId);
        if (rows > 0)
        {
            try
            {
                wecomGroupReminderMaterializer.cancelBySourceRef("recall-sched:" + scheduleId);
            }
            catch (Exception ignored)
            {
            }
        }
        return rows;
    }

    public List<CrmRecallSchedule> selectDueScheduled(String tenantId, int limit)
    {
        return scheduleMapper.selectDueScheduled(tenantId, limit);
    }

    public List<CrmRecallSchedule> selectList(String tenantId, String contactId, String status)
    {
        return scheduleMapper.selectList(tenantId, contactId, status);
    }

    void reschedulePendingEnrollments(String tenantId, String contactId, String policyId, Date nextDueAt)
    {
        if (nextDueAt == null)
        {
            return;
        }
        Instant nineAm = FollowupRuleDelayCalculator.untilFieldNineAm(nextDueAt.toInstant());
        Date nextRun = FollowupRuleDelayCalculator.toDate(nineAm);
        List<CrmFollowupRuleEnrollment> pending = enrollmentMapper.selectPendingBySubject(tenantId,
                FollowupRuleConstants.SUBJECT_CONTACT, contactId, FollowupRuleConstants.TRIGGER_PATIENT_RECALL_DUE);
        if (pending == null || pending.isEmpty())
        {
            return;
        }
        for (CrmFollowupRuleEnrollment row : pending)
        {
            if (!samePolicy(row.getTriggerPayload(), policyId))
            {
                continue;
            }
            enrollmentMapper.updateNextRunAt(row.getEnrollmentId(), nextRun);
        }
    }

    static boolean samePolicy(String triggerPayload, String policyId)
    {
        if (StringUtils.isBlank(policyId))
        {
            return true;
        }
        if (StringUtils.isBlank(triggerPayload))
        {
            return false;
        }
        try
        {
            JSONObject json = JSON.parseObject(triggerPayload);
            if (json == null)
            {
                return false;
            }
            JSONObject schedule = json.getJSONObject("recallSchedule");
            if (schedule == null)
            {
                return false;
            }
            return policyId.equals(schedule.getString("policyId"));
        }
        catch (Exception ex)
        {
            return false;
        }
    }
}
