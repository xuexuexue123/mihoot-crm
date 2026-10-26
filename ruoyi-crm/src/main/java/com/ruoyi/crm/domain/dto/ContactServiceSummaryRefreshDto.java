package com.ruoyi.crm.domain.dto;

/**
 * POST service-summary/refresh 请求
 */
public class ContactServiceSummaryRefreshDto
{
    /** manual | kf_panel | inbox */
    private String reason;
    private Boolean forceFull;

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Boolean getForceFull() { return forceFull; }
    public void setForceFull(Boolean forceFull) { this.forceFull = forceFull; }
}
