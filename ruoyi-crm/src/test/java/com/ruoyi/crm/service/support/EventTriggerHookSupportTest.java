package com.ruoyi.crm.service.support;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import com.ruoyi.crm.domain.CrmActivity;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.mapper.CrmOrgMapper;
import com.ruoyi.crm.mapper.CrmOptyMapper;
import com.ruoyi.crm.service.IEventTriggerDispatchService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

@ExtendWith(MockitoExtension.class)
class EventTriggerHookSupportTest
{
    @Mock
    private IEventTriggerDispatchService eventTriggerDispatchService;
    @Mock
    private CrmLeadMapper crmLeadMapper;
    @Mock
    private CrmContactMapper crmContactMapper;
    @Mock
    private CrmOrgMapper crmOrgMapper;
    @Mock
    private CrmOptyMapper crmOptyMapper;

    @InjectMocks
    private EventTriggerHookSupport hookSupport;

    @BeforeEach
    void setUp()
    {
        ReflectionTestUtils.setField(hookSupport, "dispatchEnabled", true);
        ReflectionTestUtils.setField(hookSupport, "eventTypeWhitelistRaw",
                "engagement.survey.completed,engagement.assessment.completed");
        hookSupport.initWhitelist();
        DynamicDataSourceContextHolder.setDataSourceKey("demo");
    }

    @AfterEach
    void tearDown()
    {
        DynamicDataSourceContextHolder.clearDataSourceKey();
    }

    @Test
    void afterActivityInserted_whitelistedWithSubject_registersDispatch()
    {
        CrmLead lead = new CrmLead();
        lead.setLeadId("lead-1");
        lead.setLeadName("张三");
        lead.setSurveyJson("{\"a\":1}");
        when(crmLeadMapper.selectCrmLeadByLeadId("lead-1")).thenReturn(lead);

        CrmActivity activity = baseActivity("engagement.assessment.completed", "Lead", "lead-1");
        hookSupport.afterActivityInserted(activity);

        verify(eventTriggerDispatchService).registerAfterCommit(eq("demo"), argThat(payload ->
                "engagement.assessment.completed".equals(payload.getEventType())
                        && "Lead".equals(payload.getSubjectType())
                        && "lead-1".equals(payload.getSubjectId())
                        && "lead".equals(payload.getSubjectEntity())
                        && "pulse-1".equals(payload.getPulseId())
                        && payload.getSubject() != null
                        && "张三".equals(payload.getSubject().get("leadName"))
                        && payload.getSubject().get("surveyJson") != null));
    }

    @Test
    void afterActivityInserted_subjectMissing_stillRegistersWithNullSubject()
    {
        when(crmLeadMapper.selectCrmLeadByLeadId("lead-missing")).thenReturn(null);

        CrmActivity activity = baseActivity("engagement.assessment.completed", "Lead", "lead-missing");
        hookSupport.afterActivityInserted(activity);

        verify(eventTriggerDispatchService).registerAfterCommit(eq("demo"), argThat(payload ->
                payload.getSubject() == null
                        && "lead-missing".equals(payload.getSubjectId())));
    }

    @Test
    void afterActivityInserted_notInWhitelist_skips()
    {
        CrmActivity activity = baseActivity("lead.created", "Lead", "lead-1");
        hookSupport.afterActivityInserted(activity);
        verify(eventTriggerDispatchService, never()).registerAfterCommit(any(), any());
    }

    private static CrmActivity baseActivity(String eventType, String subjectType, String subjectId)
    {
        CrmActivity activity = new CrmActivity();
        activity.setActivityId("act-1");
        activity.setPulseId("pulse-1");
        activity.setEventType(eventType);
        activity.setSubjectType(subjectType);
        activity.setSubjectId(subjectId);
        return activity;
    }
}
