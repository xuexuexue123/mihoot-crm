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
import com.ruoyi.crm.domain.CrmMessengerConfig;
import com.ruoyi.crm.domain.dto.messenger.MessengerPageInfo;
import com.ruoyi.crm.domain.enums.InboxChannel;
import com.ruoyi.crm.mapper.CrmMessengerConfigMapper;
import com.ruoyi.crm.messenger.MessengerClient;
import com.ruoyi.crm.service.ICrmMessengerConfigService;
import com.ruoyi.crm.service.InboxChannelGateService;
import com.ruoyi.crm.service.support.TokenEncryptor;
import com.ruoyi.tenant.domain.SysMessengerPageTenant;
import com.ruoyi.tenant.service.ISysMessengerPageTenantService;

@Service
public class CrmMessengerConfigServiceImpl implements ICrmMessengerConfigService
{
    private static final Logger log = LoggerFactory.getLogger(CrmMessengerConfigServiceImpl.class);

    @Autowired
    private CrmMessengerConfigMapper configMapper;

    @Autowired
    private TokenEncryptor tokenEncryptor;

    @Autowired
    private MessengerClient messengerClient;

    @Autowired
    private InboxChannelGateService inboxChannelGateService;

    @Autowired
    private ISysMessengerPageTenantService messengerPageTenantService;

    @Value("${messenger.callback-path-hint:/crm/messenger/webhook}")
    private String callbackPathHint;

    @Override
    public Map<String, Object> getConfigMasked(String tenantId)
    {
        CrmMessengerConfig config = configMapper.selectByTenantId(tenantId);
        if (config == null)
        {
            return null;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("pageId", config.getPageId());
        result.put("pageName", config.getPageName());
        result.put("webhookConfigured", config.getWebhookConfigured() != null && config.getWebhookConfigured() == 1);
        result.put("accessTokenConfigured", StringUtils.hasText(config.getAccessTokenEnc()));
        result.put("verifyTokenConfigured", StringUtils.hasText(config.getWebhookVerifyTokenEnc()));
        result.put("appSecretConfigured", StringUtils.hasText(config.getAppSecretEnc()));
        result.put("callbackPathHint", callbackPathHint);
        result.put("lastError", config.getLastError());
        result.put("updatedAt", config.getUpdateTime());

        boolean connected = false;
        // Skip live Graph probe when Inbox Messenger channel is disabled (e.g. China mainland).
        // Integrations can still edit credentials; save still verifies the token.
        boolean liveProbe = inboxChannelGateService.isEnabled(tenantId, InboxChannel.MESSENGER);
        if (liveProbe
                && StringUtils.hasText(config.getAccessTokenEnc())
                && StringUtils.hasText(config.getPageId()))
        {
            try
            {
                String accessToken = tokenEncryptor.decrypt(config.getAccessTokenEnc());
                MessengerPageInfo pageInfo = messengerClient.getPageInfo(config.getPageId(), accessToken);
                if (pageInfo != null)
                {
                    connected = true;
                    if (pageInfo.getPageName() != null)
                    {
                        result.put("pageName", pageInfo.getPageName());
                    }
                }
            }
            catch (Exception e)
            {
                log.warn("Failed to verify Messenger token for tenant {}: {}", tenantId, e.getMessage());
                result.put("lastError", e.getMessage());
            }
        }
        else if (!liveProbe)
        {
            connected = StringUtils.hasText(config.getAccessTokenEnc())
                    && StringUtils.hasText(config.getPageId());
        }
        result.put("connected", connected);

        return result;
    }

    @Override
    public void saveOrUpdate(String tenantId, String pageId, String pageAccessToken,
                             String webhookVerifyToken, String appSecret, String pageName)
    {
        if (!StringUtils.hasText(pageId))
        {
            throw new ServiceException("pageId and pageAccessToken are required", 400);
        }

        CrmMessengerConfig existing = configMapper.selectByTenantId(tenantId);
        boolean isCreate = existing == null;
        if (isCreate && !StringUtils.hasText(pageAccessToken))
        {
            throw new ServiceException("pageId and pageAccessToken are required", 400);
        }

        SysMessengerPageTenant bound = messengerPageTenantService.selectByPageId(pageId.trim());
        if (bound != null && bound.getTenant() != null && !bound.getTenant().equals(tenantId))
        {
            throw new ServiceException("page_id already bound to another tenant", 400);
        }

        String resolvedPageName = pageName;
        String tokenForValidate = pageAccessToken;
        if (!StringUtils.hasText(tokenForValidate) && existing != null
                && StringUtils.hasText(existing.getAccessTokenEnc()))
        {
            tokenForValidate = tokenEncryptor.decrypt(existing.getAccessTokenEnc());
        }
        if (StringUtils.hasText(tokenForValidate))
        {
            try
            {
                MessengerPageInfo pageInfo = messengerClient.getPageInfo(pageId.trim(), tokenForValidate);
                if (pageInfo == null)
                {
                    throw new ServiceException("Invalid page access token: unable to fetch page info", 400);
                }
                if (StringUtils.hasText(pageInfo.getPageName()))
                {
                    resolvedPageName = pageInfo.getPageName();
                }
            }
            catch (ServiceException e)
            {
                throw e;
            }
            catch (Exception e)
            {
                log.error("Invalid Messenger page access token for tenant {}: {}", tenantId, e.getMessage());
                throw new ServiceException("Invalid page access token: " + e.getMessage(), 400);
            }
        }

        String encryptedAccessToken = null;
        if (StringUtils.hasText(pageAccessToken))
        {
            encryptedAccessToken = tokenEncryptor.encrypt(pageAccessToken);
        }
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

        Date now = new Date();
        CrmMessengerConfig row = new CrmMessengerConfig();
        row.setTenantId(tenantId);
        row.setPageId(pageId.trim());
        row.setPageName(resolvedPageName);
        row.setAccessTokenEnc(encryptedAccessToken);
        row.setWebhookVerifyTokenEnc(encryptedVerifyToken);
        row.setAppSecretEnc(encryptedAppSecret);
        row.setLastError(null);

        if (isCreate)
        {
            row.setWebhookConfigured(0);
            row.setCreateTime(now);
            row.setUpdateTime(now);
        }
        else
        {
            row.setId(existing.getId());
            row.setWebhookConfigured(existing.getWebhookConfigured() != null
                    ? existing.getWebhookConfigured() : 0);
            row.setUpdateTime(now);
            if (!StringUtils.hasText(pageAccessToken))
            {
                row.setAccessTokenEnc(null);
            }
            if (!StringUtils.hasText(webhookVerifyToken))
            {
                row.setWebhookVerifyTokenEnc(null);
            }
            if (!StringUtils.hasText(appSecret))
            {
                row.setAppSecretEnc(null);
            }
        }

        try
        {
            configMapper.insertOrUpdate(row);
        }
        catch (Exception e)
        {
            log.error("Failed to save Messenger config for tenant {}: {}", tenantId, e.getMessage());
            throw new ServiceException("Failed to save configuration", 500);
        }
    }
}
