package com.ruoyi.crm.event;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.domain.CrmActivity;
import com.ruoyi.crm.domain.CrmOpty;
import com.ruoyi.crm.service.ICrmActivityService;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.ICustomerMetricsService;
import com.ruoyi.crm.wecom.WecomExternalContactProfileResolver;
import com.ruoyi.crm.wecom.WecomIdentityResolutionSupport;
import com.ruoyi.crm.wecom.WecomStateParser;

@ExtendWith(MockitoExtension.class)
class ActivityEventProducerOpportunityWonTest
{
    private static final String CONTACT_ID = "contact-uuid-1";
    private static final String OPTY_ID = "opty-uuid-1";

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
    void produceOpportunityWon_writesCrmEvent()
    {
        when(activityService.insertTimelineActivity(org.mockito.ArgumentMatchers.any(CrmActivity.class))).thenReturn(1);
        Date closeDate = new Date();
        CrmOpty opty = new CrmOpty();
        opty.setOptyId(OPTY_ID);
        opty.setContactId(CONTACT_ID);
        opty.setName("种植方案A");
        opty.setAmount(new BigDecimal("50000.00"));
        opty.setClosedWonBasis("down_payment_received");
        opty.setCloseDate(closeDate);

        producer.produceOpportunityWon(opty);

        verify(activityService).insertTimelineActivity(argThat(activity ->
                "opportunity.won".equals(activity.getEventType())
                        && "Contact".equals(activity.getSubjectType())
                        && CONTACT_ID.equals(activity.getSubjectId())
                        && "crm".equals(activity.getChannel())
                        && ("opty_won_" + OPTY_ID).equals(activity.getChannelEventId())
                        && "Opportunity".equals(activity.getRelateType())
                        && OPTY_ID.equals(activity.getRelateId())
                        && "种植方案A (¥50000)".equals(activity.getMessageData())
                        && activity.getPayload() != null
                        && activity.getPayload().contains("\"amount\"")
                        && activity.getPayload().contains("down_payment_received")
                        && activity.getPayload().contains("won_date")));
        verify(customerMetricsService).incrementMetric(CONTACT_ID, "opportunity.won", "crm", new BigDecimal("50000.00"));
    }

    @Test
    void produceOpportunityWon_skipsWhenContactMissing()
    {
        CrmOpty opty = new CrmOpty();
        opty.setOptyId(OPTY_ID);
        opty.setName("种植方案A");

        producer.produceOpportunityWon(opty);
        verify(activityService, org.mockito.Mockito.never())
                .insertTimelineActivity(org.mockito.ArgumentMatchers.any());
    }
}
