package com.ruoyi.crm.domain.dto;

/**
 * 线索释放请求
 */
public class LeadReleaseRequest
{
    private String reason = "manual";

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }
}
