package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.WWorkflow;
import com.ruoyi.crm.mapper.WConnectionMapper;
import com.ruoyi.crm.mapper.WNodeMapper;
import com.ruoyi.crm.mapper.WWorkflowMapper;
import com.ruoyi.crm.service.support.EventTriggerNodeSupport;
import com.ruoyi.crm.service.support.RecordTriggerNodeSupport;
import com.ruoyi.crm.service.support.WebhookPathIndexSupport;

@ExtendWith(MockitoExtension.class)
class WWorkflowServiceImplDescriptionTest
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
    void insert_withDescription_persistsValue()
    {
        WWorkflow workflow = new WWorkflow();
        workflow.setId("wf-1");
        workflow.setName("线索跟进");
        workflow.setUserId("u1");
        workflow.setDescription("新线索入库后自动分配");

        when(wWorkflowMapper.insertWWorkflow(any(WWorkflow.class))).thenReturn(1);

        wWorkflowService.insertWWorkflow(workflow);

        ArgumentCaptor<WWorkflow> captor = ArgumentCaptor.forClass(WWorkflow.class);
        verify(wWorkflowMapper).insertWWorkflow(captor.capture());
        assertEquals("新线索入库后自动分配", captor.getValue().getDescription());
    }

    @Test
    void insert_withoutDescription_defaultsToEmpty()
    {
        WWorkflow workflow = new WWorkflow();
        workflow.setId("wf-1");
        workflow.setName("线索跟进");
        workflow.setUserId("u1");

        when(wWorkflowMapper.insertWWorkflow(any(WWorkflow.class))).thenReturn(1);

        wWorkflowService.insertWWorkflow(workflow);

        ArgumentCaptor<WWorkflow> captor = ArgumentCaptor.forClass(WWorkflow.class);
        verify(wWorkflowMapper).insertWWorkflow(captor.capture());
        assertEquals("", captor.getValue().getDescription());
    }

    @Test
    void updateMeta_onlyDescription_leavesNameUnset()
    {
        WWorkflow existing = header("wf-1", "原名称", "原说明");
        when(wWorkflowMapper.selectWWorkflowById("wf-1")).thenReturn(existing);
        when(wWorkflowMapper.updateWWorkflowMeta(any(WWorkflow.class))).thenReturn(1);
        when(wNodeMapper.selectWNodeListByWorkflowIds(anyList())).thenReturn(Collections.emptyList());
        when(wConnectionMapper.selectWConnectionListByWorkflowIds(anyList())).thenReturn(Collections.emptyList());

        WWorkflow result = wWorkflowService.updateWWorkflowMeta("wf-1", null, "新说明");

        ArgumentCaptor<WWorkflow> captor = ArgumentCaptor.forClass(WWorkflow.class);
        verify(wWorkflowMapper).updateWWorkflowMeta(captor.capture());
        assertNull(captor.getValue().getName());
        assertEquals("新说明", captor.getValue().getDescription());
        assertNotNull(result);
    }

    @Test
    void updateMeta_onlyName_leavesDescriptionUnset()
    {
        WWorkflow existing = header("wf-1", "原名称", "原说明");
        when(wWorkflowMapper.selectWWorkflowById("wf-1")).thenReturn(existing);
        when(wWorkflowMapper.updateWWorkflowMeta(any(WWorkflow.class))).thenReturn(1);
        when(wNodeMapper.selectWNodeListByWorkflowIds(anyList())).thenReturn(Collections.emptyList());
        when(wConnectionMapper.selectWConnectionListByWorkflowIds(anyList())).thenReturn(Collections.emptyList());

        wWorkflowService.updateWWorkflowMeta("wf-1", "新名称", null);

        ArgumentCaptor<WWorkflow> captor = ArgumentCaptor.forClass(WWorkflow.class);
        verify(wWorkflowMapper).updateWWorkflowMeta(captor.capture());
        assertEquals("新名称", captor.getValue().getName());
        assertNull(captor.getValue().getDescription());
    }

    @Test
    void updateMeta_requiresAtLeastOneField()
    {
        assertThrows(ServiceException.class, () -> wWorkflowService.updateWWorkflowMeta("wf-1", null, null));
        verify(wWorkflowMapper, never()).updateWWorkflowMeta(any(WWorkflow.class));
    }

    @Test
    void updateWWorkflow_withoutDescription_doesNotSetDescriptionOnEntity()
    {
        WWorkflow existing = header("wf-1", "原名称", "应保留的说明");
        when(wWorkflowMapper.selectWWorkflowById("wf-1")).thenReturn(existing);
        when(wWorkflowMapper.updateWWorkflow(any(WWorkflow.class))).thenReturn(1);

        WWorkflow canvas = new WWorkflow();
        canvas.setId("wf-1");
        canvas.setName("原名称");
        // description intentionally omitted (null)

        wWorkflowService.updateWWorkflow(canvas);

        ArgumentCaptor<WWorkflow> captor = ArgumentCaptor.forClass(WWorkflow.class);
        verify(wWorkflowMapper).updateWWorkflow(captor.capture());
        assertNull(captor.getValue().getDescription());
    }

    @Test
    void clone_copiesSourceDescription()
    {
        stubSourceWithDescription("wf-src", "源说明");
        AtomicReference<WWorkflow> inserted = new AtomicReference<>();
        when(wWorkflowMapper.insertWWorkflow(any(WWorkflow.class))).thenAnswer(invocation -> {
            WWorkflow arg = invocation.getArgument(0);
            inserted.set(arg);
            return 1;
        });
        when(webhookPathIndexSupport.requireCurrentTenantId()).thenReturn("tenant-1");
        doAnswer(invocation -> {
            WWorkflow created = inserted.get();
            when(wWorkflowMapper.selectWWorkflowById(created.getId())).thenReturn(header(
                    created.getId(), created.getName(), created.getDescription()));
            when(wNodeMapper.selectWNodeListByWorkflowIds(eq(Collections.singletonList(created.getId()))))
                    .thenReturn(Collections.emptyList());
            when(wConnectionMapper.selectWConnectionListByWorkflowIds(eq(Collections.singletonList(created.getId()))))
                    .thenReturn(Collections.emptyList());
            return null;
        }).when(wNodeMapper).deleteWNodeByWorkflowId(anyString());

        wWorkflowService.cloneAsInstance("wf-src", null, null);

        assertEquals("源说明", inserted.get().getDescription());
    }

    @Test
    void clone_bodyDescriptionOverridesSource()
    {
        stubSourceWithDescription("wf-src", "源说明");
        AtomicReference<WWorkflow> inserted = new AtomicReference<>();
        when(wWorkflowMapper.insertWWorkflow(any(WWorkflow.class))).thenAnswer(invocation -> {
            WWorkflow arg = invocation.getArgument(0);
            inserted.set(arg);
            return 1;
        });
        when(webhookPathIndexSupport.requireCurrentTenantId()).thenReturn("tenant-1");
        doAnswer(invocation -> {
            WWorkflow created = inserted.get();
            when(wWorkflowMapper.selectWWorkflowById(created.getId())).thenReturn(header(
                    created.getId(), created.getName(), created.getDescription()));
            when(wNodeMapper.selectWNodeListByWorkflowIds(eq(Collections.singletonList(created.getId()))))
                    .thenReturn(Collections.emptyList());
            when(wConnectionMapper.selectWConnectionListByWorkflowIds(eq(Collections.singletonList(created.getId()))))
                    .thenReturn(Collections.emptyList());
            return null;
        }).when(wNodeMapper).deleteWNodeByWorkflowId(anyString());

        wWorkflowService.cloneAsInstance("wf-src", "自定义名", "覆盖说明");

        assertEquals("覆盖说明", inserted.get().getDescription());
        assertEquals("自定义名", inserted.get().getName());
    }

    private void stubSourceWithDescription(String id, String description)
    {
        WWorkflow source = header(id, "线索跟进", description);
        when(wWorkflowMapper.selectWWorkflowById(id)).thenReturn(source);
        when(wNodeMapper.selectWNodeListByWorkflowIds(eq(Collections.singletonList(id))))
                .thenReturn(Collections.emptyList());
        when(wConnectionMapper.selectWConnectionListByWorkflowIds(eq(Collections.singletonList(id))))
                .thenReturn(Collections.emptyList());
    }

    private static WWorkflow header(String id, String name, String description)
    {
        WWorkflow workflow = new WWorkflow();
        workflow.setId(id);
        workflow.setName(name);
        workflow.setUserId("u1");
        workflow.setOrganizationId("org-1");
        workflow.setIsTemplate(Boolean.FALSE);
        workflow.setPublishAssistant(Boolean.FALSE);
        workflow.setPublishSidebar(Boolean.FALSE);
        workflow.setDescription(description);
        return workflow;
    }
}
