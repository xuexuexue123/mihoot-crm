package com.ruoyi.crm.service.impl;

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
import com.ruoyi.crm.domain.dto.WebhookTriggerResolveRequest;
import com.ruoyi.crm.domain.vo.WebhookCrmAuthVO;
import com.ruoyi.crm.domain.vo.WebhookTriggerResolveVO;
import com.ruoyi.crm.mapper.WNodeMapper;
import com.ruoyi.crm.mapper.WWorkflowMapper;
import com.ruoyi.crm.service.IWebhookTriggerResolveService;
import com.ruoyi.crm.service.support.WaitResumeTenantBootstrap;
import com.ruoyi.crm.service.support.WebhookCrmAuthService;
import com.ruoyi.crm.service.support.WebhookPathCodec;
import com.ruoyi.crm.service.support.WebhookPathIndexSupport;
import com.ruoyi.crm.service.support.WebhookPathUtils;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

@Service
public class WebhookTriggerResolveServiceImpl implements IWebhookTriggerResolveService
{
    private static final Logger log = LoggerFactory.getLogger(WebhookTriggerResolveServiceImpl.class);

    @Autowired
    private WebhookPathIndexSupport webhookPathIndexSupport;

    @Autowired
    private WaitResumeTenantBootstrap waitResumeTenantBootstrap;

    @Autowired
    private WNodeMapper wNodeMapper;

    @Autowired
    private WWorkflowMapper wWorkflowMapper;

    @Autowired
    private WebhookCrmAuthService webhookCrmAuthService;

    @Override
    public WebhookTriggerResolveVO resolve(WebhookTriggerResolveRequest request)
    {
        if (request == null || StringUtils.isBlank(request.getPath()))
        {
            throw new ServiceException("path is required", HttpStatus.BAD_REQUEST);
        }

        String encryptedPath = request.getPath().trim();
        if (StringUtils.isNotEmpty(request.getHttpMethod()))
        {
            log.debug("webhook resolve encryptedPath={}, httpMethod={}", encryptedPath, request.getHttpMethod());
        }

        WebhookPathCodec.DecodedWebhookPath decoded = webhookPathIndexSupport.decodePublicPath(encryptedPath);
        String tenantId = decoded.getTenantId();
        String workflowId = decoded.getWorkflowId();
        String nodeId = decoded.getNodeId();

        log.debug("webhook resolve decrypted tenant={}, workflowId={}, nodeId={}", tenantId, workflowId, nodeId);
        try
        {
            waitResumeTenantBootstrap.bindTenantDataSource(tenantId);

            WNode node = wNodeMapper.selectWNodeById(nodeId);
            if (node == null || !WebhookPathUtils.NODE_TYPE.equals(node.getType()))
            {
                throw new ServiceException("Webhook endpoint not found", HttpStatus.NOT_FOUND);
            }
            if (!workflowId.equals(node.getWorkflowId()))
            {
                throw new ServiceException("Webhook endpoint not found", HttpStatus.NOT_FOUND);
            }

            Map<String, Object> nodeData = parseNodeData(node.getData());
            String storedPublicPath = WebhookPathUtils.publicPath(nodeData);
            if (!encryptedPath.equals(storedPublicPath))
            {
                throw new ServiceException("Webhook endpoint not found", HttpStatus.NOT_FOUND);
            }

            WWorkflow workflow = wWorkflowMapper.selectWWorkflowById(workflowId);
            if (workflow == null)
            {
                throw new ServiceException("Webhook endpoint not found", HttpStatus.NOT_FOUND);
            }
            if (Boolean.TRUE.equals(workflow.getIsTemplate()))
            {
                throw new ServiceException("Webhook endpoint not found", HttpStatus.NOT_FOUND);
            }

            WebhookCrmAuthVO crmAuth = webhookCrmAuthService.issueTokenForWorkflowOwner(workflow.getUserId(), tenantId);

            log.info("webhook resolve ok tenant={}, workflowId={}, nodeId={}", tenantId, workflowId, nodeId);

            WebhookTriggerResolveVO vo = new WebhookTriggerResolveVO();
            vo.setWorkflowId(workflowId);
            vo.setNodeId(nodeId);
            vo.setTenant(tenantId);
            vo.setNodeData(nodeData);
            vo.setCrmAuth(crmAuth);
            return vo;
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    private Map<String, Object> parseNodeData(String dataJson)
    {
        if (StringUtils.isEmpty(dataJson))
        {
            return new java.util.HashMap<>();
        }
        return JSON.parseObject(dataJson, new TypeReference<Map<String, Object>>() {});
    }
}
