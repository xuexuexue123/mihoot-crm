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
class ActivityEventProducerOpportunityCreatedTest
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
    void produceOpportunityCreated_writesCrmEvent()
    {
        when(activityService.insertTimelineActivity(org.mockito.ArgumentMatchers.any(CrmActivity.class))).thenReturn(1);
        Date createTime = new Date();
        CrmOpty opty = new CrmOpty();
        opty.setOptyId(OPTY_ID);
        opty.setContactId(CONTACT_ID);
        opty.setName("补牙治疗方案");
        opty.setAmount(new BigDecimal("5000"));
        opty.setStage("New");
        opty.setCreateTime(createTime);

        producer.produceOpportunityCreated(opty);

        verify(activityService).insertTimelineActivity(argThat(activity ->
                "opportunity.created".equals(activity.getEventType())
                        && Integer.valueOf(70).equals(activity.getImportance())
                        && "Contact".equals(activity.getSubjectType())
                        && CONTACT_ID.equals(activity.getSubjectId())
                        && "crm".equals(activity.getChannel())
                        && ("opty_created_" + OPTY_ID).equals(activity.getChannelEventId())
                        && "Opportunity".equals(activity.getRelateType())
                        && OPTY_ID.equals(activity.getRelateId())
                        && "创建商机：补牙治疗方案".equals(activity.getMessageData())
                        && activity.getPayload() != null
                        && activity.getPayload().contains("\"amount\"")));
        verify(customerMetricsService).incrementMetric(org.mockito.ArgumentMatchers.eq(CONTACT_ID), org.mockito.ArgumentMatchers.eq("opportunity.created"), org.mockito.ArgumentMatchers.eq("crm"), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void produceOpportunityCreated_skipsWhenContactMissing()
    {
        CrmOpty opty = new CrmOpty();
        opty.setOptyId(OPTY_ID);
        opty.setName("补牙治疗方案");

        producer.produceOpportunityCreated(opty);
        verify(activityService, org.mockito.Mockito.never())
                .insertTimelineActivity(org.mockito.ArgumentMatchers.any());
    }
}
