package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.dto.ActivityEventDTO;
import com.ruoyi.crm.mapper.CrmLeadMapper;

@ExtendWith(MockitoExtension.class)
class LeadFollowupAnchorSupportTest
{
    @Mock
    private CrmLeadMapper crmLeadMapper;

    @InjectMocks
    private LeadFollowupAnchorSupport support;

    @Test
    void assignedEventDoesNotTouch()
    {
        ActivityEventDTO dto = new ActivityEventDTO();
        dto.setEventType("lead.assigned");
        dto.setSubjectType("Lead");
        dto.setSubjectId("L1");
        support.onActivityProduced(dto, null);
        verify(crmLeadMapper, never()).touchLastFollowupAt(any(), any());
    }

    @Test
    void wecomMessageTouchesLead()
    {
        ActivityEventDTO dto = new ActivityEventDTO();
        dto.setEventType("channel.wecom.message.sent");
        dto.setSubjectType("Lead");
        dto.setSubjectId("L1");
        dto.setStartedDate(new Date());
        support.onActivityProduced(dto, null);
        verify(crmLeadMapper).touchLastFollowupAt(eq("L1"), any());
    }

    @Test
    void contactMessageResolvesAssignedLead()
    {
        CrmLead lead = new CrmLead();
        lead.setLeadId("L2");
        when(crmLeadMapper.selectAssignedByContactId("C1")).thenReturn(lead);

        ActivityEventDTO dto = new ActivityEventDTO();
        dto.setEventType("whatsapp.message.received");
        dto.setSubjectType("Contact");
        dto.setSubjectId("C1");
        support.onActivityProduced(dto, null);
        verify(crmLeadMapper).touchLastFollowupAt(eq("L2"), any());
    }

    @Test
    void aiHostedSkipped()
    {
        ActivityEventDTO dto = new ActivityEventDTO();
        dto.setEventType("webchat.message.sent");
        dto.setSubjectType("Lead");
        dto.setSubjectId("L1");
        dto.setPayload("{\"hosted_by_ai\":true}");
        support.onActivityProduced(dto, null);
        verify(crmLeadMapper, never()).touchLastFollowupAt(any(), any());
    }

    @Test
    void whitelistHelpers()
    {
        assertTrue(LeadFollowupAnchorSupport.isWhitelistedEvent("channel.wecom.message.sent"));
        assertFalse(LeadFollowupAnchorSupport.isWhitelistedEvent("lead.created"));
        assertTrue(LeadFollowupAnchorSupport.isWhitelistedCommunication("外呼"));
        assertFalse(LeadFollowupAnchorSupport.isWhitelistedCommunication("备注"));
        assertEquals(72, LeadRecyclePolicyDefaults.resolveIdleHours(null, 3));
        assertEquals(168, LeadRecyclePolicyDefaults.resolveIdleHours(null, 7));
    }
}
