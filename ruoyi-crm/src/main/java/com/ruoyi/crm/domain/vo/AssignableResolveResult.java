package com.ruoyi.crm.domain.vo;

import com.ruoyi.crm.domain.dto.LeadAssignPatch;

/**
 * assignable/resolve 响应
 */
public class AssignableResolveResult
{
    private boolean resolved;
    private AssignableUserVo user;
    private String reason;
    private LeadAssignPatch leadAssignPatch;

    public boolean isResolved()
    {
        return resolved;
    }

    public void setResolved(boolean resolved)
    {
        this.resolved = resolved;
    }

    public AssignableUserVo getUser()
    {
        return user;
    }

    public void setUser(AssignableUserVo user)
    {
        this.user = user;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public LeadAssignPatch getLeadAssignPatch()
    {
        return leadAssignPatch;
    }

    public void setLeadAssignPatch(LeadAssignPatch leadAssignPatch)
    {
        this.leadAssignPatch = leadAssignPatch;
    }
}
