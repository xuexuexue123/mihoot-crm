package com.ruoyi.crm.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.domain.CrmOpty;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.mapper.CrmOptyMapper;
import com.ruoyi.crm.service.IPatientClvService;
import com.ruoyi.crm.service.support.PatientWorkflowAutomationLauncher;
import com.ruoyi.crm.service.support.RecordTriggerCrudHookSupport;
import com.ruoyi.system.service.ISysDictTypeService;

@ExtendWith(MockitoExtension.class)
class CrmOptyServiceImplInsertActivityTest
{
    private static final String OPTY_ID = "opty-1";
    private static final String CONTACT_ID = "contact-1";

    @Mock
    private CrmOptyMapper crmOptyMapper;

    @Mock
    private ISysDictTypeService dictTypeService;

    @Mock
    private IPatientClvService patientClvService;

    @Mock
    private PatientWorkflowAutomationLauncher patientWorkflowAutomationLauncher;

    @Mock
    private RecordTriggerCrudHookSupport recordTriggerCrudHookSupport;

    @Mock
    private ActivityEventProducer activityEventProducer;

    @InjectMocks
    private CrmOptyServiceImpl service;

    @Test
    void insertCrmOpty_producesOpportunityCreatedActivity()
    {
        CrmOpty opty = new CrmOpty();
        opty.setOptyId(OPTY_ID);
        opty.setContactId(CONTACT_ID);
        opty.setName("补牙治疗方案");
        opty.setStage("New");

        when(crmOptyMapper.insertCrmOpty(any(CrmOpty.class))).thenReturn(1);

        service.insertCrmOpty(opty);

        verify(activityEventProducer).produceOpportunityCreated(eq(opty));
    }
}
