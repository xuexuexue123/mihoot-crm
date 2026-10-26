package com.ruoyi.crm.service.bulkemail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.domain.CrmBulkemail;
import com.ruoyi.crm.domain.CrmCampaignContacts;
import com.ruoyi.crm.domain.dto.BulkemailRenderResult;

@ExtendWith(MockitoExtension.class)
class BulkemailRenderServiceTest
{
    @Mock
    private BulkemailMergeTagSupport mergeTagSupport;

    @InjectMocks
    private BulkemailRenderService renderService;

    private CrmBulkemail campaign;
    private CrmCampaignContacts contact;
    private BulkemailRenderContext ctx;

    @BeforeEach
    void setUp()
    {
        campaign = new CrmBulkemail();
        campaign.setBulkId("bulk-1");
        campaign.setSubject("Sub <<CONTACT_NAME>>");

        contact = new CrmCampaignContacts();
        contact.setContactName("张三");
        contact.setEmail("zhang@example.com");

        ctx = new BulkemailRenderContext(campaign, contact, null, "tenant-a", false);
    }

    @Test
    void replacesP1MergeTagsInHtml()
    {
        lenient().when(mergeTagSupport.applyP1Tags(any(), any())).thenAnswer(invocation ->
        {
            String template = invocation.getArgument(0);
            if (template == null)
            {
                return "";
            }
            return template
                    .replace("<<CONTACT_NAME>>", "张三")
                    .replace("<<EMAIL_ADDRESS>>", "zhang@example.com")
                    .replace("<<CAMPAIGN_ID>>", "bulk-1")
                    .replace("<<TOKEN_KEY>>", "abc");
        });

        String html = renderService.render(
                "Hi <<CONTACT_NAME>> (<<EMAIL_ADDRESS>>) id=<<CAMPAIGN_ID>> token=<<TOKEN_KEY>>",
                contact, campaign, "tenant-a");

        assertTrue(html.contains("张三"));
        assertTrue(html.contains("zhang@example.com"));
        assertTrue(html.contains("bulk-1"));
        assertFalse(html.contains("<<CONTACT_NAME>>"));
    }

    @Test
    void render_appliesP2WhenPersonalizationEnabled()
    {
        ctx = new BulkemailRenderContext(campaign, contact, null, "tenant-a", true);
        Map<String, String> vars = new HashMap<>();
        vars.put("contactName", "张三");
        when(mergeTagSupport.applyP1Tags("Hello {{contactName}}", ctx)).thenReturn("Hello {{contactName}}");
        when(mergeTagSupport.buildP2Variables(ctx)).thenReturn(vars);
        when(mergeTagSupport.applyP2Tags("Hello {{contactName}}", vars)).thenReturn("Hello 张三");

        BulkemailRenderResult result = renderService.render("Hello {{contactName}}", "<p>x</p>", ctx);

        assertEquals("Hello 张三", result.getSubject());
    }

    @Test
    void passThroughWhenBlank()
    {
        lenient().when(mergeTagSupport.applyP1Tags(any(), any())).thenAnswer(invocation ->
        {
            Object template = invocation.getArgument(0);
            return template != null ? template : "";
        });
        assertEquals("", renderService.render("", null, null, null));
    }
}
