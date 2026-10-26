package com.ruoyi.crm.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.domain.CrmRespondents;
import com.ruoyi.crm.mapper.CrmRespondentsMapper;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.support.WebsiteVisitorSessionService;

@ExtendWith(MockitoExtension.class)
class CrmRespondentsServiceImplVisitorStitchTest
{
    @Mock
    private CrmRespondentsMapper crmRespondentsMapper;
    @Mock
    private WebsiteVisitorSessionService visitorSessionService;
    @Mock
    private ICrmCustomerIdentityService identityService;

    @InjectMocks
    private CrmRespondentsServiceImpl service;

    @Test
    void insert_withSessionIdOnly_stitchesContact()
    {
        CrmRespondents respondent = new CrmRespondents();
        respondent.setContactId("contact-1");
        respondent.setMetadata("{\"_sessionId\":\"sess-9\"}");
        when(crmRespondentsMapper.insertCrmRespondents(any())).thenReturn(1);

        service.insertCrmRespondents(respondent);

        verify(visitorSessionService).stitch(eq("sess-9"), isNull(), eq("Contact"), eq("contact-1"));
    }

    @Test
    void insert_withoutSessionId_skipsStitch()
    {
        CrmRespondents respondent = new CrmRespondents();
        respondent.setContactId("contact-1");
        respondent.setMetadata("{\"_browseHistory\":\"[]\"}");
        when(crmRespondentsMapper.insertCrmRespondents(any())).thenReturn(1);

        service.insertCrmRespondents(respondent);

        verify(visitorSessionService, never()).stitch(any(), any(), any(), any());
    }

    @Test
    void insert_sessionIdWithLeadId_stitchesLead()
    {
        CrmRespondents respondent = new CrmRespondents();
        respondent.setMetadata("{\"_sessionId\":\"sess-2\",\"leadId\":\"lead-7\"}");
        when(crmRespondentsMapper.insertCrmRespondents(any())).thenReturn(1);

        service.insertCrmRespondents(respondent);

        verify(visitorSessionService).stitch(eq("sess-2"), isNull(), eq("Lead"), eq("lead-7"));
    }
}
