package com.ruoyi.crm.domain.dto;

/**
 * DELETE /crm/mobile/device body (optional).
 */
public class MobileDeviceUnregisterRequest
{
    private String pushToken;

    public String getPushToken() { return pushToken; }
    public void setPushToken(String pushToken) { this.pushToken = pushToken; }
}
