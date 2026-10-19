package com.ruoyi.crm.domain.dto;

/**
 * POST /crm/mobile/device body.
 */
public class MobileDeviceRegisterRequest
{
    /** ios | android */
    private String platform;
    private String pushToken;
    private String appVersion;

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public String getPushToken() { return pushToken; }
    public void setPushToken(String pushToken) { this.pushToken = pushToken; }

    public String getAppVersion() { return appVersion; }
    public void setAppVersion(String appVersion) { this.appVersion = appVersion; }
}
