package com.ruoyi.crm.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.crm.domain.CrmOpty;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.mapper.CrmOptyMapper;
import com.ruoyi.crm.rule.SmartPushEventExecutor;
import com.ruoyi.crm.service.IPatientClvService;
import com.ruoyi.crm.service.followup.FollowupRuleHookSupport;
import com.ruoyi.crm.service.recall.RecallScheduleService;
import com.ruoyi.crm.service.support.PatientWorkflowAutomationLauncher;
import com.ruoyi.crm.service.support.RecordTriggerCrudHookSupport;
import com.ruoyi.crm.service.webhook.OutboundWebhookHookSupport;
import com.ruoyi.crm.service.wecom.contactgroup.WecomGroupReminderMaterializer;
import com.ruoyi.system.service.ISysDictTypeService;

@ExtendWith(MockitoExtension.class)
class CrmOptyServiceImplUpdateCloseReasonTest
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

    @Mock
    private SmartPushEventExecutor smartPushEventExecutor;

    @Mock
    private FollowupRuleHookSupport followupRuleHookSupport;

    @Mock
    private OutboundWebhookHookSupport outboundWebhookHookSupport;

    @Mock
    private RecallScheduleService recallScheduleService;

    @Mock
    private WecomGroupReminderMaterializer wecomGroupReminderMaterializer;

    @InjectMocks
    private CrmOptyServiceImpl service;

    @Test
    void updateCrmOpty_derivesStageFromCloseReasonAndProducesWonActivity()
    {
        CrmOpty existing = new CrmOpty();
        existing.setOptyId(OPTY_ID);
        existing.setContactId(CONTACT_ID);
        existing.setStage("negotiation");
        existing.setAmount(new BigDecimal("10000"));

        CrmOpty request = new CrmOpty();
        request.setOptyId(OPTY_ID);
        request.setCloseReason("closed_won");
        request.setClosedWonBasis("down_payment_received");
        request.setTreatmentItemCode("implant");

        CrmOpty updated = new CrmOpty();
        updated.setOptyId(OPTY_ID);
        updated.setContactId(CONTACT_ID);
        updated.setStage("closed_won");
        updated.setCloseReason("closed_won");
        updated.setClosedWonBasis("down_payment_received");
        updated.setTreatmentItemCode("implant");
        updated.setAmount(new BigDecimal("10000"));

        SysDictData wonStage = new SysDictData();
        wonStage.setDictValue("closed_won");
        wonStage.setStatus("0");

        when(crmOptyMapper.selectCrmOptyByOptyId(OPTY_ID)).thenReturn(existing, updated);
        when(crmOptyMapper.updateCrmOpty(any(CrmOpty.class))).thenReturn(1);
        when(dictTypeService.selectDictDataByType("crm_opty_stage_closed_won"))
                .thenReturn(Collections.singletonList(wonStage));
        when(dictTypeService.selectDictDataByType("crm_closed_won_basis"))
                .thenReturn(Collections.emptyList());
        when(dictTypeService.selectDictDataByType("crm_treatment_item"))
                .thenReturn(Collections.emptyList());

        service.updateCrmOpty(request);

        ArgumentCaptor<CrmOpty> updateCaptor = ArgumentCaptor.forClass(CrmOpty.class);
        verify(crmOptyMapper).updateCrmOpty(updateCaptor.capture());
        org.junit.jupiter.api.Assertions.assertEquals("closed_won", updateCaptor.getValue().getStage());
        org.junit.jupiter.api.Assertions.assertEquals("closed_won", updateCaptor.getValue().getForecastCategory());
        verify(activityEventProducer).produceOpportunityWon(eq(updated));
    }

    @Test
    void updateCrmOpty_normalizesWonCloseReasonAndOverridesStaleStage()
    {
        CrmOpty existing = new CrmOpty();
        existing.setOptyId(OPTY_ID);
        existing.setContactId(CONTACT_ID);
        existing.setStage("New");
        existing.setAmount(new BigDecimal("5000"));

        CrmOpty request = new CrmOpty();
        request.setOptyId(OPTY_ID);
        request.setCloseReason("Won");
        request.setStage("New");
        request.setClosedWonBasis("treatment_agreement_signed");
        request.setTreatmentItemCode("preventive");

        CrmOpty updated = new CrmOpty();
        updated.setOptyId(OPTY_ID);
        updated.setContactId(CONTACT_ID);
        updated.setStage("closed_won");
        updated.setCloseReason("closed_won");
        updated.setClosedWonBasis("treatment_agreement_signed");
        updated.setTreatmentItemCode("preventive");
        updated.setAmount(new BigDecimal("5000"));

        SysDictData wonStage = new SysDictData();
        wonStage.setDictValue("closed_won");
        wonStage.setStatus("0");

        when(crmOptyMapper.selectCrmOptyByOptyId(OPTY_ID)).thenReturn(existing, updated);
        when(crmOptyMapper.updateCrmOpty(any(CrmOpty.class))).thenReturn(1);
        when(dictTypeService.selectDictDataByType("crm_opty_stage_closed_won"))
                .thenReturn(Collections.singletonList(wonStage));
        when(dictTypeService.selectDictDataByType("crm_closed_won_basis"))
                .thenReturn(Collections.emptyList());
        when(dictTypeService.selectDictDataByType("crm_treatment_item"))
                .thenReturn(Collections.emptyList());

        service.updateCrmOpty(request);

        ArgumentCaptor<CrmOpty> updateCaptor = ArgumentCaptor.forClass(CrmOpty.class);
        verify(crmOptyMapper).updateCrmOpty(updateCaptor.capture());
        org.junit.jupiter.api.Assertions.assertEquals("closed_won", updateCaptor.getValue().getCloseReason());
        org.junit.jupiter.api.Assertions.assertEquals("closed_won", updateCaptor.getValue().getStage());
        verify(activityEventProducer).produceOpportunityWon(eq(updated));
    }
}
