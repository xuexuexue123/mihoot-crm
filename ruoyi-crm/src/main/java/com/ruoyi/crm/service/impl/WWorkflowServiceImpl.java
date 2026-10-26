package com.ruoyi.crm.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.mapper.WConnectionMapper;
import com.ruoyi.crm.mapper.WNodeMapper;
import com.ruoyi.crm.mapper.WWorkflowMapper;
import com.ruoyi.crm.domain.WConnection;
import com.ruoyi.crm.domain.WNode;
import com.ruoyi.crm.domain.WWorkflow;
import com.ruoyi.crm.domain.vo.WorkflowEdgeVO;
import com.ruoyi.crm.domain.vo.WorkflowNodeVO;
import com.ruoyi.crm.service.IWWorkflowService;
import com.ruoyi.crm.service.support.EventTriggerNodeSupport;
import com.ruoyi.crm.service.support.RecordTriggerNodeSupport;
import com.ruoyi.crm.service.support.WebhookPathIndexSupport;
import com.ruoyi.crm.service.support.WorkflowPublishSupport;
import com.ruoyi.crm.service.support.WorkflowTemplateGuard;
import com.ruoyi.crm.service.support.WWorkflowCloneSupport;
import com.ruoyi.crm.service.support.WWorkflowGraphAssembler;

/**
 * WWorkflowService业务层处理
 * 
 * @author luohd
 * @date 2026-05-19
 */
@Service
public class WWorkflowServiceImpl implements IWWorkflowService 
{
    private static final String COPY_NAME_SUFFIX = " (copy)";
    private static final String TEMPLATE_NAME_SUFFIX = " (template)";

    @Autowired
    private WWorkflowMapper wWorkflowMapper;

    @Autowired
    private WNodeMapper wNodeMapper;

    @Autowired
    private WConnectionMapper wConnectionMapper;

    @Autowired
    private WebhookPathIndexSupport webhookPathIndexSupport;

    @Autowired
    private RecordTriggerNodeSupport recordTriggerNodeSupport;

    @Autowired
    private EventTriggerNodeSupport eventTriggerNodeSupport;

    /**
     * 查询WWorkflow
     * 
     * @param id WWorkflow主键
     * @return WWorkflow
     */
    @Override
    public WWorkflow selectWWorkflowById(String id)
    {
        WWorkflow workflow = wWorkflowMapper.selectWWorkflowById(id);
        if (workflow == null)
        {
            return null;
        }
        List<WNode> nodes = wNodeMapper.selectWNodeListByWorkflowIds(Collections.singletonList(id));
        List<WConnection> connections = wConnectionMapper.selectWConnectionListByWorkflowIds(Collections.singletonList(id));
        WWorkflowGraphAssembler.attachGraph(workflow, nodes, connections);
        return workflow;
    }

    /**
     * 查询WWorkflow列表
     * 
     * @param wWorkflow WWorkflow
     * @return WWorkflow
     */
    @Override
    public List<WWorkflow> selectWWorkflowList(WWorkflow wWorkflow)
    {
        List<WWorkflow> workflows = wWorkflowMapper.selectWWorkflowList(wWorkflow);
        populateWorkflowGraph(workflows);
        return workflows;
    }

    /**
     * 新增WWorkflow
     * 
     * @param wWorkflow WWorkflow
     * @return 结果
     */
    @Override
    @Transactional
    public int insertWWorkflow(WWorkflow wWorkflow)
    {
        if (wWorkflow.getIsTemplate() == null)
        {
            wWorkflow.setIsTemplate(Boolean.FALSE);
        }
        if (wWorkflow.getDescription() == null)
        {
            wWorkflow.setDescription("");
        }
        prepareTemplateSideEffects(wWorkflow, Boolean.TRUE.equals(wWorkflow.getIsTemplate()));
        int rows = wWorkflowMapper.insertWWorkflow(wWorkflow);
        if (rows > 0 && hasGraphPayload(wWorkflow))
        {
            syncGraph(wWorkflow, Boolean.TRUE.equals(wWorkflow.getIsTemplate()));
        }
        return rows;
    }

