package com.ruoyi.crm.wecom;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.mapper.CrmEventMapper;

/**
 * 解析活码 state，支持 {@code EVT:{suffix}}、{@code EVT:{suffix}:{channel}}、
 * 历史 {@code EVT:{完整 UUID}}、{@code EVT_...}、{@code evt=...}、{@code rid=}、{@code eventid=} 或纯数字。
 */
@Component
public class WecomStateParser
{
    private static final Logger log = LoggerFactory.getLogger(WecomStateParser.class);

    @Autowired
    private CrmEventMapper crmEventMapper;

    /**
     * 解析 state 并反查 {@code crm_event.event_id}。
     */
    public WecomStateParseResult parse(String state)
    {
        WecomStateParseResult result = WecomStateParseResult.empty();
        if (state == null || state.isEmpty())
        {
            return result;
        }
        String s = state.trim();
        String lower = s.toLowerCase();

        if (lower.startsWith("evt:"))
        {
            return parseEvtColonFormat(s.substring(4).trim(), state);
        }
        if (lower.startsWith("evt_"))
        {
            String suffix = takeUntilDelimiter(s.substring(4));
            return resolveLegacyPayload(suffix, state, null);
        }
        int idx = lower.indexOf("evt=");
        if (idx >= 0)
        {
            return resolveLegacyPayload(takeUntilDelimiter(s.substring(idx + 4)), state, null);
        }
        idx = lower.indexOf("rid=");
        if (idx >= 0)
        {
            return resolveLegacyPayload(takeUntilDelimiter(s.substring(idx + 4)), state, null);
        }
        idx = lower.indexOf("eventid=");
        if (idx >= 0)
        {
            return resolveLegacyPayload(takeUntilDelimiter(s.substring(idx + 8)), state, null);
        }
        if (s.matches("\\d+"))
        {
            return resolveLegacyPayload(s, state, null);
        }
        return result;
    }

    /**
     * 兼容旧调用：返回完整活动 ID（UUID），无法解析时返回 null。
     */
    public String parseEventId(String state)
    {
        WecomStateParseResult parsed = parse(state);
        return parsed.getCrmEventId();
    }

    private WecomStateParseResult parseEvtColonFormat(String payload, String rawState)
    {
        if (payload == null || payload.isEmpty())
        {
            return WecomStateParseResult.empty();
        }
        String suffix;
        String channel = null;
        int colon = payload.indexOf(':');
        if (colon >= 0)
        {
            suffix = payload.substring(0, colon).trim();
            channel = payload.substring(colon + 1).trim();
            if (channel.isEmpty())
            {
                channel = null;
            }
        }
        else
        {
            suffix = payload.trim();
        }
        return resolveLegacyPayload(suffix, rawState, channel);
    }

    private WecomStateParseResult resolveLegacyPayload(String suffix, String rawState, String channel)
    {
        WecomStateParseResult result = WecomStateParseResult.empty();
        if (suffix == null || suffix.isEmpty())
        {
            return result;
        }
        result.setEventSuffix(suffix);
        if (channel != null && !channel.isEmpty())
        {
            result.setChannel(channel);
            result.setLeadSource("wecom:" + channel);
        }
        else
        {
            result.setLeadSource("wecom");
        }
        result.setCrmEventId(resolveCrmEventId(suffix));
        return result;
    }

    private String resolveCrmEventId(String suffix)
    {
        if (suffix.contains("-"))
        {
            CrmEvent event = crmEventMapper.selectCrmEventByEventId(suffix);
            return event != null ? event.getEventId() : null;
        }

        CrmEvent exact = crmEventMapper.selectCrmEventByEventId(suffix);
        if (exact != null)
        {
            return exact.getEventId();
        }

        List<String> matches = crmEventMapper.selectEventIdsByCompactSuffix(suffix);
        if (matches == null || matches.isEmpty())
        {
            return null;
        }
        if (matches.size() == 1)
        {
            return matches.get(0);
        }
        log.warn("WecomStateParser: ambiguous crm_event match for suffix={}, count={}", suffix, matches.size());
        return null;
    }

    static String takeUntilDelimiter(String rest)
    {
        if (rest == null)
        {
            return null;
        }
        int end = rest.length();
        for (int i = 0; i < rest.length(); i++)
        {
            char c = rest.charAt(i);
            if (c == '&' || c == ';' || c == ' ')
            {
                end = i;
                break;
            }
        }
        String v = rest.substring(0, end).trim();
        return v.isEmpty() ? null : v;
    }
}
