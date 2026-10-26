package com.ruoyi.crm.service.webhook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import com.alibaba.fastjson2.JSONObject;

public class OutboundWebhookJsonTemplateRendererTest
{
    @Test
    public void emptyTemplateReturnsContext()
    {
        JSONObject context = new JSONObject();
        JSONObject lead = new JSONObject();
        lead.put("leadId", "l1");
        context.put("lead", lead);
        JSONObject rendered = OutboundWebhookJsonTemplateRenderer.render(null, context);
        assertEquals("l1", rendered.getJSONObject("lead").getString("leadId"));
    }

    @Test
    public void interpolatesAndKeepsMissingEmpty()
    {
        JSONObject context = new JSONObject();
        JSONObject contact = new JSONObject();
        contact.put("contactId", "c1");
        contact.put("name", "李四");
        context.put("contact", contact);
        JSONObject rendered = OutboundWebhookJsonTemplateRenderer.render(
                "{\"id\":\"{{contact.contactId}}\",\"missing\":\"{{contact.nope}}\"}", context);
        assertEquals("c1", rendered.getString("id"));
        assertEquals("", rendered.getString("missing"));
    }

    @Test
    public void promotesParsedObject()
    {
        JSONObject context = new JSONObject();
        context.put("raw", "{\"a\":1}");
        Object value = OutboundWebhookJsonTemplateRenderer.renderString("{{raw}}", context);
        assertTrue(value instanceof JSONObject);
        assertEquals(1, ((JSONObject) value).getIntValue("a"));
    }
}
