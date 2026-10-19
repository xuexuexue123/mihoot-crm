package com.ruoyi.crm.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 租户级 Facebook Messenger（Page）配置（每租户单行）
 */
public class CrmMessengerConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String tenantId;
    private String pageId;
    private String pageName;
    /** AES-256-GCM 加密的 Page Access Token */
    private String accessTokenEnc;
    /** AES-256-GCM 加密的 webhook verify_token */
    private String webhookVerifyTokenEnc;
    /** AES-256-GCM 加密的 Meta App Secret（X-Hub-Signature-256） */
    private String appSecretEnc;
    private Integer webhookConfigured;
    private String lastError;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getPageId() { return pageId; }
    public void setPageId(String pageId) { this.pageId = pageId; }

    public String getPageName() { return pageName; }
    public void setPageName(String pageName) { this.pageName = pageName; }

    public String getAccessTokenEnc() { return accessTokenEnc; }
    public void setAccessTokenEnc(String accessTokenEnc) { this.accessTokenEnc = accessTokenEnc; }

    public String getWebhookVerifyTokenEnc() { return webhookVerifyTokenEnc; }
    public void setWebhookVerifyTokenEnc(String webhookVerifyTokenEnc) { this.webhookVerifyTokenEnc = webhookVerifyTokenEnc; }

    public String getAppSecretEnc() { return appSecretEnc; }
    public void setAppSecretEnc(String appSecretEnc) { this.appSecretEnc = appSecretEnc; }

    public Integer getWebhookConfigured() { return webhookConfigured; }
    public void setWebhookConfigured(Integer webhookConfigured) { this.webhookConfigured = webhookConfigured; }

    public String getLastError() { return lastError; }
    public void setLastError(String lastError) { this.lastError = lastError; }
}
