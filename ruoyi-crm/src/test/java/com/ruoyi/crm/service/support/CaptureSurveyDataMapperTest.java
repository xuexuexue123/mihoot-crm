package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.service.support.CaptureSurveyDataMapper.MappedLeadFields;

class CaptureSurveyDataMapperTest
{
    @Test
    void map_prefersPrimaryKeys()
    {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("leadName", "张三");
        data.put("name", "fallback");
        data.put("mobilePhone", "13800138000");
        data.put("phone", "139");
        data.put("workEmail", "a@b.com");
        data.put("email", "x@y.com");
        data.put("fullName", "Org A");
        data.put("organization", "Org B");
        data.put("remark", "hello");

        MappedLeadFields fields = CaptureSurveyDataMapper.map(data);

        assertEquals("张三", fields.getLeadName());
        assertEquals("13800138000", fields.getMobilePhone());
        assertEquals("a@b.com", fields.getWorkEmail());
        assertEquals("Org A", fields.getFullName());
        assertEquals("hello", fields.getExtraFields().get("remark"));
        assertTrue(!fields.getExtraFields().containsKey("leadName"));
    }

    @Test
    void map_fallsBackToAliases()
    {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("name", "李四");
        data.put("phone", "13900139000");
        data.put("email", "li@si.com");
        data.put("organization", "Acme");

        MappedLeadFields fields = CaptureSurveyDataMapper.map(data);

        assertEquals("李四", fields.getLeadName());
        assertEquals("13900139000", fields.getMobilePhone());
        assertEquals("li@si.com", fields.getWorkEmail());
        assertEquals("Acme", fields.getFullName());
    }

    @Test
    void buildCustomField_includesCaptureMeta()
    {
        Map<String, Object> extras = new LinkedHashMap<>();
        extras.put("remark", "hi");
        String json = CaptureCustomFieldSupport.buildCustomField(extras, "cap-1", "demo", "https://x.com/p");
        JSONArray array = JSON.parseArray(json);
        assertEquals("hi", find(array, "remark").getString("value"));
        assertEquals("cap-1", find(array, "captureId").getString("value"));
        assertEquals("demo", find(array, "tenantSlug").getString("value"));
        assertEquals("https://x.com/p", find(array, "pageUrl").getString("value"));
        assertNull(find(array, "missing"));
    }

    private static JSONObject find(JSONArray array, String fieldName)
    {
        for (int i = 0; i < array.size(); i++)
        {
            JSONObject item = array.getJSONObject(i);
            if (fieldName.equals(item.getString("fieldName")))
            {
                return item;
            }
        }
        return null;
    }
}
