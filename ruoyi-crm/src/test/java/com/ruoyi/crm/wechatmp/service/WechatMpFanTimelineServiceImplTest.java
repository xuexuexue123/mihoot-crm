package com.ruoyi.crm.wechatmp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmActivity;
import com.ruoyi.crm.wechatmp.domain.ActivityUnmatched;
import com.ruoyi.crm.wechatmp.dto.WechatMpFanTimelineItem;
import com.ruoyi.crm.wechatmp.mapper.WechatMpFanTimelineMapper;
import com.ruoyi.crm.wechatmp.service.impl.WechatMpFanTimelineServiceImpl;

@ExtendWith(MockitoExtension.class)
class WechatMpFanTimelineServiceImplTest
{
    private static final String APP_ID = "wxTestApp";
    private static final String OPEN_ID = "oTestOpenId";

    @Mock
    private WechatMpFanTimelineMapper timelineMapper;

    @InjectMocks
    private WechatMpFanTimelineServiceImpl timelineService;

    @Test
    void listTimeline_rejectsMissingAppId()
    {
        assertThrows(ServiceException.class,
            () -> timelineService.listTimeline("", OPEN_ID, 1, 30));
    }

    @Test
    void listTimeline_rejectsMissingOpenId()
    {
        assertThrows(ServiceException.class,
            () -> timelineService.listTimeline(APP_ID, "", 1, 30));
    }

    @Test
    void listTimeline_mergesSortsAndPaginates()
    {
        CrmActivity older = activityAt(date(2026, 6, 1, 10, 0), "pulse-old");
        CrmActivity newer = activityAt(date(2026, 7, 2, 10, 0), "pulse-new");

        ActivityUnmatched unmatched = new ActivityUnmatched();
        unmatched.setId(1L);
        unmatched.setPulseId("pulse-unmatched");
        unmatched.setIdentityType("wechat_mp_openid");
        unmatched.setIdentityValue(OPEN_ID);
        unmatched.setCreateTime(LocalDateTime.of(2026, 7, 1, 9, 0));

        when(timelineMapper.selectActivitiesByOpenId(eq(APP_ID), eq(OPEN_ID)))
            .thenReturn(Arrays.asList(older, newer));
        when(timelineMapper.selectUnmatchedByOpenId(eq(APP_ID), eq(OPEN_ID)))
            .thenReturn(Collections.singletonList(unmatched));

        TableDataInfo page1 = timelineService.listTimeline(APP_ID, OPEN_ID, 1, 2);
        assertEquals(3, page1.getTotal());
        assertEquals(2, page1.getRows().size());

        @SuppressWarnings("unchecked")
        List<WechatMpFanTimelineItem> rows = (List<WechatMpFanTimelineItem>) page1.getRows();
        assertEquals("activity", rows.get(0).getSource());
        assertEquals("pulse-new", rows.get(0).getActivity().getPulseId());
        assertEquals("unmatched", rows.get(1).getSource());
        assertEquals("pulse-unmatched", rows.get(1).getUnmatched().getPulseId());

        TableDataInfo page2 = timelineService.listTimeline(APP_ID, OPEN_ID, 2, 2);
        assertEquals(3, page2.getTotal());
        assertEquals(1, page2.getRows().size());

        @SuppressWarnings("unchecked")
        List<WechatMpFanTimelineItem> page2Rows = (List<WechatMpFanTimelineItem>) page2.getRows();
        assertEquals("activity", page2Rows.get(0).getSource());
        assertEquals("pulse-old", page2Rows.get(0).getActivity().getPulseId());
    }

    @Test
    void listTimeline_emptyResult()
    {
        when(timelineMapper.selectActivitiesByOpenId(eq(APP_ID), eq(OPEN_ID)))
            .thenReturn(Collections.emptyList());
        when(timelineMapper.selectUnmatchedByOpenId(eq(APP_ID), eq(OPEN_ID)))
            .thenReturn(Collections.emptyList());

        TableDataInfo data = timelineService.listTimeline(APP_ID, OPEN_ID, 1, 30);
        assertEquals(0, data.getTotal());
        assertEquals(0, data.getRows().size());
    }

    @Test
    void listTimeline_capsPageSizeAt100()
    {
        when(timelineMapper.selectActivitiesByOpenId(eq(APP_ID), eq(OPEN_ID)))
            .thenReturn(Collections.emptyList());
        when(timelineMapper.selectUnmatchedByOpenId(eq(APP_ID), eq(OPEN_ID)))
            .thenReturn(Collections.emptyList());

        TableDataInfo data = timelineService.listTimeline(APP_ID, OPEN_ID, 1, 500);
        assertNotNull(data);
        assertEquals(0, data.getTotal());
    }

    private static CrmActivity activityAt(Date startedDate, String pulseId)
    {
        CrmActivity activity = new CrmActivity();
        activity.setPulseId(pulseId);
        activity.setStartedDate(startedDate);
        activity.setCreateTime(startedDate);
        activity.setEventType("channel.wechat_mp.follow");
        activity.setChannel("wechat_mp");
        return activity;
    }

    private static Date date(int year, int month, int day, int hour, int minute)
    {
        @SuppressWarnings("deprecation")
        Date d = new Date(year - 1900, month - 1, day, hour, minute, 0);
        return d;
    }
}
