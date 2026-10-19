package com.ruoyi.crm.service.bulkemail;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.ruoyi.crm.domain.CrmBulkemail;
import com.ruoyi.crm.domain.CrmCampaignContacts;

public class BulkemailTrackingInjectorTest
{
    private final BulkemailTrackingInjector injector = new BulkemailTrackingInjector();

    @Test
    public void injectsPixelBeforeBodyClose() throws Exception
    {
        java.lang.reflect.Field field = BulkemailTrackingInjector.class.getDeclaredField("trackingBaseUrl");
        field.setAccessible(true);
        field.set(injector, "https://api.example.com");

        CrmBulkemail campaign = new CrmBulkemail();
        campaign.setBulkId("bulk-1");

        CrmCampaignContacts contact = new CrmCampaignContacts();
        contact.setEmail("test@example.com");

        String result = injector.inject("<html><body>Hello</body></html>", contact, campaign, "tenant-a");
        assertTrue(result.contains("/track?"));
        assertTrue(result.contains("width=\"1\""));
        assertTrue(result.contains("</body>"));
    }
}
