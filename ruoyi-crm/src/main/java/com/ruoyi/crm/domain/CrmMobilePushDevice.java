package com.ruoyi.crm.domain;

import java.util.Date;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * Inbox 移动推送设备注册（FCM token）。
 */
public class CrmMobilePushDevice extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String tenantId;
    private Long userId;
    /** ios | android */
    private String platform;
    private String pushToken;
    private String appVersion;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public String getPushToken() { return pushToken; }
    public void setPushToken(String pushToken) { this.pushToken = pushToken; }

    public String getAppVersion() { return appVersion; }
    public void setAppVersion(String appVersion) { this.appVersion = appVersion; }

    @Override
    public Date getCreateTime() { return super.getCreateTime(); }

    @Override
    public void setCreateTime(Date createTime) { super.setCreateTime(createTime); }

    @Override
    public Date getUpdateTime() { return super.getUpdateTime(); }

    @Override
    public void setUpdateTime(Date updateTime) { super.setUpdateTime(updateTime); }
}
