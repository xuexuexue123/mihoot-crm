package com.ruoyi.web.controller.crm;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.crm.domain.CrmMessengerConfig;
import com.ruoyi.crm.domain.CrmMessengerWebhookEvent;
import com.ruoyi.crm.mapper.CrmMessengerConfigMapper;
import com.ruoyi.crm.mapper.CrmMessengerWebhookEventMapper;
import com.ruoyi.crm.messenger.MessengerWebhookSignatureVerifier;
import com.ruoyi.crm.service.ICrmMessengerWebhookService;
import com.ruoyi.crm.service.support.TokenEncryptor;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.SysMessengerPageTenant;
import com.ruoyi.tenant.service.IMasterTenantService;
import com.ruoyi.tenant.service.ISysMessengerPageTenantService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Messenger Webhook 回调（无登录、无 tenant 头）。Meta 直接回调此端点。
 *
 * <p>主路径 {@code /crm/messenger/webhook}；运维别名 {@code /messenger/webhook}。
 */
@Slf4j
@RestController
@RequestMapping({ "/crm/messenger", "/messenger" })
public class CrmMessengerWebhookController extends BaseController
{
    @Autowired
    private ISysMessengerPageTenantService messengerPageTenantService;

    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Autowired
    private CrmMessengerConfigMapper configMapper;

    @Autowired
    private CrmMessengerWebhookEventMapper eventMapper;

    @Autowired
    private ICrmMessengerWebhookService webhookService;

    @Autowired
    private TokenEncryptor tokenEncryptor;

    @Autowired
    private MessengerWebhookSignatureVerifier signatureVerifier;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @GetMapping(value = "/webhook", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> verifyWebhook(
            @RequestParam(value = "hub.mode", required = false) String mode,
            @RequestParam(value = "hub.verify_token", required = false) String verifyToken,
            @RequestParam(value = "hub.challenge", required = false) String challenge)
    {
        if (!StringUtils.hasText(mode) || !StringUtils.hasText(verifyToken) || !StringUtils.hasText(challenge))
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing hub parameters");
        }
        if (!"subscribe".equals(mode))
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid hub.mode");
        }

        java.util.List<SysMessengerPageTenant> allMappings = messengerPageTenantService.selectAll();
        for (SysMessengerPageTenant mapping : allMappings)
        {
            try
            {
                bindTenantDataSource(mapping.getTenant());
                String tenantId = mapping.getTenant();

                CrmMessengerConfig config = configMapper.selectByTenantId(tenantId);
                if (config != null && StringUtils.hasText(config.getWebhookVerifyTokenEnc()))
                {
                    String storedVerifyToken = tokenEncryptor.decrypt(config.getWebhookVerifyTokenEnc());
                    if (verifyToken.equals(storedVerifyToken))
                    {
                        log.info("Messenger webhook verified for tenant={} page_id={}",
                                tenantId, mapping.getPageId());
                        return ResponseEntity.ok()
                                .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                                .body(challenge);
                    }
                }
            }
            catch (Exception e)
            {
                log.warn("Messenger webhook verify: failed to check tenant={}: {}",
                        mapping.getTenant(), e.getMessage());
            }
            finally
            {
                DynamicDataSourceContextHolder.clearDataSourceKey();
            }
        }

        log.warn("Messenger webhook verify: no matching verify_token found, incoming token hash={}",
                Integer.toHexString(verifyToken.hashCode()));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("verify_token mismatch");
    }

    @PostMapping(value = "/webhook", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> receiveEvent(
            @RequestBody String body,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature)
    {
        if (!StringUtils.hasText(body))
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Empty body");
        }

        try
        {
            @SuppressWarnings("unchecked")
            Map<String, Object> payload = JSON.parseObject(body, Map.class);

            String pageId = extractPageId(payload);
            if (pageId == null)
            {
                log.warn("Messenger webhook: cannot extract page_id from body");
                return ResponseEntity.ok("ignored");
            }

            String tenantId = resolveTenantForPage(pageId);
            if (tenantId == null)
            {
                log.warn("Messenger webhook: page_id={} not mapped to any tenant", pageId);
                return ResponseEntity.ok("ignored");
            }

            try
            {
                bindTenantDataSource(tenantId);

                CrmMessengerConfig config = configMapper.selectByTenantId(tenantId);
                if (config == null || !StringUtils.hasText(config.getAppSecretEnc()))
                {
                    log.warn("Messenger webhook: app_secret not configured for tenant={}", tenantId);
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("signature verification failed");
                }

                String appSecret = tokenEncryptor.decrypt(config.getAppSecretEnc());
                if (!signatureVerifier.verify(appSecret, body, signature))
                {
                    log.warn("Messenger webhook: signature mismatch for tenant={} page_id={}",
                            tenantId, pageId);
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("signature verification failed");
                }

                webhookService.processWebhook(tenantId, payload);
            }
            finally
            {
                DynamicDataSourceContextHolder.clearDataSourceKey();
            }

            return ResponseEntity.ok("OK");
        }
        catch (Exception e)
        {
            log.error("Messenger webhook processing error", e);
            return ResponseEntity.ok("OK");
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:messenger:config')")
    @GetMapping("/webhook-events")
    public TableDataInfo listEvents(
            @RequestHeader(value = "tenant", required = false) String tenant,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String psid,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String pageId,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return getDataTable(java.util.Collections.emptyList());
        }
        startPage();
        List<CrmMessengerWebhookEvent> list = eventMapper.selectPage(
                tenant, psid, status, eventType, pageId, startTime, endTime);
        return getDataTable(list);
    }

    @SuppressWarnings("unchecked")
    private String extractPageId(Map<String, Object> payload)
    {
        List<Map<String, Object>> entries = (List<Map<String, Object>>) payload.get("entry");
        if (entries == null || entries.isEmpty())
        {
            return null;
        }
        Object id = entries.get(0).get("id");
        return id != null ? String.valueOf(id) : null;
    }

    private String resolveTenantForPage(String pageId)
    {
        SysMessengerPageTenant mapping = messengerPageTenantService.selectByPageId(pageId);
        if (mapping == null)
        {
            return null;
        }
        return mapping.getTenant();
    }

    private void bindTenantDataSource(String tenantId)
    {
        if (!dynamicRoutingDataSource.existDataSource(tenantId))
        {
            MasterTenant masterTenant = masterTenantService.selectTenantByTenantId(tenantId);
            if (masterTenant == null)
            {
                throw new IllegalStateException("Messenger webhook: tenant not found: " + tenantId);
            }
            Map<String, Object> map = new HashMap<>();
            map.put("driverClassName", driverClassName);
            map.put("url", masterTenant.getUrl());
            map.put("username", masterTenant.getUsername());
            map.put("password", masterTenant.getPassword());
            dynamicRoutingDataSource.addDataSource(tenantId, map);
        }
        DynamicDataSourceContextHolder.setDataSourceKey(tenantId);
    }
}
