package com.ruoyi.crm.wechatmp.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;

class WechatMpMessagePresetRenderSupportTest
{
    @Test
    void substitute_replacesPlaceholders()
    {
        Map<String, String> vars = new HashMap<>();
        vars.put("eventName", "618种植牙体验日");
        vars.put("eventId", "evt-1");
        vars.put("baseUrl", "https://h5.example.com");

        String url = WechatMpMessagePresetRenderSupport.substitute(
                "{{baseUrl}}/events/{{eventId}}/coupon", vars);
        assertEquals("https://h5.example.com/events/evt-1/coupon", url);
    }

    @Test
    void substituteDataJson_replacesValueFieldsOnly()
    {
        Map<String, String> vars = new HashMap<>();
        vars.put("eventName", "618种植牙体验日");

        JSONObject data = JSONObject.parseObject(
                "{\"first\":{\"value\":\"感谢参加{{eventName}}\"},\"keyword1\":{\"value\":\"{{eventName}}\"}}");
        JSONObject rendered = WechatMpMessagePresetRenderSupport.substituteDataJson(data, vars);

        assertEquals("感谢参加618种植牙体验日", rendered.getJSONObject("first").getString("value"));
        assertEquals("618种植牙体验日", rendered.getJSONObject("keyword1").getString("value"));
    }

    @Test
    void validateRenderedData_rejectsBlankValue()
    {
        JSONObject data = JSONObject.parseObject("{\"first\":{\"value\":\"  \"}}");
        assertThrows(ServiceException.class,
                () -> WechatMpMessagePresetRenderSupport.validateRenderedData(data));
    }

    @Test
    void extraOverridesBuiltInViaSubstituteOrder()
    {
        Map<String, String> vars = new HashMap<>();
        vars.put("eventName", "内置名");
        vars.put("eventName", "覆盖名");

        String result = WechatMpMessagePresetRenderSupport.substitute("{{eventName}}", vars);
        assertEquals("覆盖名", result);
    }
}
