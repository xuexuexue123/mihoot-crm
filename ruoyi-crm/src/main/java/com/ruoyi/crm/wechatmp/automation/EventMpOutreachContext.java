package com.ruoyi.crm.wechatmp.automation;

/**
 * 公众号关注归因后触发自动化的上下文
 */
public class EventMpOutreachContext
{
    public static final String TRIGGER_MP_SUBSCRIBE_ATTRIBUTED = "mp_subscribe_attributed";

    private String tenantId;
    private String appId;
    private String openId;
    private String crmEventId;

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getOpenId() { return openId; }
    public void setOpenId(String openId) { this.openId = openId; }
    public String getCrmEventId() { return crmEventId; }
    public void setCrmEventId(String crmEventId) { this.crmEventId = crmEventId; }
}
