package com.ruoyi.crm.wechatmp.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

class CrmWechatMpMessagePresetJacksonTest
{
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserializesDataJsonIntoRaw() throws Exception
    {
        String json = "{"
                + "\"presetName\":\"预约成功通知\","
                + "\"description\":\"test\","
                + "\"appId\":\"wx68e8d23a9e14de21\","
                + "\"wechatTemplateId\":\"fmKk0DcKZPj1_o0V3fjHeVNqDFqy_xo0fi-byhYkWaQ\","
                + "\"urlTemplate\":\"https://www.google.com/\","
                + "\"dataJson\":{\"Date\":{\"color\":\"#173177\",\"value\":\"{{appointmentTime}}\"}},"
                + "\"tags\":\"appointment\","
                + "\"status\":\"0\""
                + "}";

        CrmWechatMpMessagePreset row = objectMapper.readValue(json, CrmWechatMpMessagePreset.class);

        assertEquals("https://www.google.com/", row.getUrlTemplate());
        assertNotNull(row.getDataJsonRaw(), "dataJsonRaw should be set from dataJson payload");
        assertNotNull(row.getDataJson());
    }
}
