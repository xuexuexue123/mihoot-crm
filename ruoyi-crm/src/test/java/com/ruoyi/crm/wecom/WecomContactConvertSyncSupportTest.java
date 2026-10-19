package com.ruoyi.crm.wecom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.domain.dto.wecom.WecomFollowUserSnapshot;

class WecomContactConvertSyncSupportTest
{
    @Test
    void buildCrmTagJsonFromFollowTags_matchesLeadFormat()
    {
        JSONArray tags = new JSONArray();
        JSONObject t1 = new JSONObject();
        t1.put("tag_name", "VIP");
        tags.add(t1);
        JSONObject t2 = new JSONObject();
        t2.put("tag_name", "复诊");
        tags.add(t2);

        String json = WecomLeadEnrichSupport.buildCrmLeadTagJson(tags);
        assertNotNull(json);
        JSONArray parsed = JSONArray.parseArray(json);
        assertEquals(2, parsed.size());
        for (int i = 0; i < parsed.size(); i++)
        {
            JSONObject item = parsed.getJSONObject(i);
            assertNotNull(item.getString("id"));
            assertTrue(item.getString("id").matches("\\d{5}"));
            assertNotNull(item.getString("text"));
        }
    }

    @Test
    void resolveFollowUser_picksMatchingFollowUser()
    {
        String raw = "{"
                + "\"external_contact\":{\"name\":\"张三\"},"
                + "\"follow_user\":["
                + "{\"userid\":\"u1\",\"remark\":\"A\",\"tags\":[{\"tag_name\":\"T1\"}]},"
                + "{\"userid\":\"u2\",\"remark\":\"B\",\"tags\":[{\"tag_name\":\"T2\"}]}"
                + "]"
                + "}";
        WecomFollowUserSnapshot follow = WecomExternalContactJsonSupport.resolveFollowUser(raw, "u2");
        assertNotNull(follow);
        assertEquals("B", follow.getRemark());
        assertEquals("T2", follow.getTags().getJSONObject(0).getString("tag_name"));
    }
}
