package com.ruoyi.crm.service.followup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.domain.CrmFollowupRule;
import com.ruoyi.crm.domain.CrmFollowupRuleEnrollment;
import com.ruoyi.crm.domain.dto.FollowupRuleDispatchEvent;
import com.ruoyi.crm.mapper.CrmFollowupRuleEnrollmentMapper;
import com.ruoyi.crm.mapper.CrmFollowupRuleMapper;

@ExtendWith(MockitoExtension.class)
public class FollowupRuleEngineTest
{
    @Mock
    private CrmFollowupRuleMapper ruleMapper;

    @Mock
    private CrmFollowupRuleEnrollmentMapper enrollmentMapper;

    @Mock
    private FollowupRuleTaskFactory taskFactory;

    @Mock
    private FollowupRuleSystemSeeder systemSeeder;

    @InjectMocks
    private FollowupRuleEngine engine;

    private CrmFollowupRule rule;

    @BeforeEach
    public void setUp()
    {
        rule = new CrmFollowupRule();
        rule.setRuleId("sys-opportunity-won");
        rule.setTenantId("demo");
        rule.setEnabled("1");
        rule.setTriggerType(FollowupRuleConstants.TRIGGER_OPPORTUNITY_WON);
        rule.setDelayMode(FollowupRuleConstants.DELAY_IMMEDIATE);
        rule.setTaskTemplate("{\"taskSubject\":\"回访\"}");
        rule.setDedupeConfig("{\"strategy\":\"once_per_record\"}");
    }

    @Test
    public void immediateFireCreatesTask()
    {
        when(ruleMapper.selectEnabledByTrigger("demo", FollowupRuleConstants.TRIGGER_OPPORTUNITY_WON))
                .thenReturn(Collections.singletonList(rule));
        when(enrollmentMapper.selectByIdempotency(eq("demo"), any())).thenReturn(null);
        when(enrollmentMapper.countFiredBySubject(eq("demo"), eq(rule.getRuleId()), eq("Opportunity"), eq("opty-1")))
                .thenReturn(0);
        when(enrollmentMapper.insertIgnoreDuplicate(any())).thenReturn(1);
        when(taskFactory.createTask(eq(rule), any())).thenReturn("task-1");

        FollowupRuleEngine.DispatchResult result = engine.dispatch(wonEvent(Instant.now()));
        assertEquals(1, result.matched);
        assertEquals(1, result.fired);
        verify(enrollmentMapper).markFired(any(), eq("task-1"));
    }

    @Test
    public void oncePerRecordSkipsWhenFiredExists()
    {
        when(ruleMapper.selectEnabledByTrigger("demo", FollowupRuleConstants.TRIGGER_OPPORTUNITY_WON))
                .thenReturn(Collections.singletonList(rule));
        when(enrollmentMapper.selectByIdempotency(eq("demo"), any())).thenReturn(null);
        when(enrollmentMapper.countFiredBySubject(eq("demo"), eq(rule.getRuleId()), eq("Opportunity"), eq("opty-1")))
                .thenReturn(1);

        FollowupRuleEngine.DispatchResult result = engine.dispatch(wonEvent(Instant.now()));
        assertEquals(1, result.skipped);
        verify(enrollmentMapper, never()).insertIgnoreDuplicate(any());
    }

