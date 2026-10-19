package com.ruoyi.crm.event;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.domain.CrmActivity;
import com.ruoyi.crm.service.ICrmActivityService;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.ICustomerMetricsService;
import com.ruoyi.crm.wecom.WecomExternalContactProfileResolver;
import com.ruoyi.crm.wecom.WecomIdentityResolutionSupport;
import com.ruoyi.crm.wecom.WecomStateParser;

@ExtendWith(MockitoExtension.class)
class ActivityEventProducerSurveyCompletedTest
{
    private static final String CONTACT_ID = "contact-uuid-1";
    private static final String SURVEY_ID = "survey-uuid-1";
    private static final String RESPONDENT_ID = "respondent-uuid-1";

    @Mock
    private ICrmCustomerIdentityService identityService;

    @Mock
    private ICrmActivityService activityService;

    @Mock
    private ICustomerMetricsService customerMetricsService;

    @Mock
    private WecomStateParser wecomStateParser;

    @Mock
    private WecomExternalContactProfileResolver profileResolver;

    @Mock
    private WecomIdentityResolutionSupport wecomIdentityResolutionSupport;

    @InjectMocks
    private ActivityEventProducer producer;

    @Test
    void produceSurveyCompleted_writesEngagementEvent()
    {
        when(activityService.insertTimelineActivity(org.mockito.ArgumentMatchers.any(CrmActivity.class))).thenReturn(1);
        Date completedAt = new Date();
        producer.produceSurveyCompleted(
                CONTACT_ID, SURVEY_ID, "客户满意度问卷", RESPONDENT_ID, "wecom_sidebar", completedAt);

        verify(activityService).insertTimelineActivity(argThat(activity ->
                "engagement.survey.completed".equals(activity.getEventType())
                        && "Contact".equals(activity.getSubjectType())
                        && CONTACT_ID.equals(activity.getSubjectId())
                        && "wecom".equals(activity.getChannel())
                        && ("survey_completed_" + RESPONDENT_ID).equals(activity.getChannelEventId())
                        && activity.getPayload() != null
                        && activity.getPayload().contains("wecom_sidebar")));
        verify(customerMetricsService).incrementMetric(org.mockito.ArgumentMatchers.eq(CONTACT_ID), org.mockito.ArgumentMatchers.eq("engagement.survey.completed"), org.mockito.ArgumentMatchers.eq("wecom"), org.mockito.ArgumentMatchers.isNull());
    }

    @Test
    void produceSurveyCompleted_skipsWhenContactMissing()
    {
        producer.produceSurveyCompleted(null, SURVEY_ID, "title", RESPONDENT_ID, "wecom_sidebar", new Date());
        verify(activityService, org.mockito.Mockito.never())
                .insertTimelineActivity(org.mockito.ArgumentMatchers.any());
    }
}
