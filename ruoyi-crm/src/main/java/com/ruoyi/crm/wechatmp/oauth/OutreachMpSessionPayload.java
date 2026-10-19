package com.ruoyi.crm.wechatmp.oauth;

/**
 * 微信公众号 OAuth session（outreach_mp_session cookie）
 */
public class OutreachMpSessionPayload
{
    private String openId;
    private String appId;
    private String tenantId;
    private String eventId;
    private long exp;

    public String getOpenId() { return openId; }
    public void setOpenId(String openId) { this.openId = openId; }
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public long getExp() { return exp; }
    public void setExp(long exp) { this.exp = exp; }

    public boolean isValidFor(String tenantId, String eventId)
    {
        if (openId == null || appId == null || exp <= 0)
        {
            return false;
        }
        if (java.time.Instant.now().getEpochSecond() > exp)
        {
            return false;
        }
        return this.tenantId.equals(tenantId) && this.eventId.equals(eventId);
    }
}
