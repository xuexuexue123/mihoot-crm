package com.ruoyi.crm.wecom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.dto.wecom.WecomExternalContactProfile;
import com.ruoyi.crm.service.ICrmLeadService;

@ExtendWith(MockitoExtension.class)
class WecomLeadMergeSupportTest
{
    @Mock
    private ICrmLeadService crmLeadService;

    @Mock
    private WecomLeadEnrichSupport wecomLeadEnrichSupport;

    @Mock
    private WecomFollowUserResolver wecomFollowUserResolver;

    @InjectMocks
    private WecomLeadMergeSupport support;

    @Test
    void mergeWecomIntoExistingLead_preservesExistingSidWhenDifferentEvent()
    {
        CrmLead existing = new CrmLead();
        existing.setLeadId("lead-1");
        existing.setSid("event-a");
        existing.setLeadName("H5用户");
        existing.setLeadSource("Marketing Event");
        existing.setMobilePhone("13800138000");

        WecomStateParseResult parsed = new WecomStateParseResult();
        parsed.setCrmEventId("event-b");
        parsed.setLeadSource("wecom:poster");
        parsed.setChannel("poster");

        WecomExternalContactProfile profile = new WecomExternalContactProfile();
        profile.setName("企微昵称");

        when(crmLeadService.updateCrmLead(any(CrmLead.class))).thenReturn(1);
        doAnswer(invocation -> {
            CrmLead patch = invocation.getArgument(0);
            patch.setWecomExternalUserid(invocation.getArgument(3));
            patch.setWecomFollowUserid(invocation.getArgument(4));
            return null;
        }).when(wecomLeadEnrichSupport).enrichNewLead(any(), any(), any(), any(), any(), any());

        String leadId = support.mergeWecomIntoExistingLead(existing, profile, null,
                "wm_ext", "staff1", "企微昵称", parsed, null, "wecom-callback");

        assertEquals("lead-1", leadId);
        ArgumentCaptor<CrmLead> captor = ArgumentCaptor.forClass(CrmLead.class);
        verify(crmLeadService).updateCrmLead(captor.capture());
        CrmLead patch = captor.getValue();
        assertNull(patch.getSid());
        assertEquals("wm_ext", patch.getWecomExternalUserid());
        assertEquals("13800138000", existing.getMobilePhone());
    }

    @Test
    void mergeWecomIntoExistingLead_setsSidWhenBlank()
    {
        CrmLead existing = new CrmLead();
        existing.setLeadId("lead-2");
        existing.setLeadName("用户");

        WecomStateParseResult parsed = new WecomStateParseResult();
        parsed.setCrmEventId("event-b");
        parsed.setLeadSource("wecom");

        when(crmLeadService.updateCrmLead(any(CrmLead.class))).thenReturn(1);

        support.mergeWecomIntoExistingLead(existing, null, null,
                "wm_ext", "staff1", null, parsed, "13900001111", "wecom-callback");

        ArgumentCaptor<CrmLead> captor = ArgumentCaptor.forClass(CrmLead.class);
        verify(crmLeadService).updateCrmLead(captor.capture());
        assertEquals("event-b", captor.getValue().getSid());
        assertEquals("13900001111", captor.getValue().getMobilePhone());
    }
}
