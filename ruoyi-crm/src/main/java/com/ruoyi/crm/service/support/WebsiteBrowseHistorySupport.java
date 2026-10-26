package com.ruoyi.crm.service.support;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.domain.dto.ActivityEventDTO;
import com.ruoyi.crm.event.ActivityEventProducer;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Website Browse History Support — 解析前端传递的浏览历史 Cookie（_pv_hist）
 *
 * 实时 pageview 与报名缝合请使用 {@link WebsiteVisitorSessionService}。
 * 本类保留解析能力，供缝合兜底补洞复用。
 *
 * @author luohd
 * @date 2026-07-12
 */
@Slf4j
@Component
public class WebsiteBrowseHistorySupport
{
    private static final String CHANNEL = "website";
    private static final String EVENT_TYPE = "website.page.view";
    private static final String EVENT_CATEGORY = "engagement";
    private static final int IMPORTANCE = 5;
    private static final String SOURCE = "website";

    @Autowired
    private ActivityEventProducer activityEventProducer;

    /**
     * @deprecated 请使用 {@link WebsiteVisitorSessionService#stitch(String, String, String, String)}
     */
    @Deprecated
    public void writeBrowseHistory(String sessionId, String browseHistory, String subjectType, String subjectId)
    {
        if (!StringUtils.hasText(browseHistory))
        {
            return;
        }
        if (!StringUtils.hasText(subjectType) || !StringUtils.hasText(subjectId))
        {
            log.warn("Skip browse history write: missing subjectType or subjectId");
            return;
        }

        List<BrowseEntry> entries = parseBrowseHistory(browseHistory);
        if (entries.isEmpty())
        {
            return;
        }

        String effectiveSessionId = sessionId != null ? sessionId : "unknown";

        for (BrowseEntry entry : entries)
        {
            ActivityEventDTO dto = buildPageViewEvent(effectiveSessionId, entry, subjectType, subjectId);
            activityEventProducer.produce(dto);
        }

        log.info("Browse history written: subjectType={}, subjectId={}, entries={}",
                subjectType, subjectId, entries.size());
    }

    /**
     * 解析 _browseHistory（URL-encoded JSON 字符串）为 BrowseEntry 列表
     *
     * @param rawValue Cookie _pv_hist 的原始值
     * @return 解析后的浏览条目列表，解析失败或为空时返回空列表
     */
    public List<BrowseEntry> parseBrowseHistory(String rawValue)
    {
        if (!StringUtils.hasText(rawValue))
        {
            return Collections.emptyList();
        }

        try
        {
            String decoded = URLDecoder.decode(rawValue, StandardCharsets.UTF_8.name());
            List<BrowseEntry> entries = JSON.parseArray(decoded, BrowseEntry.class);
            if (entries == null || entries.isEmpty())
            {
                return Collections.emptyList();
            }
            return entries;
        }
        catch (Exception e)
        {
            log.warn("Failed to parse _browseHistory: {}", rawValue, e);
            return Collections.emptyList();
        }
    }

    private ActivityEventDTO buildPageViewEvent(String sessionId, BrowseEntry entry,
            String subjectType, String subjectId)
    {
        ActivityEventDTO dto = new ActivityEventDTO();
        dto.setChannel(CHANNEL);
        dto.setChannelEventId(WebsitePageViewChannelEventIds.of(sessionId, entry.u, subjectId));
        dto.setEventType(EVENT_TYPE);
        dto.setEventCategory(EVENT_CATEGORY);
        dto.setImportance(IMPORTANCE);
        dto.setSubjectType(subjectType);
        dto.setSubjectId(subjectId);
        dto.setSource(SOURCE);
        dto.setStartedDate(new Date(entry.t));
        dto.setCreateBy("website");

        boolean hasDescription = StringUtils.hasText(entry.d);
        if (hasDescription)
        {
            dto.setMessageData("浏览了页面 " + entry.d);
        }
        else
        {
            dto.setMessageData("浏览了页面 " + entry.u);
        }

        JSONObject payload = new JSONObject();
        payload.put("page_url", entry.u);
        payload.put("session_id", sessionId);
        if (hasDescription)
        {
            payload.put("page_description", entry.d);
        }
        dto.setPayload(payload.toString());

        return dto;
    }

    /**
     * Cookie _pv_hist 中的单条浏览记录
     */
    public static class BrowseEntry
    {
        /** 页面路径（不含域名），含 ?query */
        private String u;

        /** 访问时间戳（毫秒，客户端时间） */
        private long t;

        /** 页面描述（<meta name="description"> content 值），可选 */
        private String d;

        public String getU()
        {
            return u;
        }

        public void setU(String u)
        {
            this.u = u;
        }

        public long getT()
        {
            return t;
        }

        public void setT(long t)
        {
            this.t = t;
        }

        public String getD()
        {
            return d;
        }

        public void setD(String d)
        {
            this.d = d;
        }
    }
}
