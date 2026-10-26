package com.ruoyi.crm.service.support;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 抑制同一 lead 在短时间内的重复 workflow 触发（event_check_in 优先于 stage_arrived）。
 */
@Component
public class LeadConvertTriggerDedup
{
    private static final long SUPPRESS_MS = 5000L;

    private final ConcurrentHashMap<String, Long> eventCheckInMarks = new ConcurrentHashMap<>();

    public void markEventCheckIn(String leadId)
    {
        if (!StringUtils.hasText(leadId))
        {
            return;
        }
        eventCheckInMarks.put(leadId.trim(), System.currentTimeMillis());
        purgeExpired();
    }

    public boolean shouldSuppressStageArrived(String leadId)
    {
        if (!StringUtils.hasText(leadId))
        {
            return false;
        }
        Long markedAt = eventCheckInMarks.get(leadId.trim());
        if (markedAt == null)
        {
            return false;
        }
        if (System.currentTimeMillis() - markedAt > SUPPRESS_MS)
        {
            eventCheckInMarks.remove(leadId.trim());
            return false;
        }
        return true;
    }

    private void purgeExpired()
    {
        long now = System.currentTimeMillis();
        eventCheckInMarks.entrySet().removeIf(e -> now - e.getValue() > SUPPRESS_MS);
    }
}
