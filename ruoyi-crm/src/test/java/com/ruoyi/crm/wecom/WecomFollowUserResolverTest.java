package com.ruoyi.crm.wecom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.dto.LeadAssignPatch;
import com.ruoyi.crm.domain.vo.AssignableResolveResult;
import com.ruoyi.crm.service.IAssignableUserService;
import com.ruoyi.crm.service.support.AudienceLeadVisibility;

@ExtendWith(MockitoExtension.class)
class WecomFollowUserResolverTest
{
    @Mock
    private IAssignableUserService assignableUserService;

    @InjectMocks
    private WecomFollowUserResolver resolver;

    @Test
    void applyLeadOwnership_setsAssignedWhenResolved()
    {
        LeadAssignPatch patch = new LeadAssignPatch();
        patch.setUserId(100L);
        patch.setDeptId(200L);
        patch.setOwner("罗华东");
        patch.setWecomFollowUserid("LuoHuaDong");
        patch.setStatus(AudienceLeadVisibility.STATUS_ASSIGNED);

        AssignableResolveResult resolveResult = new AssignableResolveResult();
        resolveResult.setResolved(true);
        resolveResult.setLeadAssignPatch(patch);
        when(assignableUserService.resolve(eq("LuoHuaDong"), eq(null), eq("wecom_follow_resolve")))
                .thenReturn(resolveResult);

        CrmLead lead = new CrmLead();
        resolver.applyLeadOwnership(lead, "LuoHuaDong");

        assertEquals(100L, lead.getUserId());
        assertEquals(200L, lead.getDeptId());
        assertEquals("罗华东", lead.getOwner());
        assertEquals(AudienceLeadVisibility.STATUS_ASSIGNED, lead.getStatus());
    }

    @Test
    void applyLeadOwnership_setsOpenWhenNotResolved()
    {
        AssignableResolveResult resolveResult = new AssignableResolveResult();
        resolveResult.setResolved(false);
        resolveResult.setReason("user_not_found");
        when(assignableUserService.resolve(eq("unknown"), eq(null), eq("wecom_follow_resolve")))
                .thenReturn(resolveResult);

        CrmLead lead = new CrmLead();
        resolver.applyLeadOwnership(lead, "unknown");

        assertNull(lead.getUserId());
        assertNull(lead.getOwner());
        assertEquals(AudienceLeadVisibility.STATUS_OPEN, lead.getStatus());
        assertEquals("unknown", lead.getWecomFollowUserid());
    }

    @Test
    void applyLeadOwnership_blankFollowUseridGoesOpen()
    {
        CrmLead lead = new CrmLead();
        resolver.applyLeadOwnership(lead, "  ");

        assertNull(lead.getUserId());
        assertEquals(AudienceLeadVisibility.STATUS_OPEN, lead.getStatus());
        verify(assignableUserService, org.mockito.Mockito.never())
                .resolve(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
                        org.mockito.ArgumentMatchers.any());
    }
}
