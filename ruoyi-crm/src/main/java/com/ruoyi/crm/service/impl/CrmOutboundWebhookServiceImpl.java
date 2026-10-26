package com.ruoyi.crm.service.impl;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmOutboundWebhook;
import com.ruoyi.crm.domain.CrmOutboundWebhookDelivery;
import com.ruoyi.crm.domain.dto.OutboundWebhookSaveRequest;
import com.ruoyi.crm.domain.vo.OutboundWebhookDeliveryVo;
import com.ruoyi.crm.domain.vo.OutboundWebhookTestResultVo;
import com.ruoyi.crm.domain.vo.OutboundWebhookVo;
import com.ruoyi.crm.mapper.CrmOutboundWebhookDeliveryMapper;
import com.ruoyi.crm.mapper.CrmOutboundWebhookMapper;
import com.ruoyi.crm.service.ICrmOutboundWebhookService;
import com.ruoyi.crm.service.support.OutboundWebhookValidator;
import com.ruoyi.crm.service.support.OutboundWebhookValidator.ValidatedOutboundWebhook;
import com.ruoyi.crm.service.support.TokenEncryptor;
import com.ruoyi.crm.service.webhook.OutboundWebhookConstants;
import com.ruoyi.crm.service.webhook.OutboundWebhookDeliveryWorker;
import com.ruoyi.crm.service.webhook.OutboundWebhookDispatcher;
import com.ruoyi.crm.service.webhook.OutboundWebhookSampleCatalog;

@Service
public class CrmOutboundWebhookServiceImpl implements ICrmOutboundWebhookService
{
    @Autowired
    private CrmOutboundWebhookMapper webhookMapper;

    @Autowired
    private CrmOutboundWebhookDeliveryMapper deliveryMapper;

    @Autowired
    private TokenEncryptor tokenEncryptor;

    @Autowired
    private OutboundWebhookDispatcher dispatcher;

    @Autowired
    private OutboundWebhookDeliveryWorker deliveryWorker;

    @Value("${crm.outbound-webhook.allow-http:false}")
    private boolean allowHttp;

    @Override
    public List<OutboundWebhookVo> selectList(String tenantId, String eventType, String enabled)
    {
        tenantId = requireTenantId(tenantId);
        try
        {
            return webhookMapper.selectList(tenantId, eventType, enabled);
        }
        catch (Exception ex)
        {
            if (isMissingSchema(ex))
            {
                return Collections.emptyList();
            }
            throw ex;
        }
    }

    @Override
    public OutboundWebhookVo getById(String tenantId, String webhookId)
    {
        tenantId = requireTenantId(tenantId);
        if (StringUtils.isBlank(webhookId))
        {
            return null;
        }
        return webhookMapper.selectById(tenantId, webhookId.trim());
    }

    @Override
    public void add(String tenantId, OutboundWebhookSaveRequest request)
    {
        tenantId = requireTenantId(tenantId);
        ValidatedOutboundWebhook validated = OutboundWebhookValidator.validateSaveRequest(request, false, allowHttp);
        if (webhookMapper.countActive(tenantId) >= OutboundWebhookConstants.MAX_PER_TENANT)
        {
            throw new ServiceException("每个租户最多 20 条出站 Webhook", HttpStatus.BAD_REQUEST);
        }
        String username = safeUsername();
        CrmOutboundWebhook row = new CrmOutboundWebhook();
        row.setWebhookId(IdUtils.fastUUID());
        row.setTenantId(tenantId);
        row.setDelFlag("0");
        applyValidated(row, validated, true);
        row.setCreateBy(username);
        row.setCreateTime(DateUtils.getNowDate());
        row.setUpdateBy(username);
        row.setUpdateTime(DateUtils.getNowDate());
        webhookMapper.insertCrmOutboundWebhook(row);
    }

