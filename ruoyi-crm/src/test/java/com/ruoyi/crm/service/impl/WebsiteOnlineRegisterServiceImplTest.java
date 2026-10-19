package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmOrg;
import com.ruoyi.crm.domain.dto.WebsiteLeadSubmitRequest;
import com.ruoyi.crm.domain.vo.WebsiteLeadSubmitResultVo;
import com.ruoyi.crm.service.ICrmOrgService;
import com.ruoyi.crm.service.support.OutreachRegistrationIdentityResolution;
import com.ruoyi.crm.service.support.PublicTenantSiteResolver;
import com.ruoyi.crm.service.support.WaitResumeTenantBootstrap;
import com.ruoyi.crm.service.support.WebsiteLeadAttributionService;
import com.ruoyi.crm.service.support.WebsiteLeadIdentityResolver;
import com.ruoyi.crm.service.support.WebsiteVisitorSessionService;
import com.ruoyi.tenant.domain.vo.PublicTenantSiteVo;

@ExtendWith(MockitoExtension.class)
class WebsiteOnlineRegisterServiceImplTest
{
    @Mock
    private PublicTenantSiteResolver publicTenantSiteResolver;
    @Mock
    private WaitResumeTenantBootstrap waitResumeTenantBootstrap;
    @Mock
    private ICrmOrgService crmOrgService;
    @Mock
    private WebsiteLeadIdentityResolver identityResolver;
    @Mock
    private WebsiteLeadAttributionService leadAttributionService;
    @Mock
    private WebsiteVisitorSessionService visitorSessionService;

    @InjectMocks
    private WebsiteOnlineRegisterServiceImpl service;

    private WebsiteLeadSubmitRequest validRequest()
    {
        WebsiteLeadSubmitRequest request = new WebsiteLeadSubmitRequest();
        request.setTenantSlug("demo");
        request.setLeadName("张三");
        request.setMobilePhone("13800138000");
        request.setOrgId("org-1");
        request.setAppointmentDate("2026-07-15");
        request.setAppointmentTime("11:30-12:00");
        request.setConsentAccepted(true);
        return request;
    }

    private PublicTenantSiteVo activeSite()
    {
        PublicTenantSiteVo site = new PublicTenantSiteVo();
        site.setTenantName("tenant_demo");
        site.setWebsiteStatus("active");
        return site;
    }

    @Test
    void submit_createsLead()
    {
        WebsiteLeadSubmitRequest request = validRequest();
        when(publicTenantSiteResolver.resolveActiveSite("demo")).thenReturn(activeSite());
        CrmOrg org = new CrmOrg();
        when(crmOrgService.selectCrmOrgByOrgId("org-1")).thenReturn(org);
        when(identityResolver.resolve("tenant_demo", request.getMobilePhone()))
                .thenReturn(OutreachRegistrationIdentityResolution.createNewLead());
        when(leadAttributionService.submitNewLead(request)).thenReturn("lead-new");

        WebsiteLeadSubmitResultVo result = service.submit(request);

        assertEquals("created", result.getResolution());
        assertEquals("lead-new", result.getLeadId());
        verify(waitResumeTenantBootstrap).bindTenantDataSource("tenant_demo");
        verify(leadAttributionService).submitNewLead(request);
        verify(visitorSessionService).stitch(isNull(), isNull(), eq("Lead"), eq("lead-new"));
    }

    @Test
    void submit_contactKnown_skipsLeadWrite()
    {
        WebsiteLeadSubmitRequest request = validRequest();
        request.set_sessionId("sess-1");
        request.set_browseHistory("[{\"u\":\"/\",\"t\":1}]");
        when(publicTenantSiteResolver.resolveActiveSite("demo")).thenReturn(activeSite());
        when(crmOrgService.selectCrmOrgByOrgId("org-1")).thenReturn(new CrmOrg());
        when(identityResolver.resolve("tenant_demo", request.getMobilePhone()))
                .thenReturn(OutreachRegistrationIdentityResolution.contactKnown("contact-1", "mobile"));

        WebsiteLeadSubmitResultVo result = service.submit(request);

        assertEquals("contact_known", result.getResolution());
        assertEquals("contact-1", result.getContactId());
        assertNull(result.getLeadId());
        verify(visitorSessionService).stitch(eq("sess-1"), eq("[{\"u\":\"/\",\"t\":1}]"),
                eq("Contact"), eq("contact-1"));
    }

    @Test
    void submit_updatesInitialLead()
    {
        WebsiteLeadSubmitRequest request = validRequest();
        CrmLead existing = new CrmLead();
        existing.setLeadId("lead-existing");
        when(publicTenantSiteResolver.resolveActiveSite("demo")).thenReturn(activeSite());
        when(crmOrgService.selectCrmOrgByOrgId("org-1")).thenReturn(new CrmOrg());
        when(identityResolver.resolve("tenant_demo", request.getMobilePhone()))
                .thenReturn(OutreachRegistrationIdentityResolution.leadUpsert(existing));
        when(leadAttributionService.submitLeadUpdate(request, existing)).thenReturn("lead-existing");

        WebsiteLeadSubmitResultVo result = service.submit(request);

        assertEquals("updated", result.getResolution());
        assertEquals("lead-existing", result.getLeadId());
        verify(visitorSessionService).stitch(isNull(), isNull(), eq("Lead"), eq("lead-existing"));
    }
}
