package com.ruoyi.crm.service.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.vo.WorkflowNodeVO;

/**
 * CRM_EVENT_TRIGGER 节点解析与校验
 */
public final class EventTriggerUtils
{
    public static final String NODE_TYPE = "CRM_EVENT_TRIGGER";

    public static final String DEFAULT_VARIABLE_NAME = "activityEvent";

    public static final String EVENT_TYPE_KEY = "eventType";

    public static final String VARIABLE_NAME_KEY = "variableName";

    private EventTriggerUtils()
    {
    }

    /**
     * crm_activity.subject_type → payload.subjectEntity（小写实体名）
     */
    public static String toSubjectEntity(String subjectType)
    {
        if (StringUtils.isBlank(subjectType))
        {
            return null;
        }
        String trimmed = subjectType.trim();
        if ("Lead".equalsIgnoreCase(trimmed))
        {
            return "lead";
        }
        if ("Contact".equalsIgnoreCase(trimmed))
        {
            return "contact";
        }
        if ("Organization".equalsIgnoreCase(trimmed) || "Org".equalsIgnoreCase(trimmed)
                || "Company".equalsIgnoreCase(trimmed))
        {
            return "organization";
        }
        if ("Opportunity".equalsIgnoreCase(trimmed) || "Opty".equalsIgnoreCase(trimmed))
        {
            return "opportunity";
        }
        return trimmed.toLowerCase(Locale.ROOT);
    }

    public static List<EventTriggerEntry> extractEventTriggerNodes(List<WorkflowNodeVO> nodes)
    {
        if (nodes == null || nodes.isEmpty())
        {
            return Collections.emptyList();
        }
        List<EventTriggerEntry> entries = new ArrayList<>();
        for (WorkflowNodeVO node : nodes)
        {
            if (node == null || !NODE_TYPE.equals(node.getType()) || StringUtils.isEmpty(node.getId()))
            {
                continue;
            }
            Map<String, Object> data = node.getData();
            String eventType = stringField(data, EVENT_TYPE_KEY);
            if (StringUtils.isBlank(eventType))
            {
                throw new ServiceException(
                        "CRM event trigger eventType is required on node " + node.getId(),
                        HttpStatus.BAD_REQUEST);
            }
            entries.add(new EventTriggerEntry(node.getId(), eventType));
        }
        return entries;
    }

    public static void validateBeforeSave(List<WorkflowNodeVO> nodes, String workflowName)
    {
        List<EventTriggerEntry> entries = extractEventTriggerNodes(nodes);
        validateUniqueInGraph(entries, workflowName);
    }

    public static void validateUniqueInGraph(List<EventTriggerEntry> entries, String workflowName)
    {
        Set<String> seen = new HashSet<>();
        for (EventTriggerEntry entry : entries)
        {
            if (!seen.add(entry.getEventType()))
            {
                throw new ServiceException(
                        "Duplicate CRM event trigger eventType: " + entry.getEventType()
                                + " in workflow " + workflowName,
                        HttpStatus.BAD_REQUEST);
            }
        }
    }

    public static String resolveVariableName(Map<String, Object> nodeData)
    {
        String value = stringField(nodeData, VARIABLE_NAME_KEY);
        return StringUtils.isNotBlank(value) ? value : DEFAULT_VARIABLE_NAME;
    }

    private static String stringField(Map<String, Object> data, String key)
    {
        if (data == null)
        {
            return null;
        }
        Object value = data.get(key);
        if (value == null)
        {
            return null;
        }
        return value.toString().trim();
    }

    public static final class EventTriggerEntry
    {
        private final String nodeId;
        private final String eventType;

        public EventTriggerEntry(String nodeId, String eventType)
        {
            this.nodeId = nodeId;
            this.eventType = eventType;
        }

        public String getNodeId()
        {
            return nodeId;
        }

        public String getEventType()
        {
            return eventType;
        }
    }
}
