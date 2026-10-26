package com.ruoyi.crm.domain.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * claim-due 响应 data
 */
public class ScheduledTaskClaimDueResult
{
    private String slot;
    private List<ClaimedScheduledTask> tasks = new ArrayList<>();

    public String getSlot()
    {
        return slot;
    }

    public void setSlot(String slot)
    {
        this.slot = slot;
    }

    public List<ClaimedScheduledTask> getTasks()
    {
        return tasks;
    }

    public void setTasks(List<ClaimedScheduledTask> tasks)
    {
        this.tasks = tasks;
    }
}
