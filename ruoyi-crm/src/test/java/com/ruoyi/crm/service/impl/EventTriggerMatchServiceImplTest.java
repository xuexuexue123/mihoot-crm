package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ruoyi.crm.domain.WNode;
import com.ruoyi.crm.domain.WWorkflow;
import com.ruoyi.crm.domain.dto.EventTriggerMatchRequest;
import com.ruoyi.crm.domain.vo.EventTriggerMatchVO;
import com.ruoyi.crm.domain.vo.WebhookCrmAuthVO;
import com.ruoyi.crm.mapper.WNodeMapper;
import com.ruoyi.crm.mapper.WWorkflowMapper;
import com.ruoyi.crm.service.support.EventTriggerUtils;
import com.ruoyi.crm.service.support.WaitResumeTenantBootstrap;
import com.ruoyi.crm.service.support.WebhookCrmAuthService;

@ExtendWith(MockitoExtension.class)
public class EventTriggerMatchServiceImplTest
{
    @Mock
    private WaitResumeTenantBootstrap waitResumeTenantBootstrap;

    @Mock
    private WNodeMapper wNodeMapper;

    @Mock
    private WWorkflowMapper wWorkflowMapper;

    @Mock
    private WebhookCrmAuthService webhookCrmAuthService;

    @InjectMocks
    private EventTriggerMatchServiceImpl eventTriggerMatchService;

    @Test
    public void matchReturnsEmptyWhenNoNodes()
    {
        EventTriggerMatchRequest request = baseRequest();
        when(wNodeMapper.selectEventTriggerMatchNodes(eq("engagement.assessment.completed")))
                .thenReturn(Collections.emptyList());

        assertEquals(0, eventTriggerMatchService.match(request).size());
        verify(waitResumeTenantBootstrap).bindTenantDataSource("demo");
        verify(wNodeMapper).selectEventTriggerMatchNodes("engagement.assessment.completed");
    }

    @Test
    public void matchReturnsNodeWithCrmAuth()
    {
        EventTriggerMatchRequest request = baseRequest();

        WNode node = new WNode();
        node.setId("node-1");
        node.setWorkflowId("wf-1");
        node.setType(EventTriggerUtils.NODE_TYPE);
        node.setData("{\"eventType\":\"engagement.assessment.completed\"}");

        WWorkflow workflow = new WWorkflow();
        workflow.setId("wf-1");
        workflow.setUserId("42");

        WebhookCrmAuthVO auth = new WebhookCrmAuthVO();
        auth.setToken("jwt-token");
        auth.setTenant("demo");

        when(wNodeMapper.selectEventTriggerMatchNodes("engagement.assessment.completed"))
                .thenReturn(Collections.singletonList(node));
        when(wWorkflowMapper.selectWWorkflowById("wf-1")).thenReturn(workflow);
        when(webhookCrmAuthService.issueTokenForWorkflowOwner("42", "demo")).thenReturn(auth);

        List<EventTriggerMatchVO> results = eventTriggerMatchService.match(request);
        assertEquals(1, results.size());
        EventTriggerMatchVO vo = results.get(0);
        assertEquals("wf-1", vo.getWorkflowId());
        assertEquals("node-1", vo.getNodeId());
        assertEquals("demo", vo.getTenant());
        assertNotNull(vo.getCrmAuth());
        assertEquals("jwt-token", vo.getCrmAuth().getToken());
        assertEquals("activityEvent", vo.getNodeData().get("variableName"));
    }

    private static EventTriggerMatchRequest baseRequest()
    {
        EventTriggerMatchRequest request = new EventTriggerMatchRequest();
        request.setTenant("demo");
        request.setEventType("engagement.assessment.completed");
        request.setSubjectType("Lead");
        request.setSubjectId("lead-1");
        return request;
    }
}
