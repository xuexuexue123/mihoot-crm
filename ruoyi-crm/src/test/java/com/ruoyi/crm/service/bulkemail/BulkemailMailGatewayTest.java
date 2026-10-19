package com.ruoyi.crm.service.bulkemail;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.ruoyi.crm.domain.CrmBulkemail;

class BulkemailMailGatewayTest
{
    @Test
    void resolveFrom_usesCampaignSenderWhenPresent()
    {
        CrmBulkemail campaign = new CrmBulkemail();
        campaign.setSender("Campaign Sender <campaign@mg.example.com>");

        BulkemailProviderRuntimeConfig cfg = runtimeConfig();

        assertEquals("Campaign Sender <campaign@mg.example.com>",
                BulkemailMailGateway.resolveFrom(campaign, cfg));
    }

    @Test
    void resolveFrom_usesProviderDefaultWhenSenderBlank()
    {
        CrmBulkemail campaign = new CrmBulkemail();
        campaign.setSender("  ");

        BulkemailProviderRuntimeConfig cfg = runtimeConfig();

        assertEquals("Example Clinic <marketing@mg.example.com>",
                BulkemailMailGateway.resolveFrom(campaign, cfg));
    }

    @Test
    void formatFrom_withName()
    {
        assertEquals("Example Clinic <marketing@mg.example.com>",
                com.ruoyi.common.utils.mailgun.MailgunClient.formatFrom("Example Clinic", "marketing@mg.example.com"));
    }

    @Test
    void formatFrom_withoutName()
    {
        assertEquals("marketing@mg.example.com",
                com.ruoyi.common.utils.mailgun.MailgunClient.formatFrom(null, "marketing@mg.example.com"));
    }

    private static BulkemailProviderRuntimeConfig runtimeConfig()
    {
        BulkemailProviderRuntimeConfig cfg = new BulkemailProviderRuntimeConfig();
        cfg.setFromName("Example Clinic");
        cfg.setFromEmail("marketing@mg.example.com");
        cfg.setDomain("mg.example.com");
        cfg.setRegion("us");
        cfg.setApiKey("key");
        return cfg;
    }
}
