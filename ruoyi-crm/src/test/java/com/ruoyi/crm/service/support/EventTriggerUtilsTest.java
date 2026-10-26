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

public class EventTriggerUtilsTest
{
    @Test
    public void toSubjectEntityMapsKnownTypes()
    {
        assertEquals("lead", EventTriggerUtils.toSubjectEntity("Lead"));
        assertEquals("contact", EventTriggerUtils.toSubjectEntity("Contact"));
        assertEquals("organization", EventTriggerUtils.toSubjectEntity("Organization"));
        assertEquals("opportunity", EventTriggerUtils.toSubjectEntity("Opportunity"));
    }

    @Test
    public void extractEventTriggerNodesReadsEventType()
    {
        WorkflowNodeVO node = triggerNode("n1", "engagement.assessment.completed");
        List<EventTriggerUtils.EventTriggerEntry> entries = EventTriggerUtils.extractEventTriggerNodes(
                Collections.singletonList(node));
        assertEquals(1, entries.size());
        assertEquals("engagement.assessment.completed", entries.get(0).getEventType());
    }

    @Test
    public void validateUniqueInGraphRejectsDuplicateEventType()
    {
        WorkflowNodeVO a = triggerNode("n1", "engagement.assessment.completed");
        WorkflowNodeVO b = triggerNode("n2", "engagement.assessment.completed");
        List<EventTriggerUtils.EventTriggerEntry> entries = EventTriggerUtils.extractEventTriggerNodes(
                Arrays.asList(a, b));
        assertThrows(ServiceException.class,
                () -> EventTriggerUtils.validateUniqueInGraph(entries, "wf-1"));
    }

    @Test
    public void extractRequiresEventType()
    {
        WorkflowNodeVO node = new WorkflowNodeVO();
        node.setId("n1");
        node.setType(EventTriggerUtils.NODE_TYPE);
        node.setData(new HashMap<>());
        assertThrows(ServiceException.class,
                () -> EventTriggerUtils.extractEventTriggerNodes(Collections.singletonList(node)));
    }

    @Test
    public void resolveVariableNameFallsBackToActivityEvent()
    {
        assertEquals("activityEvent", EventTriggerUtils.resolveVariableName(Collections.emptyMap()));
        Map<String, Object> data = new HashMap<>();
        data.put("variableName", "myActivity");
        assertEquals("myActivity", EventTriggerUtils.resolveVariableName(data));
    }

    private WorkflowNodeVO triggerNode(String id, String eventType)
    {
        WorkflowNodeVO vo = new WorkflowNodeVO();
        vo.setId(id);
        vo.setType(EventTriggerUtils.NODE_TYPE);
        Map<String, Object> data = new HashMap<>();
        data.put("eventType", eventType);
        vo.setData(data);
        return vo;
    }
}
