package com.ruoyi.crm.service.support;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.vo.EventAudienceRowVo;
import com.ruoyi.crm.enums.EventRegistrationStatus;

/**
 * 活动受众行签到状态解析（含 audienceKey 别名匹配）
 */
@Component
public class EventAudienceCheckInResolver
{
    public Map<String, Date> buildIndex(List<EventCheckInAggRow> rows)
    {
        Map<String, Date> index = new HashMap<>();
        if (rows == null)
        {
            return index;
        }
        for (EventCheckInAggRow row : rows)
        {
            if (row == null || StringUtils.isEmpty(row.getAudienceKey()) || row.getCheckedInAt() == null)
            {
                continue;
            }
            Date existing = index.get(row.getAudienceKey());
            if (existing == null || row.getCheckedInAt().after(existing))
            {
                index.put(row.getAudienceKey(), row.getCheckedInAt());
            }
        }
        return index;
    }

    public void enrichRow(EventAudienceRowVo row, Map<String, Date> checkInIndex,
            EventRegistrationBestRow registration)
    {
        if (row == null)
        {
            return;
        }
        Date checkedInAt = resolveCheckedInAt(row, checkInIndex);
        if (checkedInAt != null)
        {
            row.setCheckInStatus("checked_in");
            row.setCheckedInAt(checkedInAt);
            return;
        }
        if (isCheckInApplicable(row, registration))
        {
            row.setCheckInStatus("not_checked_in");
            row.setCheckedInAt(null);
            return;
        }
        row.setCheckInStatus(null);
        row.setCheckedInAt(null);
    }

    Date resolveCheckedInAt(EventAudienceRowVo row, Map<String, Date> checkInIndex)
    {
        if (row == null || checkInIndex == null || checkInIndex.isEmpty())
        {
            return null;
        }
        Date max = null;
        for (String key : AudienceKeyAliasSupport.lookupKeys(row))
        {
            Date hit = checkInIndex.get(key);
            if (hit != null && (max == null || hit.after(max)))
            {
                max = hit;
            }
        }
        return max;
    }

    private boolean isCheckInApplicable(EventAudienceRowVo row, EventRegistrationBestRow registration)
    {
        if (hasActiveRegistration(registration))
        {
            return true;
        }
        return row.getChannels() != null
                && row.getChannels().contains("h5")
                && !AudienceStatusResolver.CHANNEL_ONLY.equals(row.getAudienceStatus());
    }

    private boolean hasActiveRegistration(EventRegistrationBestRow registration)
    {
        if (registration == null || StringUtils.isEmpty(registration.getRegistrationStatus()))
        {
            return false;
        }
        EventRegistrationStatus status = EventRegistrationStatus.fromCode(registration.getRegistrationStatus());
        return status == EventRegistrationStatus.CONFIRMED || status == EventRegistrationStatus.WAITLIST;
    }
}
