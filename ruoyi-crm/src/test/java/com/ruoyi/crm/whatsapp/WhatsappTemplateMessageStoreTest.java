package com.ruoyi.crm.whatsapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

class WhatsappTemplateMessageStoreTest
{
    @Test
    void rendersBodyPlaceholdersAndWritesRawJson()
    {
        String definition = "[{\"type\":\"BODY\",\"text\":\"你好 {{1}}，预约 {{2}}\"}]";
        JSONArray send = JSON.parseArray(
                "[{\"type\":\"body\",\"parameters\":[{\"type\":\"text\",\"text\":\"张三\"},{\"type\":\"text\",\"text\":\"明天3点\"}]}]");

        WhatsappTemplateMessageStore.StoredTemplate stored = WhatsappTemplateMessageStore.build(
                null, "appointment_reminder", "zh_CN", send, definition);

        assertEquals("appointment_reminder\n你好 张三，预约 明天3点", stored.content);
        assertTrue(stored.preview.contains("appointment_reminder"));
        assertNotNull(stored.rawJson);

        JSONObject raw = JSON.parseObject(stored.rawJson);
        assertEquals("template", raw.getString("type"));
        assertEquals("appointment_reminder", raw.getJSONObject("template").getString("name"));
        assertEquals("你好 张三，预约 明天3点", raw.getString("renderedBody"));
    }

    @Test
    void prefersClientContent()
    {
        WhatsappTemplateMessageStore.StoredTemplate stored = WhatsappTemplateMessageStore.build(
                "前端预览正文", "hello_world", "en", null, null);
        assertEquals("前端预览正文", stored.content);
    }
}