    /**
     * 修改WWorkflow
     * 
     * @param wWorkflow WWorkflow
     * @return 结果
     */
    @Override
    @Transactional
    public int updateWWorkflow(WWorkflow wWorkflow)
    {
        boolean asTemplate = resolveIsTemplateForUpdate(wWorkflow);
        if (wWorkflow.getIsTemplate() == null)
        {
            wWorkflow.setIsTemplate(asTemplate);
        }
        prepareTemplateSideEffects(wWorkflow, asTemplate);
        int rows = wWorkflowMapper.updateWWorkflow(wWorkflow);
        if (rows > 0 && hasGraphPayload(wWorkflow))
        {
            syncGraph(wWorkflow, asTemplate);
        }
        return rows;
    }

    @Override
    public int updateWWorkflowName(WWorkflow wWorkflow)
    {
        return wWorkflowMapper.updateWWorkflowName(wWorkflow);
    }

    @Override
    @Transactional
    public WWorkflow updateWWorkflowMeta(String id, String name, String description)
    {
        if (StringUtils.isEmpty(id))
        {
            throw new ServiceException("id 不能为空", HttpStatus.BAD_REQUEST);
        }
        boolean updateName = StringUtils.isNotEmpty(name);
        boolean updateDescription = description != null;
        if (!updateName && !updateDescription)
        {
            throw new ServiceException("name 或 description 至少提供一项", HttpStatus.BAD_REQUEST);
        }
        WWorkflow existing = wWorkflowMapper.selectWWorkflowById(id);
        if (existing == null)
        {
            throw new ServiceException("工作流不存在", HttpStatus.NOT_FOUND);
        }
        WWorkflow patch = new WWorkflow();
        patch.setId(id);
        if (updateName)
        {
            patch.setName(name.trim());
        }
        if (updateDescription)
        {
            patch.setDescription(description);
        }
        wWorkflowMapper.updateWWorkflowMeta(patch);
        return selectWWorkflowById(id);
    }

