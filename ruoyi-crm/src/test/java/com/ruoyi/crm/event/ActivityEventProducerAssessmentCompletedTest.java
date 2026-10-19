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
class ActivityEventProducerAssessmentCompletedTest
{
    private static final String LEAD_ID = "lead-uuid-1";

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
    void produceAssessmentCompleted_writesEngagementEvent()
    {
        when(activityService.insertTimelineActivity(org.mockito.ArgumentMatchers.any(CrmActivity.class))).thenReturn(1);
        Date completedAt = new Date();
        producer.produceAssessmentCompleted(LEAD_ID, "张三", completedAt);

        verify(activityService).insertTimelineActivity(argThat(activity ->
                "engagement.assessment.completed".equals(activity.getEventType())
                        && "完成评估".equals(activity.getEventName())
                        && "Lead".equals(activity.getSubjectType())
                        && LEAD_ID.equals(activity.getSubjectId())
                        && "crm".equals(activity.getChannel())
                        && ("lead_assessment_" + LEAD_ID).equals(activity.getChannelEventId())
                        && "业务线索".equals(activity.getRelateType())
                        && LEAD_ID.equals(activity.getRelateId())
                        && activity.getPayload() != null
                        && activity.getPayload().contains("lead.survey_json")
                        && activity.getPayload().contains(LEAD_ID)));
        verify(customerMetricsService, org.mockito.Mockito.never()).incrementMetric(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.any());
    }

    @Test
    void produceAssessmentCompleted_skipsWhenLeadMissing()
    {
        producer.produceAssessmentCompleted(null, "title", new Date());
        verify(activityService, org.mockito.Mockito.never())
                .insertTimelineActivity(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void produceAssessmentCompleted_qualifyQrWritesChannelAndTemplate()
    {
        when(activityService.insertTimelineActivity(org.mockito.ArgumentMatchers.any(CrmActivity.class))).thenReturn(1);
        Date completedAt = new Date();
        String templateId = "8a530792-da39-4230-826f-fa768b54373e";
        producer.produceAssessmentCompleted(LEAD_ID, "张三", completedAt, "qualify_qr", templateId);

        verify(activityService).insertTimelineActivity(argThat(activity ->
                "engagement.assessment.completed".equals(activity.getEventType())
                        && "完成认定评估".equals(activity.getEventName())
                        && "Lead".equals(activity.getSubjectType())
                        && LEAD_ID.equals(activity.getSubjectId())
                        && "qualify_qr".equals(activity.getChannel())
                        && Integer.valueOf(70).equals(activity.getImportance())
                        && ("lead_assessment_" + LEAD_ID).equals(activity.getChannelEventId())
                        && activity.getPayload() != null
                        && activity.getPayload().contains("qualify_qr")
                        && activity.getPayload().contains(templateId)
                        && activity.getPayload().contains(LEAD_ID)));
    }
}
