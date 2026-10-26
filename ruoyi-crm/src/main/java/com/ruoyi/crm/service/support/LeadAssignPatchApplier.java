package com.ruoyi.crm.service.support;

import org.springframework.util.StringUtils;

import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.dto.LeadAssignPatch;

/**
 * 将 {@link LeadAssignPatch} 合并到 {@link CrmLead}
 */
public final class LeadAssignPatchApplier
{
    private LeadAssignPatchApplier()
    {
    }

    public static void apply(CrmLead lead, LeadAssignPatch patch)
    {
        if (lead == null || patch == null)
        {
            return;
        }
        if (patch.getUserId() != null)
        {
            lead.setUserId(patch.getUserId());
        }
        if (patch.getDeptId() != null)
        {
            lead.setDeptId(patch.getDeptId());
        }
        if (patch.getOwner() != null)
        {
            lead.setOwner(patch.getOwner());
        }
        if (StringUtils.hasText(patch.getWecomFollowUserid()))
        {
            lead.setWecomFollowUserid(patch.getWecomFollowUserid());
        }
        if (StringUtils.hasText(patch.getStatus()))
        {
            lead.setStatus(patch.getStatus());
        }
    }

    /** 释放到公海：清空归属，status=Open */
    public static void applyOpenPool(CrmLead lead)
    {
        if (lead == null)
        {
            return;
        }
        lead.setStatus(AudienceLeadVisibility.STATUS_OPEN);
        lead.setUserId(null);
        lead.setOwner(null);
        lead.setDeptId(null);
    }
}
