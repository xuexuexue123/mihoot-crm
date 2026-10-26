package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.domain.dto.WebsiteLeadSubmitRequest;

class WebsiteLeadCustomFieldSupportTest
{
    @Test
    void buildCustomField_includesAppointmentFieldsWithFieldLabel()
    {
        WebsiteLeadSubmitRequest request = new WebsiteLeadSubmitRequest();
        request.setAppointmentDate("2026-07-08");
        request.setAppointmentTime("11:30-12:00");
        request.setCustomField("[{\"fieldName\":\"gender\",\"value\":\"male\"}]");

        String customField = WebsiteLeadCustomFieldSupport.buildCustomField(request);

        assertNotNull(customField);
        JSONArray array = JSON.parseArray(customField);
        JSONObject appointmentDate = findField(array, "appointmentDate");
        assertEquals("2026-07-08", appointmentDate.getString("value"));
        assertEquals("", appointmentDate.getString("fieldLabel"));
        JSONObject appointmentTime = findField(array, "appointmentTime");
        assertEquals("11:30-12:00", appointmentTime.getString("value"));
        assertEquals("", appointmentTime.getString("fieldLabel"));
        JSONObject gender = findField(array, "gender");
        assertEquals("male", gender.getString("value"));
        assertEquals("", gender.getString("fieldLabel"));
    }

    @Test
    void mergeCustomField_mergesByFieldNameAndNormalizesFieldLabel()
    {
        String existing = "[{\"fieldName\":\"gender\",\"value\":\"male\",\"fieldLabel\":\"性别\"}]";
        String incoming = "[{\"fieldName\":\"remarks\",\"value\":\"new\"},{\"fieldName\":\"storeName\",\"value\":\"Store B\"}]";

        String merged = WebsiteLeadCustomFieldSupport.mergeCustomField(existing, incoming);

        JSONArray array = JSON.parseArray(merged);
        assertEquals("", findField(array, "gender").getString("fieldLabel"));
        assertEquals("male", findField(array, "gender").getString("value"));
        assertEquals("new", findField(array, "remarks").getString("value"));
        assertEquals("", findField(array, "remarks").getString("fieldLabel"));
        assertTrue(merged.contains("\"fieldName\":\"storeName\""));
    }

    private static JSONObject findField(JSONArray array, String fieldName)
    {
        for (int i = 0; i < array.size(); i++)
        {
            JSONObject item = array.getJSONObject(i);
            if (fieldName.equals(item.getString("fieldName")))
            {
                return item;
            }
        }
        throw new AssertionError("field not found: " + fieldName);
    }
}
