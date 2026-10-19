package com.ruoyi.crm.service.support;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.domain.dto.LeadConvertWorkflowTriggerRequest;

/**
 * 异步调用 Next BFF POST /workflow-trigger/lead-convert/
 */
@Component
public class LeadConvertWorkflowTriggerClient
{
    private static final Logger log = LoggerFactory.getLogger(LeadConvertWorkflowTriggerClient.class);

    private static final String TENANT_HEADER = "tenant";

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${crm.workflow.trigger.base-url:}")
    private String baseUrl;

    @Value("${crm.workflow.trigger.service-token:}")
    private String serviceToken;

    @Value("${crm.workflow.trigger.enabled:true}")
    private boolean enabled;

    @Value("${crm.workflow.trigger.retry-max-attempts:3}")
    private int retryMaxAttempts;

    @Autowired
    @Qualifier("threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public void fireAsync(String tenantId, LeadConvertWorkflowTriggerRequest request)
    {
        if (!enabled || request == null || !StringUtils.hasText(tenantId))
        {
            return;
        }
        threadPoolTaskExecutor.execute(() -> fireWithRetry(tenantId.trim(), request));
    }

    void fireWithRetry(String tenantId, LeadConvertWorkflowTriggerRequest request)
    {
        if (!StringUtils.hasText(baseUrl))
        {
            log.debug("lead convert workflow trigger skipped: crm.workflow.trigger.base-url not configured");
            return;
        }
        if (!StringUtils.hasText(serviceToken))
        {
            log.warn("lead convert workflow trigger skipped: crm.workflow.trigger.service-token not configured");
            return;
        }

        int attempts = Math.max(1, retryMaxAttempts);
        for (int attempt = 1; attempt <= attempts; attempt++)
        {
            try
            {
                TriggerResult result = fireOnce(tenantId, request);
                if (result.success || result.skipped)
                {
                    if (result.skipped)
                    {
                        log.info("lead convert workflow skipped leadId={} trigger={} reason={}",
                                request.getLeadId(), request.getTrigger(), result.reason);
                    }
                    else
                    {
                        log.info("lead convert workflow triggered leadId={} trigger={} workflowId={}",
                                request.getLeadId(), request.getTrigger(), result.workflowId);
                    }
                    return;
                }
                if (result.httpStatus == HttpStatus.UNAUTHORIZED.value())
                {
                    log.warn("lead convert workflow trigger 401 leadId={} tenant={} — check WORKFLOW_CRM_SERVICE_TOKEN",
                            request.getLeadId(), tenantId);
                    return;
                }
                if (attempt < attempts)
                {
                    sleepBackoff(attempt);
                }
                else
                {
                    log.error("lead convert workflow trigger failed leadId={} trigger={} status={} body={}",
                            request.getLeadId(), request.getTrigger(), result.httpStatus, result.body);
                }
            }
            catch (RestClientException ex)
            {
                if (attempt < attempts)
                {
                    sleepBackoff(attempt);
                }
                else
                {
                    log.error("lead convert workflow trigger failed leadId={} trigger={}: {}",
                            request.getLeadId(), request.getTrigger(), ex.getMessage());
                }
            }
        }
    }

    TriggerResult fireOnce(String tenantId, LeadConvertWorkflowTriggerRequest request)
    {
        String url = baseUrl.trim().replaceAll("/+$", "") + "/workflow-trigger/lead-convert/";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(TENANT_HEADER, tenantId);
        headers.setBearerAuth(serviceToken.trim());

        String json = JSON.toJSONString(request);
        ResponseEntity<String> response = restTemplate.postForEntity(
                url, new HttpEntity<>(json, headers), String.class);

        TriggerResult result = new TriggerResult();
        result.httpStatus = response.getStatusCodeValue();
        result.body = response.getBody();

        if (!response.getStatusCode().is2xxSuccessful())
        {
            return result;
        }

        JSONObject body = parseBody(response.getBody());
        if (body == null)
        {
            result.success = true;
            return result;
        }
        if (Boolean.TRUE.equals(body.getBoolean("skipped")))
        {
            result.skipped = true;
            result.reason = body.getString("reason");
            return result;
        }
        if (Boolean.TRUE.equals(body.getBoolean("ok")))
        {
            result.success = true;
            result.workflowId = body.getString("workflowId");
            return result;
        }
        result.success = true;
        return result;
    }

    private static JSONObject parseBody(String body)
    {
        if (!StringUtils.hasText(body))
        {
            return null;
        }
        try
        {
            return JSON.parseObject(body);
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    private static void sleepBackoff(int attempt)
    {
        try
        {
            Thread.sleep((long) Math.pow(2, attempt - 1) * 500L);
        }
        catch (InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }

    static final class TriggerResult
    {
        boolean success;
        boolean skipped;
        String reason;
        String workflowId;
        int httpStatus;
        String body;
    }
}
