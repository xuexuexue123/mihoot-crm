package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.ruoyi.crm.domain.vo.WorkflowNodeVO;

class WorkflowPublishSupportTest
{
    @Test
    void workflowPublishAssistant_noManualTrigger_returnsFalse()
    {
        WorkflowNodeVO http = node("n1", "HTTP_REQUEST", Collections.emptyMap());
        assertFalse(WorkflowPublishSupport.workflowPublishAssistant(Collections.singletonList(http)));
        assertFalse(WorkflowPublishSupport.workflowPublishSidebar(Collections.singletonList(http)));
    }

    @Test
    void workflowPublishAssistant_enabledWithDescription_returnsTrue()
    {
        Map<String, Object> assistant = channel(true, "Generate monthly lead report");
        WorkflowNodeVO trigger = manualTriggerWithPublish(assistant, null);
        List<WorkflowNodeVO> nodes = Collections.singletonList(trigger);

        assertTrue(WorkflowPublishSupport.workflowPublishAssistant(nodes));
        assertFalse(WorkflowPublishSupport.workflowPublishSidebar(nodes));
    }

    @Test
    void workflowPublishSidebar_enabledOnly_returnsTrue()
    {
        Map<String, Object> sidebar = channel(true, null);
        WorkflowNodeVO trigger = manualTriggerWithPublish(null, sidebar);
        List<WorkflowNodeVO> nodes = Collections.singletonList(trigger);

        assertFalse(WorkflowPublishSupport.workflowPublishAssistant(nodes));
        assertTrue(WorkflowPublishSupport.workflowPublishSidebar(nodes));
    }

    @Test
    void workflowPublishAssistant_legacyAgentPublish_returnsTrue()
    {
        Map<String, Object> data = new HashMap<>();
        data.put("agentPublish", channel(true, "Legacy assistant description"));
        WorkflowNodeVO trigger = node("mt1", WorkflowPublishSupport.NODE_TYPE, data);
        List<WorkflowNodeVO> nodes = Collections.singletonList(trigger);

        assertTrue(WorkflowPublishSupport.workflowPublishAssistant(nodes));
        assertFalse(WorkflowPublishSupport.workflowPublishSidebar(nodes));
    }

    @Test
    void workflowPublish_bothChannelsComplete_returnsTrueForBoth()
    {
        Map<String, Object> assistant = channel(true, "Assistant desc");
        Map<String, Object> sidebar = channel(true, "Sidebar desc");
        WorkflowNodeVO trigger = manualTriggerWithPublish(assistant, sidebar);
        List<WorkflowNodeVO> nodes = Collections.singletonList(trigger);

        assertTrue(WorkflowPublishSupport.workflowPublishAssistant(nodes));
        assertTrue(WorkflowPublishSupport.workflowPublishSidebar(nodes));
    }

    @Test
    void workflowPublishAssistant_enabledWithoutDescription_returnsFalse()
    {
        Map<String, Object> assistant = channel(true, "   ");
        WorkflowNodeVO trigger = manualTriggerWithPublish(assistant, null);
        List<WorkflowNodeVO> nodes = Collections.singletonList(trigger);

        assertFalse(WorkflowPublishSupport.workflowPublishAssistant(nodes));
    }

    @Test
    void workflowPublish_anyManualTriggerSatisfies_returnsTrue()
    {
        Map<String, Object> incomplete = channel(true, "");
        Map<String, Object> complete = channel(true, "Ready");
        WorkflowNodeVO first = manualTriggerWithPublish(incomplete, null);
        WorkflowNodeVO second = manualTriggerWithPublish(complete, null);
        List<WorkflowNodeVO> nodes = Arrays.asList(first, second);

        assertTrue(WorkflowPublishSupport.workflowPublishAssistant(nodes));
    }

    @Test
    void disablePublishChannels_clearsEnabledFlags()
    {
        Map<String, Object> assistant = channel(true, "Generate monthly lead report");
        Map<String, Object> sidebar = channel(true, "Sidebar");
        WorkflowNodeVO trigger = manualTriggerWithPublish(assistant, sidebar);
        WorkflowPublishSupport.disablePublishChannels(trigger.getData());

        assertFalse(WorkflowPublishSupport.workflowPublishAssistant(Collections.singletonList(trigger)));
        assertFalse(WorkflowPublishSupport.workflowPublishSidebar(Collections.singletonList(trigger)));
    }

    private static WorkflowNodeVO manualTriggerWithPublish(Map<String, Object> assistant, Map<String, Object> sidebar)
    {
        Map<String, Object> publish = new HashMap<>();
        if (assistant != null)
        {
            publish.put("assistant", assistant);
        }
        if (sidebar != null)
        {
            publish.put("wecomSidebar", sidebar);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("publish", publish);
        return node("mt1", WorkflowPublishSupport.NODE_TYPE, data);
    }

    private static Map<String, Object> channel(boolean enabled, String description)
    {
        Map<String, Object> channel = new HashMap<>();
        channel.put("enabled", enabled);
        if (description != null)
        {
            channel.put("description", description);
        }
        return channel;
    }

    private static WorkflowNodeVO node(String id, String type, Map<String, Object> data)
    {
        WorkflowNodeVO vo = new WorkflowNodeVO();
        vo.setId(id);
        vo.setType(type);
        vo.setData(data);
        return vo;
    }
}
