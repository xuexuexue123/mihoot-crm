package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.dto.LeadQualifySurveySubmitRequest;
import com.ruoyi.crm.domain.vo.LeadQualifySurveySubmitResultVo;
import com.ruoyi.crm.domain.vo.LeadQualifySurveyVo;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.service.support.OutreachRegistrationTokenService.QualifyVerifiedPayload;
import com.ruoyi.ems.domain.SysReTemplate;
import com.ruoyi.ems.service.ISysReTemplateService;

@ExtendWith(MockitoExtension.class)
class LeadQualifySurveyServiceTest
{
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String TENANT = "tenant-a";
    private static final String LEAD_ID = "lead-1";
    private static final String TEMPLATE_ID = "8a530792-da39-4230-826f-fa768b54373e";
    private static final String TOKEN = "signed.token";
    private static final long EXP = 1_700_000_800L;

    @Mock
    private OutreachRegistrationTokenService outreachRegistrationTokenService;

    @Mock
    private CrmLeadMapper crmLeadMapper;

    @Mock
    private ISysReTemplateService templateService;

    @Mock
    private ActivityEventProducer activityEventProducer;

    @InjectMocks
    private LeadQualifySurveyService service;

    @Test
    void loadSurvey_pending()
    {
        when(crmLeadMapper.selectCrmLeadByLeadId(LEAD_ID)).thenReturn(lead(null));
        when(templateService.selectSysReTemplateByTemplateId(TEMPLATE_ID)).thenReturn(template());

        LeadQualifySurveyVo vo = service.loadSurvey(payload());

        assertEquals(LEAD_ID, vo.getLeadId());
        assertEquals("张*", vo.getDisplayName());
        assertEquals(LeadQualifySurveyService.STATUS_PENDING, vo.getStatus());
        assertNull(vo.getAnswers());
        assertEquals("{\"title\":\"评估\"}", vo.getSurveyJson());
        assertEquals(OutreachRegistrationTokenService.CHANNEL_QUALIFY_QR, vo.getChannel());
    }

    @Test
    void loadSurvey_completed()
    {
        when(crmLeadMapper.selectCrmLeadByLeadId(LEAD_ID))
                .thenReturn(lead("{\"budget\":\"Yes\",\"surveyScores\":{\"total\":2}}"));
        when(templateService.selectSysReTemplateByTemplateId(TEMPLATE_ID)).thenReturn(template());

        LeadQualifySurveyVo vo = service.loadSurvey(payload());

        assertEquals(LeadQualifySurveyService.STATUS_COMPLETED, vo.getStatus());
        assertEquals("Yes", vo.getAnswers().path("budget").asText());
    }

    @Test
    void loadSurvey_masksEmptyNameAsCustomer()
    {
        CrmLead lead = lead(null);
        lead.setLeadName(null);
        lead.setFullName(null);
        when(crmLeadMapper.selectCrmLeadByLeadId(LEAD_ID)).thenReturn(lead);
        when(templateService.selectSysReTemplateByTemplateId(TEMPLATE_ID)).thenReturn(template());

        LeadQualifySurveyVo vo = service.loadSurvey(payload());
        assertEquals("客户", vo.getDisplayName());
    }

