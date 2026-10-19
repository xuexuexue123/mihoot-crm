package com.ruoyi.crm.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 租户级 Website Chat 配置（每租户单行）
 */
public class CrmWebchatConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String tenantId;
    private String websiteToken;
    private Integer enabled;
    private String displayName;
    private String logoUrl;
    private String welcomeMessage;
    private String themeColor;
    private String position;
    /** JSON array of hostnames */
    private String allowedDomains;
    private Integer aiEnabled;
    private String offlineMessage;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getWebsiteToken() { return websiteToken; }
    public void setWebsiteToken(String websiteToken) { this.websiteToken = websiteToken; }

    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getWelcomeMessage() { return welcomeMessage; }
    public void setWelcomeMessage(String welcomeMessage) { this.welcomeMessage = welcomeMessage; }

    public String getThemeColor() { return themeColor; }
    public void setThemeColor(String themeColor) { this.themeColor = themeColor; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getAllowedDomains() { return allowedDomains; }
    public void setAllowedDomains(String allowedDomains) { this.allowedDomains = allowedDomains; }

    public Integer getAiEnabled() { return aiEnabled; }
    public void setAiEnabled(Integer aiEnabled) { this.aiEnabled = aiEnabled; }

    public String getOfflineMessage() { return offlineMessage; }
    public void setOfflineMessage(String offlineMessage) { this.offlineMessage = offlineMessage; }
}
