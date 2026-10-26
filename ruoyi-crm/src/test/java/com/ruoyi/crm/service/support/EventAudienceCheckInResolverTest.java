package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.ruoyi.crm.domain.vo.EventAudienceRowVo;

class EventAudienceCheckInResolverTest
{
    private final EventAudienceCheckInResolver resolver = new EventAudienceCheckInResolver();

    @Test
    void enrichRow_exactAudienceKey_checkedIn()
    {
        Date checkedAt = new Date(1_700_000_000_000L);
        Map<String, Date> index = resolver.buildIndex(Collections.singletonList(agg("mobile:13800138000", checkedAt)));
        EventAudienceRowVo row = row("mobile:13800138000", "13800138000", Collections.singletonList("h5"),
                AudienceStatusResolver.REGISTRATION_PENDING);

        resolver.enrichRow(row, index, null);

        assertEquals("checked_in", row.getCheckInStatus());
        assertEquals(checkedAt, row.getCheckedInAt());
    }

    @Test
    void enrichRow_mobileAliasForH5Key_checkedIn()
    {
        Date checkedAt = new Date(1_700_000_000_000L);
        Map<String, Date> index = resolver.buildIndex(Collections.singletonList(agg("mobile:13800138000", checkedAt)));
        EventAudienceRowVo row = row("h5:lead-1", "13800138000", Collections.singletonList("h5"),
                AudienceStatusResolver.LEAD_ACTIVE);

        resolver.enrichRow(row, index, null);

        assertEquals("checked_in", row.getCheckInStatus());
        assertEquals(checkedAt, row.getCheckedInAt());
    }

    @Test
    void resolveCheckedInAt_crossSessionTakesMax()
    {
        Date earlier = new Date(1_700_000_000_000L);
        Date later = new Date(1_800_000_000_000L);
        List<EventCheckInAggRow> rows = Arrays.asList(
                agg("mobile:13800138000", earlier),
                agg("mobile:13800138000", later));
        Map<String, Date> index = resolver.buildIndex(rows);
        EventAudienceRowVo audienceRow = row("mobile:13800138000", "13800138000", Collections.singletonList("h5"),
                AudienceStatusResolver.REGISTRATION_PENDING);

        Date resolved = resolver.resolveCheckedInAt(audienceRow, index);

        assertEquals(later, resolved);
    }

    @Test
    void enrichRow_h5NotCheckedIn_withActiveRegistration()
    {
        EventRegistrationBestRow registration = reg("mobile:13800138000", "confirmed", "默认场次");
        EventAudienceRowVo row = row("mobile:13800138000", "13800138000", Collections.singletonList("h5"),
                AudienceStatusResolver.REGISTRATION_PENDING);

        resolver.enrichRow(row, Collections.emptyMap(), registration);

        assertEquals("not_checked_in", row.getCheckInStatus());
        assertNull(row.getCheckedInAt());
    }

    @Test
    void enrichRow_channelOnly_noRegistration_checkInNull()
    {
        EventAudienceRowVo row = row("mp:openid-1", null, Collections.singletonList("mp"),
                AudienceStatusResolver.CHANNEL_ONLY);

        resolver.enrichRow(row, Collections.emptyMap(), null);

        assertNull(row.getCheckInStatus());
        assertNull(row.getCheckedInAt());
    }

    private static EventCheckInAggRow agg(String audienceKey, Date checkedInAt)
    {
        EventCheckInAggRow row = new EventCheckInAggRow();
        row.setAudienceKey(audienceKey);
        row.setCheckedInAt(checkedInAt);
        return row;
    }

    private static EventRegistrationBestRow reg(String audienceKey, String status, String sessionName)
    {
        EventRegistrationBestRow row = new EventRegistrationBestRow();
        row.setAudienceKey(audienceKey);
        row.setRegistrationStatus(status);
        row.setSessionName(sessionName);
        return row;
    }

    private static EventAudienceRowVo row(String audienceKey, String mobile, List<String> channels, String status)
    {
        EventAudienceRowVo row = new EventAudienceRowVo();
        row.setAudienceKey(audienceKey);
        row.setMobilePhone(mobile);
        row.setChannels(channels);
        row.setAudienceStatus(status);
        return row;
    }
}
