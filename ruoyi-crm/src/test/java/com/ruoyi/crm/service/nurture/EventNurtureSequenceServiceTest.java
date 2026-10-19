package com.ruoyi.crm.service.nurture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.domain.CrmEventNurtureSequence;
import com.ruoyi.crm.domain.CrmEventNurtureStep;
import com.ruoyi.crm.domain.dto.EventNurtureSequenceSaveRequest;
import com.ruoyi.crm.domain.vo.EventNurtureStepVo;
import com.ruoyi.crm.enums.EventNurtureTriggerType;
import com.ruoyi.crm.mapper.CrmEventMapper;
import com.ruoyi.crm.mapper.EventNurtureEnrollmentMapper;
import com.ruoyi.crm.mapper.EventNurtureSequenceMapper;
import com.ruoyi.crm.mapper.EventNurtureStepMapper;
import com.ruoyi.crm.wechatmp.service.IWechatMpMessagePresetService;

@ExtendWith(MockitoExtension.class)
class EventNurtureSequenceServiceTest
{
    @Mock
    private EventNurtureSequenceMapper sequenceMapper;

    @Mock
    private EventNurtureStepMapper stepMapper;

    @Mock
    private EventNurtureEnrollmentMapper enrollmentMapper;

    @Mock
    private CrmEventMapper crmEventMapper;

    @Mock
    private IWechatMpMessagePresetService presetService;

    @InjectMocks
    private EventNurtureSequenceServiceImpl service;

    private CrmEvent event;

    @BeforeEach
    void setUp()
    {
        event = new CrmEvent();
        event.setEventId("evt-1");
        event.setSubject("Test Event");
        event.setMpRegistrationAutomationEnabledRaw("1");
        when(crmEventMapper.selectCrmEventByEventId("evt-1")).thenReturn(event);
    }

    @Test
    void saveSequence_enablingWithoutStepsThrows()
    {
        CrmEventNurtureSequence seq = new CrmEventNurtureSequence();
        seq.setId(1L);
        seq.setTenantId("ten");
        seq.setEventId("evt-1");
        when(sequenceMapper.selectByEventId("ten", "evt-1")).thenReturn(seq);
        when(stepMapper.selectBySequenceId(1L)).thenReturn(Collections.emptyList());

        EventNurtureSequenceSaveRequest request = new EventNurtureSequenceSaveRequest();
        request.setEnabled(true);
        request.setTriggerType(EventNurtureTriggerType.MP_SUBSCRIBE_ATTRIBUTED.getCode());

        assertThrows(ServiceException.class,
                () -> service.saveSequence("ten", "evt-1", request, "admin"));
    }

    @Test
    void saveSequence_enablingDisablesLegacyMpAutomation()
    {
        CrmEventNurtureSequence seq = new CrmEventNurtureSequence();
        seq.setId(1L);
        seq.setTenantId("ten");
        seq.setEventId("evt-1");
        seq.setEnabled("0");
        seq.setTriggerType(EventNurtureTriggerType.MP_SUBSCRIBE_ATTRIBUTED.getCode());
        seq.setExitOnContact("1");
        when(sequenceMapper.selectByEventId("ten", "evt-1")).thenReturn(seq);

        CrmEventNurtureStep step = new CrmEventNurtureStep();
        step.setId(10L);
        step.setStepOrder(1);
        step.setActionType("delay");
        when(stepMapper.selectBySequenceId(1L)).thenReturn(Collections.singletonList(step));
        when(enrollmentMapper.countActiveBySequenceId(1L)).thenReturn(0);

        EventNurtureSequenceSaveRequest request = new EventNurtureSequenceSaveRequest();
        request.setEnabled(true);
        request.setTriggerType(EventNurtureTriggerType.MP_SUBSCRIBE_ATTRIBUTED.getCode());

        service.saveSequence("ten", "evt-1", request, "admin");

        verify(crmEventMapper).updateCrmEvent(any(CrmEvent.class));
    }

    @Test
    void replaceSteps_validatesContinuousOrder()
    {
        EventNurtureStepVo step = new EventNurtureStepVo();
        step.setStepOrder(2);
        step.setActionType("delay");
        step.setDelaySeconds(0);

        com.ruoyi.crm.domain.dto.EventNurtureStepsSaveRequest request =
                new com.ruoyi.crm.domain.dto.EventNurtureStepsSaveRequest();
        request.setSteps(Collections.singletonList(step));

        assertThrows(ServiceException.class,
                () -> service.replaceSteps("ten", "evt-1", request));
    }
}
