package com.ruoyi.crm.domain.dto;

/**
 * claim-due 请求体
 */
public class ScheduledTaskClaimDueRequest
{
    /** ISO-8601 slot，如 2026-07-26T09:30+08:00 */
    private String slot;

    public String getSlot()
    {
        return slot;
    }

    public void setSlot(String slot)
    {
        this.slot = slot;
    }
}
