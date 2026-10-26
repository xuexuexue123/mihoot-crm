package com.ruoyi.crm.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmWhatsappConfig;
import com.ruoyi.crm.domain.dto.whatsapp.WhatsAppPhoneInfo;
import com.ruoyi.crm.domain.enums.InboxChannel;
import com.ruoyi.crm.mapper.CrmWhatsappConfigMapper;
import com.ruoyi.crm.service.ICrmWhatsappConfigService;
import com.ruoyi.crm.service.InboxChannelGateService;
import com.ruoyi.crm.service.support.TokenEncryptor;
import com.ruoyi.crm.whatsapp.WhatsAppClient;

@Service
public class CrmWhatsappConfigServiceImpl implements ICrmWhatsappConfigService
{
    private static final Logger log = LoggerFactory.getLogger(CrmWhatsappConfigServiceImpl.class);

    @Autowired
    private CrmWhatsappConfigMapper configMapper;

    @Autowired
    private TokenEncryptor tokenEncryptor;

    @Autowired
    private WhatsAppClient whatsappClient;

    @Autowired
    private InboxChannelGateService inboxChannelGateService;

    @Value("${whatsapp.callback-path-hint:/crm/whatsapp/webhook}")
    private String callbackPathHint;

    @Override
    public Map<String, Object> getConfigMasked(String tenantId)
    {
        CrmWhatsappConfig config = configMapper.selectByTenantId(tenantId);
        if (config == null)
        {
            return null;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("phoneNumberId", config.getPhoneNumberId());
        result.put("wabaId", config.getWabaId());
        result.put("businessName", config.getBusinessName());
        result.put("webhookConfigured", config.getWebhookConfigured() != null && config.getWebhookConfigured() == 1);
        result.put("accessTokenConfigured", StringUtils.hasText(config.getAccessTokenEnc()));
        result.put("verifyTokenConfigured", StringUtils.hasText(config.getWebhookVerifyTokenEnc()));
        result.put("appSecretConfigured", StringUtils.hasText(config.getAppSecretEnc()));
        result.put("callbackPathHint", callbackPathHint);
        result.put("lastError", config.getLastError());
        result.put("updatedAt", config.getUpdateTime());

        boolean connected = false;
        // Skip live Graph probe when Inbox WhatsApp channel is disabled (e.g. China mainland).
        // Integrations can still edit credentials; save still verifies the token.
        boolean liveProbe = inboxChannelGateService.isEnabled(tenantId, InboxChannel.WHATSAPP);
        if (liveProbe
                && StringUtils.hasText(config.getAccessTokenEnc())
                && StringUtils.hasText(config.getPhoneNumberId()))
        {
            try
            {
                String accessToken = tokenEncryptor.decrypt(config.getAccessTokenEnc());
                WhatsAppPhoneInfo phoneInfo = whatsappClient.getPhoneNumberInfo(
                        config.getPhoneNumberId(), accessToken);
                if (phoneInfo != null)
                {
                    connected = true;
                    if (phoneInfo.getDisplayPhoneNumber() != null)
                    {
                        result.put("verifiedPhoneNumbers", java.util.Collections.singletonList(
                                phoneInfo.getDisplayPhoneNumber()));
                    }
                    if (phoneInfo.getVerifiedName() != null)
                    {
                        result.put("businessName", phoneInfo.getVerifiedName());
                    }
                }
            }
            catch (Exception e)
            {
                log.warn("Failed to verify WhatsApp token for tenant {}: {}", tenantId, e.getMessage());
                result.put("lastError", e.getMessage());
            }
        }
        else if (!liveProbe)
        {
            connected = StringUtils.hasText(config.getAccessTokenEnc())
                    && StringUtils.hasText(config.getPhoneNumberId());
        }
        result.put("connected", connected);

        return result;
    }

    @Override
    public void saveOrUpdate(String tenantId, String phoneNumberId, String wabaId,
                             String accessToken, String webhookVerifyToken, String appSecret)
    {
        if (!StringUtils.hasText(phoneNumberId))
        {
            throw new ServiceException("phoneNumberId is required");
        }
        if (!StringUtils.hasText(accessToken))
        {
            throw new ServiceException("accessToken is required");
        }

        String businessName = null;
        try
        {
            WhatsAppPhoneInfo phoneInfo = whatsappClient.getPhoneNumberInfo(phoneNumberId, accessToken);
            if (phoneInfo != null)
            {
                businessName = phoneInfo.getVerifiedName();
            }
        }
        catch (Exception e)
        {
            log.error("Invalid WhatsApp access token for tenant {}: {}", tenantId, e.getMessage());
            throw new ServiceException("Invalid access token: " + e.getMessage(), 400);
        }

        String encryptedAccessToken = tokenEncryptor.encrypt(accessToken);
        String encryptedVerifyToken = null;
        if (StringUtils.hasText(webhookVerifyToken))
        {
            encryptedVerifyToken = tokenEncryptor.encrypt(webhookVerifyToken);
        }
        String encryptedAppSecret = null;
        if (StringUtils.hasText(appSecret))
        {
            encryptedAppSecret = tokenEncryptor.encrypt(appSecret);
        }

        CrmWhatsappConfig existing = configMapper.selectByTenantId(tenantId);
        Date now = new Date();

        CrmWhatsappConfig row = new CrmWhatsappConfig();
        row.setTenantId(tenantId);
        row.setPhoneNumberId(phoneNumberId);
        row.setWabaId(wabaId);
        row.setBusinessName(businessName);
        row.setAccessTokenEnc(encryptedAccessToken);
        row.setWebhookVerifyTokenEnc(encryptedVerifyToken);
        row.setAppSecretEnc(encryptedAppSecret);

        if (existing == null)
        {
            row.setWebhookConfigured(0);
            row.setCreateTime(now);
            row.setUpdateTime(now);
        }
        else
        {
            row.setId(existing.getId());
            row.setWebhookConfigured(existing.getWebhookConfigured() != null ? existing.getWebhookConfigured() : 0);
            row.setUpdateTime(now);
            // 空串不覆盖：mapper COALESCE；此处传 null 即可
            if (!StringUtils.hasText(webhookVerifyToken))
            {
                row.setWebhookVerifyTokenEnc(null);
            }
            if (!StringUtils.hasText(appSecret))
            {
                row.setAppSecretEnc(null);
            }
        }

        configMapper.insertOrUpdate(row);
    }
}
