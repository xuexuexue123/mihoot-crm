package com.ruoyi.crm.service.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.dto.LeadConvertEvaluateRequest;
import com.ruoyi.crm.domain.dto.LeadConvertEvaluateResult;
import com.ruoyi.crm.domain.dto.LeadConvertWorkflowTriggerRequest;
import com.ruoyi.crm.service.ICrmLeadConvertService;

/**
 * Lead 自动转化 workflow 触发编排（evaluate → 异步 BFF）。
 */
@Component
public class LeadConvertTriggerService
{
    private static final Logger log = LoggerFactory.getLogger(LeadConvertTriggerService.class);

    @Autowired
    private LeadArrivedStageDetector arrivedStageDetector;

    @Autowired
    private LeadConvertTriggerDedup triggerDedup;

    @Autowired
    private ICrmLeadConvertService leadConvertService;

    @Autowired
    private LeadConvertWorkflowTriggerClient workflowTriggerClient;

    public void registerStageArrivedAfterCommit(CrmLead before, CrmLead after)
    {
        if (before == null || after == null)
        {
            return;
        }
        String tenantId = TenantDataSourceSupport.currentTenantKey();
        if (!StringUtils.hasText(tenantId))
        {
            return;
        }
        runAfterCommit(() -> tryTriggerStageArrived(tenantId, before, after));
    }

    public void registerEventCheckInAfterCommit(String eventId, String registrationId,
            String audienceKey, CrmLead leadHint)
    {
        String tenantId = TenantDataSourceSupport.currentTenantKey();
        if (!StringUtils.hasText(tenantId))
        {
            return;
        }
        runAfterCommit(() -> tryTriggerEventCheckIn(tenantId, eventId, registrationId, audienceKey, leadHint));
    }

    void tryTriggerStageArrived(String tenantId, CrmLead before, CrmLead after)
    {
        if (!arrivedStageDetector.stageChangedToArrived(before.getStage(), after.getStage()))
        {
            return;
        }
        if (triggerDedup.shouldSuppressStageArrived(after.getLeadId()))
        {
            log.debug("lead convert stage_arrived suppressed by recent event_check_in leadId={}", after.getLeadId());
            return;
        }
        LeadConvertEvaluateRequest req = new LeadConvertEvaluateRequest();
        req.setTrigger("stage_arrived");
        LeadConvertEvaluateResult eval = leadConvertService.evaluate(after.getLeadId(), req);
        fireIfEligible(tenantId, after, "stage_arrived", eval, null);
    }

    void tryTriggerEventCheckIn(String tenantId, String eventId, String registrationId,
            String audienceKey, CrmLead leadHint)
    {
        String leadId = leadConvertService.resolveLeadIdForCheckIn(eventId, registrationId, audienceKey);
        if (!StringUtils.hasText(leadId))
        {
            return;
        }
        triggerDedup.markEventCheckIn(leadId);

        LeadConvertEvaluateRequest req = new LeadConvertEvaluateRequest();
        req.setTrigger("event_check_in");
        req.setEventId(eventId);
        req.setRegistrationId(registrationId);
        req.setAudienceKey(audienceKey);
        LeadConvertEvaluateResult eval = leadConvertService.evaluate(leadId, req);

        CrmLead lead = leadHint;
        if (lead == null || !leadId.equals(lead.getLeadId()))
        {
            lead = buildLeadFromEvaluate(eval, leadId);
        }
        fireIfEligible(tenantId, lead, "event_check_in", eval, eventId);
    }

    private void fireIfEligible(String tenantId, CrmLead lead, String trigger,
            LeadConvertEvaluateResult eval, String eventId)
    {
        if (eval == null || !eval.isEligible())
        {
            String reason = eval != null ? eval.getReason() : "null_evaluate";
            log.debug("lead convert trigger skipped leadId={} trigger={} reason={}",
                    lead != null ? lead.getLeadId() : null, trigger, reason);
            return;
        }
        if (lead == null || !StringUtils.hasText(lead.getLeadId()))
        {
            log.warn("lead convert trigger skipped: lead snapshot missing trigger={}", trigger);
            return;
        }
        LeadConvertWorkflowTriggerRequest payload = LeadConvertWorkflowTriggerRequest.from(
                lead, trigger, eval.getOrgId(), eventId);
        workflowTriggerClient.fireAsync(tenantId, payload);
    }

    private static CrmLead buildLeadFromEvaluate(LeadConvertEvaluateResult eval, String leadId)
    {
        CrmLead lead = new CrmLead();
        lead.setLeadId(leadId);
        if (eval != null && eval.getLead() != null)
        {
            lead.setLeadName(eval.getLead().getLeadName());
            lead.setMobilePhone(eval.getLead().getMobilePhone());
            lead.setStage(eval.getLead().getStage());
        }
        if (eval != null && StringUtils.hasText(eval.getOrgId()))
        {
            lead.setOrgId(eval.getOrgId());
        }
        return lead;
    }

    private static void runAfterCommit(Runnable task)
    {
        if (TransactionSynchronizationManager.isSynchronizationActive())
        {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization()
            {
                @Override
                public void afterCommit()
                {
                    task.run();
                }
            });
        }
        else
        {
            task.run();
        }
    }
}
