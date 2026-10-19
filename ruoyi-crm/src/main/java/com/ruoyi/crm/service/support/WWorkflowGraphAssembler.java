package com.ruoyi.crm.service.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.WConnection;
import com.ruoyi.crm.domain.WNode;
import com.ruoyi.crm.domain.WWorkflow;
import com.ruoyi.crm.domain.vo.WorkflowEdgeVO;
import com.ruoyi.crm.domain.vo.WorkflowNodeVO;

/**
 * 将 w_node / w_connection 组装为 React Flow 图结构
 */
public final class WWorkflowGraphAssembler
{
    private static final String DEFAULT_HANDLE = "main";

    private WWorkflowGraphAssembler()
    {
    }

    public static void attachGraph(WWorkflow workflow, List<WNode> nodes, List<WConnection> connections)
    {
        if (workflow == null)
        {
            return;
        }
        String workflowId = workflow.getId();
        List<WNode> workflowNodes = filterNodes(nodes, workflowId);
        List<WConnection> workflowConnections = filterConnections(connections, workflowId);
        workflow.setNodes(toNodes(workflowNodes));
        workflow.setEdges(toEdges(workflowConnections));
    }

    public static void attachGraph(List<WWorkflow> workflows, List<WNode> nodes, List<WConnection> connections)
    {
        if (workflows == null || workflows.isEmpty())
        {
            return;
        }
        Map<String, List<WNode>> nodesByWorkflow = groupNodes(nodes);
        Map<String, List<WConnection>> connectionsByWorkflow = groupConnections(connections);
        for (WWorkflow workflow : workflows)
        {
            String workflowId = workflow.getId();
            workflow.setNodes(toNodes(nodesByWorkflow.getOrDefault(workflowId, Collections.emptyList())));
            workflow.setEdges(toEdges(connectionsByWorkflow.getOrDefault(workflowId, Collections.emptyList())));
        }
    }

    public static List<WorkflowNodeVO> toNodes(List<WNode> nodes)
    {
        if (nodes == null || nodes.isEmpty())
        {
            return new ArrayList<>();
        }
        List<WorkflowNodeVO> result = new ArrayList<>(nodes.size());
        for (WNode node : nodes)
        {
            result.add(toNode(node));
        }
        return result;
    }

    public static List<WorkflowEdgeVO> toEdges(List<WConnection> connections)
    {
        if (connections == null || connections.isEmpty())
        {
            return new ArrayList<>();
        }
        List<WorkflowEdgeVO> result = new ArrayList<>(connections.size());
        for (WConnection connection : connections)
        {
            result.add(toEdge(connection));
        }
        return result;
    }

    public static WorkflowNodeVO toNode(WNode node)
    {
        WorkflowNodeVO vo = new WorkflowNodeVO();
        vo.setId(node.getId());
        vo.setType(node.getType());
        vo.setPosition(parseJsonMap(node.getPosition()));
        vo.setData(buildNodeData(node));
        return vo;
    }

    public static WorkflowEdgeVO toEdge(WConnection connection)
    {
        WorkflowEdgeVO vo = new WorkflowEdgeVO();
        vo.setId(connection.getId());
        vo.setSource(connection.getFromNodeId());
        vo.setTarget(connection.getToNodeId());
        vo.setSourceHandle(toHandle(connection.getFromOutput()));
        vo.setTargetHandle(toHandle(connection.getToInput()));
        vo.setType(connection.getType());
        vo.setLabel(connection.getLabel());
        Map<String, Object> data = parseJsonMap(connection.getData());
        vo.setData(data.isEmpty() ? null : data);
        return vo;
    }

    public static WNode fromNode(WorkflowNodeVO vo, String workflowId)
    {
        WNode node = new WNode();
        node.setId(vo.getId());
        node.setWorkflowId(workflowId);
        node.setType(vo.getType());
        if (vo.getPosition() != null && !vo.getPosition().isEmpty())
        {
            node.setPosition(JSON.toJSONString(vo.getPosition()));
        }
        Map<String, Object> data = vo.getData() != null ? new HashMap<>(vo.getData()) : new HashMap<>();

        Object name = data.remove("name");
        if (name != null)
        {
            node.setName(String.valueOf(name));
        }
        else
        {
            Object variableName = data.get("variableName");
            if (variableName != null && StringUtils.isNotEmpty(String.valueOf(variableName)))
            {
                node.setName(String.valueOf(variableName));
            }
            else if (StringUtils.isNotEmpty(vo.getType()))
            {
                node.setName(vo.getType());
            }
            else if (StringUtils.isNotEmpty(vo.getId()))
            {
                node.setName(vo.getId());
            }
            else
            {
                node.setName("node");
            }
        }
        Object inputs = data.remove("inputs");
        if (inputs != null)
        {
            node.setInputs(JSON.toJSONString(inputs));
        }
        Object outputs = data.remove("outputs");
        if (outputs != null)
        {
            node.setOutputs(JSON.toJSONString(outputs));
        }
        Object credentialId = data.remove("credentialId");
        if (credentialId != null)
        {
            node.setCredentialId(String.valueOf(credentialId));
        }
        if (!data.isEmpty())
        {
            node.setData(JSON.toJSONString(data));
        }
        return node;
    }

