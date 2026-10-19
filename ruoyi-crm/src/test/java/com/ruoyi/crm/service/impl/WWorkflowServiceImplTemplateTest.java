package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.domain.WConnection;
import com.ruoyi.crm.domain.WNode;
import com.ruoyi.crm.domain.WWorkflow;
import com.ruoyi.crm.domain.vo.WorkflowEdgeVO;
import com.ruoyi.crm.domain.vo.WorkflowNodeVO;
import com.ruoyi.crm.mapper.WConnectionMapper;
import com.ruoyi.crm.mapper.WNodeMapper;
import com.ruoyi.crm.mapper.WWorkflowMapper;
import com.ruoyi.crm.service.support.EventTriggerNodeSupport;
import com.ruoyi.crm.service.support.RecordTriggerNodeSupport;
import com.ruoyi.crm.service.support.WebhookPathIndexSupport;
import com.ruoyi.crm.service.support.WebhookPathUtils;
import com.ruoyi.crm.service.support.WorkflowPublishSupport;

@ExtendWith(MockitoExtension.class)
class WWorkflowServiceImplTemplateTest
{
    @Mock
    private WWorkflowMapper wWorkflowMapper;

    @Mock
    private WNodeMapper wNodeMapper;

    @Mock
    private WConnectionMapper wConnectionMapper;

    @Mock
    private WebhookPathIndexSupport webhookPathIndexSupport;

    @Mock
    private RecordTriggerNodeSupport recordTriggerNodeSupport;

    @Mock
    private EventTriggerNodeSupport eventTriggerNodeSupport;

    @InjectMocks
    private WWorkflowServiceImpl wWorkflowService;

    @Test
    void insertTemplate_forcesPublishFalse()
    {
        WWorkflow workflow = new WWorkflow();
        workflow.setId("tpl-1");
        workflow.setName("模板A");
        workflow.setUserId("u1");
        workflow.setIsTemplate(Boolean.TRUE);
        workflow.setNodes(Collections.singletonList(manualTrigger("mt-1", true)));

        when(wWorkflowMapper.insertWWorkflow(any(WWorkflow.class))).thenReturn(1);
        when(webhookPathIndexSupport.requireCurrentTenantId()).thenReturn("tenant-1");

        int rows = wWorkflowService.insertWWorkflow(workflow);

        assertEquals(1, rows);
        assertEquals(Boolean.FALSE, workflow.getPublishAssistant());
        assertEquals(Boolean.FALSE, workflow.getPublishSidebar());
        verify(webhookPathIndexSupport, never()).enrichPublicPathsForSave(anyString(), anyString(), anyList());
        verify(wNodeMapper).insertWNode(any(WNode.class));
    }

    @Test
    void updateTemplateFlag_true_clearsPublishColumnsAndNodes()
    {
        WWorkflow existing = sourceWorkflow("wf-1", "源流程", false);
        when(wWorkflowMapper.selectWWorkflowById("wf-1")).thenReturn(headerOnly(existing));
        when(wNodeMapper.selectWNodeListByWorkflowIds(anyList())).thenReturn(Collections.singletonList(
                toDbNode("mt-1", "wf-1", WorkflowPublishSupport.NODE_TYPE, manualPublishJson(true))));
        when(wConnectionMapper.selectWConnectionListByWorkflowIds(anyList())).thenReturn(Collections.emptyList());
        when(webhookPathIndexSupport.requireCurrentTenantId()).thenReturn("tenant-1");
        when(wWorkflowMapper.updateWWorkflowTemplateFlag(any(WWorkflow.class))).thenReturn(1);

        WWorkflow result = wWorkflowService.updateTemplateFlag("wf-1", Boolean.TRUE);

        ArgumentCaptor<WWorkflow> flagCaptor = ArgumentCaptor.forClass(WWorkflow.class);
        verify(wWorkflowMapper).updateWWorkflowTemplateFlag(flagCaptor.capture());
        assertEquals(Boolean.TRUE, flagCaptor.getValue().getIsTemplate());
        assertEquals(Boolean.FALSE, flagCaptor.getValue().getPublishAssistant());
        assertEquals(Boolean.FALSE, flagCaptor.getValue().getPublishSidebar());
        verify(webhookPathIndexSupport, never()).enrichPublicPathsForSave(anyString(), anyString(), anyList());
        assertNotNull(result);
    }

