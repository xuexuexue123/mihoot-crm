package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.vo.WorkflowNodeVO;

public class RecordTriggerUtilsTest
{
    @Test
    public void extractRecordTriggerNodesReadsEntityAndEvent()
    {
        WorkflowNodeVO node = triggerNode("n1", "lead", "created");
        List<RecordTriggerUtils.RecordTriggerEntry> entries = RecordTriggerUtils.extractRecordTriggerNodes(
                Collections.singletonList(node));
        assertEquals(1, entries.size());
        assertEquals("lead", entries.get(0).getEntityType());
        assertEquals("created", entries.get(0).getEventType());
    }

    @Test
    public void validateUniqueInGraphRejectsDuplicateEntityEvent()
    {
        WorkflowNodeVO a = triggerNode("n1", "lead", "created");
        WorkflowNodeVO b = triggerNode("n2", "lead", "created");
        List<RecordTriggerUtils.RecordTriggerEntry> entries = RecordTriggerUtils.extractRecordTriggerNodes(
                Arrays.asList(a, b));
        assertThrows(ServiceException.class,
                () -> RecordTriggerUtils.validateUniqueInGraph(entries, "wf-1"));
    }

    @Test
    public void eventTypesForOperationMapsCreateUpdateDelete()
    {
        assertEquals(Arrays.asList("created", "upsert"),
                RecordTriggerUtils.eventTypesForOperation(RecordTriggerUtils.OPERATION_CREATE));
        assertEquals(Arrays.asList("updated", "upsert"),
                RecordTriggerUtils.eventTypesForOperation(RecordTriggerUtils.OPERATION_UPDATE));
        assertEquals(Collections.singletonList("deleted"),
                RecordTriggerUtils.eventTypesForOperation(RecordTriggerUtils.OPERATION_DELETE));
    }

    @Test
    public void resolveVariableNameFallsBackToRecordEvent()
    {
        assertEquals("recordEvent", RecordTriggerUtils.resolveVariableName(Collections.emptyMap()));
        Map<String, Object> data = new HashMap<>();
        data.put("variableName", "myEvent");
        assertEquals("myEvent", RecordTriggerUtils.resolveVariableName(data));
    }

    @Test
    public void validateEnumsRejectsInvalidValues()
    {
        assertThrows(ServiceException.class, () -> RecordTriggerUtils.validateEnums("invalid", "created"));
        assertThrows(ServiceException.class, () -> RecordTriggerUtils.validateEnums("lead", "invalid"));
    }

    private WorkflowNodeVO triggerNode(String id, String entityType, String eventType)
    {
        WorkflowNodeVO vo = new WorkflowNodeVO();
        vo.setId(id);
        vo.setType(RecordTriggerUtils.NODE_TYPE);
        Map<String, Object> data = new HashMap<>();
        data.put("entityType", entityType);
        data.put("eventType", eventType);
        vo.setData(data);
        return vo;
    }
}
