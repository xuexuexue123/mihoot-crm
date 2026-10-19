package com.ruoyi.crm.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.WNode;
import com.ruoyi.crm.domain.WWorkflow;
import com.ruoyi.crm.domain.dto.RecordTriggerMatchRequest;
import com.ruoyi.crm.domain.vo.RecordTriggerMatchVO;
import com.ruoyi.crm.domain.vo.WebhookCrmAuthVO;
import com.ruoyi.crm.mapper.WNodeMapper;
import com.ruoyi.crm.mapper.WWorkflowMapper;
import com.ruoyi.crm.service.IRecordTriggerMatchService;
import com.ruoyi.crm.service.support.RecordTriggerUtils;
import com.ruoyi.crm.service.support.WaitResumeTenantBootstrap;
import com.ruoyi.crm.service.support.WebhookCrmAuthService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

@Service
public class RecordTriggerMatchServiceImpl implements IRecordTriggerMatchService
{
    private static final Logger log = LoggerFactory.getLogger(RecordTriggerMatchServiceImpl.class);

    @Autowired
    private WaitResumeTenantBootstrap waitResumeTenantBootstrap;

    @Autowired
    private WNodeMapper wNodeMapper;

    @Autowired
    private WWorkflowMapper wWorkflowMapper;

    @Autowired
    private WebhookCrmAuthService webhookCrmAuthService;

    @Override
    public List<RecordTriggerMatchVO> match(RecordTriggerMatchRequest request)
    {
        validateRequest(request);

        String tenantId = request.getTenant().trim();
        String entityType = request.getEntityType().trim();
        String operation = request.getOperation().trim();

        if (!RecordTriggerUtils.ENTITY_TYPES.contains(entityType))
        {
            throw new ServiceException("Invalid entityType: " + entityType, HttpStatus.BAD_REQUEST);
        }

        if (RecordTriggerUtils.OPERATION_UPDATE.equals(operation)
                && (request.getChangedFields() == null || request.getChangedFields().isEmpty()))
        {
            return Collections.emptyList();
        }

        List<String> eventTypes = RecordTriggerUtils.eventTypesForOperation(operation);
        List<RecordTriggerMatchVO> results = new ArrayList<>();
        try
        {
            waitResumeTenantBootstrap.bindTenantDataSource(tenantId);
            List<WNode> nodes = wNodeMapper.selectRecordTriggerMatchNodes(entityType, eventTypes);
            if (nodes != null)
            {
                for (WNode node : nodes)
                {
                    RecordTriggerMatchVO vo = resolveMatchNode(tenantId, node);
                    if (vo != null)
                    {
                        results.add(vo);
                    }
                }
            }
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }

        log.debug("record trigger match tenant={}, entity={}, operation={}, hits={}",
                tenantId, entityType, operation, results.size());
        return results;
    }

    private RecordTriggerMatchVO resolveMatchNode(String tenantId, WNode node)
    {
        if (node == null || StringUtils.isEmpty(node.getId()) || StringUtils.isEmpty(node.getWorkflowId()))
        {
            return null;
        }
        if (!RecordTriggerUtils.NODE_TYPE.equals(node.getType()))
        {
            return null;
        }

        WWorkflow workflow = wWorkflowMapper.selectWWorkflowById(node.getWorkflowId());
        if (workflow == null)
        {
            return null;
        }
        if (Boolean.TRUE.equals(workflow.getIsTemplate()))
        {
            return null;
        }

        Map<String, Object> nodeData = parseNodeData(node.getData());
        if (!nodeData.containsKey(RecordTriggerUtils.VARIABLE_NAME_KEY))
        {
            nodeData.put(RecordTriggerUtils.VARIABLE_NAME_KEY,
                    RecordTriggerUtils.resolveVariableName(nodeData));
        }

        WebhookCrmAuthVO crmAuth = webhookCrmAuthService.issueTokenForWorkflowOwner(
                workflow.getUserId(), tenantId);

        RecordTriggerMatchVO vo = new RecordTriggerMatchVO();
        vo.setWorkflowId(node.getWorkflowId());
        vo.setNodeId(node.getId());
        vo.setTenant(tenantId);
        vo.setNodeData(nodeData);
        vo.setCrmAuth(crmAuth);
        return vo;
    }

    private void validateRequest(RecordTriggerMatchRequest request)
    {
        if (request == null)
        {
            throw new ServiceException("request body is required", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getTenant()))
        {
            throw new ServiceException("tenant is required", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getEntityType()))
        {
            throw new ServiceException("entityType is required", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getOperation()))
        {
            throw new ServiceException("operation is required", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getRecordId()))
        {
            throw new ServiceException("recordId is required", HttpStatus.BAD_REQUEST);
        }
        if (!RecordTriggerUtils.ENTITY_TYPES.contains(request.getEntityType().trim()))
        {
            throw new ServiceException("Invalid entityType: " + request.getEntityType(), HttpStatus.BAD_REQUEST);
        }
    }

    private Map<String, Object> parseNodeData(String dataJson)
    {
        if (StringUtils.isEmpty(dataJson))
        {
            return new HashMap<>();
        }
        return JSON.parseObject(dataJson, new TypeReference<Map<String, Object>>() {});
    }
}
