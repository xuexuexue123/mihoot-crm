package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmSurveys;
import com.ruoyi.crm.domain.dto.SurveyOutreachSendRequest;
import com.ruoyi.crm.domain.vo.SurveyOutreachTaskVo;
import com.ruoyi.crm.mapper.CrmSurveyOutreachLogMapper;
import com.ruoyi.crm.service.ICrmContactService;
import com.ruoyi.crm.service.ICrmSurveysService;

@ExtendWith(MockitoExtension.class)
class SurveyOutreachSendServiceTest
{
    private static final String SURVEY_ID = "survey-uuid-1";
    private static final String CONTACT_ID = "contact-uuid-1";

    @Mock
    private ICrmSurveysService crmSurveysService;

    @Mock
    private ICrmContactService crmContactService;

    @Mock
    private SurveyOutreachLogService surveyOutreachLogService;

    @Mock
    private CrmSurveyOutreachLogMapper outreachLogMapper;

    @InjectMocks
    private SurveyOutreachSendService surveyOutreachSendService;

    @Test
    void confirmSend_createsSentTask()
    {
        stubIssuableSurvey();
        when(crmContactService.selectCrmContactByContactId(CONTACT_ID)).thenReturn(new CrmContact());
        SurveyOutreachTaskVo expected = buildTaskVo(1L);
        when(surveyOutreachLogService.recordSentUpsert(
                SURVEY_ID,
                SurveyOutreachLogService.CHANNEL_WECOM_SIDEBAR,
                SurveyOutreachLogService.RECIPIENT_TYPE_CONTACT,
                CONTACT_ID,
                null)).thenReturn(expected);

        SurveyOutreachTaskVo result = surveyOutreachSendService.confirmSend(SURVEY_ID, sidebarRequest());

        verify(surveyOutreachLogService).recordSentUpsert(
                SURVEY_ID,
                SurveyOutreachLogService.CHANNEL_WECOM_SIDEBAR,
                SurveyOutreachLogService.RECIPIENT_TYPE_CONTACT,
                CONTACT_ID,
                null);
        assertEquals(Long.valueOf(1L), result.getId());
        assertEquals(SURVEY_ID, result.getSurveyId());
        assertEquals("sent", result.getStatus());
    }

    @Test
    void confirmSend_updatesSentAtOnRepeat()
    {
        stubIssuableSurvey();
        when(crmContactService.selectCrmContactByContactId(CONTACT_ID)).thenReturn(new CrmContact());
        when(surveyOutreachLogService.recordSentUpsert(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.isNull()))
                .thenReturn(buildTaskVo(42L));

        surveyOutreachSendService.confirmSend(SURVEY_ID, sidebarRequest());
        surveyOutreachSendService.confirmSend(SURVEY_ID, sidebarRequest());

        verify(surveyOutreachLogService, org.mockito.Mockito.times(2)).recordSentUpsert(
                SURVEY_ID,
                SurveyOutreachLogService.CHANNEL_WECOM_SIDEBAR,
                SurveyOutreachLogService.RECIPIENT_TYPE_CONTACT,
                CONTACT_ID,
                null);
    }

