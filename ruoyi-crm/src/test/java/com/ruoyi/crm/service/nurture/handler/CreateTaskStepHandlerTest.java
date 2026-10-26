package com.ruoyi.crm.service.nurture.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.domain.CrmEventNurtureEnrollment;
import com.ruoyi.crm.domain.CrmEventNurtureStep;
import com.ruoyi.crm.domain.CrmTask;
import com.ruoyi.crm.domain.dto.EventNurtureAudienceSnapshot;
import com.ruoyi.crm.mapper.CrmEventMapper;
import com.ruoyi.crm.service.ICrmLeadService;
import com.ruoyi.crm.service.ICrmTaskService;
import com.ruoyi.crm.service.nurture.support.EventNurtureTaskDefaults;

@ExtendWith(MockitoExtension.class)
class CreateTaskStepHandlerTest
{
    @Mock
    private CrmEventMapper crmEventMapper;

    @Mock
    private ICrmTaskService crmTaskService;

    @Mock
    private ICrmLeadService crmLeadService;

    @InjectMocks
    private CreateTaskStepHandler handler;

    @Test
    void execute_setsDefaultTaskTypeWhenMissing()
    {
        CrmEvent event = new CrmEvent();
        event.setEventId("evt-1");
        event.setSubject("七月讲座");
        event.setOwner("周老板");
        when(crmEventMapper.selectCrmEventByEventId("evt-1")).thenReturn(event);

        CrmEventNurtureEnrollment enrollment = new CrmEventNurtureEnrollment();
        enrollment.setEventId("evt-1");

        CrmEventNurtureStep step = new CrmEventNurtureStep();
        step.setActionJson("{\"subjectTemplate\":\"跟进活动受众：{{displayName}}\",\"assignTo\":\"event_owner\"}");

        EventNurtureAudienceSnapshot audience = new EventNurtureAudienceSnapshot();
        audience.setDisplayName("张三");

        handler.execute(enrollment, step, audience);

        ArgumentCaptor<CrmTask> captor = ArgumentCaptor.forClass(CrmTask.class);
        verify(crmTaskService).insertCrmTask(captor.capture());
        CrmTask task = captor.getValue();
        assertNotNull(task.getTaskId());
        assertEquals(EventNurtureTaskDefaults.TASK_TYPE, task.getTaskType());
        assertEquals(EventNurtureTaskDefaults.CREATE_BY, task.getCreateBy());
        assertEquals("周老板", task.getAssignedTo());
    }

    @Test
    void execute_usesTaskTypeFromActionJson()
    {
        CrmEvent event = new CrmEvent();
        event.setEventId("evt-1");
        event.setOwner("周老板");
        when(crmEventMapper.selectCrmEventByEventId("evt-1")).thenReturn(event);

        CrmEventNurtureEnrollment enrollment = new CrmEventNurtureEnrollment();
        enrollment.setEventId("evt-1");

        CrmEventNurtureStep step = new CrmEventNurtureStep();
        step.setActionJson("{\"subjectTemplate\":\"跟进\",\"taskType\":\"电话\",\"assignTo\":\"event_owner\"}");

        handler.execute(enrollment, step, new EventNurtureAudienceSnapshot());

        ArgumentCaptor<CrmTask> captor = ArgumentCaptor.forClass(CrmTask.class);
        verify(crmTaskService).insertCrmTask(captor.capture());
        assertEquals("电话", captor.getValue().getTaskType());
    }
}
