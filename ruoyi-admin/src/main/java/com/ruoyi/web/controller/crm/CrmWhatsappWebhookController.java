package com.ruoyi.web.controller.crm;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.crm.domain.CrmWhatsappConfig;
import com.ruoyi.crm.domain.CrmWhatsappWebhookEvent;
import com.ruoyi.crm.mapper.CrmWhatsappConfigMapper;
import com.ruoyi.crm.mapper.CrmWhatsappWebhookEventMapper;
import com.ruoyi.crm.service.ICrmWhatsappWebhookService;
import com.ruoyi.crm.service.support.TokenEncryptor;
import com.ruoyi.crm.whatsapp.WhatsappWebhookSignatureVerifier;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.SysWhatsappPhoneTenant;
import com.ruoyi.tenant.service.IMasterTenantService;
import com.ruoyi.tenant.service.ISysWhatsappPhoneTenantService;

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
 * WhatsApp Webhook 回调（无登录、无 tenant 头）。Meta 直接回调此端点。
 *
 * <p>主路径 {@code /crm/whatsapp/webhook}；运维别名 {@code /whatsapp/webhook}。
 * <p>GET  — Meta hub.challenge URL 验证
 * <p>POST — Meta 事件推送（raw JSON + X-Hub-Signature-256）
 * <p>GET  /webhook-events — 前端分页查询事件日志（需 JWT + tenant Header）
 */
@Slf4j
@RestController
@RequestMapping({ "/crm/whatsapp", "/whatsapp" })
public class CrmWhatsappWebhookController extends BaseController
{
    @Autowired
    private ISysWhatsappPhoneTenantService whatsappPhoneTenantService;

    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Autowired
    private CrmWhatsappConfigMapper configMapper;

    @Autowired
    private CrmWhatsappWebhookEventMapper eventMapper;

    @Autowired
    private ICrmWhatsappWebhookService webhookService;

    @Autowired
    private TokenEncryptor tokenEncryptor;

    @Autowired
    private WhatsappWebhookSignatureVerifier signatureVerifier;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    /**
     * Meta Webhook URL 验证（GET）。
     */
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

        java.util.List<SysWhatsappPhoneTenant> allMappings = whatsappPhoneTenantService.selectAll();
        for (SysWhatsappPhoneTenant mapping : allMappings)
        {
            try
            {
                bindTenantDataSource(mapping.getTenant());
                String tenantId = mapping.getTenant();

                CrmWhatsappConfig config = configMapper.selectByTenantId(tenantId);
                if (config != null && StringUtils.hasText(config.getWebhookVerifyTokenEnc()))
                {
                    String storedVerifyToken = tokenEncryptor.decrypt(config.getWebhookVerifyTokenEnc());
                    if (verifyToken.equals(storedVerifyToken))
                    {
                        log.info("WhatsApp webhook verified for tenant={} phone_number_id={}",
                                tenantId, mapping.getPhoneNumberId());
                        return ResponseEntity.ok()
                                .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                                .body(challenge);
                    }
                }
            }
            catch (Exception e)
            {
                log.warn("WhatsApp webhook verify: failed to check tenant={}: {}",
                        mapping.getTenant(), e.getMessage());
            }
            finally
            {
                DynamicDataSourceContextHolder.clearDataSourceKey();
            }
        }

        log.warn("WhatsApp webhook verify: no matching verify_token found, incoming token hash={}",
                Integer.toHexString(verifyToken.hashCode()));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("verify_token mismatch");
    }

    /**
     * Meta Webhook 事件推送（POST）。验签 → 定租户 → 落库处理。
     */
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

            String phoneNumberId = extractPhoneNumberId(payload);
            if (phoneNumberId == null)
            {
                log.warn("WhatsApp webhook: cannot extract phone_number_id from body");
                return ResponseEntity.ok("ignored");
            }

            String tenantId = resolveTenantForPhone(phoneNumberId);
            if (tenantId == null)
            {
                log.warn("WhatsApp webhook: phone_number_id={} not mapped to any tenant", phoneNumberId);
                return ResponseEntity.ok("ignored");
            }

            try
            {
                bindTenantDataSource(tenantId);

                CrmWhatsappConfig config = configMapper.selectByTenantId(tenantId);
                if (config == null || !StringUtils.hasText(config.getAppSecretEnc()))
                {
                    log.warn("WhatsApp webhook: app_secret not configured for tenant={}", tenantId);
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("signature verification failed");
                }

                String appSecret = tokenEncryptor.decrypt(config.getAppSecretEnc());
                if (!signatureVerifier.verify(appSecret, body, signature))
                {
                    log.warn("WhatsApp webhook: signature mismatch for tenant={} phone_number_id={}",
                            tenantId, phoneNumberId);
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
            log.error("WhatsApp webhook processing error", e);
            // 业务处理失败仍返回 200，避免 Meta 重试风暴；验签失败已在上方返回 403
            return ResponseEntity.ok("OK");
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:whatsapp:events')")
    @GetMapping("/webhook-events")
    public TableDataInfo listEvents(
            @RequestHeader(value = "tenant", required = false) String tenant,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String waId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String phoneNumberId,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return getDataTable(java.util.Collections.emptyList());
        }
        startPage();
        List<CrmWhatsappWebhookEvent> list = eventMapper.selectPage(
                tenant, waId, status, eventType, phoneNumberId, startTime, endTime);
        return getDataTable(list);
    }

    @SuppressWarnings("unchecked")
    private String extractPhoneNumberId(Map<String, Object> payload)
    {
        List<Map<String, Object>> entries = (List<Map<String, Object>>) payload.get("entry");
        if (entries == null || entries.isEmpty())
        {
            return null;
        }
        for (Map<String, Object> entry : entries)
        {
            List<Map<String, Object>> changes = (List<Map<String, Object>>) entry.get("changes");
            if (changes == null)
            {
                continue;
            }
            for (Map<String, Object> change : changes)
            {
                Map<String, Object> value = (Map<String, Object>) change.get("value");
                if (value == null)
                {
                    continue;
                }
                Map<String, Object> metadata = (Map<String, Object>) value.get("metadata");
                if (metadata == null)
                {
                    continue;
                }
                Object id = metadata.get("phone_number_id");
                if (id == null)
                {
                    id = metadata.get("phoneNumberId");
                }
                if (id != null)
                {
                    return String.valueOf(id);
                }
            }
        }
        return null;
    }

    private String resolveTenantForPhone(String phoneNumberId)
    {
        SysWhatsappPhoneTenant mapping = whatsappPhoneTenantService.selectByPhoneNumberId(phoneNumberId);
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
                throw new IllegalStateException("WhatsApp webhook: tenant not found: " + tenantId);
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
