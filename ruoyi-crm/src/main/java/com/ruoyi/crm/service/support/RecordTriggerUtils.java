package com.ruoyi.crm.service.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.vo.WorkflowNodeVO;

/**
 * CRM_RECORD_TRIGGER 节点解析与校验
 */
public final class RecordTriggerUtils
{
    public static final String NODE_TYPE = "CRM_RECORD_TRIGGER";

    public static final String DEFAULT_VARIABLE_NAME = "recordEvent";

    public static final String ENTITY_TYPE_KEY = "entityType";

    public static final String EVENT_TYPE_KEY = "eventType";

    public static final String VARIABLE_NAME_KEY = "variableName";

    public static final Set<String> ENTITY_TYPES = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList("lead", "contact", "organization", "opportunity")));

    public static final Set<String> EVENT_TYPES = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList("created", "updated", "upsert", "deleted")));

    public static final String OPERATION_CREATE = "create";

    public static final String OPERATION_UPDATE = "update";

    public static final String OPERATION_DELETE = "delete";

    private RecordTriggerUtils()
    {
    }

    public static List<RecordTriggerEntry> extractRecordTriggerNodes(List<WorkflowNodeVO> nodes)
    {
        if (nodes == null || nodes.isEmpty())
        {
            return Collections.emptyList();
        }
        List<RecordTriggerEntry> entries = new ArrayList<>();
        for (WorkflowNodeVO node : nodes)
        {
            if (node == null || !NODE_TYPE.equals(node.getType()) || StringUtils.isEmpty(node.getId()))
            {
                continue;
            }
            Map<String, Object> data = node.getData();
            String entityType = stringField(data, ENTITY_TYPE_KEY);
            String eventType = stringField(data, EVENT_TYPE_KEY);
            validateEnums(entityType, eventType);
            entries.add(new RecordTriggerEntry(node.getId(), entityType, eventType));
        }
        return entries;
    }

    public static void validateBeforeSave(List<WorkflowNodeVO> nodes, String workflowName)
    {
        List<RecordTriggerEntry> entries = extractRecordTriggerNodes(nodes);
        validateUniqueInGraph(entries, workflowName);
    }

    public static void validateEnums(String entityType, String eventType)
    {
        if (StringUtils.isBlank(entityType) || !ENTITY_TYPES.contains(entityType))
        {
            throw new ServiceException("Invalid CRM record trigger entityType: " + entityType, HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(eventType) || !EVENT_TYPES.contains(eventType))
        {
            throw new ServiceException("Invalid CRM record trigger eventType: " + eventType, HttpStatus.BAD_REQUEST);
        }
    }

    public static void validateUniqueInGraph(List<RecordTriggerEntry> entries, String workflowName)
    {
        Set<String> seen = new HashSet<>();
        for (RecordTriggerEntry entry : entries)
        {
            String key = entry.getEntityType() + ":" + entry.getEventType();
            if (!seen.add(key))
            {
                throw new ServiceException(
                        "Duplicate CRM record trigger: " + entry.getEntityType() + " / " + entry.getEventType()
                                + " in workflow " + workflowName,
                        HttpStatus.BAD_REQUEST);
            }
        }
    }

    public static List<String> eventTypesForOperation(String operation)
    {
        if (OPERATION_CREATE.equals(operation))
        {
            return Arrays.asList("created", "upsert");
        }
        if (OPERATION_UPDATE.equals(operation))
        {
            return Arrays.asList("updated", "upsert");
        }
        if (OPERATION_DELETE.equals(operation))
        {
            return Collections.singletonList("deleted");
        }
        throw new ServiceException("Invalid operation: " + operation, HttpStatus.BAD_REQUEST);
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

    public static final class RecordTriggerEntry
    {
        private final String nodeId;
        private final String entityType;
        private final String eventType;

        public RecordTriggerEntry(String nodeId, String entityType, String eventType)
        {
            this.nodeId = nodeId;
            this.entityType = entityType;
            this.eventType = eventType;
        }

        public String getNodeId()
        {
            return nodeId;
        }

        public String getEntityType()
        {
            return entityType;
        }

        public String getEventType()
        {
            return eventType;
        }
    }
}
