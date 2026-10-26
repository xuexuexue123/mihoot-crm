package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.exception.ServiceException;
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
class CrmOptyServiceImplTreatmentItemTest
{
    private static final String OPTY_ID = "opty-ti-1";

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
    void updateCrmOpty_rejectsClosedWonWithoutTreatmentItemCode()
    {
        CrmOpty existing = baseExisting("negotiation");
        CrmOpty request = new CrmOpty();
        request.setOptyId(OPTY_ID);
        request.setStage("closed_won");
        request.setClosedWonBasis("down_payment_received");

        stubWonStageAndBasis();
        when(crmOptyMapper.selectCrmOptyByOptyId(OPTY_ID)).thenReturn(existing);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.updateCrmOpty(request));
        assertTrue(ex.getMessage().contains("treatment_item_code required"));
        verify(crmOptyMapper, never()).updateCrmOpty(any());
    }

    @Test
    void updateCrmOpty_rejectsClosedWonWithUnknownTreatmentItemCode()
    {
        CrmOpty existing = baseExisting("negotiation");
        CrmOpty request = new CrmOpty();
        request.setOptyId(OPTY_ID);
        request.setStage("closed_won");
        request.setClosedWonBasis("down_payment_received");
        request.setTreatmentItemCode("not_a_real_code");

        stubWonStageAndBasis();
        SysDictData implant = dict("implant");
        when(dictTypeService.selectDictDataByType("crm_treatment_item"))
                .thenReturn(Collections.singletonList(implant));
        when(crmOptyMapper.selectCrmOptyByOptyId(OPTY_ID)).thenReturn(existing);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.updateCrmOpty(request));
        assertTrue(ex.getMessage().contains("treatment_item_code not in crm_treatment_item"));
        verify(crmOptyMapper, never()).updateCrmOpty(any());
    }

    @Test
    void updateCrmOpty_acceptsClosedWonWithDictTreatmentItemCode()
    {
        CrmOpty existing = baseExisting("negotiation");
        CrmOpty request = new CrmOpty();
        request.setOptyId(OPTY_ID);
        request.setStage("closed_won");
        request.setClosedWonBasis("down_payment_received");
        request.setTreatmentItemCode("implant");

        CrmOpty updated = baseExisting("closed_won");
        updated.setClosedWonBasis("down_payment_received");
        updated.setTreatmentItemCode("implant");

        stubWonStageAndBasis();
        when(dictTypeService.selectDictDataByType("crm_treatment_item"))
                .thenReturn(Collections.singletonList(dict("implant")));
        when(crmOptyMapper.selectCrmOptyByOptyId(OPTY_ID)).thenReturn(existing, updated);
        when(crmOptyMapper.updateCrmOpty(any(CrmOpty.class))).thenReturn(1);

        service.updateCrmOpty(request);

        verify(crmOptyMapper).updateCrmOpty(any(CrmOpty.class));
        verify(recallScheduleService).onOpportunityWon(updated);
    }

    private void stubWonStageAndBasis()
    {
        when(dictTypeService.selectDictDataByType("crm_opty_stage_closed_won"))
                .thenReturn(Collections.singletonList(dict("closed_won")));
        when(dictTypeService.selectDictDataByType("crm_closed_won_basis"))
                .thenReturn(Collections.singletonList(dict("down_payment_received")));
    }

    private static CrmOpty baseExisting(String stage)
    {
        CrmOpty o = new CrmOpty();
        o.setOptyId(OPTY_ID);
        o.setContactId("c1");
        o.setStage(stage);
        return o;
    }

    private static SysDictData dict(String value)
    {
        SysDictData d = new SysDictData();
        d.setDictValue(value);
        d.setStatus("0");
        return d;
    }
}
