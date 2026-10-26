package com.ruoyi.crm.event;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
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
class ActivityEventProducerEventRegistrationTest
{
    private static final String CONTACT_ID = "contact-uuid-1";
    private static final String LEAD_ID = "lead-uuid-1";
    private static final String EVENT_ID = "event-uuid-1";
    private static final String REGISTRATION_ID = "reg-uuid-1";
    private static final String SESSION_ID = "session-uuid-1";

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
    void produceEventRegistrationSubmitted_writesContactEvent()
    {
        when(activityService.insertTimelineActivity(org.mockito.ArgumentMatchers.any(CrmActivity.class))).thenReturn(1);
        Date registeredAt = new Date();

        producer.produceEventRegistrationSubmitted(
                "Contact", CONTACT_ID, "website",
                EVENT_ID, "春季沙龙", REGISTRATION_ID, SESSION_ID,
                "h5", "confirmed", registeredAt, "wx-app", "o-open");

        verify(activityService).insertTimelineActivity(argThat(activity ->
                "event.registration.submitted".equals(activity.getEventType())
                        && "活动报名".equals(activity.getEventName())
                        && "conversion".equals(activity.getEventCategory())
                        && Integer.valueOf(70).equals(activity.getImportance())
                        && "Contact".equals(activity.getSubjectType())
                        && CONTACT_ID.equals(activity.getSubjectId())
                        && "website".equals(activity.getChannel())
                        && ("event:registration:" + REGISTRATION_ID).equals(activity.getChannelEventId())
                        && "Event".equals(activity.getRelateType())
                        && EVENT_ID.equals(activity.getRelateId())
                        && "春季沙龙".equals(activity.getRelateName())
                        && activity.getMessageData() != null
                        && activity.getMessageData().contains("春季沙龙")
                        && activity.getPayload() != null
                        && activity.getPayload().contains(REGISTRATION_ID)
                        && activity.getPayload().contains("\"registration_source\":\"h5\"")
                        && activity.getContext() != null
                        && activity.getContext().contains("wx-app")
                        && activity.getContext().contains("o-open")));
        verify(customerMetricsService).incrementMetric(
                org.mockito.ArgumentMatchers.eq(CONTACT_ID),
                org.mockito.ArgumentMatchers.eq("event.registration.submitted"),
                org.mockito.ArgumentMatchers.eq("website"),
                org.mockito.ArgumentMatchers.isNull());
    }

    @Test
    void produceEventRegistrationSubmitted_writesLeadEventWithoutMetrics()
    {
        when(activityService.insertTimelineActivity(org.mockito.ArgumentMatchers.any(CrmActivity.class))).thenReturn(1);

        producer.produceEventRegistrationSubmitted(
                "Lead", LEAD_ID, "manual",
                EVENT_ID, "春季沙龙", REGISTRATION_ID, SESSION_ID,
                "crm_manual", "confirmed", new Date(), null, null);

        verify(activityService).insertTimelineActivity(argThat(activity ->
                "Lead".equals(activity.getSubjectType())
                        && LEAD_ID.equals(activity.getSubjectId())
                        && "manual".equals(activity.getChannel())
                        && ("event:registration:" + REGISTRATION_ID).equals(activity.getChannelEventId())));
        verify(customerMetricsService, never()).incrementMetric(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any());
    }

    @Test
    void produceEventRegistrationSubmitted_skipsWhenSubjectMissing()
    {
        producer.produceEventRegistrationSubmitted(
                "Contact", null, "website",
                EVENT_ID, "春季沙龙", REGISTRATION_ID, SESSION_ID,
                "h5", "confirmed", new Date(), null, null);

        verify(activityService, never()).insertTimelineActivity(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void produceEventRegistrationSubmitted_skipsWhenRegistrationIdMissing()
    {
        producer.produceEventRegistrationSubmitted(
                "Contact", CONTACT_ID, "website",
                EVENT_ID, "春季沙龙", "  ", SESSION_ID,
                "h5", "confirmed", new Date(), null, null);

        verify(activityService, never()).insertTimelineActivity(org.mockito.ArgumentMatchers.any());
    }
}