    public static WConnection fromEdge(WorkflowEdgeVO vo, String workflowId)
    {
        WConnection connection = new WConnection();
        connection.setId(vo.getId());
        connection.setWorkflowId(workflowId);
        connection.setFromNodeId(vo.getSource());
        connection.setToNodeId(vo.getTarget());
        connection.setFromOutput(fromHandle(vo.getSourceHandle()));
        connection.setToInput(fromHandle(vo.getTargetHandle()));
        connection.setType(vo.getType());
        connection.setLabel(vo.getLabel());
        if (vo.getData() != null && !vo.getData().isEmpty())
        {
            connection.setData(JSON.toJSONString(vo.getData()));
        }
        return connection;
    }

    private static Map<String, Object> buildNodeData(WNode node)
    {
        Map<String, Object> data = parseJsonMap(node.getData());
        if (StringUtils.isNotEmpty(node.getName()))
        {
            data.put("name", node.getName());
        }
        if (StringUtils.isNotEmpty(node.getInputs()))
        {
            data.put("inputs", parseJsonValue(node.getInputs()));
        }
        if (StringUtils.isNotEmpty(node.getOutputs()))
        {
            data.put("outputs", parseJsonValue(node.getOutputs()));
        }
        if (StringUtils.isNotEmpty(node.getCredentialId()))
        {
            data.put("credentialId", node.getCredentialId());
        }
        return data;
    }

    private static String toHandle(String handle)
    {
        if (StringUtils.isEmpty(handle) || DEFAULT_HANDLE.equals(handle))
        {
            return null;
        }
        return handle;
    }

    private static String fromHandle(String handle)
    {
        return StringUtils.isEmpty(handle) ? DEFAULT_HANDLE : handle;
    }

    private static Map<String, Object> parseJsonMap(String json)
    {
        if (StringUtils.isEmpty(json))
        {
            return new HashMap<>();
        }
        try
        {
            Map<String, Object> map = JSON.parseObject(json, new TypeReference<Map<String, Object>>() {});
            return map != null ? map : new HashMap<>();
        }
        catch (Exception ex)
        {
            return new HashMap<>();
        }
    }

    private static Object parseJsonValue(String json)
    {
        if (StringUtils.isEmpty(json))
        {
            return null;
        }
        try
        {
            return JSON.parse(json);
        }
        catch (Exception ex)
        {
            return json;
        }
    }

    private static List<WNode> filterNodes(List<WNode> nodes, String workflowId)
    {
        if (nodes == null || StringUtils.isEmpty(workflowId))
        {
            return Collections.emptyList();
        }
        return nodes.stream()
            .filter(node -> workflowId.equals(node.getWorkflowId()))
            .collect(Collectors.toList());
    }

    private static List<WConnection> filterConnections(List<WConnection> connections, String workflowId)
    {
        if (connections == null || StringUtils.isEmpty(workflowId))
        {
            return Collections.emptyList();
        }
        return connections.stream()
            .filter(connection -> workflowId.equals(connection.getWorkflowId()))
            .collect(Collectors.toList());
    }

    private static Map<String, List<WNode>> groupNodes(List<WNode> nodes)
    {
        if (nodes == null || nodes.isEmpty())
        {
            return Collections.emptyMap();
        }
        return nodes.stream().collect(Collectors.groupingBy(WNode::getWorkflowId));
    }

    private static Map<String, List<WConnection>> groupConnections(List<WConnection> connections)
    {
        if (connections == null || connections.isEmpty())
        {
            return Collections.emptyMap();
        }
        return connections.stream().collect(Collectors.groupingBy(WConnection::getWorkflowId));
    }
}
