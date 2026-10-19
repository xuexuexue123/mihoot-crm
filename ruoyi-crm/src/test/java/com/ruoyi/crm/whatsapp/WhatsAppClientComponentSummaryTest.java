package com.ruoyi.crm.whatsapp;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.alibaba.fastjson2.JSONArray;

class WhatsAppClientComponentSummaryTest
{
    @Test
    void summarize_marksMissingMediaIdAndLink()
    {
        JSONArray comps = JSONArray.parseArray("["
                + "{\"type\":\"header\",\"parameters\":[{\"type\":\"image\",\"image\":{}}]},"
                + "{\"type\":\"body\",\"parameters\":[{\"type\":\"text\",\"text\":\"Hi\"}]}"
                + "]");
        String summary = WhatsAppClient.summarizeTemplateComponents(comps);
        assertTrue(summary.contains("image id=false link=false"), summary);
        assertTrue(summary.contains("text(len=2)"), summary);
    }

    @Test
    void summarize_empty()
    {
        assertTrue(WhatsAppClient.summarizeTemplateComponents(null).equals("[]"));
    }
}
