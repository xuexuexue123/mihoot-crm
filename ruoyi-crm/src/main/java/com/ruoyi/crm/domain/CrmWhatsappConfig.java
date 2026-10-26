package com.ruoyi.crm.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 租户级 WhatsApp Business Platform 配置（每租户单行）
 */
public class CrmWhatsappConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String tenantId;
    private String phoneNumberId;
    private String wabaId;
    private String businessName;
    /** AES-256-GCM 加密的 access_token */
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

    public String getPhoneNumberId() { return phoneNumberId; }
    public void setPhoneNumberId(String phoneNumberId) { this.phoneNumberId = phoneNumberId; }

    public String getWabaId() { return wabaId; }
    public void setWabaId(String wabaId) { this.wabaId = wabaId; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

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
