package com.ruoyi.crm.service.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.WNode;
import com.ruoyi.crm.domain.vo.WorkflowNodeVO;
import com.ruoyi.crm.mapper.WNodeMapper;
import com.ruoyi.crm.mapper.WWorkflowMapper;

/**
 * WEBHOOK_TRIGGER path：租户内 slug 校验、保存时写入加密 publicPath、resolve 解密路由
 */
@Component
public class WebhookPathIndexSupport
{
    private static final Logger log = LoggerFactory.getLogger(WebhookPathIndexSupport.class);

    @Autowired
    private WNodeMapper wNodeMapper;

    @Autowired
    private WWorkflowMapper wWorkflowMapper;

    @Autowired
    private WebhookPathCodec webhookPathCodec;

    public void validateBeforeSave(String tenantId, String workflowId, String workflowName, List<WorkflowNodeVO> nodes)
    {
        List<WebhookPathUtils.WebhookPathEntry> entries = WebhookPathUtils.extractWebhookPaths(nodes);
        WebhookPathUtils.validateUniquePathsInGraph(entries, workflowName);

        List<String> customSlugs = entries.stream()
                .filter(entry -> !entry.getNodeId().equals(entry.getSlugPath()))
                .map(WebhookPathUtils.WebhookPathEntry::getSlugPath)
                .collect(Collectors.toList());
        if (customSlugs.isEmpty())
        {
            return;
        }

        WNode query = new WNode();
        query.setType(WebhookPathUtils.NODE_TYPE);
        List<WNode> existingNodes = wNodeMapper.selectWNodeList(query);
        for (WNode existing : existingNodes)
        {
            if (existing == null || workflowId.equals(existing.getWorkflowId()))
            {
                continue;
            }
            Map<String, Object> data = parseNodeDataJson(existing.getData());
            String slug = WebhookPathUtils.slugPath(existing.getId(), data);
            if (!customSlugs.contains(slug))
            {
                continue;
            }
            String otherWorkflowName = resolveWorkflowName(existing.getWorkflowId());
            throw new ServiceException(
                    "Webhook path conflict: \"" + slug + "\" already used by workflow " + otherWorkflowName,
                    HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 保存前为每个 WEBHOOK_TRIGGER 节点写入 {@code data.publicPath}（加密 tenantId|workflowId|nodeId）
     */
    public void enrichPublicPathsForSave(String tenantId, String workflowId, List<WorkflowNodeVO> nodes)
    {
        if (nodes == null || nodes.isEmpty())
        {
            return;
        }
        for (WorkflowNodeVO node : nodes)
        {
            if (node == null || !WebhookPathUtils.NODE_TYPE.equals(node.getType())
                    || StringUtils.isEmpty(node.getId()))
            {
                continue;
            }
            Map<String, Object> data = node.getData();
            if (data == null)
            {
                data = new HashMap<>();
                node.setData(data);
            }
            String publicPath = webhookPathCodec.encode(tenantId, workflowId, node.getId());
            data.put(WebhookPathUtils.PUBLIC_PATH_KEY, publicPath);
        }
    }

    public void removeIndexForWorkflow(String workflowId)
    {
        // 索引已写入 tenant 库 w_node.data.publicPath，删除 workflow 图时一并删除节点，无需额外操作
        log.debug("webhook nodes removed with workflowId={}", workflowId);
    }

    public WebhookPathCodec.DecodedWebhookPath decodePublicPath(String encryptedPath)
    {
        return webhookPathCodec.decode(encryptedPath);
    }

    /**
     * 为当前租户所有 WEBHOOK_TRIGGER 节点重新生成 publicPath（历史数据迁移）
     */
    public int rebuildPublicPathsForCurrentTenant(String tenantId)
    {
        WNode query = new WNode();
        query.setType(WebhookPathUtils.NODE_TYPE);
        List<WNode> nodes = wNodeMapper.selectWNodeList(query);
        int updated = 0;
        for (WNode node : nodes)
        {
            if (node == null || StringUtils.isEmpty(node.getId()) || StringUtils.isEmpty(node.getWorkflowId()))
            {
                continue;
            }
            Map<String, Object> data = parseNodeDataJson(node.getData());
            String publicPath = webhookPathCodec.encode(tenantId, node.getWorkflowId(), node.getId());
            data.put(WebhookPathUtils.PUBLIC_PATH_KEY, publicPath);
            node.setData(JSON.toJSONString(data));
            wNodeMapper.updateWNode(node);
            updated++;
        }
        log.info("webhook rebuild publicPath tenant={}, nodes={}", tenantId, updated);
        return updated;
    }

    public String requireCurrentTenantId()
    {
        try
        {
            String tenant = SecurityUtils.getLoginUser().getTenant();
            if (StringUtils.isNotBlank(tenant))
            {
                return tenant.trim();
            }
        }
        catch (Exception ignored)
        {
        }
        String dsKey = TenantDataSourceSupport.currentTenantKey();
        if (StringUtils.isNotBlank(dsKey))
        {
            return dsKey.trim();
        }
        throw new ServiceException("tenant 不能为空", HttpStatus.BAD_REQUEST);
    }

    private String resolveWorkflowName(String workflowId)
    {
        if (StringUtils.isEmpty(workflowId))
        {
            return workflowId;
        }
        com.ruoyi.crm.domain.WWorkflow workflow = wWorkflowMapper.selectWWorkflowById(workflowId);
        if (workflow != null && StringUtils.isNotEmpty(workflow.getName()))
        {
            return workflow.getName();
        }
        return workflowId;
    }

    private Map<String, Object> parseNodeDataJson(String dataJson)
    {
        if (StringUtils.isEmpty(dataJson))
        {
            return new HashMap<>();
        }
        return JSON.parseObject(dataJson, new TypeReference<Map<String, Object>>() {});
    }
}
