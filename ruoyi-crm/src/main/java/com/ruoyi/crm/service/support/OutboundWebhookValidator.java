package com.ruoyi.crm.service.support;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.dto.OutboundWebhookSaveRequest;
import com.ruoyi.crm.service.webhook.OutboundWebhookConstants;
import com.ruoyi.crm.service.webhook.OutboundWebhookUrlValidator;

public final class OutboundWebhookValidator
{
    private OutboundWebhookValidator()
    {
    }

    public static ValidatedOutboundWebhook validateSaveRequest(OutboundWebhookSaveRequest request,
            boolean requireId, boolean allowHttp)
    {
        if (request == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        if (requireId && StringUtils.isBlank(request.getWebhookId()))
        {
            throw new ServiceException("webhookId 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getName()))
        {
            throw new ServiceException("name 不能为空", HttpStatus.BAD_REQUEST);
        }
        String name = request.getName().trim();
        if (name.length() > 128)
        {
            throw new ServiceException("name 不能超过 128 字符", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getUrl()))
        {
            throw new ServiceException("url 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getEventType())
                || !OutboundWebhookConstants.EVENT_TYPES.contains(request.getEventType().trim()))
        {
            throw new ServiceException("eventType 不合法", HttpStatus.BAD_REQUEST);
        }
        OutboundWebhookUrlValidator.validateOrThrow(request.getUrl(), allowHttp);

        int retry = request.getRetryCount() == null ? OutboundWebhookConstants.DEFAULT_RETRY : request.getRetryCount();
        if (retry < OutboundWebhookConstants.MIN_RETRY || retry > OutboundWebhookConstants.MAX_RETRY)
        {
            throw new ServiceException("retryCount 必须为 0–8", HttpStatus.BAD_REQUEST);
        }
        int timeout = request.getTimeoutMs() == null ? OutboundWebhookConstants.DEFAULT_TIMEOUT_MS : request.getTimeoutMs();
        if (timeout < OutboundWebhookConstants.MIN_TIMEOUT_MS || timeout > OutboundWebhookConstants.MAX_TIMEOUT_MS)
        {
            throw new ServiceException("timeoutMs 必须为 1000–30000", HttpStatus.BAD_REQUEST);
        }

        ValidatedOutboundWebhook validated = new ValidatedOutboundWebhook();
        validated.webhookId = trimToNull(request.getWebhookId());
        validated.name = name;
        validated.url = request.getUrl().trim();
        validated.secret = request.getSecret();
        validated.eventType = request.getEventType().trim();
        validated.payloadTemplate = optionalJsonObject(request.getPayloadTemplate());
        validated.retryCount = retry;
        validated.timeoutMs = timeout;
        validated.enabled = normalizeFlag(request.getEnabled());
        return validated;
    }

    private static String optionalJsonObject(String raw)
    {
        if (StringUtils.isBlank(raw))
        {
            return null;
        }
        try
        {
            Object parsed = JSON.parse(raw.trim());
            if (!(parsed instanceof JSONObject))
            {
                throw new ServiceException("payloadTemplate 必须是 JSON object", HttpStatus.BAD_REQUEST);
            }
            return ((JSONObject) parsed).toJSONString();
        }
        catch (ServiceException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new ServiceException("payloadTemplate 必须是 JSON object", HttpStatus.BAD_REQUEST);
        }
    }

    private static String normalizeFlag(String value)
    {
        if (StringUtils.isBlank(value))
        {
            return OutboundWebhookConstants.ENABLED;
        }
        String trimmed = value.trim();
        if (OutboundWebhookConstants.ENABLED.equals(trimmed) || OutboundWebhookConstants.DISABLED.equals(trimmed))
        {
            return trimmed;
        }
        throw new ServiceException("enabled 必须为 0 或 1", HttpStatus.BAD_REQUEST);
    }

    private static String trimToNull(String value)
    {
        if (StringUtils.isBlank(value))
        {
            return null;
        }
        return value.trim();
    }

    public static final class ValidatedOutboundWebhook
    {
        public String webhookId;
        public String name;
        public String url;
        public String secret;
        public String eventType;
        public String payloadTemplate;
        public int retryCount;
        public int timeoutMs;
        public String enabled;
    }
}
