package com.ruoyi.crm.service.bulkemail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.domain.CrmBulkemail;
import com.ruoyi.crm.domain.CrmCampaignContacts;
import com.ruoyi.crm.domain.CrmContact;

@ExtendWith(MockitoExtension.class)
class BulkemailMergeTagSupportTest
{
    @Mock
    private BulkemailTenantBrandingService tenantBrandingService;

    @InjectMocks
    private BulkemailMergeTagSupport mergeTagSupport;

    private BulkemailRenderContext ctx;

    @BeforeEach
    void setUp()
    {
        CrmContact crmContact = new CrmContact();
        crmContact.setName("张三");
        crmContact.setWorkEmail("zhang@example.com");

        CrmCampaignContacts campaignContact = new CrmCampaignContacts();
        campaignContact.setContactName("Campaign Name");
        campaignContact.setEmail("campaign@example.com");

        CrmBulkemail campaign = new CrmBulkemail();
        campaign.setBulkId("bulk-1");

        ctx = new BulkemailRenderContext(campaign, campaignContact, crmContact, "tenant-a", true);
    }

    @Test
    void buildP2Variables_prefersCrmContact()
    {
        when(tenantBrandingService.getClinicName("tenant-a")).thenReturn("Demo Clinic");

        Map<String, String> vars = mergeTagSupport.buildP2Variables(ctx);

        assertEquals("张三", vars.get("contactName"));
        assertEquals("zhang@example.com", vars.get("workEmail"));
        assertEquals("Demo Clinic", vars.get("clinicName"));
        assertEquals("#", vars.get("unsubscribeUrl"));
    }

    @Test
    void applyP2Tags_replacesKnownAndStripsUnknown()
    {
        when(tenantBrandingService.getClinicName("tenant-a")).thenReturn("Demo Clinic");
        Map<String, String> vars = mergeTagSupport.buildP2Variables(ctx);

        String rendered = mergeTagSupport.applyP2Tags(
                "Hi {{contactName}} from {{clinicName}} {{unknownTag}}", vars);

        assertEquals("Hi 张三 from Demo Clinic ", rendered);
    }

    @Test
    void applyP1Tags_replacesLegacyTokens()
    {
        String rendered = mergeTagSupport.applyP1Tags(
                "Hi <<CONTACT_NAME>> (<<EMAIL_ADDRESS>>) id=<<CAMPAIGN_ID>>", ctx);

        assertTrue(rendered.contains("张三"));
        assertTrue(rendered.contains("zhang@example.com"));
        assertTrue(rendered.contains("bulk-1"));
        assertFalse(rendered.contains("<<CONTACT_NAME>>"));
    }

    @Test
    void p1AndP2Coexist()
    {
        when(tenantBrandingService.getClinicName("tenant-a")).thenReturn("Demo Clinic");
        Map<String, String> vars = mergeTagSupport.buildP2Variables(ctx);

        String p1 = mergeTagSupport.applyP1Tags("<<CONTACT_NAME>>", ctx);
        String p2 = mergeTagSupport.applyP2Tags("{{contactName}}", vars);

        assertEquals("张三", p1);
        assertEquals("张三", p2);
    }
}
