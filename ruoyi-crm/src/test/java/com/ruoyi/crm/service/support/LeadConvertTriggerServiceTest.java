package com.ruoyi.crm.service.support;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.dto.LeadConvertEvaluateResult;
import com.ruoyi.crm.domain.dto.LeadConvertLeadSummary;
import com.ruoyi.crm.service.ICrmLeadConvertService;

@ExtendWith(MockitoExtension.class)
class LeadConvertTriggerServiceTest
{
    @Mock
    private LeadArrivedStageDetector arrivedStageDetector;

    @Mock
    private LeadConvertTriggerDedup triggerDedup;

    @Mock
    private ICrmLeadConvertService leadConvertService;

    @Mock
    private LeadConvertWorkflowTriggerClient workflowTriggerClient;

    @InjectMocks
    private LeadConvertTriggerService triggerService;

    private CrmLead before;
    private CrmLead after;

    @BeforeEach
    void setUp()
    {
        before = new CrmLead();
        before.setLeadId("lead-1");
        before.setStage("跟进中");
        before.setLeadName("张三");
        before.setMobilePhone("13800138000");

        after = new CrmLead();
        after.setLeadId("lead-1");
        after.setStage("已到店");
        after.setLeadName("张三");
        after.setMobilePhone("13800138000");
        after.setOrgId("org-1");
    }

    @Test
    void tryTriggerStageArrived_firesWhenEligible()
    {
        when(arrivedStageDetector.stageChangedToArrived("跟进中", "已到店")).thenReturn(true);
        when(triggerDedup.shouldSuppressStageArrived("lead-1")).thenReturn(false);
        LeadConvertEvaluateResult eval = eligibleEval();
        when(leadConvertService.evaluate(eq("lead-1"), any())).thenReturn(eval);

        triggerService.tryTriggerStageArrived("tenant3", before, after);

        verify(workflowTriggerClient).fireAsync(eq("tenant3"), any());
    }

    @Test
    void tryTriggerStageArrived_skipsWhenNotEligible()
    {
        when(arrivedStageDetector.stageChangedToArrived("跟进中", "已到店")).thenReturn(true);
        when(triggerDedup.shouldSuppressStageArrived("lead-1")).thenReturn(false);
        LeadConvertEvaluateResult eval = new LeadConvertEvaluateResult();
        eval.setEligible(false);
        eval.setReason("missing_mobile");
        when(leadConvertService.evaluate(eq("lead-1"), any())).thenReturn(eval);

        triggerService.tryTriggerStageArrived("tenant3", before, after);

        verify(workflowTriggerClient, never()).fireAsync(any(), any());
    }

    @Test
    void tryTriggerStageArrived_suppressedByRecentCheckIn()
    {
        when(arrivedStageDetector.stageChangedToArrived("跟进中", "已到店")).thenReturn(true);
        when(triggerDedup.shouldSuppressStageArrived("lead-1")).thenReturn(true);

        triggerService.tryTriggerStageArrived("tenant3", before, after);

        verify(leadConvertService, never()).evaluate(any(), any());
        verify(workflowTriggerClient, never()).fireAsync(any(), any());
    }

    @Test
    void tryTriggerEventCheckIn_marksDedupAndFires()
    {
        when(leadConvertService.resolveLeadIdForCheckIn("evt-1", "reg-1", "mobile:138")).thenReturn("lead-1");
        LeadConvertEvaluateResult eval = eligibleEval();
        when(leadConvertService.evaluate(eq("lead-1"), any())).thenReturn(eval);

        triggerService.tryTriggerEventCheckIn("tenant3", "evt-1", "reg-1", "mobile:138", after);

        verify(triggerDedup).markEventCheckIn("lead-1");
        verify(workflowTriggerClient).fireAsync(eq("tenant3"), any());
    }

    private static LeadConvertEvaluateResult eligibleEval()
    {
        LeadConvertEvaluateResult eval = new LeadConvertEvaluateResult();
        eval.setEligible(true);
        eval.setOrgId("org-1");
        LeadConvertLeadSummary summary = new LeadConvertLeadSummary();
        summary.setLeadId("lead-1");
        summary.setLeadName("张三");
        summary.setMobilePhone("13800138000");
        eval.setLead(summary);
        return eval;
    }
}
