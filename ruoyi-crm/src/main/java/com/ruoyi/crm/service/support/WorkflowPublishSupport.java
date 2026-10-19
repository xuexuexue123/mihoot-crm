package com.ruoyi.crm.service.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.vo.WorkflowNodeVO;

/**
 * MANUAL_TRIGGER {@code data.publish} / legacy {@code agentPublish} — dual-channel publish flags.
 */
public final class WorkflowPublishSupport
{
    public static final String NODE_TYPE = "MANUAL_TRIGGER";

    private static final String PUBLISH_KEY = "publish";

    private static final String ASSISTANT_KEY = "assistant";

    private static final String WECOM_SIDEBAR_KEY = "wecomSidebar";

    private static final String AGENT_PUBLISH_KEY = "agentPublish";

    private static final String ENABLED_KEY = "enabled";

    private static final String DESCRIPTION_KEY = "description";

    private WorkflowPublishSupport()
    {
    }

    public static boolean workflowPublishAssistant(List<WorkflowNodeVO> nodes)
    {
        if (nodes == null || nodes.isEmpty())
        {
            return false;
        }
        for (WorkflowNodeVO node : nodes)
        {
            if (!isManualTrigger(node))
            {
                continue;
            }
            Map<String, Object> publish = resolvePublish(node.getData());
            if (isAssistantPublishComplete(asMap(publish.get(ASSISTANT_KEY))))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean workflowPublishSidebar(List<WorkflowNodeVO> nodes)
    {
        if (nodes == null || nodes.isEmpty())
        {
            return false;
        }
        for (WorkflowNodeVO node : nodes)
        {
            if (!isManualTrigger(node))
            {
                continue;
            }
            Map<String, Object> publish = resolvePublish(node.getData());
            if (isSidebarPublishComplete(asMap(publish.get(WECOM_SIDEBAR_KEY))))
            {
                return true;
            }
        }
        return false;
    }

    static Map<String, Object> resolvePublish(Map<String, Object> data)
    {
        Map<String, Object> publish = new HashMap<>();
        if (data == null)
        {
            return publish;
        }
        Map<String, Object> existing = asMap(data.get(PUBLISH_KEY));
        if (existing != null)
        {
            publish.putAll(existing);
        }
        if (publish.get(ASSISTANT_KEY) == null && data.get(AGENT_PUBLISH_KEY) != null)
        {
            publish.put(ASSISTANT_KEY, data.get(AGENT_PUBLISH_KEY));
        }
        return publish;
    }

    static boolean isAssistantPublishComplete(Map<String, Object> channel)
    {
        return channel != null
                && Boolean.TRUE.equals(channel.get(ENABLED_KEY))
                && StringUtils.isNotBlank(stringField(channel, DESCRIPTION_KEY));
    }

    static boolean isSidebarPublishComplete(Map<String, Object> channel)
    {
        return channel != null && Boolean.TRUE.equals(channel.get(ENABLED_KEY));
    }

    /**
     * 清除 MANUAL_TRIGGER 节点上的双渠道发布配置（含 legacy agentPublish）。
     */
    @SuppressWarnings("unchecked")
    public static void disablePublishChannels(Map<String, Object> data)
    {
        if (data == null)
        {
            return;
        }
        Object publishObj = data.get(PUBLISH_KEY);
        if (publishObj instanceof Map)
        {
            Map<String, Object> publish = (Map<String, Object>) publishObj;
            disableChannel(asMap(publish.get(ASSISTANT_KEY)));
            disableChannel(asMap(publish.get(WECOM_SIDEBAR_KEY)));
        }
        Object agentPublish = data.get(AGENT_PUBLISH_KEY);
        if (agentPublish instanceof Map)
        {
            disableChannel((Map<String, Object>) agentPublish);
        }
        else
        {
            data.remove(AGENT_PUBLISH_KEY);
        }
    }

    private static void disableChannel(Map<String, Object> channel)
    {
        if (channel != null)
        {
            channel.put(ENABLED_KEY, false);
        }
    }

    private static boolean isManualTrigger(WorkflowNodeVO node)
    {
        return node != null && NODE_TYPE.equals(node.getType()) && StringUtils.isNotEmpty(node.getId());
    }

    @SuppressWarnings("unchecked")
    static Map<String, Object> asMap(Object value)
    {
        if (value instanceof Map)
        {
            return (Map<String, Object>) value;
        }
        return null;
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
}
