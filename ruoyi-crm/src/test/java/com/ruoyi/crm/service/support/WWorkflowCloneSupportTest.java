package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.ruoyi.crm.domain.vo.WorkflowEdgeVO;
import com.ruoyi.crm.domain.vo.WorkflowNodeVO;

class WWorkflowCloneSupportTest
{
    @Test
    void deepCopyGraph_remapsNodeAndEdgeIdsWithoutOverlap()
    {
        WorkflowNodeVO n1 = node("node-a", WorkflowPublishSupport.NODE_TYPE, manualPublishData());
        WorkflowNodeVO n2 = node("node-b", WebhookPathUtils.NODE_TYPE, webhookData("enc-path"));
        WorkflowEdgeVO e1 = edge("edge-1", "node-a", "node-b");

        WWorkflowCloneSupport.GraphCopy copy = WWorkflowCloneSupport.deepCopyGraph(
                Arrays.asList(n1, n2), Collections.singletonList(e1));

        Set<String> sourceNodeIds = new HashSet<>(Arrays.asList("node-a", "node-b"));
        Set<String> sourceEdgeIds = new HashSet<>(Collections.singletonList("edge-1"));
        Set<String> newNodeIds = copy.getNodes().stream().map(WorkflowNodeVO::getId).collect(Collectors.toSet());
        Set<String> newEdgeIds = copy.getEdges().stream().map(WorkflowEdgeVO::getId).collect(Collectors.toSet());

        assertEquals(2, newNodeIds.size());
        assertEquals(1, newEdgeIds.size());
        assertTrue(Collections.disjoint(sourceNodeIds, newNodeIds));
        assertTrue(Collections.disjoint(sourceEdgeIds, newEdgeIds));

        WorkflowEdgeVO copiedEdge = copy.getEdges().get(0);
        assertTrue(newNodeIds.contains(copiedEdge.getSource()));
        assertTrue(newNodeIds.contains(copiedEdge.getTarget()));
        assertNotEquals(copiedEdge.getSource(), copiedEdge.getTarget());
    }

    @Test
    void deepCopyGraph_clearsPublishAndPublicPath()
    {
        WorkflowNodeVO manual = node("mt", WorkflowPublishSupport.NODE_TYPE, manualPublishData());
        WorkflowNodeVO webhook = node("wh", WebhookPathUtils.NODE_TYPE, webhookData("public-abc"));

        WWorkflowCloneSupport.GraphCopy copy = WWorkflowCloneSupport.deepCopyGraph(
                Arrays.asList(manual, webhook), Collections.emptyList());

        WorkflowNodeVO copiedManual = copy.getNodes().stream()
                .filter(n -> WorkflowPublishSupport.NODE_TYPE.equals(n.getType()))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
        WorkflowNodeVO copiedWebhook = copy.getNodes().stream()
                .filter(n -> WebhookPathUtils.NODE_TYPE.equals(n.getType()))
                .findFirst()
                .orElseThrow(IllegalStateException::new);

        assertFalse(WorkflowPublishSupport.workflowPublishAssistant(Collections.singletonList(copiedManual)));
        assertFalse(WorkflowPublishSupport.workflowPublishSidebar(Collections.singletonList(copiedManual)));
        assertNull(copiedWebhook.getData().get(WebhookPathUtils.PUBLIC_PATH_KEY));
        assertEquals("my-slug", copiedWebhook.getData().get(WebhookPathUtils.SLUG_PATH_KEY));
    }

    @Test
    void deepCopyGraph_preservesCredentialId()
    {
        Map<String, Object> data = new HashMap<>();
        data.put("credentialId", "cred-1");
        WorkflowNodeVO http = node("http-1", "HTTP_REQUEST", data);

        WWorkflowCloneSupport.GraphCopy copy = WWorkflowCloneSupport.deepCopyGraph(
                Collections.singletonList(http), Collections.emptyList());

        assertEquals("cred-1", copy.getNodes().get(0).getData().get("credentialId"));
        assertNotEquals("http-1", copy.getNodes().get(0).getId());
    }

    private static Map<String, Object> manualPublishData()
    {
        Map<String, Object> assistant = new HashMap<>();
        assistant.put("enabled", true);
        assistant.put("description", "desc");
        Map<String, Object> sidebar = new HashMap<>();
        sidebar.put("enabled", true);
        Map<String, Object> publish = new HashMap<>();
        publish.put("assistant", assistant);
        publish.put("wecomSidebar", sidebar);
        Map<String, Object> data = new HashMap<>();
        data.put("publish", publish);
        return data;
    }

    private static Map<String, Object> webhookData(String publicPath)
    {
        Map<String, Object> data = new HashMap<>();
        data.put(WebhookPathUtils.PUBLIC_PATH_KEY, publicPath);
        data.put(WebhookPathUtils.SLUG_PATH_KEY, "my-slug");
        return data;
    }

    private static WorkflowNodeVO node(String id, String type, Map<String, Object> data)
    {
        WorkflowNodeVO vo = new WorkflowNodeVO();
        vo.setId(id);
        vo.setType(type);
        vo.setData(data);
        return vo;
    }

    private static WorkflowEdgeVO edge(String id, String source, String target)
    {
        WorkflowEdgeVO edge = new WorkflowEdgeVO();
        edge.setId(id);
        edge.setSource(source);
        edge.setTarget(target);
        edge.setSourceHandle("main");
        edge.setTargetHandle("main");
        return edge;
    }
}
