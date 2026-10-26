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

public class WebhookPathUtilsTest
{
    @Test
    public void effectivePathUsesCustomPathWhenSet()
    {
        Map<String, Object> data = new HashMap<>();
        data.put("path", "order-created");
        assertEquals("order-created", WebhookPathUtils.effectivePath("node-1", data));
    }

    @Test
    public void effectivePathFallsBackToNodeId()
    {
        assertEquals("node-abc", WebhookPathUtils.effectivePath("node-abc", Collections.emptyMap()));
        assertEquals("node-abc", WebhookPathUtils.effectivePath("node-abc", null));
    }

    @Test
    public void effectivePathIgnoresBlankCustomPath()
    {
        Map<String, Object> data = new HashMap<>();
        data.put("path", "   ");
        assertEquals("node-xyz", WebhookPathUtils.effectivePath("node-xyz", data));
    }

    @Test
    public void extractWebhookPathsSkipsNonTriggerNodes()
    {
        WorkflowNodeVO trigger = node("n1", WebhookPathUtils.NODE_TYPE, "my-hook");
        WorkflowNodeVO other = node("n2", "HTTP_REQUEST", null);
        List<WebhookPathUtils.WebhookPathEntry> entries = WebhookPathUtils.extractWebhookPaths(
                Arrays.asList(trigger, other));
        assertEquals(1, entries.size());
        assertEquals("my-hook", entries.get(0).getSlugPath());
    }

    @Test
    public void validateUniquePathsInGraphRejectsDuplicateCustomPaths()
    {
        WorkflowNodeVO a = node("n1", WebhookPathUtils.NODE_TYPE, "dup");
        WorkflowNodeVO b = node("n2", WebhookPathUtils.NODE_TYPE, "dup");
        List<WebhookPathUtils.WebhookPathEntry> entries = WebhookPathUtils.extractWebhookPaths(
                Arrays.asList(a, b));
        assertThrows(ServiceException.class,
                () -> WebhookPathUtils.validateUniquePathsInGraph(entries, "wf-1"));
    }

    private WorkflowNodeVO node(String id, String type, String path)
    {
        WorkflowNodeVO vo = new WorkflowNodeVO();
        vo.setId(id);
        vo.setType(type);
        if (path != null)
        {
            Map<String, Object> data = new HashMap<>();
            data.put("path", path);
            vo.setData(data);
        }
        return vo;
    }
}
