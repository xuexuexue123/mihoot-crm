package com.ruoyi.crm.service.support;

import java.util.ArrayList;
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
 * WEBHOOK_TRIGGER path 计算：租户内 slug（{@code data.path}）与公网加密 path（{@code data.publicPath}）
 */
public final class WebhookPathUtils
{
    public static final String NODE_TYPE = "WEBHOOK_TRIGGER";

    /** 租户内可选自定义 slug，仅用于编辑器展示与租户内冲突检测 */
    public static final String SLUG_PATH_KEY = "path";

    /** 公网 URL 段：AES 加密后的 tenantId|workflowId|nodeId，保存时由后端写入 */
    public static final String PUBLIC_PATH_KEY = "publicPath";

    private WebhookPathUtils()
    {
    }

    public static String slugPath(String nodeId, Map<String, Object> data)
    {
        if (data != null)
        {
            Object pathValue = data.get(SLUG_PATH_KEY);
            if (pathValue != null && StringUtils.isNotBlank(pathValue.toString()))
            {
                return pathValue.toString().trim();
            }
        }
        return nodeId;
    }

    /** @deprecated 使用 {@link #slugPath(String, Map)} */
    @Deprecated
    public static String effectivePath(String nodeId, Map<String, Object> data)
    {
        return slugPath(nodeId, data);
    }

    public static String publicPath(Map<String, Object> data)
    {
        if (data == null)
        {
            return null;
        }
        Object value = data.get(PUBLIC_PATH_KEY);
        if (value == null || StringUtils.isBlank(value.toString()))
        {
            return null;
        }
        return value.toString().trim();
    }

    public static List<WebhookPathEntry> extractWebhookPaths(List<WorkflowNodeVO> nodes)
    {
        if (nodes == null || nodes.isEmpty())
        {
            return Collections.emptyList();
        }
        List<WebhookPathEntry> entries = new ArrayList<>();
        for (WorkflowNodeVO node : nodes)
        {
            if (node == null || !NODE_TYPE.equals(node.getType()) || StringUtils.isEmpty(node.getId()))
            {
                continue;
            }
            String slug = slugPath(node.getId(), node.getData());
            entries.add(new WebhookPathEntry(node.getId(), slug));
        }
        return entries;
    }

    public static void validateUniquePathsInGraph(List<WebhookPathEntry> entries, String workflowName)
    {
        Set<String> seenCustomPaths = new HashSet<>();
        for (WebhookPathEntry entry : entries)
        {
            if (entry.getNodeId().equals(entry.getSlugPath()))
            {
                continue;
            }
            if (!seenCustomPaths.add(entry.getSlugPath()))
            {
                throw new ServiceException(
                        "Ambiguous webhook path: " + entry.getSlugPath() + " in workflow " + workflowName,
                        HttpStatus.BAD_REQUEST);
            }
        }
    }

    public static final class WebhookPathEntry
    {
        private final String nodeId;
        private final String slugPath;

        public WebhookPathEntry(String nodeId, String slugPath)
        {
            this.nodeId = nodeId;
            this.slugPath = slugPath;
        }

        public String getNodeId()
        {
            return nodeId;
        }

        public String getSlugPath()
        {
            return slugPath;
        }

        /** @deprecated 使用 {@link #getSlugPath()} */
        @Deprecated
        public String getEffectivePath()
        {
            return slugPath;
        }
    }
}
