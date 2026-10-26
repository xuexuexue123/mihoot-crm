package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ruoyi.crm.domain.dto.EventAudienceQuery;
import com.ruoyi.crm.domain.vo.EventAudienceRowVo;

public class AudienceMergerTest
{
    private AudienceMerger merger;

    @BeforeEach
    public void setUp() throws Exception
    {
        merger = new AudienceMerger();
        java.lang.reflect.Field field = AudienceMerger.class.getDeclaredField("audienceKeyResolver");
        field.setAccessible(true);
        field.set(merger, new EventAudienceKeyResolver());
    }

    @Test
    public void mergesH5AndMpByOpenIdWithoutMobile()
    {
        AudienceSourceRow h5 = h5Row("lead-1", "仅姓名", null, "Initial");
        h5.setWechatMpOpenid("oABC123");
        h5.setWechatMpAppId("wx_app_1");
        AudienceSourceRow mp = mpRow("oABC123", "wx_app_1", null);

        List<EventAudienceRowVo> merged = merger.merge(
                Collections.singletonList(h5),
                Collections.emptyList(),
                Collections.singletonList(mp),
                Collections.emptyMap(),
                Collections.emptyMap(),
                Collections.emptyMap(),
                false);

        assertEquals(1, merged.size());
        EventAudienceRowVo row = merged.get(0);
        assertEquals(2, row.getChannels().size());
        assertTrue(row.getChannels().contains("h5"));
        assertTrue(row.getChannels().contains("mp"));
        assertEquals("h5:lead-1", row.getAudienceKey());
        assertEquals("registration_pending", row.getAudienceStatus());
        assertEquals("oABC123", row.getWechatMpOpenid());
    }

    @Test
    public void doesNotMergeMpOpenIdAcrossDifferentApps()
    {
        AudienceSourceRow h5 = h5Row("lead-1", "A", null, "Initial");
        h5.setWechatMpOpenid("oABC123");
        h5.setWechatMpAppId("wx_app_1");
        AudienceSourceRow mp = mpRow("oABC123", "wx_app_2", null);

        List<EventAudienceRowVo> merged = merger.merge(
                Collections.singletonList(h5),
                Collections.emptyList(),
                Collections.singletonList(mp),
                Collections.emptyMap(),
                Collections.emptyMap(),
                Collections.emptyMap(),
                false);

        assertEquals(2, merged.size());
    }

    @Test
    public void mpMergeKey_includesAppIdWhenPresent()
    {
        AudienceSourceRow row = new AudienceSourceRow();
        row.setWechatMpOpenid("o1");
        row.setWechatMpAppId("wx1");
        assertEquals("wx1:o1", AudienceMerger.mpMergeKey(row));
        row.setWechatMpAppId(null);
        assertEquals("o1", AudienceMerger.mpMergeKey(row));
    }

    @Test
    public void mergesSameMobileAcrossH5AndWecom()
    {
        AudienceSourceRow h5 = h5Row("lead-1", "张三", "13800138000", "Initial");
        h5.setTouchedAt(date(2026, 6, 1, 10, 0));
        h5.setLastActivityAt(date(2026, 6, 1, 10, 0));
        AudienceSourceRow wecom = wecomRow("wm001", "13800138000");
        wecom.setTouchedAt(date(2026, 6, 2, 15, 30));
        wecom.setLastActivityAt(date(2026, 6, 2, 15, 30));

        List<EventAudienceRowVo> merged = merger.merge(
                Collections.singletonList(h5),
                Collections.singletonList(wecom),
                Collections.emptyList(),
                Collections.emptyMap(),
                Collections.emptyMap(),
                Collections.emptyMap(),
                false);

        assertEquals(1, merged.size());
        EventAudienceRowVo row = merged.get(0);
        assertEquals(2, row.getChannels().size());
        assertTrue(row.getChannels().contains("h5"));
        assertTrue(row.getChannels().contains("wecom"));
        assertEquals("mobile:13800138000", row.getAudienceKey());
        assertEquals(date(2026, 6, 1, 10, 0), row.getFirstTouchAt());
        assertEquals(date(2026, 6, 2, 15, 30), row.getLastActivityAt());
    }

    @Test
    public void mergesByContactId()
    {
        AudienceSourceRow h5 = h5Row("lead-1", "张三", null, "Converted");
        h5.setContactId("contact-1");
        AudienceSourceRow mp = mpRow("openid-1", "contact-1");

        Map<String, String> names = new HashMap<>();
        names.put("contact-1", "张三联系人");

        List<EventAudienceRowVo> merged = merger.merge(
                Collections.singletonList(h5),
                Collections.emptyList(),
                Collections.singletonList(mp),
                Collections.emptyMap(),
                Collections.emptyMap(),
                names,
                false);

        assertEquals(1, merged.size());
        assertEquals("contact:contact-1", merged.get(0).getAudienceKey());
        assertEquals("张三", merged.get(0).getDisplayName());
    }

