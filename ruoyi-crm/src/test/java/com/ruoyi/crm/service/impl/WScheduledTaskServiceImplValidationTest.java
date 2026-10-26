package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.WScheduledTask;
import com.ruoyi.crm.domain.WWorkflow;
import com.ruoyi.crm.mapper.WWorkflowMapper;
import com.ruoyi.crm.service.support.WorkflowTemplateGuard;

/**
 * 工作流定时任务字段校验
 */
@ExtendWith(MockitoExtension.class)
public class WScheduledTaskServiceImplValidationTest
{
    @Mock
    private WWorkflowMapper wWorkflowMapper;

    @InjectMocks
    private WScheduledTaskServiceImpl service;

    @Test
    public void runTimeAcceptsHalfHour()
    {
        assertDoesNotThrow(() -> WScheduledTaskServiceImpl.validateRunTime("08:00"));
        assertDoesNotThrow(() -> WScheduledTaskServiceImpl.validateRunTime("08:30"));
        assertDoesNotThrow(() -> WScheduledTaskServiceImpl.validateRunTime("23:30"));
    }

    @Test
    public void runTimeRejectsQuarterHour()
    {
        ServiceException ex = assertThrows(ServiceException.class,
                () -> WScheduledTaskServiceImpl.validateRunTime("08:15"));
        assertEquals(400, ex.getCode());
    }

    @Test
    public void scheduleTypeRejectsUnknown()
    {
        ServiceException ex = assertThrows(ServiceException.class,
                () -> WScheduledTaskServiceImpl.validateScheduleType("cron"));
        assertEquals(400, ex.getCode());
    }

    @Test
    public void weeklyRequiresDaysOfWeek()
    {
        ServiceException ex = assertThrows(ServiceException.class,
                () -> WScheduledTaskServiceImpl.validateDaysOfWeek(null, true));
        assertEquals(400, ex.getCode());
    }

    @Test
    public void daysOfWeekRejectsOutOfRange()
    {
        ServiceException ex = assertThrows(ServiceException.class,
                () -> WScheduledTaskServiceImpl.validateDaysOfWeek("1,8", true));
        assertEquals(400, ex.getCode());
    }

    @Test
    public void daysOfWeekAcceptsIsoWeekdays()
    {
        assertDoesNotThrow(() -> WScheduledTaskServiceImpl.validateDaysOfWeek("1,3,5", true));
        assertDoesNotThrow(() -> WScheduledTaskServiceImpl.validateDaysOfWeek("7", true));
    }

    @Test
    public void insertValidationRejectsBadRunTime()
    {
        WWorkflow workflow = new WWorkflow();
        workflow.setId("wf-1");
        workflow.setIsTemplate(Boolean.FALSE);
        when(wWorkflowMapper.selectWWorkflowById("wf-1")).thenReturn(workflow);

        WScheduledTask task = new WScheduledTask();
        task.setName("每日回访");
        task.setWorkflowId("wf-1");
        task.setScheduleType("daily");
        task.setRunTime("08:15");
        ServiceException ex = assertThrows(ServiceException.class, () -> service.validateForWrite(task, true));
        assertTrue(ex.getMessage().contains("runTime"));
    }

    @Test
    public void insertValidationRejectsTemplateWorkflow()
    {
        WWorkflow template = new WWorkflow();
        template.setId("tpl-1");
        template.setIsTemplate(Boolean.TRUE);
        when(wWorkflowMapper.selectWWorkflowById("tpl-1")).thenReturn(template);

        WScheduledTask task = new WScheduledTask();
        task.setName("每日回访");
        task.setWorkflowId("tpl-1");
        task.setScheduleType("daily");
        task.setRunTime("08:00");
        ServiceException ex = assertThrows(ServiceException.class, () -> service.validateForWrite(task, true));
        assertEquals(WorkflowTemplateGuard.TEMPLATE_NOT_EXECUTABLE, ex.getMessage());
    }
}
