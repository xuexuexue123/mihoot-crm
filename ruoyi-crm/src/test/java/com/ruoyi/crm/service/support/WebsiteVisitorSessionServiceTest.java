package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmWebsitePageView;
import com.ruoyi.crm.domain.CrmWebsiteVisitorSession;
import com.ruoyi.crm.domain.dto.ActivityEventDTO;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.mapper.CrmActivityMapper;
import com.ruoyi.crm.mapper.CrmWebsitePageViewMapper;
import com.ruoyi.crm.mapper.CrmWebsiteVisitorSessionMapper;

@ExtendWith(MockitoExtension.class)
class WebsiteVisitorSessionServiceTest
{
    @Mock
    private CrmWebsiteVisitorSessionMapper sessionMapper;
    @Mock
    private CrmWebsitePageViewMapper pageViewMapper;
    @Mock
    private CrmActivityMapper activityMapper;
    @Mock
    private ActivityEventProducer activityEventProducer;
    @Mock
    private WebsiteBrowseHistorySupport browseHistorySupport;
    @Mock
    private RedisTemplate<Object, Object> redisTemplate;
    @Mock
    private ValueOperations<Object, Object> valueOperations;

    @InjectMocks
    private WebsiteVisitorSessionService service;

    @Test
    void recordPageview_unbound_insertsWithoutActivity()
    {
        stubRateLimitOk();
        when(sessionMapper.selectBySessionId("sess-1")).thenReturn(unboundSession());
        when(pageViewMapper.countRecentDuplicate(eq("sess-1"), eq("/a"), any(Date.class), eq(30)))
                .thenReturn(0);

        boolean[] result = service.recordPageview("sess-1", "/a", "desc", "/", new Date());

        assertTrue(result[0]);
        assertEquals(false, result[1]);
        verify(pageViewMapper).insertPageView(any(CrmWebsitePageView.class));
        verify(activityEventProducer, never()).produce(any());
    }

    @Test
    void recordPageview_bound_projectsActivity()
    {
        stubRateLimitOk();
        when(sessionMapper.selectBySessionId("sess-1")).thenReturn(boundSession("Lead", "lead-1"));
        when(pageViewMapper.countRecentDuplicate(eq("sess-1"), eq("/a"), any(Date.class), eq(30)))
                .thenReturn(0);
        when(activityMapper.selectActivityIdByChannelEventId(eq("website"), anyString()))
                .thenReturn("act-1");

        boolean[] result = service.recordPageview("sess-1", "/a", null, null, new Date());

        assertTrue(result[0]);
        assertTrue(result[1]);
        verify(activityEventProducer).produce(any(ActivityEventDTO.class));
        verify(pageViewMapper).markSynced(anyString(), eq("act-1"));
    }

    @Test
    void recordPageview_dedupeWithin30s_skipsInsert()
    {
        stubRateLimitOk();
        when(sessionMapper.selectBySessionId("sess-1")).thenReturn(unboundSession());
        when(pageViewMapper.countRecentDuplicate(eq("sess-1"), eq("/a"), any(Date.class), eq(30)))
                .thenReturn(1);

        boolean[] result = service.recordPageview("sess-1", "/a", null, null, new Date());

        assertTrue(result[0]);
        verify(pageViewMapper, never()).insertPageView(any());
        verify(activityEventProducer, never()).produce(any());
    }

    @Test
    void stitch_nullSession_skips()
    {
        service.stitch(null, null, "Lead", "lead-1");
        verify(sessionMapper, never()).upsertSession(any());
        verify(sessionMapper, never()).bindSubject(anyString(), anyString(), anyString());
    }

    @Test
    void stitch_bindsAndProjectsUnsynced_withBrowseHistoryFallback()
    {
        WebsiteBrowseHistorySupport.BrowseEntry entry = new WebsiteBrowseHistorySupport.BrowseEntry();
        entry.setU("/fallback");
        entry.setT(1_700_000_000_000L);
        entry.setD("fallback page");
        when(browseHistorySupport.parseBrowseHistory("hist")).thenReturn(Collections.singletonList(entry));
        when(pageViewMapper.countNearDuplicate(eq("sess-1"), eq("/fallback"), any(Date.class), eq(60)))
                .thenReturn(0);

        CrmWebsitePageView unsynced = new CrmWebsitePageView();
        unsynced.setId("pv-1");
        unsynced.setSessionId("sess-1");
        unsynced.setPageUrl("/fallback");
        unsynced.setPageDescription("fallback page");
        unsynced.setViewedAt(new Date(1_700_000_000_000L));
        when(pageViewMapper.selectUnsyncedBySession("sess-1"))
                .thenReturn(Collections.singletonList(unsynced));
        when(activityMapper.selectActivityIdByChannelEventId(eq("website"), anyString()))
                .thenReturn("act-9");

        service.stitch("sess-1", "hist", "Lead", "lead-1");

        verify(sessionMapper).upsertSession(any(CrmWebsiteVisitorSession.class));
        verify(sessionMapper).bindSubject("sess-1", "Lead", "lead-1");
        verify(pageViewMapper).insertPageView(any(CrmWebsitePageView.class));
        ArgumentCaptor<ActivityEventDTO> captor = ArgumentCaptor.forClass(ActivityEventDTO.class);
        verify(activityEventProducer).produce(captor.capture());
        assertEquals("website.page.view", captor.getValue().getEventType());
        assertEquals("Lead", captor.getValue().getSubjectType());
        assertEquals("lead-1", captor.getValue().getSubjectId());
        verify(pageViewMapper).markSynced("pv-1", "act-9");
    }

    @Test
    void recordPageview_blankPageUrl_throws400()
    {
        assertThrows(ServiceException.class,
                () -> service.recordPageview("sess-1", "  ", null, null, null));
    }

    @Test
    void migrateSubjectLeadToContact_delegatesToMapper()
    {
        when(sessionMapper.migrateSubjectLeadToContact("lead-1", "contact-1")).thenReturn(2);
        service.migrateSubjectLeadToContact("lead-1", "contact-1");
        verify(sessionMapper).migrateSubjectLeadToContact("lead-1", "contact-1");
    }

    private void stubRateLimitOk()
    {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(1L);
        when(redisTemplate.expire(anyString(), eq(60L), eq(TimeUnit.SECONDS))).thenReturn(true);
    }

    private static CrmWebsiteVisitorSession unboundSession()
    {
        CrmWebsiteVisitorSession session = new CrmWebsiteVisitorSession();
        session.setSessionId("sess-1");
        return session;
    }

    private static CrmWebsiteVisitorSession boundSession(String subjectType, String subjectId)
    {
        CrmWebsiteVisitorSession session = unboundSession();
        session.setSubjectType(subjectType);
        session.setSubjectId(subjectId);
        session.setBoundAt(new Date());
        return session;
    }
}
