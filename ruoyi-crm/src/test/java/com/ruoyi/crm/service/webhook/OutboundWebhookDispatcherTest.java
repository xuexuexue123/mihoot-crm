package com.ruoyi.crm.service.webhook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.domain.CrmOutboundWebhook;
import com.ruoyi.crm.domain.CrmOutboundWebhookDelivery;
import com.ruoyi.crm.domain.dto.OutboundWebhookDispatchEvent;
import com.ruoyi.crm.mapper.CrmOutboundWebhookDeliveryMapper;
import com.ruoyi.crm.mapper.CrmOutboundWebhookMapper;

@ExtendWith(MockitoExtension.class)
public class OutboundWebhookDispatcherTest
{
    @Mock
    private CrmOutboundWebhookMapper webhookMapper;

    @Mock
    private CrmOutboundWebhookDeliveryMapper deliveryMapper;

    @Mock
    private OutboundWebhookDeliveryWorker deliveryWorker;

    @Mock
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @InjectMocks
    private OutboundWebhookDispatcher dispatcher;

    @Test
    public void duplicateInsertIsSkipped()
    {
        CrmOutboundWebhook hook = new CrmOutboundWebhook();
        hook.setWebhookId("wh-1");
        hook.setTenantId("demo");
        hook.setEventType(OutboundWebhookConstants.EVENT_LEAD_CREATED);
        when(webhookMapper.selectEnabledByEvent("demo", OutboundWebhookConstants.EVENT_LEAD_CREATED))
                .thenReturn(Collections.singletonList(hook));
        when(deliveryMapper.insertIgnoreDuplicate(any(CrmOutboundWebhookDelivery.class))).thenReturn(0);

        OutboundWebhookDispatchEvent event = new OutboundWebhookDispatchEvent();
        event.setTenant("demo");
        event.setEventType(OutboundWebhookConstants.EVENT_LEAD_CREATED);
        event.setOccurredAt(Instant.now());
        JSONObject payload = new JSONObject();
        JSONObject lead = new JSONObject();
        lead.put("leadId", "lead-1");
        payload.put("lead", lead);
        event.setPayload(payload);

        dispatcher.dispatch(event);
        verify(threadPoolTaskExecutor, never()).execute(any());
    }

    @Test
    public void enqueueTestAlwaysNewId()
    {
        CrmOutboundWebhook hook = new CrmOutboundWebhook();
        hook.setWebhookId("wh-1");
        hook.setTenantId("demo");
        hook.setEventType(OutboundWebhookConstants.EVENT_CONTACT_CREATED);
        when(deliveryMapper.insertIgnoreDuplicate(any(CrmOutboundWebhookDelivery.class))).thenReturn(1);

        CrmOutboundWebhookDelivery row = dispatcher.enqueueTest(hook, new JSONObject());
        assertEquals(OutboundWebhookConstants.SOURCE_TEST, row.getSource());
        assertEquals("wh-1", row.getWebhookId());
        assertNull(row.getHttpStatus());
        verify(threadPoolTaskExecutor, never()).execute(any());
    }
}