    @Override
    public void update(String tenantId, OutboundWebhookSaveRequest request)
    {
        tenantId = requireTenantId(tenantId);
        ValidatedOutboundWebhook validated = OutboundWebhookValidator.validateSaveRequest(request, true, allowHttp);
        CrmOutboundWebhook existing = webhookMapper.selectEntityById(tenantId, validated.webhookId);
        if (existing == null)
        {
            throw new ServiceException("Webhook 不存在或不属于当前租户", HttpStatus.BAD_REQUEST);
        }
        applyValidated(existing, validated, false);
        existing.setUpdateBy(safeUsername());
        existing.setUpdateTime(DateUtils.getNowDate());
        webhookMapper.updateCrmOutboundWebhook(existing);
    }

    @Override
    public void remove(String tenantId, String webhookId)
    {
        tenantId = requireTenantId(tenantId);
        if (StringUtils.isBlank(webhookId))
        {
            return;
        }
        webhookMapper.logicalDeleteById(webhookId.trim(), tenantId, safeUsername());
    }

    @Override
    public List<OutboundWebhookDeliveryVo> selectDeliveries(String tenantId, String webhookId, String status)
    {
        tenantId = requireTenantId(tenantId);
        if (StringUtils.isBlank(webhookId))
        {
            return Collections.emptyList();
        }
        return deliveryMapper.selectList(tenantId, webhookId.trim(), status);
    }

    @Override
    public OutboundWebhookTestResultVo test(String tenantId, String webhookId)
    {
        tenantId = requireTenantId(tenantId);
        CrmOutboundWebhook hook = webhookMapper.selectEntityById(tenantId, webhookId);
        if (hook == null)
        {
            throw new ServiceException("Webhook 不存在或不属于当前租户", HttpStatus.BAD_REQUEST);
        }
        JSONObject sample = OutboundWebhookSampleCatalog.samplePayload(hook.getEventType());
        CrmOutboundWebhookDelivery queued = dispatcher.enqueueTest(hook, sample);
        if (queued == null)
        {
            throw new ServiceException("测试投递入队失败", HttpStatus.ERROR);
        }
        CrmOutboundWebhookDelivery result = deliveryWorker.deliverById(queued.getDeliveryId(), allowHttp);
        OutboundWebhookTestResultVo vo = new OutboundWebhookTestResultVo();
        vo.setDeliveryId(queued.getDeliveryId());
        if (result != null)
        {
            vo.setStatus(result.getStatus());
            vo.setHttpStatus(result.getHttpStatus());
            vo.setErrorMessage(result.getErrorMessage());
        }
        else
        {
            vo.setStatus(OutboundWebhookConstants.STATUS_PENDING);
        }
        return vo;
    }

    private void applyValidated(CrmOutboundWebhook row, ValidatedOutboundWebhook validated, boolean creating)
    {
        row.setName(validated.name);
        row.setUrl(validated.url);
        row.setEventType(validated.eventType);
        row.setPayloadTemplate(validated.payloadTemplate);
        row.setRetryCount(validated.retryCount);
        row.setTimeoutMs(validated.timeoutMs);
        row.setEnabled(validated.enabled);
        if (StringUtils.isNotEmpty(validated.secret))
        {
            row.setSecretEnc(tokenEncryptor.encrypt(validated.secret));
        }
        else if (creating)
        {
            row.setSecretEnc(null);
        }
    }

    private String requireTenantId(String tenantId)
    {
        if (StringUtils.isBlank(tenantId))
        {
            throw new ServiceException("缺少租户头 tenant", HttpStatus.BAD_REQUEST);
        }
        return tenantId.trim();
    }

    private String safeUsername()
    {
        try
        {
            return SecurityUtils.getUsername();
        }
        catch (Exception ex)
        {
            return "system";
        }
    }

    private static boolean isMissingSchema(Throwable ex)
    {
        Throwable current = ex;
        while (current != null)
        {
            String message = current.getMessage();
            if (message != null && message.contains("crm_outbound_webhook") && message.contains("does not exist"))
            {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
