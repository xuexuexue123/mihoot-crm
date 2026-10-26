package com.ruoyi.crm.service.webhook;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmOutboundWebhook;
import com.ruoyi.crm.domain.CrmOutboundWebhookDelivery;
import com.ruoyi.crm.domain.dto.OutboundWebhookDispatchEvent;
import com.ruoyi.crm.mapper.CrmOutboundWebhookDeliveryMapper;
import com.ruoyi.crm.mapper.CrmOutboundWebhookMapper;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

@Component
public class OutboundWebhookDispatcher
{
    private static final Logger log = LoggerFactory.getLogger(OutboundWebhookDispatcher.class);

    @Autowired
    private CrmOutboundWebhookMapper webhookMapper;

    @Autowired
    private CrmOutboundWebhookDeliveryMapper deliveryMapper;

    @Autowired
    private OutboundWebhookDeliveryWorker deliveryWorker;

    @Autowired
    @Qualifier("threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Value("${crm.outbound-webhook.allow-http:false}")
    private boolean allowHttp;

    public void dispatch(OutboundWebhookDispatchEvent event)
    {
        if (event == null || StringUtils.isBlank(event.getTenant()) || StringUtils.isBlank(event.getEventType()))
        {
            return;
        }
        java.util.List<CrmOutboundWebhook> hooks = webhookMapper.selectEnabledByEvent(event.getTenant(),
                event.getEventType());
        if (hooks == null || hooks.isEmpty())
        {
            return;
        }
        JSONObject context = event.getPayload() == null ? new JSONObject() : event.getPayload();
        for (CrmOutboundWebhook hook : hooks)
        {
            enqueue(event, hook, context, OutboundWebhookConstants.SOURCE_EVENT, OutboundWebhookEventIds.forEvent(event),
                    true);
        }
    }

    public CrmOutboundWebhookDelivery enqueueTest(CrmOutboundWebhook hook, JSONObject context)
    {
        OutboundWebhookDispatchEvent event = new OutboundWebhookDispatchEvent();
        event.setTenant(hook.getTenantId());
        event.setEventType(hook.getEventType());
        event.setOccurredAt(java.time.Instant.now());
        event.setPayload(context);
        return enqueue(event, hook, context, OutboundWebhookConstants.SOURCE_TEST, OutboundWebhookEventIds.forTest(),
                false);
    }

    private CrmOutboundWebhookDelivery enqueue(OutboundWebhookDispatchEvent event, CrmOutboundWebhook hook,
            JSONObject context, String source, String eventId, boolean async)
    {
        JSONObject data = OutboundWebhookJsonTemplateRenderer.render(hook.getPayloadTemplate(), context);
        String payloadJson = OutboundWebhookEnvelope.toJson(eventId, hook.getEventType(), event.getOccurredAt(),
                event.getTenant(), data);
        Date now = DateUtils.getNowDate();
        CrmOutboundWebhookDelivery row = new CrmOutboundWebhookDelivery();
        row.setDeliveryId(IdUtils.fastUUID());
        row.setTenantId(event.getTenant());
        row.setWebhookId(hook.getWebhookId());
        row.setEventId(eventId);
        row.setEventType(hook.getEventType());
        row.setSource(source);
        row.setStatus(OutboundWebhookConstants.STATUS_PENDING);
        row.setAttempt(0);
        row.setRequestId(row.getDeliveryId());
        row.setPayloadJson(payloadJson);
        row.setCreateTime(now);
        row.setUpdateTime(now);
        int inserted = deliveryMapper.insertIgnoreDuplicate(row);
        if (inserted <= 0)
        {
            return null;
        }
        if (async)
        {
            scheduleDeliver(event.getTenant(), row.getDeliveryId());
        }
        return row;
    }

    public void scheduleDeliver(String tenantId, String deliveryId)
    {
        threadPoolTaskExecutor.execute(() -> {
            try
            {
                DynamicDataSourceContextHolder.setDataSourceKey(tenantId);
                deliveryWorker.deliverById(deliveryId, allowHttp);
            }
            catch (Exception ex)
            {
                log.warn("outbound webhook deliver {} failed: {}", deliveryId, ex.getMessage());
            }
            finally
            {
                DynamicDataSourceContextHolder.clearDataSourceKey();
            }
        });
    }
}
