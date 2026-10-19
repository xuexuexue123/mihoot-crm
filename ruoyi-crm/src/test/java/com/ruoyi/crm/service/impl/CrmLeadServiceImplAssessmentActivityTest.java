package com.ruoyi.crm.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.rule.SmartPushEventExecutor;
import com.ruoyi.crm.service.ICrmWecomExternalContactService;
import com.ruoyi.crm.service.nurture.support.EventNurtureHookSupport;
import com.ruoyi.crm.service.support.LeadConvertTriggerService;
import com.ruoyi.crm.service.support.RecordTriggerCrudHookSupport;
import com.ruoyi.crm.wechatmp.service.MpLeadConvertSyncSupport;
import com.ruoyi.crm.wecom.WecomFollowUserResolver;
import com.ruoyi.crm.wecom.WecomLeadConvertSyncSupport;

@ExtendWith(MockitoExtension.class)
class CrmLeadServiceImplAssessmentActivityTest
{
    private static final String LEAD_ID = "lead-uuid-1";

    @Mock
    private CrmLeadMapper crmLeadMapper;
    @Mock
    private RecordTriggerCrudHookSupport recordTriggerCrudHookSupport;
    @Mock
    private EventNurtureHookSupport nurtureHookSupport;
    @Mock
    private WecomLeadConvertSyncSupport wecomLeadConvertSyncSupport;
    @Mock
    private MpLeadConvertSyncSupport mpLeadConvertSyncSupport;
    @Mock
    private ICrmWecomExternalContactService wecomExternalContactService;
    @Mock
    private LeadConvertTriggerService leadConvertTriggerService;
    @Mock
    private WecomFollowUserResolver wecomFollowUserResolver;
    @Mock
    private SmartPushEventExecutor smartPushEventExecutor;
    @Mock
    private ActivityEventProducer activityEventProducer;

    @InjectMocks
    private CrmLeadServiceImpl leadService;

    @Test
    void insertCrmLead_withSurveyJson_producesAssessmentCompleted()
    {
        CrmLead lead = new CrmLead();
        lead.setLeadId(LEAD_ID);
        lead.setLeadName("张三");
        lead.setStage("New");
        lead.setUserId(1L);
        lead.setDeptId(100L);
        lead.setSurveyJson("{\"answers\":1}");
        when(crmLeadMapper.insertCrmLead(lead)).thenReturn(1);

        leadService.insertCrmLead(lead);

        verify(activityEventProducer).produceAssessmentCompleted(eq(LEAD_ID), eq("张三"), any());
    }

    @Test
    void insertCrmLead_withoutSurveyJson_skipsAssessment()
    {
        CrmLead lead = new CrmLead();
        lead.setLeadId(LEAD_ID);
        lead.setLeadName("张三");
        lead.setStage("New");
        lead.setUserId(1L);
        lead.setDeptId(100L);
        when(crmLeadMapper.insertCrmLead(lead)).thenReturn(1);

        leadService.insertCrmLead(lead);

        verify(activityEventProducer, never()).produceAssessmentCompleted(any(), any(), any());
    }

    @Test
    void updateCrmLead_surveyJsonNullToValue_producesAssessmentCompleted()
    {
        CrmLead existing = new CrmLead();
        existing.setLeadId(LEAD_ID);
        existing.setLeadName("张三");
        existing.setSurveyJson(null);

        CrmLead incoming = new CrmLead();
        incoming.setLeadId(LEAD_ID);
        incoming.setSurveyJson("{\"answers\":1}");

        CrmLead updated = new CrmLead();
        updated.setLeadId(LEAD_ID);
        updated.setLeadName("张三");
        updated.setSurveyJson("{\"answers\":1}");

        when(crmLeadMapper.selectCrmLeadByLeadId(LEAD_ID)).thenReturn(existing, updated);
        when(crmLeadMapper.updateCrmLead(incoming)).thenReturn(1);

        leadService.updateCrmLead(incoming);

        verify(activityEventProducer).produceAssessmentCompleted(eq(LEAD_ID), eq("张三"), any());
    }

    @Test
    void updateCrmLead_surveyJsonAlreadyPresent_skipsAssessment()
    {
        CrmLead existing = new CrmLead();
        existing.setLeadId(LEAD_ID);
        existing.setLeadName("张三");
        existing.setSurveyJson("{\"answers\":0}");

        CrmLead incoming = new CrmLead();
        incoming.setLeadId(LEAD_ID);
        incoming.setSurveyJson("{\"answers\":1}");

        CrmLead updated = new CrmLead();
        updated.setLeadId(LEAD_ID);
        updated.setLeadName("张三");
        updated.setSurveyJson("{\"answers\":1}");

        when(crmLeadMapper.selectCrmLeadByLeadId(LEAD_ID)).thenReturn(existing, updated);
        when(crmLeadMapper.updateCrmLead(incoming)).thenReturn(1);

        leadService.updateCrmLead(incoming);

        verify(activityEventProducer, never()).produceAssessmentCompleted(any(), any(), any());
    }
}
