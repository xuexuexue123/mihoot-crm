package com.ruoyi.crm.service.support;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmWebsitePageView;
import com.ruoyi.crm.domain.CrmWebsiteVisitorSession;
import com.ruoyi.crm.domain.dto.ActivityEventDTO;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.mapper.CrmActivityMapper;
import com.ruoyi.crm.mapper.CrmWebsitePageViewMapper;
import com.ruoyi.crm.mapper.CrmWebsiteVisitorSessionMapper;
import com.ruoyi.crm.service.support.WebsiteBrowseHistorySupport.BrowseEntry;

import lombok.extern.slf4j.Slf4j;

/**
 * 官网访客 Session / Page View：实时入库、报名缝合投影、Lead 转化主体迁移。
 */
@Slf4j
@Service
public class WebsiteVisitorSessionService
{
    private static final String CHANNEL = "website";
    private static final String EVENT_TYPE = "website.page.view";
    private static final String EVENT_CATEGORY = "engagement";
    private static final int IMPORTANCE = 5;
    private static final String SOURCE = "website";

    private static final int PAGE_URL_MAX = 2048;
    private static final int DEDUPE_SECONDS = 30;
    private static final int FALLBACK_NEAR_SECONDS = 60;
    private static final int SESSION_RATE_LIMIT = 30;
    private static final int SESSION_RATE_WINDOW_SECONDS = 60;
    private static final String SESSION_RATE_KEY_PREFIX = "rate_limit:website_pageview:session:";

    @Autowired
    private CrmWebsiteVisitorSessionMapper sessionMapper;

    @Autowired
    private CrmWebsitePageViewMapper pageViewMapper;

    @Autowired
    private CrmActivityMapper activityMapper;

    @Autowired
    private ActivityEventProducer activityEventProducer;

    @Autowired
    private WebsiteBrowseHistorySupport browseHistorySupport;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * 在已绑定租户数据源的前提下记录一次 pageview。
     *
     * @return [accepted, bound]
     */
    public boolean[] recordPageview(String sessionId, String pageUrl, String pageDescription,
            String referrer, Date viewedAt)
    {
        validateSessionAndUrl(sessionId, pageUrl);
        enforceSessionRateLimit(sessionId);

        Date effectiveViewedAt = viewedAt != null ? viewedAt : new Date();
        upsertAnonymousSession(sessionId);

        CrmWebsiteVisitorSession session = sessionMapper.selectBySessionId(sessionId);
        boolean bound = session != null && session.isBound();

        if (pageViewMapper.countRecentDuplicate(sessionId, pageUrl, effectiveViewedAt, DEDUPE_SECONDS) > 0)
        {
            return new boolean[] { true, bound };
        }

        CrmWebsitePageView pageView = new CrmWebsitePageView();
        pageView.setId(UUID.randomUUID().toString());
        pageView.setSessionId(sessionId);
        pageView.setPageUrl(pageUrl);
        pageView.setPageDescription(trimToNull(pageDescription, 512));
        pageView.setReferrer(trimToNull(referrer, 2048));
        pageView.setViewedAt(effectiveViewedAt);
        pageView.setSyncedToActivity(false);
        pageViewMapper.insertPageView(pageView);

        if (bound)
        {
            projectAndMark(pageView, session.getSubjectType(), session.getSubjectId());
        }

        return new boolean[] { true, bound };
    }

    /**
     * 报名 / 问卷提交后：绑定 session 并投影未同步 page_view（含 _browseHistory 兜底补洞）。
     * 独立事务：投影失败不拖垮外层报名/问卷事务。
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void stitch(String sessionId, String browseHistory, String subjectType, String subjectId)
    {
        if (!StringUtils.hasText(sessionId))
        {
            return;
        }
        if (!StringUtils.hasText(subjectType) || !StringUtils.hasText(subjectId))
        {
            log.warn("Skip visitor stitch: missing subjectType or subjectId");
            return;
        }

        String effectiveSessionId = sessionId.trim();
        upsertAnonymousSession(effectiveSessionId);
        sessionMapper.bindSubject(effectiveSessionId, subjectType, subjectId);

        insertBrowseHistoryFallback(effectiveSessionId, browseHistory);

        List<CrmWebsitePageView> unsynced = pageViewMapper.selectUnsyncedBySession(effectiveSessionId);
        int projected = 0;
        for (CrmWebsitePageView pageView : unsynced)
        {
            try
            {
                projectAndMark(pageView, subjectType, subjectId);
                projected++;
            }
            catch (Exception e)
            {
                log.warn("Failed to project page_view to activity: pageViewId={}, sessionId={}",
                        pageView.getId(), effectiveSessionId, e);
            }
        }

        log.info("Visitor session stitched: sessionId={}, subjectType={}, subjectId={}, projected={}/{}",
                effectiveSessionId, subjectType, subjectId, projected, unsynced.size());
    }

    /**
     * Lead → Contact 时迁移 visitor_session 绑定主体。
     */
    public void migrateSubjectLeadToContact(String oldLeadId, String newContactId)
    {
        if (!StringUtils.hasText(oldLeadId) || !StringUtils.hasText(newContactId))
        {
            return;
        }
        int rows = sessionMapper.migrateSubjectLeadToContact(oldLeadId, newContactId);
        if (rows > 0)
        {
            log.info("Migrated visitor sessions Lead→Contact: leadId={}, contactId={}, rows={}",
                    oldLeadId, newContactId, rows);
        }
    }

