package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.dto.LeadAssignPatch;
import com.ruoyi.crm.domain.vo.AssignableResolveResult;
import com.ruoyi.crm.domain.vo.LeadConvertConfigVo;
import com.ruoyi.crm.service.IAssignableUserService;
import com.ruoyi.system.mapper.SysUserMapper;

@ExtendWith(MockitoExtension.class)
class LeadConvertOwnerResolverTest
{
    @Mock
    private IAssignableUserService assignableUserService;
    @Mock
    private SysUserMapper sysUserMapper;

    @InjectMocks
    private LeadConvertOwnerResolver resolver;

    @Test
    void keepLeadOwnerCopiesLead()
    {
        CrmLead lead = new CrmLead();
        lead.setUserId(7L);
        lead.setDeptId(8L);
        lead.setOwner("销售甲");
        CrmContact contact = new CrmContact();
        LeadConvertConfigVo config = LeadConvertConfigVo.defaults();
        resolver.applyToNewContact(contact, lead, config, "op");
        assertEquals(Long.valueOf(7L), contact.getUserId());
        assertEquals("销售甲", contact.getOwner());
    }

    @Test
    void specifiedUserInvalidFallsBack()
    {
        CrmLead lead = new CrmLead();
        lead.setUserId(3L);
        lead.setOwner("线索负责人");
        LeadConvertConfigVo config = LeadConvertConfigVo.defaults();
        config.setContactOwnerMode("specified_user");
        config.setContactOwnerUserId(99L);
        AssignableResolveResult unresolved = new AssignableResolveResult();
        unresolved.setResolved(false);
        when(assignableUserService.resolve(any(), eq(99L), eq("contact_owner_dropdown"))).thenReturn(unresolved);

        CrmContact contact = new CrmContact();
        resolver.applyToNewContact(contact, lead, config, "op");
        assertEquals(Long.valueOf(3L), contact.getUserId());
        assertEquals("线索负责人", contact.getOwner());
    }

    @Test
    void specifiedUserAppliesPatch()
    {
        LeadConvertConfigVo config = LeadConvertConfigVo.defaults();
        config.setContactOwnerMode("specified_user");
        config.setContactOwnerUserId(12L);
        LeadAssignPatch patch = new LeadAssignPatch();
        patch.setUserId(12L);
        patch.setDeptId(20L);
        patch.setOwner("指定人");
        AssignableResolveResult resolved = new AssignableResolveResult();
        resolved.setResolved(true);
        resolved.setLeadAssignPatch(patch);
        when(assignableUserService.resolve(any(), eq(12L), eq("contact_owner_dropdown"))).thenReturn(resolved);

        CrmContact contact = new CrmContact();
        resolver.applyToNewContact(contact, new CrmLead(), config, "op");
        assertEquals(Long.valueOf(12L), contact.getUserId());
        assertEquals("指定人", contact.getOwner());
    }
}