    @Test
    public void filtersByChannelAndStatus()
    {
        AudienceSourceRow h5 = h5Row("lead-1", "A", "13800138001", "Initial");
        AudienceSourceRow wecom = wecomRow("wm002", null);
        List<EventAudienceRowVo> merged = merger.merge(
                Collections.singletonList(h5),
                Collections.singletonList(wecom),
                Collections.emptyList(),
                Collections.emptyMap(),
                Collections.emptyMap(),
                Collections.emptyMap(),
                false);
        assertEquals(2, merged.size());

        EventAudienceQuery query = new EventAudienceQuery();
        query.setChannels("h5");
        List<EventAudienceRowVo> filtered = merger.applyFilters(merged, query);
        assertEquals(1, filtered.size());
        assertEquals("lead-1", filtered.get(0).getLeadId());

        List<EventAudienceRowVo> wecomOnly = merger.merge(
                Collections.emptyList(),
                Collections.singletonList(wecom),
                Collections.emptyList(),
                Collections.emptyMap(),
                Collections.emptyMap(),
                Collections.emptyMap(),
                false);
        query.setChannels(null);
        query.setAudienceStatus(AudienceStatusResolver.CHANNEL_ONLY);
        filtered = merger.applyFilters(wecomOnly, query);
        assertEquals(1, filtered.size());
        assertEquals("wm002", filtered.get(0).getWecomExternalUserid());
    }

    @Test
    public void includesChannelDetailsWhenRequested()
    {
        AudienceSourceRow h5 = h5Row("lead-1", "A", "13800138001", "Initial");
        h5.setRegistrationStatus("confirmed");
        List<EventAudienceRowVo> merged = merger.merge(
                Collections.singletonList(h5),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyMap(),
                Collections.emptyMap(),
                Collections.emptyMap(),
                true);
        assertEquals(1, merged.get(0).getChannelDetails().size());
        assertEquals("h5", merged.get(0).getChannelDetails().get(0).getChannel());
        assertEquals("confirmed", merged.get(0).getChannelDetails().get(0).getRawStatus());
        assertEquals("reg-lead-1", merged.get(0).getChannelDetails().get(0).getSourceId());
    }

    @Test
    public void mergesWebsiteRegistrationChannel()
    {
        AudienceSourceRow website = websiteRow("lead-w1", "李四", "13800138002", "Initial");
        website.setRegistrationStatus("confirmed");
        website.setSourceId("reg-website-1");

        List<EventAudienceRowVo> merged = merger.merge(
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.singletonList(website),
                Collections.emptyMap(),
                Collections.emptyMap(),
                Collections.emptyMap(),
                true);

        assertEquals(1, merged.size());
        EventAudienceRowVo row = merged.get(0);
        assertTrue(row.getChannels().contains("website"));
        assertEquals("lead-w1", row.getLeadId());
        assertEquals("registration_pending", row.getAudienceStatus());
        assertEquals("website", row.getChannelDetails().get(0).getChannel());
        assertEquals("reg-website-1", row.getChannelDetails().get(0).getSourceId());
        assertEquals("confirmed", row.getChannelDetails().get(0).getRawStatus());
    }

    @Test
    public void h5ContactKnown_usesRegistrationIdAsSourceId()
    {
        AudienceSourceRow h5 = h5Row(null, "陈大发", "13922009933", null);
        h5.setSourceId("reg-uuid-123");
        h5.setContactId("contact-1");
        h5.setRegistrationStatus("confirmed");

        List<EventAudienceRowVo> merged = merger.merge(
                Collections.singletonList(h5),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyMap(),
                Collections.emptyMap(),
                Collections.emptyMap(),
                true);

        assertEquals(1, merged.size());
        assertEquals("reg-uuid-123", merged.get(0).getChannelDetails().get(0).getSourceId());
        assertEquals(AudienceStatusResolver.CONTACT_KNOWN, merged.get(0).getAudienceStatus());
    }

    private static AudienceSourceRow h5Row(String leadId, String name, String mobile, String status)
    {
        AudienceSourceRow row = new AudienceSourceRow();
        row.setChannel("h5");
        row.setSourceId(leadId != null ? "reg-" + leadId : "reg-unknown");
        row.setLeadId(leadId);
        row.setDisplayName(name);
        row.setMobilePhone(mobile);
        row.setLeadStatus(status);
        row.setRegistrationStatus("confirmed");
        row.setTouchedAt(new Date());
        row.setLastActivityAt(new Date());
        return row;
    }

    private static AudienceSourceRow websiteRow(String leadId, String name, String mobile, String status)
    {
        AudienceSourceRow row = h5Row(leadId, name, mobile, status);
        row.setChannel("website");
        return row;
    }

    private static AudienceSourceRow wecomRow(String externalUserid, String mobile)
    {
        AudienceSourceRow row = new AudienceSourceRow();
        row.setChannel("wecom");
        row.setSourceId(externalUserid);
        row.setWecomExternalUserid(externalUserid);
        row.setMobilePhone(mobile);
        row.setTouchedAt(new Date());
        row.setLastActivityAt(new Date());
        return row;
    }

    private static AudienceSourceRow mpRow(String openid, String contactId)
    {
        return mpRow(openid, null, contactId);
    }

    private static AudienceSourceRow mpRow(String openid, String appId, String contactId)
    {
        AudienceSourceRow row = new AudienceSourceRow();
        row.setChannel("mp");
        row.setSourceId(openid);
        row.setWechatMpOpenid(openid);
        row.setWechatMpAppId(appId);
        row.setContactId(contactId);
        row.setMpNickname("粉丝");
        row.setTouchedAt(new Date());
        row.setLastActivityAt(new Date());
        return row;
    }

    @SuppressWarnings("deprecation")
    private static Date date(int year, int month, int day, int hour, int minute)
    {
        return new Date(year - 1900, month - 1, day, hour, minute, 0);
    }
}
