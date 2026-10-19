package com.ruoyi.crm.service.impl;

import java.util.ArrayList;
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
import com.ruoyi.crm.domain.dto.EventTriggerMatchRequest;
import com.ruoyi.crm.domain.vo.EventTriggerMatchVO;
import com.ruoyi.crm.domain.vo.WebhookCrmAuthVO;
import com.ruoyi.crm.mapper.WNodeMapper;
import com.ruoyi.crm.mapper.WWorkflowMapper;
import com.ruoyi.crm.service.IEventTriggerMatchService;
import com.ruoyi.crm.service.support.EventTriggerUtils;
import com.ruoyi.crm.service.support.WaitResumeTenantBootstrap;
import com.ruoyi.crm.service.support.WebhookCrmAuthService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

@Service
public class EventTriggerMatchServiceImpl implements IEventTriggerMatchService
{
    private static final Logger log = LoggerFactory.getLogger(EventTriggerMatchServiceImpl.class);

    @Autowired
    private WaitResumeTenantBootstrap waitResumeTenantBootstrap;

    @Autowired
    private WNodeMapper wNodeMapper;

    @Autowired
    private WWorkflowMapper wWorkflowMapper;

    @Autowired
    private WebhookCrmAuthService webhookCrmAuthService;

    @Override
    public List<EventTriggerMatchVO> match(EventTriggerMatchRequest request)
    {
        validateRequest(request);

        String tenantId = request.getTenant().trim();
        String eventType = request.getEventType().trim();

        List<EventTriggerMatchVO> results = new ArrayList<>();
        try
        {
            waitResumeTenantBootstrap.bindTenantDataSource(tenantId);
            List<WNode> nodes = wNodeMapper.selectEventTriggerMatchNodes(eventType);
            if (nodes != null)
            {
                for (WNode node : nodes)
                {
                    EventTriggerMatchVO vo = resolveMatchNode(tenantId, node);
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

        log.debug("event trigger match tenant={}, eventType={}, hits={}",
                tenantId, eventType, results.size());
        return results;
    }

    private EventTriggerMatchVO resolveMatchNode(String tenantId, WNode node)
    {
        if (node == null || StringUtils.isEmpty(node.getId()) || StringUtils.isEmpty(node.getWorkflowId()))
        {
            return null;
        }
        if (!EventTriggerUtils.NODE_TYPE.equals(node.getType()))
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
        if (!nodeData.containsKey(EventTriggerUtils.VARIABLE_NAME_KEY))
        {
            nodeData.put(EventTriggerUtils.VARIABLE_NAME_KEY,
                    EventTriggerUtils.resolveVariableName(nodeData));
        }

        WebhookCrmAuthVO crmAuth = webhookCrmAuthService.issueTokenForWorkflowOwner(
                workflow.getUserId(), tenantId);

        EventTriggerMatchVO vo = new EventTriggerMatchVO();
        vo.setWorkflowId(node.getWorkflowId());
        vo.setNodeId(node.getId());
        vo.setTenant(tenantId);
        vo.setNodeData(nodeData);
        vo.setCrmAuth(crmAuth);
        return vo;
    }

    private void validateRequest(EventTriggerMatchRequest request)
    {
        if (request == null)
        {
            throw new ServiceException("request body is required", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getTenant()))
        {
            throw new ServiceException("tenant is required", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getEventType()))
        {
            throw new ServiceException("eventType is required", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getSubjectType()))
        {
            throw new ServiceException("subjectType is required", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getSubjectId()))
        {
            throw new ServiceException("subjectId is required", HttpStatus.BAD_REQUEST);
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