    @Test
    public void delayedRuleEnrollsPending()
    {
        rule.setDelayMode(FollowupRuleConstants.DELAY_FIXED);
        rule.setDelayValue(4);
        rule.setDelayUnit(FollowupRuleConstants.UNIT_HOURS);
        when(ruleMapper.selectEnabledByTrigger("demo", FollowupRuleConstants.TRIGGER_OPPORTUNITY_WON))
                .thenReturn(Collections.singletonList(rule));
        when(enrollmentMapper.selectByIdempotency(eq("demo"), any())).thenReturn(null);
        when(enrollmentMapper.countFiredBySubject(any(), any(), any(), any())).thenReturn(0);
        when(enrollmentMapper.insertIgnoreDuplicate(any())).thenReturn(1);

        FollowupRuleEngine.DispatchResult result = engine.dispatch(wonEvent(Instant.now()));
        assertEquals(1, result.enrolled);
        ArgumentCaptor<CrmFollowupRuleEnrollment> captor = ArgumentCaptor.forClass(CrmFollowupRuleEnrollment.class);
        verify(enrollmentMapper).insertIgnoreDuplicate(captor.capture());
        assertEquals(FollowupRuleConstants.STATUS_PENDING, captor.getValue().getStatus());
        verify(taskFactory, never()).createTask(any(), any());
    }

    @Test
    public void duplicateIdempotencySkips()
    {
        when(ruleMapper.selectEnabledByTrigger("demo", FollowupRuleConstants.TRIGGER_OPPORTUNITY_WON))
                .thenReturn(Collections.singletonList(rule));
        when(enrollmentMapper.selectByIdempotency(eq("demo"), any())).thenReturn(new CrmFollowupRuleEnrollment());

        FollowupRuleEngine.DispatchResult result = engine.dispatch(wonEvent(Instant.now()));
        assertEquals(1, result.skipped);
        verify(enrollmentMapper, never()).insertIgnoreDuplicate(any());
    }

    @Test
    public void missingAssigneeMarksSkipped()
    {
        when(ruleMapper.selectEnabledByTrigger("demo", FollowupRuleConstants.TRIGGER_OPPORTUNITY_WON))
                .thenReturn(Collections.singletonList(rule));
        when(enrollmentMapper.selectByIdempotency(eq("demo"), any())).thenReturn(null);
        when(enrollmentMapper.countFiredBySubject(any(), any(), any(), any())).thenReturn(0);
        when(enrollmentMapper.insertIgnoreDuplicate(any())).thenReturn(1);
        when(taskFactory.createTask(any(), any())).thenThrow(
                new FollowupRuleTaskFactory.FollowupRuleSkipException(FollowupRuleConstants.SKIP_MISSING_ASSIGNEE));

        FollowupRuleEngine.DispatchResult result = engine.dispatch(wonEvent(Instant.now()));
        assertEquals(1, result.skipped);
        verify(enrollmentMapper).markSkipped(any(), eq(FollowupRuleConstants.SKIP_MISSING_ASSIGNEE));
    }

    @Test
    public void processDueEnrollmentCatchUp()
    {
        CrmFollowupRuleEnrollment enrollment = new CrmFollowupRuleEnrollment();
        enrollment.setEnrollmentId("en-1");
        enrollment.setTenantId("demo");
        enrollment.setRuleId(rule.getRuleId());
        enrollment.setSubjectType("Opportunity");
        enrollment.setSubjectId("opty-1");
        enrollment.setStatus("pending");
        enrollment.setNextRunAt(new Date(System.currentTimeMillis() - 3600_000));
        when(ruleMapper.selectEntityById("demo", rule.getRuleId())).thenReturn(rule);
        when(taskFactory.createTask(rule, enrollment)).thenReturn("task-9");

        engine.processDueEnrollment(enrollment);
        verify(enrollmentMapper).markFired("en-1", "task-9");
        assertSame("task-9", enrollment.getTaskId());
    }

    private static FollowupRuleDispatchEvent wonEvent(Instant occurred)
    {
        FollowupRuleDispatchEvent event = new FollowupRuleDispatchEvent();
        event.setTenant("demo");
        event.setTriggerType(FollowupRuleConstants.TRIGGER_OPPORTUNITY_WON);
        event.setOccurredAt(occurred);
        JSONObject payload = new JSONObject();
        JSONObject opty = new JSONObject();
        opty.put("optyId", "opty-1");
        opty.put("name", "种植方案");
        opty.put("owner", "张咨询师");
        payload.put("opportunity", opty);
        event.setPayload(payload);
        return event;
    }
}
