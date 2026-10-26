package com.ruoyi.crm.service.nurture;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.domain.CrmEventNurtureEnrollment;
import com.ruoyi.crm.domain.CrmEventNurtureStep;
import com.ruoyi.crm.domain.dto.EventNurtureAudienceSnapshot;
import com.ruoyi.crm.service.nurture.handler.CreateTaskStepHandler;
import com.ruoyi.crm.service.nurture.handler.DelayStepHandler;
import com.ruoyi.crm.service.nurture.handler.MpTemplateStepHandler;
import com.ruoyi.crm.service.nurture.support.EventNurtureStepResult;

@ExtendWith(MockitoExtension.class)
class EventNurtureStepExecutorTest
{
    @Mock
    private DelayStepHandler delayStepHandler;

    @Mock
    private MpTemplateStepHandler mpTemplateStepHandler;

    @Mock
    private CreateTaskStepHandler createTaskStepHandler;

    @InjectMocks
    private EventNurtureStepExecutor executor;

    @Test
    void executesDelayStep()
    {
        CrmEventNurtureEnrollment enrollment = new CrmEventNurtureEnrollment();
        CrmEventNurtureStep step = new CrmEventNurtureStep();
        step.setActionType("delay");
        when(delayStepHandler.execute(enrollment, step))
                .thenReturn(EventNurtureStepResult.sent("delay:1"));

        EventNurtureStepResult result = executor.execute("ten", enrollment, step, null);

        assertTrue(result.isSuccess());
    }

    @Test
    void mpTemplateSkipsWithoutOpenId()
    {
        CrmEventNurtureEnrollment enrollment = new CrmEventNurtureEnrollment();
        CrmEventNurtureStep step = new CrmEventNurtureStep();
        step.setActionType("mp_template");
        step.setActionJson("{\"presetId\":1,\"requireOpenId\":true}");
        when(mpTemplateStepHandler.execute("ten", enrollment, step, null))
                .thenReturn(EventNurtureStepResult.skipped("no_openid"));

        EventNurtureStepResult result = executor.execute("ten", enrollment, step, null);

        assertTrue(result.isSkipped());
    }
}