    @Test
    void confirmSend_rejectsMissingContact()
    {
        stubIssuableSurvey();
        when(crmContactService.selectCrmContactByContactId(CONTACT_ID)).thenReturn(null);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> surveyOutreachSendService.confirmSend(SURVEY_ID, sidebarRequest()));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getCode());
    }

    @Test
    void confirmSend_rejectsMissingSurvey()
    {
        when(crmSurveysService.selectCrmSurveysById(SURVEY_ID)).thenReturn(null);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> surveyOutreachSendService.confirmSend(SURVEY_ID, sidebarRequest()));
        assertEquals(HttpStatus.NOT_FOUND, ex.getCode());
    }

    @Test
    void confirmSend_acceptsWhatsappInboxChannel()
    {
        stubIssuableSurvey();
        when(crmContactService.selectCrmContactByContactId(CONTACT_ID)).thenReturn(new CrmContact());
        SurveyOutreachTaskVo expected = buildTaskVo(2L);
        expected.setChannel(SurveyOutreachLogService.CHANNEL_WHATSAPP_INBOX);
        when(surveyOutreachLogService.recordSentUpsert(
                SURVEY_ID,
                SurveyOutreachLogService.CHANNEL_WHATSAPP_INBOX,
                SurveyOutreachLogService.RECIPIENT_TYPE_CONTACT,
                CONTACT_ID,
                null)).thenReturn(expected);

        SurveyOutreachTaskVo result = surveyOutreachSendService.confirmSend(SURVEY_ID, whatsappInboxRequest());

        verify(surveyOutreachLogService).recordSentUpsert(
                SURVEY_ID,
                SurveyOutreachLogService.CHANNEL_WHATSAPP_INBOX,
                SurveyOutreachLogService.RECIPIENT_TYPE_CONTACT,
                CONTACT_ID,
                null);
        assertEquals(Long.valueOf(2L), result.getId());
        assertEquals(SurveyOutreachLogService.CHANNEL_WHATSAPP_INBOX, result.getChannel());
    }

    @Test
    void confirmSend_rejectsWrongChannel()
    {
        SurveyOutreachSendRequest request = sidebarRequest();
        request.setChannel("wecom_mass");

        ServiceException ex = assertThrows(ServiceException.class,
                () -> surveyOutreachSendService.confirmSend(SURVEY_ID, request));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getCode());
    }

    @Test
    void listTasks_filtersByRecipientAndChannel()
    {
        SurveyOutreachStatsService statsService = new SurveyOutreachStatsService();
        org.springframework.test.util.ReflectionTestUtils.setField(
                statsService, "outreachLogMapper", outreachLogMapper);

        SurveyOutreachTaskVo task = new SurveyOutreachTaskVo();
        task.setSurveyId(SURVEY_ID);
        task.setRecipientId(CONTACT_ID);
        task.setChannel(SurveyOutreachLogService.CHANNEL_WECOM_SIDEBAR);
        when(outreachLogMapper.selectTasksBySurveyId(
                SURVEY_ID, CONTACT_ID, SurveyOutreachLogService.CHANNEL_WECOM_SIDEBAR))
                .thenReturn(Collections.singletonList(task));

        List<SurveyOutreachTaskVo> rows = statsService.listTasks(
                SURVEY_ID, CONTACT_ID, SurveyOutreachLogService.CHANNEL_WECOM_SIDEBAR);

        assertEquals(1, rows.size());
        verify(outreachLogMapper).selectTasksBySurveyId(
                eq(SURVEY_ID), eq(CONTACT_ID), eq(SurveyOutreachLogService.CHANNEL_WECOM_SIDEBAR));
    }

    private void stubIssuableSurvey()
    {
        CrmSurveys survey = new CrmSurveys();
        survey.setId(SURVEY_ID);
        survey.setStatus("published");
        when(crmSurveysService.selectCrmSurveysById(SURVEY_ID)).thenReturn(survey);
    }

    private static SurveyOutreachSendRequest sidebarRequest()
    {
        SurveyOutreachSendRequest request = new SurveyOutreachSendRequest();
        request.setContactId(CONTACT_ID);
        request.setChannel(SurveyOutreachLogService.CHANNEL_WECOM_SIDEBAR);
        request.setRecipientType(SurveyOutreachLogService.RECIPIENT_TYPE_CONTACT);
        request.setRecipientId(CONTACT_ID);
        return request;
    }

    private static SurveyOutreachSendRequest whatsappInboxRequest()
    {
        SurveyOutreachSendRequest request = new SurveyOutreachSendRequest();
        request.setContactId(CONTACT_ID);
        request.setChannel(SurveyOutreachLogService.CHANNEL_WHATSAPP_INBOX);
        request.setRecipientType(SurveyOutreachLogService.RECIPIENT_TYPE_CONTACT);
        request.setRecipientId(CONTACT_ID);
        return request;
    }

    private static SurveyOutreachTaskVo buildTaskVo(Long id)
    {
        SurveyOutreachTaskVo vo = new SurveyOutreachTaskVo();
        vo.setId(id);
        vo.setSurveyId(SURVEY_ID);
        vo.setChannel(SurveyOutreachLogService.CHANNEL_WECOM_SIDEBAR);
        vo.setRecipientType(SurveyOutreachLogService.RECIPIENT_TYPE_CONTACT);
        vo.setRecipientId(CONTACT_ID);
        vo.setStatus("sent");
        vo.setSentAt(new Date());
        return vo;
    }
}
