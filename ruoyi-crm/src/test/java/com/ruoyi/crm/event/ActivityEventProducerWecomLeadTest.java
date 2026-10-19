package com.ruoyi.crm.event;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.ruoyi.crm.wecom.WecomStateParseResult;
import com.ruoyi.crm.wecom.WecomStateParser;

@ExtendWith(MockitoExtension.class)
class ActivityEventProducerWecomLeadTest
{
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
    void produceWecomLeadCreated_writesLeadCreatedActivity()
    {
        when(activityService.insertTimelineActivity(org.mockito.ArgumentMatchers.any(CrmActivity.class))).thenReturn(1);

        WecomStateParseResult parsed = new WecomStateParseResult();
        parsed.setCrmEventId("evt-1");
        parsed.setChannel("qr_campaign");

        producer.produceWecomLeadCreated("lead-abc", "测试客户", "wm_ext", "LuoHuaDong", "state1", parsed,
                "contact-1", "union-1");

        verify(activityService).insertTimelineActivity(argThat(activity ->
                "lead.created".equals(activity.getEventType())
                        && "Lead".equals(activity.getSubjectType())
                        && "lead-abc".equals(activity.getSubjectId())
                        && "wecom".equals(activity.getChannel())
                        && "lead_created_lead-abc".equals(activity.getChannelEventId())
                        && "业务线索".equals(activity.getRelateType())
                        && "lead-abc".equals(activity.getRelateId())
                        && activity.getPayload() != null
                        && activity.getPayload().contains("wm_ext")
                        && activity.getPayload().contains("union-1")));
    }

    @Test
    void produceWecomAdded_includesAddWayAndSubjectLead()
    {
        when(activityService.insertTimelineActivity(org.mockito.ArgumentMatchers.any(CrmActivity.class))).thenReturn(1);

        WecomStateParseResult parsed = new WecomStateParseResult();
        parsed.setCrmEventId("evt-1");
        parsed.setChannel("poster");
        parsed.setLeadSource("wecom:poster");

        java.util.Date addTime = new java.util.Date(1_700_000_000_000L);
        producer.produceWecomAdded("wm_ext", "staff1", "EVT:abc:poster", parsed, "union-1",
                "Lead", "lead-xyz", 1, addTime);

        verify(activityService).insertTimelineActivity(argThat(activity ->
                "channel.wecom.added".equals(activity.getEventType())
                        && "Lead".equals(activity.getSubjectType())
                        && "lead-xyz".equals(activity.getSubjectId())
                        && activity.getPayload() != null
                        && activity.getPayload().contains("\"add_way\":1")
                        && activity.getPayload().contains("\"state\":\"EVT:abc:poster\"")
                        && activity.getPayload().contains("\"source\":\"wecom_callback\"")));
    }

    @Test
    void produceWecomAdded_includesResolutionWhenProvided()
    {
        when(activityService.insertTimelineActivity(org.mockito.ArgumentMatchers.any(CrmActivity.class))).thenReturn(1);

        WecomStateParseResult parsed = new WecomStateParseResult();
        parsed.setCrmEventId("evt-1");

        producer.produceWecomAdded("wm_ext", "staff1", "EVT:abc", parsed, "union-1",
                "Contact", "contact-1", 1, new java.util.Date(), "lead_merged", "lead-xyz");

        verify(activityService).insertTimelineActivity(argThat(activity ->
                activity.getPayload() != null
                        && activity.getPayload().contains("\"resolution\":\"lead_merged\"")
                        && activity.getPayload().contains("\"merged_lead_id\":\"lead-xyz\"")));
    }

    @Test
    void produceWecomLeadCreated_skipsBlankLeadId()
    {
        producer.produceWecomLeadCreated("  ", "name", "wm_ext", "staff", null, null, null, null);
        verify(activityService, org.mockito.Mockito.never()).insertTimelineActivity(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void produceWebsiteLeadCreated_writesLeadCreatedActivity()
    {
        when(activityService.insertTimelineActivity(org.mockito.ArgumentMatchers.any(CrmActivity.class))).thenReturn(1);

        producer.produceWebsiteLeadCreated("lead-001", "测试客户", "Website Online Register", "/apply");

        verify(activityService).insertTimelineActivity(argThat(activity ->
                "lead.created".equals(activity.getEventType())
                        && "Lead".equals(activity.getSubjectType())
                        && "lead-001".equals(activity.getSubjectId())
                        && "website".equals(activity.getChannel())
                        && "lead_created_lead-001".equals(activity.getChannelEventId())
                        && "website_register".equals(activity.getSource())
                        && "线索创建（官网注册）".equals(activity.getMessageData())
                        && "业务线索".equals(activity.getRelateType())
                        && "lead-001".equals(activity.getRelateId())
                        && activity.getPayload() != null
                        && activity.getPayload().contains("\"lead_source\":\"Website Online Register\"")
                        && activity.getPayload().contains("\"page_path\":\"/apply\"")));
    }

    @Test
    void produceWebsiteLeadCreated_skipsBlankLeadId()
    {
        producer.produceWebsiteLeadCreated("  ", "name", "source", null);
        verify(activityService, org.mockito.Mockito.never()).insertTimelineActivity(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void produceOutreachLeadCreated_writesLeadCreatedActivity()
    {
        when(activityService.insertTimelineActivity(org.mockito.ArgumentMatchers.any(CrmActivity.class))).thenReturn(1);

        producer.produceOutreachLeadCreated("lead-002", "活动客户", "evt-1", "新年峰会");

        verify(activityService).insertTimelineActivity(argThat(activity ->
                "lead.created".equals(activity.getEventType())
                        && "Lead".equals(activity.getSubjectType())
                        && "lead-002".equals(activity.getSubjectId())
                        && "outreach".equals(activity.getChannel())
                        && "lead_created_lead-002".equals(activity.getChannelEventId())
                        && "outreach_registration".equals(activity.getSource())
                        && "线索创建（活动报名）".equals(activity.getMessageData())
                        && "业务线索".equals(activity.getRelateType())
                        && "lead-002".equals(activity.getRelateId())
                        && activity.getPayload() != null
                        && activity.getPayload().contains("\"event_id\":\"evt-1\"")
                        && activity.getPayload().contains("\"event_name\":\"新年峰会\"")));
    }

    @Test
    void produceOutreachLeadCreated_skipsBlankLeadId()
    {
        producer.produceOutreachLeadCreated("  ", "name", "evt-1", "峰会");
        verify(activityService, org.mockito.Mockito.never()).insertTimelineActivity(org.mockito.ArgumentMatchers.any());
    }
}