    @Test
    void cloneAsInstance_remapsIdsAndClearsPublish()
    {
        stubSourceGraph("wf-src");
        AtomicReference<WWorkflow> inserted = new AtomicReference<>();
        when(wWorkflowMapper.insertWWorkflow(any(WWorkflow.class))).thenAnswer(invocation -> {
            WWorkflow arg = invocation.getArgument(0);
            inserted.set(arg);
            return 1;
        });
        when(webhookPathIndexSupport.requireCurrentTenantId()).thenReturn("tenant-1");
        doAnswer(invocation -> {
            WWorkflow created = inserted.get();
            when(wWorkflowMapper.selectWWorkflowById(created.getId())).thenReturn(headerOnly(created));
            when(wNodeMapper.selectWNodeListByWorkflowIds(eq(Collections.singletonList(created.getId()))))
                    .thenReturn(toDbNodes(created.getNodes(), created.getId()));
            when(wConnectionMapper.selectWConnectionListByWorkflowIds(eq(Collections.singletonList(created.getId()))))
                    .thenReturn(toDbEdges(created.getEdges(), created.getId()));
            return null;
        }).when(wNodeMapper).deleteWNodeByWorkflowId(anyString());

        WWorkflow cloned = wWorkflowService.cloneAsInstance("wf-src", null, null);

        assertNotNull(cloned);
        assertNotEquals("wf-src", cloned.getId());
        assertFalse(Boolean.TRUE.equals(cloned.getIsTemplate()));
        assertEquals("线索跟进 (copy)", inserted.get().getName());
        assertEquals(Boolean.FALSE, inserted.get().getPublishAssistant());
        assertEquals(Boolean.FALSE, inserted.get().getPublishSidebar());

        List<String> newNodeIds = inserted.get().getNodes().stream()
                .map(WorkflowNodeVO::getId)
                .collect(java.util.stream.Collectors.toList());
        assertFalse(newNodeIds.contains("mt-1"));
        assertFalse(newNodeIds.contains("wh-1"));
    }

    @Test
    void saveAsTemplate_setsTemplateTrue()
    {
        stubSourceGraph("wf-src");
        AtomicReference<WWorkflow> inserted = new AtomicReference<>();
        when(wWorkflowMapper.insertWWorkflow(any(WWorkflow.class))).thenAnswer(invocation -> {
            WWorkflow arg = invocation.getArgument(0);
            inserted.set(arg);
            return 1;
        });
        when(webhookPathIndexSupport.requireCurrentTenantId()).thenReturn("tenant-1");
        doAnswer(invocation -> {
            WWorkflow created = inserted.get();
            when(wWorkflowMapper.selectWWorkflowById(created.getId())).thenReturn(headerOnly(created));
            when(wNodeMapper.selectWNodeListByWorkflowIds(eq(Collections.singletonList(created.getId()))))
                    .thenReturn(Collections.emptyList());
            when(wConnectionMapper.selectWConnectionListByWorkflowIds(eq(Collections.singletonList(created.getId()))))
                    .thenReturn(Collections.emptyList());
            return null;
        }).when(wNodeMapper).deleteWNodeByWorkflowId(anyString());

        WWorkflow saved = wWorkflowService.saveAsTemplate("wf-src", "我的模板", null);

        assertNotNull(saved);
        assertTrue(Boolean.TRUE.equals(inserted.get().getIsTemplate()));
        assertEquals("我的模板", inserted.get().getName());
        verify(webhookPathIndexSupport, never()).enrichPublicPathsForSave(anyString(), anyString(), anyList());
    }

