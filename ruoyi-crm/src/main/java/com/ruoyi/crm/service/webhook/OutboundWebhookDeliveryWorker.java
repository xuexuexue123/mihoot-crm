package com.ruoyi.crm.service.webhook;

import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmOutboundWebhook;
import com.ruoyi.crm.domain.CrmOutboundWebhookDelivery;
import com.ruoyi.crm.mapper.CrmOutboundWebhookDeliveryMapper;
import com.ruoyi.crm.mapper.CrmOutboundWebhookMapper;
import com.ruoyi.crm.service.support.TokenEncryptor;

@Component
public class OutboundWebhookDeliveryWorker
{
    private static final Logger log = LoggerFactory.getLogger(OutboundWebhookDeliveryWorker.class);

    private static final long[] BACKOFF_MS = { 30_000L, 120_000L, 600_000L };

    private final ConcurrentHashMap<String, Semaphore> inFlight = new ConcurrentHashMap<String, Semaphore>();

    @Autowired
    private CrmOutboundWebhookMapper webhookMapper;

    @Autowired
    private CrmOutboundWebhookDeliveryMapper deliveryMapper;

    @Autowired
    private TokenEncryptor tokenEncryptor;

    @Value("${crm.outbound-webhook.max-in-flight-per-tenant:5}")
    private int maxInFlightPerTenant;

    public CrmOutboundWebhookDelivery deliverById(String deliveryId, boolean allowHttp)
    {
        CrmOutboundWebhookDelivery delivery = deliveryMapper.selectById(deliveryId);
        if (delivery == null)
        {
            return null;
        }
        return deliver(delivery, allowHttp);
    }

    public CrmOutboundWebhookDelivery deliver(CrmOutboundWebhookDelivery delivery, boolean allowHttp)
    {
        if (delivery == null)
        {
            return null;
        }
        if (OutboundWebhookConstants.STATUS_SUCCESS.equals(delivery.getStatus())
                || OutboundWebhookConstants.STATUS_FAILED.equals(delivery.getStatus()))
        {
            return delivery;
        }
        Semaphore limiter = limiter(delivery.getTenantId());
        if (!limiter.tryAcquire())
        {
            return delivery;
        }
        try
        {
            return doDeliver(delivery, allowHttp);
        }
        finally
        {
            limiter.release();
        }
    }

