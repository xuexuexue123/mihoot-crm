package com.ruoyi.crm.domain.vo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 公开查询 Reminder Link 响应（不含运营敏感字段）
 */
public class ReminderLinkPublicVo
{
    private String type;
    private String status;
    private Date expiresAt;
    private Map<String, Object> payload;
    private List<String> actions;
    private ReminderLinkPublicBrandingVo branding;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Date expiresAt) { this.expiresAt = expiresAt; }

    public Map<String, Object> getPayload() { return payload; }
    public void setPayload(Map<String, Object> payload) { this.payload = payload; }

    public List<String> getActions() { return actions; }
    public void setActions(List<String> actions) { this.actions = actions; }

    public ReminderLinkPublicBrandingVo getBranding() { return branding; }
    public void setBranding(ReminderLinkPublicBrandingVo branding) { this.branding = branding; }
}
