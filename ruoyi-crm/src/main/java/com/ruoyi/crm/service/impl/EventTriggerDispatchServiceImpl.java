package com.ruoyi.crm.service.impl;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.client.RestTemplate;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.dto.EventTriggerDispatchPayload;
import com.ruoyi.crm.service.IEventTriggerDispatchService;
import com.ruoyi.crm.service.support.RecordTriggerBffSecretValidator;

@Service
public class EventTriggerDispatchServiceImpl implements IEventTriggerDispatchService
{
    private static final Logger log = LoggerFactory.getLogger(EventTriggerDispatchServiceImpl.class);

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${event-trigger.next-app-url:}")
    private String nextAppUrl;

    @Value("${event-trigger.bff-secret:}")
    private String bffSecret;

    @Value("${event-trigger.dispatch-enabled:true}")
    private boolean dispatchEnabled;

    @Override
    public void registerAfterCommit(String tenant, EventTriggerDispatchPayload payload)
    {
        if (shouldSkip(payload))
        {
            return;
        }
        if (TransactionSynchronizationManager.isSynchronizationActive())
        {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization()
            {
                @Override
                public void afterCommit()
                {
                    dispatchAfterCommit(tenant, payload);
                }
            });
        }
        else
        {
            dispatchAfterCommit(tenant, payload);
        }
    }

    @Override
    public void dispatchAfterCommit(String tenant, EventTriggerDispatchPayload payload)
    {
        if (!dispatchEnabled || shouldSkip(payload))
        {
            return;
        }
        if (StringUtils.isBlank(nextAppUrl))
        {
            log.debug("event trigger dispatch skipped: event-trigger.next-app-url is not configured");
            return;
        }
        if (StringUtils.isBlank(tenant))
        {
            log.warn("event trigger dispatch skipped: tenant is blank");
            return;
        }

        try
        {
            String base = nextAppUrl.trim().replaceAll("/+$", "");
            String url = base + "/internal/event-trigger/dispatch/";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (StringUtils.isNotBlank(bffSecret))
            {
                headers.set(RecordTriggerBffSecretValidator.HEADER_NAME, bffSecret.trim());
            }

            Map<String, Object> body = new HashMap<>();
            body.put("tenant", tenant);
            body.put("pulseId", payload.getPulseId());
            body.put("event", toEventMap(payload));

            ResponseEntity<String> response = restTemplate.postForEntity(
                    url, new HttpEntity<>(body, headers), String.class);
            if (!response.getStatusCode().is2xxSuccessful())
            {
                log.warn("event trigger dispatch non-2xx tenant={}, pulseId={}, eventType={}, status={}, body={}",
                        tenant, payload.getPulseId(), payload.getEventType(),
                        response.getStatusCode(), response.getBody());
            }
            else
            {
                log.debug("event trigger dispatch ok tenant={}, pulseId={}, eventType={}",
                        tenant, payload.getPulseId(), payload.getEventType());
            }
        }
        catch (Exception e)
        {
            log.warn("event trigger dispatch failed tenant={}, pulseId={}, eventType={}: {}",
                    tenant, payload.getPulseId(), payload.getEventType(), e.getMessage());
        }
    }

    private boolean shouldSkip(EventTriggerDispatchPayload payload)
    {
        return payload == null || StringUtils.isBlank(payload.getEventType());
    }

    private Map<String, Object> toEventMap(EventTriggerDispatchPayload payload)
    {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", payload.getEventType());
        event.put("subjectType", payload.getSubjectType());
        event.put("subjectId", payload.getSubjectId());
        event.put("occurredAt", payload.getOccurredAt());
        event.put("subjectEntity", payload.getSubjectEntity());
        event.put("subject", payload.getSubject());
        return event;
    }
}
