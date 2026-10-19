package com.ruoyi.crm.wecom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.dto.wecom.WecomExternalContactProfile;
import com.ruoyi.crm.domain.dto.wecom.WecomFollowUserSnapshot;

class WecomLeadEnrichSupportTest
{
    @Test
    void resolveLeadType_mapsWecomTypeToCrmLeadType()
    {
        assertEquals("business", WecomLeadEnrichSupport.resolveLeadType(2));
        assertEquals("individual", WecomLeadEnrichSupport.resolveLeadType(1));
        assertEquals("individual", WecomLeadEnrichSupport.resolveLeadType(null));
    }

    @Test
    void buildCrmLeadTagJson_convertsTagNameToTextWithFiveDigitId()
    {
        JSONArray wecomTags = new JSONArray();
        JSONObject t1 = new JSONObject();
        t1.put("group_name", "来源");
        t1.put("tag_name", "大众点评");
        t1.put("type", 1);
        wecomTags.add(t1);
        JSONObject t2 = new JSONObject();
        t2.put("tag_name", "新线索");
        wecomTags.add(t2);

        String json = WecomLeadEnrichSupport.buildCrmLeadTagJson(wecomTags);
        assertNotNull(json);
        JSONArray arr = JSON.parseArray(json);
        assertEquals(2, arr.size());

        JSONObject first = arr.getJSONObject(0);
        assertEquals("大众点评", first.getString("text"));
        assertTrue(first.getString("id").matches("\\d{5}"));

        JSONObject second = arr.getJSONObject(1);
        assertEquals("新线索", second.getString("text"));
        assertTrue(second.getString("id").matches("\\d{5}"));
    }

    @Test
    void buildCrmLeadTagJson_deduplicatesByTagName()
    {
        JSONArray wecomTags = new JSONArray();
        JSONObject t1 = new JSONObject();
        t1.put("tag_name", "重点");
        wecomTags.add(t1);
        JSONObject t2 = new JSONObject();
        t2.put("tag_name", "重点");
        wecomTags.add(t2);

        String json = WecomLeadEnrichSupport.buildCrmLeadTagJson(wecomTags);
        assertEquals(1, JSON.parseArray(json).size());
    }

    @Test
    void resolveFollowUser_readsFollowInfoFromBatchJson()
    {
        String raw = "{\"external_contact\":{\"external_userid\":\"wm1\",\"name\":\"张三\"},"
                + "\"follow_info\":{\"userid\":\"staff1\",\"remark\":\"张总\","
                + "\"tags\":[{\"tag_name\":\"大众点评\"}]}}";
        WecomFollowUserSnapshot snap = WecomExternalContactJsonSupport.resolveFollowUser(raw, "staff1");
        assertNotNull(snap);
        assertEquals("张总", snap.getRemark());
        assertEquals(1, snap.getTags().size());
    }

    @Test
    void resolveFollowUser_readsFollowUserArrayFromGetJson()
    {
        String raw = "{\"external_contact\":{\"name\":\"李四\"},"
                + "\"follow_user\":[{\"userid\":\"staff2\",\"remark\":\"李总\","
                + "\"tags\":[{\"tag_name\":\"直播\"}]}]}";
        WecomFollowUserSnapshot snap = WecomExternalContactJsonSupport.resolveFollowUser(raw, "staff2");
        assertNotNull(snap);
        assertEquals("李总", snap.getRemark());
        assertEquals("直播", snap.getTags().getJSONObject(0).getString("tag_name"));
    }

    @Test
    void parseProfileFromRaw_extractsCorpAndPosition()
    {
        String raw = "{\"external_contact\":{\"external_userid\":\"wm1\",\"name\":\"王五\",\"type\":2,"
                + "\"gender\":1,\"position\":\"经理\",\"corp_name\":\"ACME\",\"corp_full_name\":\"ACME Ltd\"}}";
        WecomExternalContactProfile profile = WecomExternalContactProfileResolver.parseProfileFromRaw(raw, "wm1");
        assertNotNull(profile);
        assertEquals(Integer.valueOf(2), profile.getType());
        assertEquals("经理", profile.getPosition());
        assertEquals("ACME Ltd", profile.getCorpFullName());
    }

    @Test
    void enrichNewLead_setsLeadFieldsWithoutSpringContext()
    {
        WecomLeadEnrichSupport support = new WecomLeadEnrichSupport();

        WecomExternalContactProfile profile = new WecomExternalContactProfile();
        profile.setName("企微昵称");
        profile.setType(1);
        profile.setAvatar("https://avatar.example/a.png");
        profile.setGender(2);
        profile.setPosition("总监");
        profile.setCorpFullName("示例科技");

        WecomFollowUserSnapshot follow = new WecomFollowUserSnapshot();
        follow.setRemark("王总");
        JSONArray tags = new JSONArray();
        JSONObject tag = new JSONObject();
        tag.put("tag_name", "大众点评");
        tags.add(tag);
        follow.setTags(tags);

        CrmLead lead = new CrmLead();
        support.enrichNewLead(lead, profile, follow, "wm_ext_1", "staff1", null);

        assertEquals("individual", lead.getLeadType());
        assertEquals("王总", lead.getLeadName());
        assertEquals("王总", lead.getContactName());
        assertEquals("https://avatar.example/a.png", lead.getAvatar());
        assertEquals("示例科技", lead.getFullName());
        assertEquals("wm_ext_1", lead.getWecomExternalUserid());
        assertEquals("staff1", lead.getWecomFollowUserid());
        assertNotNull(lead.getTag());
        assertTrue(lead.getTag().contains("大众点评"));
        assertNull(lead.getCustomField());
    }
}