    private void stubSourceGraph(String id)
    {
        WWorkflow source = sourceWorkflow(id, "线索跟进", false);
        when(wWorkflowMapper.selectWWorkflowById(id)).thenReturn(headerOnly(source));
        List<WNode> nodes = new ArrayList<>();
        nodes.add(toDbNode("mt-1", id, WorkflowPublishSupport.NODE_TYPE, manualPublishJson(true)));
        nodes.add(toDbNode("wh-1", id, WebhookPathUtils.NODE_TYPE,
                "{\"publicPath\":\"enc-1\",\"path\":\"slug\"}"));
        when(wNodeMapper.selectWNodeListByWorkflowIds(eq(Collections.singletonList(id)))).thenReturn(nodes);
        WConnection edge = new WConnection();
        edge.setId("e-1");
        edge.setWorkflowId(id);
        edge.setFromNodeId("mt-1");
        edge.setToNodeId("wh-1");
        edge.setFromOutput("main");
        edge.setToInput("main");
        when(wConnectionMapper.selectWConnectionListByWorkflowIds(eq(Collections.singletonList(id))))
                .thenReturn(Collections.singletonList(edge));
    }

    private static WWorkflow sourceWorkflow(String id, String name, boolean template)
    {
        WWorkflow workflow = new WWorkflow();
        workflow.setId(id);
        workflow.setName(name);
        workflow.setUserId("u1");
        workflow.setOrganizationId("org-1");
        workflow.setIsTemplate(template);
        workflow.setPublishAssistant(Boolean.TRUE);
        workflow.setPublishSidebar(Boolean.TRUE);
        return workflow;
    }

    private static WWorkflow headerOnly(WWorkflow source)
    {
        WWorkflow header = new WWorkflow();
        header.setId(source.getId());
        header.setName(source.getName());
        header.setUserId(source.getUserId());
        header.setOrganizationId(source.getOrganizationId());
        header.setIsTemplate(source.getIsTemplate());
        header.setPublishAssistant(source.getPublishAssistant());
        header.setPublishSidebar(source.getPublishSidebar());
        return header;
    }

    private static WorkflowNodeVO manualTrigger(String id, boolean enabled)
    {
        WorkflowNodeVO node = new WorkflowNodeVO();
        node.setId(id);
        node.setType(WorkflowPublishSupport.NODE_TYPE);
        Map<String, Object> assistant = new HashMap<>();
        assistant.put("enabled", enabled);
        assistant.put("description", "desc");
        Map<String, Object> publish = new HashMap<>();
        publish.put("assistant", assistant);
        Map<String, Object> data = new HashMap<>();
        data.put("publish", publish);
        node.setData(data);
        return node;
    }

    private static String manualPublishJson(boolean enabled)
    {
        return "{\"publish\":{\"assistant\":{\"enabled\":" + enabled
                + ",\"description\":\"desc\"},\"wecomSidebar\":{\"enabled\":" + enabled + "}}}";
    }

    private static WNode toDbNode(String id, String workflowId, String type, String dataJson)
    {
        WNode node = new WNode();
        node.setId(id);
        node.setWorkflowId(workflowId);
        node.setType(type);
        node.setName(type);
        node.setData(dataJson);
        node.setPosition("{\"x\":0,\"y\":0}");
        return node;
    }

    private static List<WNode> toDbNodes(List<WorkflowNodeVO> nodes, String workflowId)
    {
        List<WNode> result = new ArrayList<>();
        if (nodes == null)
        {
            return result;
        }
        for (WorkflowNodeVO vo : nodes)
        {
            WNode node = new WNode();
            node.setId(vo.getId());
            node.setWorkflowId(workflowId);
            node.setType(vo.getType());
            node.setName(vo.getType());
            node.setData(vo.getData() == null ? null : com.alibaba.fastjson2.JSON.toJSONString(vo.getData()));
            node.setPosition("{\"x\":0,\"y\":0}");
            result.add(node);
        }
        return result;
    }

    private static List<WConnection> toDbEdges(List<WorkflowEdgeVO> edges, String workflowId)
    {
        List<WConnection> result = new ArrayList<>();
        if (edges == null)
        {
            return result;
        }
        for (WorkflowEdgeVO vo : edges)
        {
            WConnection edge = new WConnection();
            edge.setId(vo.getId());
            edge.setWorkflowId(workflowId);
            edge.setFromNodeId(vo.getSource());
            edge.setToNodeId(vo.getTarget());
            edge.setFromOutput(vo.getSourceHandle());
            edge.setToInput(vo.getTargetHandle());
            result.add(edge);
        }
        return result;
    }
}
