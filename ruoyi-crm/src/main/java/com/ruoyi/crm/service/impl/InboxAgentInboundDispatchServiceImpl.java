package com.ruoyi.crm.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import com.ruoyi.crm.domain.dto.InboxAgentInboundPayload;
import com.ruoyi.crm.service.IInboxAgentInboundDispatchService;
import com.ruoyi.crm.service.InboxChannelGateService;
import com.ruoyi.crm.service.support.RecordTriggerBffSecretValidator;

/**
 * 对齐 EventTrigger / WhatsApp media persist：afterCommit + RestTemplate POST Next /internal/...
 */
@Service
public class InboxAgentInboundDispatchServiceImpl implements IInboxAgentInboundDispatchService
{
    private static final Logger log = LoggerFactory.getLogger(InboxAgentInboundDispatchServiceImpl.class);

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${inbox-agent.next-app-url:}")
    private String nextAppUrl;

    @Value("${inbox-agent.bff-secret:}")
    private String bffSecret;

    @Value("${inbox-agent.dispatch-enabled:true}")
    private boolean dispatchEnabled;

    @Autowired(required = false)
    @Qualifier("threadPoolTaskExecutor")
    private Executor executor;

    @Autowired
    private InboxChannelGateService inboxChannelGateService;

    @Override
    public void scheduleDispatch(String tenant, InboxAgentInboundPayload payload)
    {
        if (!dispatchEnabled || shouldSkip(payload) || StringUtils.isBlank(tenant))
        {
            return;
        }
        if (!inboxChannelGateService.isEnabled(tenant, payload != null ? payload.getChannel() : null))
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
                    runAsync(tenant, payload);
                }
            });
        }
        else
        {
            runAsync(tenant, payload);
        }
    }

    private void runAsync(String tenant, InboxAgentInboundPayload payload)
    {
        if (executor != null)
        {
            executor.execute(() -> dispatch(tenant, payload));
        }
        else
        {
            dispatch(tenant, payload);
        }
    }

    private void dispatch(String tenant, InboxAgentInboundPayload payload)
    {
        if (StringUtils.isBlank(nextAppUrl))
        {
            log.debug("inbox-agent dispatch skipped: inbox-agent.next-app-url is not configured");
            return;
        }
        try
        {
            String base = nextAppUrl.trim().replaceAll("/+$", "");
            String url = base + "/internal/inbox-agent/inbound/";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("tenant", tenant.trim());
            if (StringUtils.isNotBlank(bffSecret))
            {
                headers.set(RecordTriggerBffSecretValidator.HEADER_NAME, bffSecret.trim());
            }

            Map<String, Object> body = new HashMap<>();
            body.put("tenant", tenant.trim());
            body.put("channel", payload.getChannel());
            body.put("conversationId", payload.getConversationId());
            body.put("inboundMessageId", payload.getInboundMessageId());
            body.put("accountId", payload.getAccountId());
            body.put("peerId", payload.getPeerId());
            body.put("msgType", payload.getMsgType());
            body.put("text", payload.getText());
            body.put("keywordAutoReplied", payload.isKeywordAutoReplied());

            ResponseEntity<String> response = restTemplate.postForEntity(
                    url, new HttpEntity<>(body, headers), String.class);
            if (!response.getStatusCode().is2xxSuccessful())
            {
                log.warn("inbox-agent dispatch non-2xx tenant={} channel={} conversationId={} status={} body={}",
                        tenant, payload.getChannel(), payload.getConversationId(),
                        response.getStatusCode(), response.getBody());
            }
            else
            {
                log.debug("inbox-agent dispatch ok tenant={} channel={} conversationId={} inboundMessageId={}",
                        tenant, payload.getChannel(), payload.getConversationId(),
                        payload.getInboundMessageId());
            }
        }
        catch (Exception e)
        {
            log.warn("inbox-agent dispatch failed tenant={} channel={} conversationId={}: {}",
                    tenant, payload.getChannel(), payload.getConversationId(), e.getMessage());
        }
    }

    private boolean shouldSkip(InboxAgentInboundPayload payload)
    {
        return payload == null
                || StringUtils.isBlank(payload.getChannel())
                || payload.getConversationId() == null;
    }
}
