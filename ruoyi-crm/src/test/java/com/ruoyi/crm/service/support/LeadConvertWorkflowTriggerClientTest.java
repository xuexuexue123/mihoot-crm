package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.dto.LeadConvertWorkflowTriggerRequest;

class LeadConvertWorkflowTriggerClientTest
{
    private LeadConvertWorkflowTriggerClient client;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp()
    {
        client = new LeadConvertWorkflowTriggerClient();
        restTemplate = mock(RestTemplate.class);
        ReflectionTestUtils.setField(client, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(client, "baseUrl", "http://localhost:3000");
        ReflectionTestUtils.setField(client, "serviceToken", "test-token");
        ReflectionTestUtils.setField(client, "enabled", true);
        ReflectionTestUtils.setField(client, "retryMaxAttempts", 1);
    }

    @Test
    void fireWithRetry_successOnOk()
    {
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("{\"ok\":true,\"workflowId\":\"wf-1\"}", HttpStatus.OK));

        CrmLead lead = new CrmLead();
        lead.setLeadId("lead-1");
        lead.setLeadName("张三");
        lead.setMobilePhone("13800138000");
        LeadConvertWorkflowTriggerRequest req = LeadConvertWorkflowTriggerRequest.from(
                lead, "stage_arrived", "org-1", null);

        LeadConvertWorkflowTriggerClient.TriggerResult result = client.fireOnce("tenant3", req);
        assertTrue(result.success);
    }

    @Test
    void fireWithRetry_skippedResponse()
    {
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("{\"skipped\":true,\"reason\":\"no_workflow\"}", HttpStatus.OK));

        CrmLead lead = new CrmLead();
        lead.setLeadId("lead-1");
        LeadConvertWorkflowTriggerRequest req = LeadConvertWorkflowTriggerRequest.from(
                lead, "event_check_in", null, "evt-1");

        LeadConvertWorkflowTriggerClient.TriggerResult result = client.fireOnce("tenant3", req);
        assertTrue(result.skipped);
    }
}
