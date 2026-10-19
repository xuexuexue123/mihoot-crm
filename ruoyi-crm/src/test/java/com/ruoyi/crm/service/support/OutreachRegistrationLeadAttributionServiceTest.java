package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmOutreachlead;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.ICrmLeadService;
import com.ruoyi.crm.service.nurture.support.EventNurtureHookSupport;

@ExtendWith(MockitoExtension.class)
class OutreachRegistrationLeadAttributionServiceTest
{
    @Mock
    private ICrmLeadService crmLeadService;

    @Mock
    private OutreachEventLeadResolveService outreachEventLeadResolveService;

    @Mock
    private ICrmCustomerIdentityService customerIdentityService;

    @Mock
    private EventNurtureHookSupport nurtureHookSupport;

    @Mock
    private ActivityEventProducer activityEventProducer;

    @InjectMocks
    private OutreachRegistrationLeadAttributionService service;

    private CrmEvent event;

    @BeforeEach
    void setUp()
    {
        event = new CrmEvent();
        event.setEventId("evt-1");
    }

    @Test
    void insert_writesOpenIdColumnsFromToken()
    {
        CrmOutreachlead submission = newSubmission("Alice", "13800000001");
        when(outreachEventLeadResolveService.resolveExistingLead("evt-1", "13800000001", "o1", "wx1"))
                .thenReturn(null);
        when(crmLeadService.insertCrmLead(any(CrmOutreachlead.class))).thenReturn(1);

        int result = service.submitRegistration(submission, event, "o1", "wx1");

        assertEquals(1, result);
        assertEquals("o1", submission.getWechatMpOpenid());
        assertEquals("wx1", submission.getWechatMpAppId());
        verify(crmLeadService).insertCrmLead(submission);
    }

    @Test
    void insert_ignoresBodyOpenIdAndUsesToken()
    {
        CrmOutreachlead submission = newSubmission("Alice", "13800000001");
        submission.setWechatMpOpenid("forged-openid");
        submission.setWechatMpAppId("forged-app");
        when(outreachEventLeadResolveService.resolveExistingLead("evt-1", "13800000001", "o1", "wx1"))
                .thenReturn(null);
        when(crmLeadService.insertCrmLead(any(CrmOutreachlead.class))).thenReturn(1);

        service.submitRegistration(submission, event, "o1", "wx1");

        assertEquals("o1", submission.getWechatMpOpenid());
        assertEquals("wx1", submission.getWechatMpAppId());
    }

    @Test
    void genericToken_doesNotWriteOpenIdColumns()
    {
        CrmOutreachlead submission = newSubmission("Alice", "13800000001");
        when(outreachEventLeadResolveService.resolveExistingLead("evt-1", "13800000001", null, null))
                .thenReturn(null);
        when(crmLeadService.insertCrmLead(any(CrmOutreachlead.class))).thenReturn(1);

        service.submitRegistration(submission, event, null, null);

        assertNull(submission.getWechatMpOpenid());
        assertNull(submission.getWechatMpAppId());
    }

    @Test
    void update_existingInitialLead()
    {
        CrmOutreachlead submission = newSubmission("Alice", "13800000001");
        CrmLead existing = new CrmLead();
        existing.setLeadId("lead-existing");
        existing.setStatus("Initial");
        when(outreachEventLeadResolveService.resolveExistingLead("evt-1", "13800000001", "o1", "wx1"))
                .thenReturn(existing);
        when(crmLeadService.updateCrmLead(any(CrmOutreachlead.class))).thenReturn(1);

        service.submitRegistration(submission, event, "o1", "wx1");

        assertEquals("lead-existing", submission.getLeadId());
        assertEquals("o1", submission.getWechatMpOpenid());
        verify(crmLeadService, never()).insertCrmLead(any());
        verify(crmLeadService).updateCrmLead(submission);
    }

    @Test
    void rejectsNonInitialExistingLead()
    {
        CrmOutreachlead submission = newSubmission("Alice", "13800000001");
        CrmLead existing = new CrmLead();
        existing.setLeadId("lead-existing");
        existing.setStatus("Converted");
        when(outreachEventLeadResolveService.resolveExistingLead("evt-1", "13800000001", "o1", "wx1"))
                .thenReturn(existing);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.submitRegistration(submission, event, "o1", "wx1"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode());
    }

    private static CrmOutreachlead newSubmission(String name, String mobile)
    {
        CrmOutreachlead lead = new CrmOutreachlead();
        lead.setLeadName(name);
        lead.setMobilePhone(mobile);
        lead.setSid("evt-1");
        return lead;
    }
}
