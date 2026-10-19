package com.ruoyi.crm.service.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.vo.WorkflowEdgeVO;
import com.ruoyi.crm.domain.vo.WorkflowNodeVO;

/**
 * 工作流图深拷贝：重映射 node/edge ID，并清除发布与 Webhook publicPath。
 */
public final class WWorkflowCloneSupport
{
    public static final class GraphCopy
    {
        private final List<WorkflowNodeVO> nodes;
        private final List<WorkflowEdgeVO> edges;

        public GraphCopy(List<WorkflowNodeVO> nodes, List<WorkflowEdgeVO> edges)
        {
            this.nodes = nodes != null ? nodes : new ArrayList<>();
            this.edges = edges != null ? edges : new ArrayList<>();
        }

        public List<WorkflowNodeVO> getNodes()
        {
            return nodes;
        }

        public List<WorkflowEdgeVO> getEdges()
        {
            return edges;
        }
    }

    private WWorkflowCloneSupport()
    {
    }

    public static GraphCopy deepCopyGraph(List<WorkflowNodeVO> sourceNodes, List<WorkflowEdgeVO> sourceEdges)
    {
        List<WorkflowNodeVO> nodes = sourceNodes != null ? sourceNodes : Collections.emptyList();
        List<WorkflowEdgeVO> edges = sourceEdges != null ? sourceEdges : Collections.emptyList();

        Map<String, String> idMap = new HashMap<>();
        for (WorkflowNodeVO node : nodes)
        {
            if (node == null || StringUtils.isEmpty(node.getId()))
            {
                continue;
            }
            idMap.put(node.getId(), newUuid());
        }

        List<WorkflowNodeVO> newNodes = new ArrayList<>(nodes.size());
        for (WorkflowNodeVO node : nodes)
        {
            if (node == null || StringUtils.isEmpty(node.getId()))
            {
                continue;
            }
            WorkflowNodeVO copy = deepCloneNode(node);
            copy.setId(idMap.get(node.getId()));
            clearPublishAndWebhookPublicPath(copy);
            newNodes.add(copy);
        }

        List<WorkflowEdgeVO> newEdges = new ArrayList<>(edges.size());
        for (WorkflowEdgeVO edge : edges)
        {
            if (edge == null)
            {
                continue;
            }
            String newSource = idMap.get(edge.getSource());
            String newTarget = idMap.get(edge.getTarget());
            if (StringUtils.isEmpty(newSource) || StringUtils.isEmpty(newTarget))
            {
                continue;
            }
            WorkflowEdgeVO copy = deepCloneEdge(edge);
            copy.setId(newUuid());
            copy.setSource(newSource);
            copy.setTarget(newTarget);
            newEdges.add(copy);
        }
        return new GraphCopy(newNodes, newEdges);
    }

    public static void clearPublishAndWebhookPublicPath(WorkflowNodeVO node)
    {
        if (node == null)
        {
            return;
        }
        Map<String, Object> data = node.getData();
        if (data == null)
        {
            return;
        }
        if (WorkflowPublishSupport.NODE_TYPE.equals(node.getType()))
        {
            WorkflowPublishSupport.disablePublishChannels(data);
        }
        if (WebhookPathUtils.NODE_TYPE.equals(node.getType()))
        {
            data.remove(WebhookPathUtils.PUBLIC_PATH_KEY);
        }
    }

    public static void clearPublishAndWebhookPublicPath(List<WorkflowNodeVO> nodes)
    {
        if (nodes == null)
        {
            return;
        }
        for (WorkflowNodeVO node : nodes)
        {
            clearPublishAndWebhookPublicPath(node);
        }
    }

    private static WorkflowNodeVO deepCloneNode(WorkflowNodeVO source)
    {
        WorkflowNodeVO copy = new WorkflowNodeVO();
        copy.setType(source.getType());
        copy.setPosition(deepCloneMap(source.getPosition()));
        copy.setData(deepCloneMap(source.getData()));
        return copy;
    }

    private static WorkflowEdgeVO deepCloneEdge(WorkflowEdgeVO source)
    {
        WorkflowEdgeVO copy = new WorkflowEdgeVO();
        copy.setSourceHandle(source.getSourceHandle());
        copy.setTargetHandle(source.getTargetHandle());
        copy.setType(source.getType());
        copy.setLabel(source.getLabel());
        copy.setData(deepCloneMap(source.getData()));
        return copy;
    }

    private static Map<String, Object> deepCloneMap(Map<String, Object> source)
    {
        if (source == null)
        {
            return null;
        }
        if (source.isEmpty())
        {
            return new HashMap<>();
        }
        return JSON.parseObject(JSON.toJSONString(source), new TypeReference<Map<String, Object>>() {});
    }

    private static String newUuid()
    {
        return UUID.randomUUID().toString();
    }
}