    @Override
    @Transactional
    public WWorkflow updateTemplateFlag(String id, Boolean isTemplate)
    {
        if (StringUtils.isEmpty(id))
        {
            throw new ServiceException("id 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (isTemplate == null)
        {
            throw new ServiceException("isTemplate 不能为空", HttpStatus.BAD_REQUEST);
        }
        WWorkflow existing = selectWWorkflowById(id);
        if (existing == null)
        {
            throw new ServiceException("工作流不存在", HttpStatus.NOT_FOUND);
        }

        if (Boolean.TRUE.equals(isTemplate))
        {
            WWorkflowCloneSupport.clearPublishAndWebhookPublicPath(existing.getNodes());
            existing.setIsTemplate(Boolean.TRUE);
            existing.setPublishAssistant(Boolean.FALSE);
            existing.setPublishSidebar(Boolean.FALSE);
            wWorkflowMapper.updateWWorkflowTemplateFlag(existing);
            syncGraph(existing, true);
        }
        else
        {
            WWorkflow patch = new WWorkflow();
            patch.setId(id);
            patch.setIsTemplate(Boolean.FALSE);
            patch.setPublishAssistant(Boolean.TRUE.equals(existing.getPublishAssistant()));
            patch.setPublishSidebar(Boolean.TRUE.equals(existing.getPublishSidebar()));
            wWorkflowMapper.updateWWorkflowTemplateFlag(patch);
            existing.setIsTemplate(Boolean.FALSE);
        }
        return selectWWorkflowById(id);
    }

    @Override
    @Transactional
    public WWorkflow cloneAsInstance(String id, String name, String description)
    {
        return cloneWorkflow(id, name, description, false, COPY_NAME_SUFFIX);
    }

    @Override
    @Transactional
    public WWorkflow saveAsTemplate(String id, String name, String description)
    {
        return cloneWorkflow(id, name, description, true, TEMPLATE_NAME_SUFFIX);
    }

    @Override
    @Transactional
    public int backfillPublishFlags()
    {
        List<WWorkflow> workflows = wWorkflowMapper.selectWWorkflowList(new WWorkflow());
        if (workflows == null || workflows.isEmpty())
        {
            return 0;
        }
        List<String> workflowIds = workflows.stream()
            .map(WWorkflow::getId)
            .filter(StringUtils::isNotEmpty)
            .collect(Collectors.toList());
        if (workflowIds.isEmpty())
        {
            return 0;
        }
        List<WNode> allNodes = wNodeMapper.selectWNodeListByWorkflowIds(workflowIds);
        Map<String, List<WNode>> nodesByWorkflow = allNodes.stream()
            .collect(Collectors.groupingBy(WNode::getWorkflowId));

        int updated = 0;
        for (WWorkflow workflow : workflows)
        {
            if (workflow == null || StringUtils.isEmpty(workflow.getId()))
            {
                continue;
            }
            List<WNode> nodes = nodesByWorkflow.getOrDefault(workflow.getId(), Collections.emptyList());
            List<WorkflowNodeVO> nodeVOs = WWorkflowGraphAssembler.toNodes(nodes);
            boolean assistant;
            boolean sidebar;
            if (Boolean.TRUE.equals(workflow.getIsTemplate()))
            {
                assistant = false;
                sidebar = false;
            }
            else
            {
                assistant = WorkflowPublishSupport.workflowPublishAssistant(nodeVOs);
                sidebar = WorkflowPublishSupport.workflowPublishSidebar(nodeVOs);
            }
            boolean currentA = Boolean.TRUE.equals(workflow.getPublishAssistant());
            boolean currentS = Boolean.TRUE.equals(workflow.getPublishSidebar());
            if (assistant == currentA && sidebar == currentS)
            {
                continue;
            }
            WWorkflow patch = new WWorkflow();
            patch.setId(workflow.getId());
            patch.setPublishAssistant(assistant);
            patch.setPublishSidebar(sidebar);
            updated += wWorkflowMapper.updateWWorkflowPublishFlags(patch);
        }
        return updated;
    }

    /**
     * 批量删除WWorkflow
     * 
     * @param ids 需要删除的WWorkflow主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteWWorkflowByIds(String[] ids)
    {
        if (ids != null)
        {
            for (String id : ids)
            {
                deleteWorkflowGraph(id);
            }
        }
        return wWorkflowMapper.deleteWWorkflowByIds(ids);
    }

    /**
     * 删除WWorkflow信息
     * 
     * @param id WWorkflow主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteWWorkflowById(String id)
    {
        deleteWorkflowGraph(id);
        return wWorkflowMapper.deleteWWorkflowById(id);
    }

    private WWorkflow cloneWorkflow(String id, String name, String descriptionOverride,
            boolean asTemplate, String defaultSuffix)
    {
        if (StringUtils.isEmpty(id))
        {
            throw new ServiceException("id 不能为空", HttpStatus.BAD_REQUEST);
        }
        WWorkflow source = selectWWorkflowById(id);
        if (source == null)
        {
            throw new ServiceException("工作流不存在", HttpStatus.NOT_FOUND);
        }

        WWorkflowCloneSupport.GraphCopy graph = WWorkflowCloneSupport.deepCopyGraph(
                source.getNodes(), source.getEdges());

        WWorkflow target = new WWorkflow();
        target.setId(IdUtils.fastUUID());
        target.setName(resolveCloneName(name, source.getName(), defaultSuffix));
        target.setUserId(source.getUserId());
        target.setOrganizationId(source.getOrganizationId());
        target.setIsTemplate(asTemplate);
        target.setPublishAssistant(Boolean.FALSE);
        target.setPublishSidebar(Boolean.FALSE);
        if (descriptionOverride != null)
        {
            target.setDescription(descriptionOverride);
        }
        else
        {
            target.setDescription(source.getDescription() != null ? source.getDescription() : "");
        }
        target.setNodes(graph.getNodes());
        target.setEdges(graph.getEdges());

        wWorkflowMapper.insertWWorkflow(target);
        syncGraph(target, asTemplate);
        return selectWWorkflowById(target.getId());
    }

    private static String resolveCloneName(String requested, String sourceName, String suffix)
    {
        if (StringUtils.isNotEmpty(requested))
        {
            return requested.trim();
        }
        String base = StringUtils.isNotEmpty(sourceName) ? sourceName : "Workflow";
        return base + suffix;
    }

    private boolean resolveIsTemplateForUpdate(WWorkflow wWorkflow)
    {
        if (wWorkflow.getIsTemplate() != null)
        {
            return Boolean.TRUE.equals(wWorkflow.getIsTemplate());
        }
        if (StringUtils.isEmpty(wWorkflow.getId()))
        {
            return false;
        }
        WWorkflow existing = wWorkflowMapper.selectWWorkflowById(wWorkflow.getId());
        return WorkflowTemplateGuard.isTemplate(existing);
    }

    private void prepareTemplateSideEffects(WWorkflow wWorkflow, boolean asTemplate)
    {
        if (asTemplate)
        {
            wWorkflow.setPublishAssistant(Boolean.FALSE);
            wWorkflow.setPublishSidebar(Boolean.FALSE);
            if (hasGraphPayload(wWorkflow) && wWorkflow.getNodes() != null)
            {
                WWorkflowCloneSupport.clearPublishAndWebhookPublicPath(wWorkflow.getNodes());
            }
            return;
        }
        applyPublishFlagsFromGraph(wWorkflow);
    }

    private void populateWorkflowGraph(List<WWorkflow> workflows)
    {
        if (workflows == null || workflows.isEmpty())
        {
            return;
        }
        List<String> workflowIds = workflows.stream()
            .map(WWorkflow::getId)
            .filter(StringUtils::isNotEmpty)
            .collect(Collectors.toList());
        if (workflowIds.isEmpty())
        {
            return;
        }
        List<WNode> nodes = wNodeMapper.selectWNodeListByWorkflowIds(workflowIds);
        List<WConnection> connections = wConnectionMapper.selectWConnectionListByWorkflowIds(workflowIds);
        WWorkflowGraphAssembler.attachGraph(workflows, nodes, connections);
    }

    private boolean hasGraphPayload(WWorkflow wWorkflow)
    {
        return wWorkflow.getNodes() != null || wWorkflow.getEdges() != null;
    }

    private void applyPublishFlagsFromGraph(WWorkflow wWorkflow)
    {
        if (!hasGraphPayload(wWorkflow))
        {
            return;
        }
        List<WorkflowNodeVO> nodeVOs = wWorkflow.getNodes() != null
            ? wWorkflow.getNodes()
            : Collections.emptyList();
        wWorkflow.setPublishAssistant(WorkflowPublishSupport.workflowPublishAssistant(nodeVOs));
        wWorkflow.setPublishSidebar(WorkflowPublishSupport.workflowPublishSidebar(nodeVOs));
    }

    private void syncGraph(WWorkflow wWorkflow, boolean asTemplate)
    {
        String workflowId = wWorkflow.getId();
        if (StringUtils.isEmpty(workflowId))
        {
            return;
        }
        List<WorkflowNodeVO> nodeVOs = wWorkflow.getNodes() != null
            ? wWorkflow.getNodes()
            : Collections.emptyList();
        List<WorkflowEdgeVO> edgeVOs = wWorkflow.getEdges() != null
            ? wWorkflow.getEdges()
            : Collections.emptyList();

        if (asTemplate)
        {
            WWorkflowCloneSupport.clearPublishAndWebhookPublicPath(nodeVOs);
        }

        String tenantId = webhookPathIndexSupport.requireCurrentTenantId();
        String workflowName = StringUtils.isNotEmpty(wWorkflow.getName()) ? wWorkflow.getName() : workflowId;
        webhookPathIndexSupport.validateBeforeSave(tenantId, workflowId, workflowName, nodeVOs);
        if (!asTemplate)
        {
            webhookPathIndexSupport.enrichPublicPathsForSave(tenantId, workflowId, nodeVOs);
        }
        recordTriggerNodeSupport.validateBeforeSave(tenantId, workflowId, workflowName, nodeVOs);
        eventTriggerNodeSupport.validateBeforeSave(tenantId, workflowId, workflowName, nodeVOs);

        wConnectionMapper.deleteWConnectionByWorkflowId(workflowId);
        wNodeMapper.deleteWNodeByWorkflowId(workflowId);

        for (WorkflowNodeVO nodeVO : nodeVOs)
        {
            if (nodeVO == null || StringUtils.isEmpty(nodeVO.getId()))
            {
                continue;
            }
            wNodeMapper.insertWNode(WWorkflowGraphAssembler.fromNode(nodeVO, workflowId));
        }
        for (WorkflowEdgeVO edgeVO : edgeVOs)
        {
            if (edgeVO == null || StringUtils.isEmpty(edgeVO.getId()))
            {
                continue;
            }
            wConnectionMapper.insertWConnection(WWorkflowGraphAssembler.fromEdge(edgeVO, workflowId));
        }
    }

    private void deleteWorkflowGraph(String workflowId)
    {
        if (StringUtils.isEmpty(workflowId))
        {
            return;
        }
        webhookPathIndexSupport.removeIndexForWorkflow(workflowId);
        wConnectionMapper.deleteWConnectionByWorkflowId(workflowId);
        wNodeMapper.deleteWNodeByWorkflowId(workflowId);
    }
}