    @Test
    void submit_writesScoresAndProducesActivity()
    {
        when(outreachRegistrationTokenService.verifyQualify(TOKEN)).thenReturn(payload());
        when(crmLeadMapper.selectCrmLeadByLeadId(LEAD_ID)).thenReturn(lead(null));
        when(crmLeadMapper.updateCrmLead(any(CrmLead.class))).thenReturn(1);

        ObjectNode data = MAPPER.createObjectNode();
        data.put("budget", "Yes");
        data.put("need", "High");
        LeadQualifySurveySubmitRequest request = new LeadQualifySurveySubmitRequest();
        request.setToken(TOKEN);
        request.setData(data);

        LeadQualifySurveySubmitResultVo result = service.submit(request);

        assertEquals(LEAD_ID, result.getLeadId());
        assertEquals(LeadQualifySurveyService.STATUS_COMPLETED, result.getStatus());

        ArgumentCaptor<CrmLead> captor = ArgumentCaptor.forClass(CrmLead.class);
        verify(crmLeadMapper).updateCrmLead(captor.capture());
        String surveyJson = captor.getValue().getSurveyJson();
        assertTrue(surveyJson.contains("surveyScores"));
        assertTrue(surveyJson.contains("\"BANT\""));

        verify(activityEventProducer).produceAssessmentCompleted(
                eq(LEAD_ID), eq("张三"), any(),
                eq(OutreachRegistrationTokenService.CHANNEL_QUALIFY_QR), eq(TEMPLATE_ID));
    }

    @Test
    void submit_alreadyCompleted_returns409()
    {
        when(outreachRegistrationTokenService.verifyQualify(TOKEN)).thenReturn(payload());
        when(crmLeadMapper.selectCrmLeadByLeadId(LEAD_ID))
                .thenReturn(lead("{\"budget\":\"Yes\"}"));

        ObjectNode data = MAPPER.createObjectNode();
        data.put("budget", "No");
        LeadQualifySurveySubmitRequest request = new LeadQualifySurveySubmitRequest();
        request.setToken(TOKEN);
        request.setData(data);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.submit(request));
        assertEquals(HttpStatus.CONFLICT, ex.getCode());
        verify(crmLeadMapper, never()).updateCrmLead(any());
        verify(activityEventProducer, never()).produceAssessmentCompleted(
                any(), any(), any(), any(), any());
    }

    @Test
    void submit_missingData_returns400()
    {
        LeadQualifySurveySubmitRequest request = new LeadQualifySurveySubmitRequest();
        request.setToken(TOKEN);
        request.setData(null);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.submit(request));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getCode());
    }

    @Test
    void submit_leadMissing_returns404()
    {
        when(outreachRegistrationTokenService.verifyQualify(TOKEN)).thenReturn(payload());
        when(crmLeadMapper.selectCrmLeadByLeadId(LEAD_ID)).thenReturn(null);

        ObjectNode data = MAPPER.createObjectNode();
        data.put("budget", "Yes");
        LeadQualifySurveySubmitRequest request = new LeadQualifySurveySubmitRequest();
        request.setToken(TOKEN);
        request.setData(data);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.submit(request));
        assertEquals(HttpStatus.NOT_FOUND, ex.getCode());
    }

    @Test
    void parseAnswersObject_andMaskDisplayName()
    {
        assertNull(LeadQualifySurveyService.parseAnswersObject(null));
        assertNull(LeadQualifySurveyService.parseAnswersObject(""));
        assertNull(LeadQualifySurveyService.parseAnswersObject("[]"));
        assertEquals(0, LeadQualifySurveyService.parseAnswersObject("{}").size());
        assertEquals("客*", LeadQualifySurveyService.maskDisplayName("客户"));
        assertEquals("客户", LeadQualifySurveyService.maskDisplayName(null));
        assertEquals("A", LeadQualifySurveyService.maskDisplayName("A"));
    }

    private static QualifyVerifiedPayload payload()
    {
        return new QualifyVerifiedPayload(TENANT, LEAD_ID, TEMPLATE_ID,
                OutreachRegistrationTokenService.CHANNEL_QUALIFY_QR, EXP);
    }

    private static CrmLead lead(String surveyJson)
    {
        CrmLead lead = new CrmLead();
        lead.setLeadId(LEAD_ID);
        lead.setLeadName("张三");
        lead.setSurveyJson(surveyJson);
        return lead;
    }

    private static SysReTemplate template()
    {
        SysReTemplate t = new SysReTemplate();
        t.setTemplateId(TEMPLATE_ID);
        t.setTemplateName("认定潜客评估");
        t.setContent("{\"title\":\"评估\"}");
        return t;
    }
}