    private CrmOutboundWebhookDelivery doDeliver(CrmOutboundWebhookDelivery delivery, boolean allowHttp)
    {
        CrmOutboundWebhook webhook = webhookMapper.selectEntityById(delivery.getTenantId(), delivery.getWebhookId());
        int attempt = (delivery.getAttempt() == null ? 0 : delivery.getAttempt()) + 1;
        Integer httpStatus = null;
        String error = null;
        boolean transportError = false;

        if (webhook == null || !"0".equals(webhook.getDelFlag()))
        {
            error = "webhook_deleted";
            return persist(delivery, attempt, OutboundWebhookConstants.STATUS_FAILED, null, error, null);
        }

        String ssrf = OutboundWebhookUrlValidator.validate(webhook.getUrl(), allowHttp);
        if (ssrf != null)
        {
            return persist(delivery, attempt, OutboundWebhookConstants.STATUS_FAILED, null,
                    OutboundWebhookConstants.ERROR_SSRF, null);
        }

        int timeout = webhook.getTimeoutMs() == null ? OutboundWebhookConstants.DEFAULT_TIMEOUT_MS
                : webhook.getTimeoutMs();
        String body = delivery.getPayloadJson() == null ? "{}" : delivery.getPayloadJson();
        long unixSeconds = InstantSeconds.now();
        try
        {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory()
            {
                @Override
                protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws java.io.IOException
                {
                    super.prepareConnection(connection, httpMethod);
                    connection.setInstanceFollowRedirects(false);
                }
            };
            factory.setConnectTimeout(timeout);
            factory.setReadTimeout(timeout);
            RestTemplate restTemplate = new RestTemplate(factory);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/json;charset=UTF-8"));
            headers.set("X-Webhook-Id", webhook.getWebhookId());
            headers.set("X-Webhook-Delivery", delivery.getDeliveryId());
            headers.set("X-Webhook-Timestamp", String.valueOf(unixSeconds));
            if (StringUtils.isNotEmpty(webhook.getSecretEnc()))
            {
                String secret = tokenEncryptor.decrypt(webhook.getSecretEnc());
                if (StringUtils.isNotEmpty(secret))
                {
                    headers.set("X-Webhook-Signature",
                            OutboundWebhookSigner.signHeaderValue(secret, unixSeconds, body));
                }
            }
            HttpEntity<byte[]> entity = new HttpEntity<byte[]>(body.getBytes(StandardCharsets.UTF_8), headers);
            ResponseEntity<String> response = restTemplate.exchange(URI.create(webhook.getUrl()), HttpMethod.POST,
                    entity, String.class);
            httpStatus = response.getStatusCodeValue();
        }
        catch (HttpStatusCodeException ex)
        {
            httpStatus = ex.getRawStatusCode();
            error = truncate(ex.getStatusText());
        }
        catch (ResourceAccessException ex)
        {
            transportError = true;
            error = truncate(ex.getMostSpecificCause() == null ? ex.getMessage() : ex.getMostSpecificCause().getMessage());
        }
        catch (Exception ex)
        {
            transportError = true;
            error = truncate(ex.getMessage());
        }

        int retryCount = webhook.getRetryCount() == null ? OutboundWebhookConstants.DEFAULT_RETRY
                : webhook.getRetryCount();
        boolean retryable = isRetryable(httpStatus, transportError);
        String status;
        Date nextRetry = null;
        if (httpStatus != null && httpStatus >= 200 && httpStatus < 300)
        {
            status = OutboundWebhookConstants.STATUS_SUCCESS;
            error = null;
        }
        else if (retryable && attempt < 1 + retryCount)
        {
            status = OutboundWebhookConstants.STATUS_RETRYING;
            nextRetry = nextRetryAt(attempt);
        }
        else
        {
            status = OutboundWebhookConstants.STATUS_FAILED;
            if (StringUtils.isEmpty(error) && httpStatus != null)
            {
                error = "http_" + httpStatus;
            }
        }
        log.info("outbound webhook delivery={} eventType={} status={} http={} attempt={}",
                delivery.getDeliveryId(), delivery.getEventType(), status, httpStatus, attempt);
        return persist(delivery, attempt, status, httpStatus, error, nextRetry);
    }

    public static boolean isRetryable(Integer httpStatus, boolean transportError)
    {
        if (transportError || httpStatus == null)
        {
            return true;
        }
        int s = httpStatus.intValue();
        return s == 408 || s == 429 || s >= 500;
    }

    public static Date nextRetryAt(int attempt)
    {
        int idx = Math.max(0, Math.min(attempt - 1, BACKOFF_MS.length - 1));
        return new Date(System.currentTimeMillis() + BACKOFF_MS[idx]);
    }

    private CrmOutboundWebhookDelivery persist(CrmOutboundWebhookDelivery delivery, int attempt, String status,
            Integer httpStatus, String error, Date nextRetry)
    {
        delivery.setAttempt(attempt);
        delivery.setStatus(status);
        delivery.setHttpStatus(httpStatus);
        delivery.setErrorMessage(truncate(error));
        delivery.setRequestId(delivery.getDeliveryId());
        delivery.setNextRetryAt(nextRetry);
        delivery.setUpdateTime(DateUtils.getNowDate());
        deliveryMapper.markAttemptResult(delivery);
        return delivery;
    }

    private Semaphore limiter(String tenantId)
    {
        int permits = Math.max(1, maxInFlightPerTenant);
        return inFlight.computeIfAbsent(tenantId, key -> new Semaphore(permits));
    }

    private static String truncate(String message)
    {
        if (message == null)
        {
            return null;
        }
        String cleaned = message.replaceAll("https?://[^\\s]+\\?[^\\s]+", "[url]");
        if (cleaned.length() <= 500)
        {
            return cleaned;
        }
        return cleaned.substring(0, 500);
    }

    static final class InstantSeconds
    {
        static long now()
        {
            return System.currentTimeMillis() / 1000L;
        }
    }
}
