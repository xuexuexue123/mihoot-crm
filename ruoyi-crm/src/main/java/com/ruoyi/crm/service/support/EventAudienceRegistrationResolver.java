package com.ruoyi.crm.service.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.vo.EventAudienceRowVo;
import com.ruoyi.crm.enums.EventRegistrationStatus;

/**
 * 活动受众行最优报名解析（含 audienceKey 别名匹配）
 */
@Component
public class EventAudienceRegistrationResolver
{
    public Map<String, EventRegistrationBestRow> buildIndex(List<EventRegistrationBestRow> rows)
    {
        Map<String, EventRegistrationBestRow> index = new HashMap<>();
        if (rows == null)
        {
            return index;
        }
        for (EventRegistrationBestRow row : rows)
        {
            if (row == null || StringUtils.isEmpty(row.getAudienceKey()))
            {
                continue;
            }
            EventRegistrationBestRow existing = index.get(row.getAudienceKey());
            if (existing == null || compareStatus(row, existing) < 0)
            {
                index.put(row.getAudienceKey(), row);
            }
        }
        return index;
    }

    public void enrichRow(EventAudienceRowVo row, Map<String, EventRegistrationBestRow> registrationIndex)
    {
        if (row == null)
        {
            return;
        }
        EventRegistrationBestRow best = match(row, registrationIndex);
        if (best == null)
        {
            row.setRegistrationStatus(null);
            row.setRegistrationSessionName(null);
            return;
        }
        row.setRegistrationStatus(best.getRegistrationStatus());
        row.setRegistrationSessionName(best.getSessionName());
    }

    public EventRegistrationBestRow match(EventAudienceRowVo row, Map<String, EventRegistrationBestRow> registrationIndex)
    {
        if (row == null || registrationIndex == null || registrationIndex.isEmpty())
        {
            return null;
        }
        EventRegistrationBestRow best = null;
        for (String key : AudienceKeyAliasSupport.lookupKeys(row))
        {
            EventRegistrationBestRow hit = registrationIndex.get(key);
            if (hit != null && (best == null || compareStatus(hit, best) < 0))
            {
                best = hit;
            }
        }
        return best;
    }

    private int compareStatus(EventRegistrationBestRow a, EventRegistrationBestRow b)
    {
        return statusRank(a.getRegistrationStatus()) - statusRank(b.getRegistrationStatus());
    }

    private int statusRank(String status)
    {
        EventRegistrationStatus parsed = EventRegistrationStatus.fromCode(status);
        if (parsed == EventRegistrationStatus.CONFIRMED)
        {
            return 1;
        }
        if (parsed == EventRegistrationStatus.WAITLIST)
        {
            return 2;
        }
        if (parsed == EventRegistrationStatus.CANCELLED)
        {
            return 3;
        }
        return 4;
    }
}
