package com.ruoyi.crm.service.nurture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.domain.CrmEventNurtureEnrollment;
import com.ruoyi.crm.domain.CrmEventNurtureSequence;
import com.ruoyi.crm.domain.CrmEventNurtureStep;
import com.ruoyi.crm.domain.dto.EventNurtureAudienceSnapshot;
import com.ruoyi.crm.enums.EventNurtureTriggerType;
import com.ruoyi.crm.mapper.EventNurtureEnrollmentMapper;
import com.ruoyi.crm.mapper.EventNurtureSequenceMapper;
import com.ruoyi.crm.mapper.EventNurtureStepExecutionMapper;
import com.ruoyi.crm.mapper.EventNurtureStepMapper;
import com.ruoyi.crm.service.nurture.support.EventNurtureStepResult;

@ExtendWith(MockitoExtension.class)
class EventNurtureEnrollmentServiceTest
{
    @Mock
    private EventNurtureSequenceMapper sequenceMapper;

    @Mock
    private EventNurtureStepMapper stepMapper;

    @Mock
    private EventNurtureEnrollmentMapper enrollmentMapper;

    @Mock
    private EventNurtureStepExecutionMapper executionMapper;

    @Mock
    private EventNurtureStepExecutor stepExecutor;

    @Mock
    private com.ruoyi.crm.service.nurture.support.EventNurtureAudienceResolver audienceResolver;

    @InjectMocks
    private EventNurtureEnrollmentServiceImpl enrollmentService;

    @Test
    void tryEnroll_skipsWhenSequenceDisabled()
    {
        when(sequenceMapper.selectEnabledByEventAndTrigger("ten", "evt-1",
                EventNurtureTriggerType.H5_REGISTRATION_CREATED.getCode())).thenReturn(null);

        EventNurtureAudienceSnapshot audience = new EventNurtureAudienceSnapshot();
        audience.setAudienceKey("h5:lead-1");

        enrollmentService.tryEnroll("ten", "evt-1",
                EventNurtureTriggerType.H5_REGISTRATION_CREATED.getCode(), audience);

        verify(enrollmentMapper, never()).insert(any());
    }

    @Test
    void processDueEnrollment_completesWhenNoMoreSteps()
    {
        CrmEventNurtureEnrollment enrollment = new CrmEventNurtureEnrollment();
        enrollment.setId(100L);
        enrollment.setTenantId("ten");
        enrollment.setEventId("evt-1");
        enrollment.setSequenceId(1L);
        enrollment.setAudienceKey("mp:o1");
        enrollment.setStatus("active");
        enrollment.setCurrentStepOrder(2);

        CrmEventNurtureSequence sequence = new CrmEventNurtureSequence();
        sequence.setId(1L);
        sequence.setEventId("evt-1");
        sequence.setEnabled("1");
        sequence.setExitOnContact("1");

        CrmEventNurtureStep step = new CrmEventNurtureStep();
        step.setId(20L);
        step.setStepOrder(2);
        step.setActionType("delay");

        when(enrollmentMapper.selectById(100L)).thenReturn(enrollment);
        when(sequenceMapper.selectByEventId("ten", "evt-1")).thenReturn(sequence);
        when(audienceResolver.resolve("ten", "evt-1", "mp:o1")).thenReturn(null);
        when(stepMapper.selectBySequenceAndOrder(1L, 2)).thenReturn(step);
        when(executionMapper.selectByEnrollmentAndStep(100L, 20L)).thenReturn(null);
        when(stepExecutor.execute(any(), any(), any(), any()))
                .thenReturn(EventNurtureStepResult.sent("delay:2"));
        when(stepMapper.selectBySequenceAndOrder(1L, 3)).thenReturn(null);

        enrollmentService.processDueEnrollment(100L, "ten");

        verify(enrollmentMapper).markCompleted(100L);
    }
}
