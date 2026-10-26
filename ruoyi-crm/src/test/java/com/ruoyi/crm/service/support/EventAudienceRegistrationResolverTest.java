package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.ruoyi.crm.domain.vo.EventAudienceRowVo;

class EventAudienceRegistrationResolverTest
{
    private final EventAudienceRegistrationResolver resolver = new EventAudienceRegistrationResolver();

    @Test
    void enrichRow_exactKey_returnsBestRegistration()
    {
        Map<String, EventRegistrationBestRow> index = resolver.buildIndex(Collections.singletonList(
                reg("mobile:13800138000", "confirmed", "默认场次")));
        EventAudienceRowVo row = row("mobile:13800138000", "13800138000");

        resolver.enrichRow(row, index);

        assertEquals("confirmed", row.getRegistrationStatus());
        assertEquals("默认场次", row.getRegistrationSessionName());
    }

    @Test
    void match_mobileAliasForH5Key()
    {
        Map<String, EventRegistrationBestRow> index = resolver.buildIndex(Collections.singletonList(
                reg("mobile:13800138000", "confirmed", "默认场次")));
        EventAudienceRowVo row = row("h5:lead-1", "13800138000");

        EventRegistrationBestRow best = resolver.match(row, index);

        assertEquals("confirmed", best.getRegistrationStatus());
        assertEquals("默认场次", best.getSessionName());
    }

    @Test
    void match_aliasKeys_prefersConfirmedOverWaitlist()
    {
        List<EventRegistrationBestRow> rows = Arrays.asList(
                reg("h5:lead-1", "waitlist", "候补场"),
                reg("mobile:13800138000", "confirmed", "默认场次"));
        Map<String, EventRegistrationBestRow> index = resolver.buildIndex(rows);
        EventAudienceRowVo row = row("h5:lead-1", "13800138000");

        EventRegistrationBestRow best = resolver.match(row, index);

        assertEquals("confirmed", best.getRegistrationStatus());
        assertEquals("默认场次", best.getSessionName());
    }

    @Test
    void buildIndex_sameKey_keepsBestStatus()
    {
        List<EventRegistrationBestRow> rows = Arrays.asList(
                reg("mobile:13800138000", "waitlist", "候补场"),
                reg("mobile:13800138000", "confirmed", "默认场次"));
        Map<String, EventRegistrationBestRow> index = resolver.buildIndex(rows);

        assertEquals("confirmed", index.get("mobile:13800138000").getRegistrationStatus());
    }

    @Test
    void enrichRow_noMatch_setsNull()
    {
        EventAudienceRowVo row = row("mp:openid-1", null);

        resolver.enrichRow(row, Collections.emptyMap());

        assertNull(row.getRegistrationStatus());
        assertNull(row.getRegistrationSessionName());
    }

    private static EventRegistrationBestRow reg(String audienceKey, String status, String sessionName)
    {
        EventRegistrationBestRow row = new EventRegistrationBestRow();
        row.setAudienceKey(audienceKey);
        row.setRegistrationStatus(status);
        row.setSessionName(sessionName);
        return row;
    }

    private static EventAudienceRowVo row(String audienceKey, String mobile)
    {
        EventAudienceRowVo row = new EventAudienceRowVo();
        row.setAudienceKey(audienceKey);
        row.setMobilePhone(mobile);
        return row;
    }
}
