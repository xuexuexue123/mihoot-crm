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
import com.ruoyi.crm.domain.dto.CrmRecordDomainEvent;
import com.ruoyi.crm.service.IRecordTriggerDispatchService;
import com.ruoyi.crm.service.support.RecordTriggerBffSecretValidator;
import com.ruoyi.crm.service.support.RecordTriggerLoopGuard;
import com.ruoyi.crm.service.support.RecordTriggerUtils;

@Service
public class RecordTriggerDispatchServiceImpl implements IRecordTriggerDispatchService
{
    private static final Logger log = LoggerFactory.getLogger(RecordTriggerDispatchServiceImpl.class);

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${record-trigger.next-app-url:}")
    private String nextAppUrl;

    @Value("${record-trigger.bff-secret:}")
    private String bffSecret;

    @Value("${record-trigger.dispatch-enabled:true}")
    private boolean dispatchEnabled;

    @Override
    public void registerAfterCommit(String tenant, CrmRecordDomainEvent event)
    {
        if (!shouldSkip(event))
        {
            if (TransactionSynchronizationManager.isSynchronizationActive())
            {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization()
                {
                    @Override
                    public void afterCommit()
                    {
                        dispatchAfterCommit(tenant, event);
                    }
                });
            }
            else
            {
                dispatchAfterCommit(tenant, event);
            }
        }
    }

    @Override
    public void dispatchAfterCommit(String tenant, CrmRecordDomainEvent event)
    {
        if (!dispatchEnabled || shouldSkip(event))
        {
            return;
        }
        if (StringUtils.isBlank(nextAppUrl))
        {
            log.debug("record trigger dispatch skipped: record-trigger.next-app-url is not configured");
            return;
        }
        if (StringUtils.isBlank(tenant) || event == null)
        {
            log.warn("record trigger dispatch skipped: tenant or event is blank");
            return;
        }

        try
        {
            String base = nextAppUrl.trim().replaceAll("/+$", "");
            String url = base + "/internal/record-trigger/dispatch";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (StringUtils.isNotBlank(bffSecret))
            {
                headers.set(RecordTriggerBffSecretValidator.HEADER_NAME, bffSecret.trim());
            }

            Map<String, Object> body = new HashMap<>();
            body.put("tenant", tenant);
            body.put("event", toEventPayload(event));

            ResponseEntity<String> response = restTemplate.postForEntity(
                    url, new HttpEntity<>(body, headers), String.class);
            if (!response.getStatusCode().is2xxSuccessful())
            {
                log.warn("record trigger dispatch non-2xx tenant={}, entity={}, recordId={}, status={}, body={}",
                        tenant, event.getEntityType(), event.getRecordId(),
                        response.getStatusCode(), response.getBody());
            }
            else
            {
                log.debug("record trigger dispatch ok tenant={}, entity={}, recordId={}, operation={}",
                        tenant, event.getEntityType(), event.getRecordId(), event.getOperation());
            }
        }
        catch (Exception e)
        {
            log.warn("record trigger dispatch failed tenant={}, entity={}, recordId={}: {}",
                    tenant, event.getEntityType(), event.getRecordId(), e.getMessage());
        }
    }

    private boolean shouldSkip(CrmRecordDomainEvent event)
    {
        if (event == null)
        {
            return true;
        }
        if ("workflow".equals(event.getSource()))
        {
            return true;
        }
        if (RecordTriggerLoopGuard.shouldSkipDispatch())
        {
            return true;
        }
        if (RecordTriggerUtils.OPERATION_UPDATE.equals(event.getOperation())
                && (event.getChangedFields() == null || event.getChangedFields().isEmpty()))
        {
            return true;
        }
        return false;
    }

    private Map<String, Object> toEventPayload(CrmRecordDomainEvent event)
    {
        Map<String, Object> payload = new HashMap<>();
        payload.put("entityType", event.getEntityType());
        payload.put("eventType", event.getEventType());
        payload.put("recordId", event.getRecordId());
        payload.put("record", event.getRecord());
        payload.put("previousRecord", event.getPreviousRecord());
        payload.put("changedFields", event.getChangedFields());
        payload.put("occurredAt", event.getOccurredAt());
        payload.put("source", event.getSource());
        payload.put("operation", event.getOperation());
        return payload;
    }
}
