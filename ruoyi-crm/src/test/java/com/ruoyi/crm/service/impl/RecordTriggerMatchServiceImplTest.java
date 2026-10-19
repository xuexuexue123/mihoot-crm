package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ruoyi.crm.domain.WNode;
import com.ruoyi.crm.domain.WWorkflow;
import com.ruoyi.crm.domain.dto.RecordTriggerMatchRequest;
import com.ruoyi.crm.domain.vo.WebhookCrmAuthVO;
import com.ruoyi.crm.mapper.WNodeMapper;
import com.ruoyi.crm.mapper.WWorkflowMapper;
import com.ruoyi.crm.service.support.WaitResumeTenantBootstrap;
import com.ruoyi.crm.service.support.WebhookCrmAuthService;

@ExtendWith(MockitoExtension.class)
public class RecordTriggerMatchServiceImplTest
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
    private RecordTriggerMatchServiceImpl recordTriggerMatchService;

    @Test
    public void matchReturnsEmptyForUpdateWithNoChangedFields()
    {
        RecordTriggerMatchRequest request = new RecordTriggerMatchRequest();
        request.setTenant("demo");
        request.setEntityType("lead");
        request.setOperation("update");
        request.setRecordId("lead-1");
        request.setChangedFields(Collections.emptyList());

        assertTrue(recordTriggerMatchService.match(request).isEmpty());
        verify(wNodeMapper, never()).selectRecordTriggerMatchNodes(any(), any());
    }

    @Test
    public void matchQueriesWNodeForCreateOperation()
    {
        RecordTriggerMatchRequest request = new RecordTriggerMatchRequest();
        request.setTenant("demo");
        request.setEntityType("lead");
        request.setOperation("create");
        request.setRecordId("lead-1");

        when(wNodeMapper.selectRecordTriggerMatchNodes(eq("lead"), any()))
                .thenReturn(Collections.emptyList());

        assertEquals(0, recordTriggerMatchService.match(request).size());
        verify(waitResumeTenantBootstrap).bindTenantDataSource("demo");
        verify(wNodeMapper).selectRecordTriggerMatchNodes(eq("lead"), any());
    }

    @Test
    public void matchSkipsTemplateWorkflows()
    {
        RecordTriggerMatchRequest request = new RecordTriggerMatchRequest();
        request.setTenant("demo");
        request.setEntityType("lead");
        request.setOperation("create");
        request.setRecordId("lead-1");

        WNode node = new WNode();
        node.setId("n1");
        node.setWorkflowId("tpl-1");
        node.setType("CRM_RECORD_TRIGGER");
        node.setData("{\"entityType\":\"lead\",\"eventType\":\"record.created\"}");

        WWorkflow template = new WWorkflow();
        template.setId("tpl-1");
        template.setIsTemplate(Boolean.TRUE);
        template.setUserId("u1");

        when(wNodeMapper.selectRecordTriggerMatchNodes(eq("lead"), any()))
                .thenReturn(Collections.singletonList(node));
        when(wWorkflowMapper.selectWWorkflowById("tpl-1")).thenReturn(template);

        assertTrue(recordTriggerMatchService.match(request).isEmpty());
        verify(webhookCrmAuthService, never()).issueTokenForWorkflowOwner(any(), any());
    }

    @Test
    public void matchIncludesInstanceWorkflows()
    {
        RecordTriggerMatchRequest request = new RecordTriggerMatchRequest();
        request.setTenant("demo");
        request.setEntityType("lead");
        request.setOperation("create");
        request.setRecordId("lead-1");

        WNode node = new WNode();
        node.setId("n1");
        node.setWorkflowId("wf-1");
        node.setType("CRM_RECORD_TRIGGER");
        node.setData("{\"entityType\":\"lead\",\"eventType\":\"record.created\"}");

        WWorkflow instance = new WWorkflow();
        instance.setId("wf-1");
        instance.setIsTemplate(Boolean.FALSE);
        instance.setUserId("u1");

        when(wNodeMapper.selectRecordTriggerMatchNodes(eq("lead"), any()))
                .thenReturn(Collections.singletonList(node));
        when(wWorkflowMapper.selectWWorkflowById("wf-1")).thenReturn(instance);
        when(webhookCrmAuthService.issueTokenForWorkflowOwner("u1", "demo"))
                .thenReturn(new WebhookCrmAuthVO());

        assertEquals(1, recordTriggerMatchService.match(request).size());
    }
}