    public static Date parseViewedAt(Object viewedAt)
    {
        if (viewedAt == null)
        {
            return null;
        }
        if (viewedAt instanceof Number)
        {
            return new Date(((Number) viewedAt).longValue());
        }
        String text = viewedAt.toString().trim();
        if (!StringUtils.hasText(text))
        {
            return null;
        }
        try
        {
            return new Date(Long.parseLong(text));
        }
        catch (NumberFormatException ignored)
        {
            // fall through to ISO
        }
        try
        {
            return Date.from(Instant.parse(text));
        }
        catch (DateTimeParseException e)
        {
            throw new ServiceException("viewedAt 格式无效", HttpStatus.BAD_REQUEST);
        }
    }

    private void insertBrowseHistoryFallback(String sessionId, String browseHistory)
    {
        if (!StringUtils.hasText(browseHistory))
        {
            return;
        }
        List<BrowseEntry> entries = browseHistorySupport.parseBrowseHistory(browseHistory);
        for (BrowseEntry entry : entries)
        {
            if (entry == null || !StringUtils.hasText(entry.getU()))
            {
                continue;
            }
            String pageUrl = entry.getU().trim();
            if (pageUrl.length() > PAGE_URL_MAX)
            {
                pageUrl = pageUrl.substring(0, PAGE_URL_MAX);
            }
            Date viewedAt = new Date(entry.getT());
            if (pageViewMapper.countNearDuplicate(sessionId, pageUrl, viewedAt, FALLBACK_NEAR_SECONDS) > 0)
            {
                continue;
            }
            CrmWebsitePageView pageView = new CrmWebsitePageView();
            pageView.setId(UUID.randomUUID().toString());
            pageView.setSessionId(sessionId);
            pageView.setPageUrl(pageUrl);
            pageView.setPageDescription(trimToNull(entry.getD(), 512));
            pageView.setViewedAt(viewedAt);
            pageView.setSyncedToActivity(false);
            pageViewMapper.insertPageView(pageView);
        }
    }

    private void projectAndMark(CrmWebsitePageView pageView, String subjectType, String subjectId)
    {
        ActivityEventDTO dto = buildPageViewEvent(pageView, subjectType, subjectId);
        activityEventProducer.produce(dto);
        String activityId = activityMapper.selectActivityIdByChannelEventId(CHANNEL, dto.getChannelEventId());
        pageViewMapper.markSynced(pageView.getId(), activityId);
    }

    private ActivityEventDTO buildPageViewEvent(CrmWebsitePageView pageView, String subjectType, String subjectId)
    {
        ActivityEventDTO dto = new ActivityEventDTO();
        dto.setChannel(CHANNEL);
        dto.setChannelEventId(WebsitePageViewChannelEventIds.of(
                pageView.getSessionId(), pageView.getPageUrl(), subjectId));
        dto.setEventType(EVENT_TYPE);
        dto.setEventCategory(EVENT_CATEGORY);
        dto.setImportance(IMPORTANCE);
        dto.setSubjectType(subjectType);
        dto.setSubjectId(subjectId);
        dto.setSource(SOURCE);
        dto.setStartedDate(pageView.getViewedAt());
        dto.setCreateBy("website");

        boolean hasDescription = StringUtils.hasText(pageView.getPageDescription());
        if (hasDescription)
        {
            dto.setMessageData("浏览了页面 " + pageView.getPageDescription());
        }
        else
        {
            dto.setMessageData("浏览了页面 " + pageView.getPageUrl());
        }

        JSONObject payload = new JSONObject();
        payload.put("page_url", pageView.getPageUrl());
        payload.put("session_id", pageView.getSessionId());
        if (hasDescription)
        {
            payload.put("page_description", pageView.getPageDescription());
        }
        if (StringUtils.hasText(pageView.getReferrer()))
        {
            payload.put("referrer", pageView.getReferrer());
        }
        dto.setPayload(payload.toString());
        return dto;
    }

    private void upsertAnonymousSession(String sessionId)
    {
        CrmWebsiteVisitorSession session = new CrmWebsiteVisitorSession();
        session.setId(UUID.randomUUID().toString());
        session.setSessionId(sessionId);
        sessionMapper.upsertSession(session);
    }

    private void validateSessionAndUrl(String sessionId, String pageUrl)
    {
        if (!StringUtils.hasText(sessionId))
        {
            throw new ServiceException("sessionId 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (!StringUtils.hasText(pageUrl))
        {
            throw new ServiceException("pageUrl 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (pageUrl.length() > PAGE_URL_MAX)
        {
            throw new ServiceException("pageUrl 过长", HttpStatus.BAD_REQUEST);
        }
    }

    private void enforceSessionRateLimit(String sessionId)
    {
        String key = SESSION_RATE_KEY_PREFIX + sessionId;
        try
        {
            Long count = redisTemplate.opsForValue().increment(key);
            if (count != null && count == 1L)
            {
                redisTemplate.expire(key, SESSION_RATE_WINDOW_SECONDS, TimeUnit.SECONDS);
            }
            if (count != null && count > SESSION_RATE_LIMIT)
            {
                throw new ServiceException("访问过于频繁，请稍候再试", HttpStatus.TOO_MANY_REQUESTS);
            }
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            log.warn("Session rate limit check failed, allowing request: sessionId={}", sessionId, e);
        }
    }

    private static String trimToNull(String value, int maxLen)
    {
        if (!StringUtils.hasText(value))
        {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.length() > maxLen)
        {
            return trimmed.substring(0, maxLen);
        }
        return trimmed;
    }
}
