package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.ruoyi.crm.domain.vo.EventAudienceRowVo;

class AudienceKeyAliasSupportTest
{
    @Test
    void lookupKeys_includesContactMobileAndMpAliases()
    {
        EventAudienceRowVo row = new EventAudienceRowVo();
        row.setAudienceKey("contact:contact-1");
        row.setContactId("contact-1");
        row.setMobilePhone("13800138000");
        row.setWechatMpOpenid("oABC123");

        List<String> keys = AudienceKeyAliasSupport.lookupKeys(row);

        assertEquals(3, keys.size());
        assertTrue(keys.contains("contact:contact-1"));
        assertTrue(keys.contains("mobile:13800138000"));
        assertTrue(keys.contains("mp:oABC123"));
    }

    @Test
    void matchRegistration_contactKeyOnMpRow()
    {
        EventAudienceRegistrationResolver resolver = new EventAudienceRegistrationResolver();
        Map<String, EventRegistrationBestRow> index = resolver.buildIndex(java.util.Collections.singletonList(
                registrationRow("contact:contact-1", "confirmed", "默认场次")));

        EventAudienceRowVo mpRow = new EventAudienceRowVo();
        mpRow.setAudienceKey("mp:oABC123");
        mpRow.setContactId("contact-1");
        mpRow.setWechatMpOpenid("oABC123");

        EventRegistrationBestRow best = resolver.match(mpRow, index);
        assertEquals("confirmed", best.getRegistrationStatus());
    }

    private static EventRegistrationBestRow registrationRow(String audienceKey, String status, String sessionName)
    {
        EventRegistrationBestRow row = new EventRegistrationBestRow();
        row.setAudienceKey(audienceKey);
        row.setRegistrationStatus(status);
        row.setSessionName(sessionName);
        return row;
    }
}
