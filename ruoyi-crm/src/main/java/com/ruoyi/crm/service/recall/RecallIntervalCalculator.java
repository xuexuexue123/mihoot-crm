package com.ruoyi.crm.service.recall;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import com.ruoyi.crm.service.followup.FollowupRuleConstants;

/**
 * 复诊间隔：days 按整天；months 按上海日历月。
 */
public final class RecallIntervalCalculator
{
    public static final ZoneId SHANGHAI = ZoneId.of(FollowupRuleConstants.ZONE_SHANGHAI);

    private RecallIntervalCalculator()
    {
    }

    public static Instant add(Instant anchor, int intervalValue, String intervalUnit)
    {
        Instant start = anchor == null ? Instant.now() : anchor;
        int value = Math.max(intervalValue, 0);
        if (RecallPolicyConstants.UNIT_MONTHS.equals(intervalUnit))
        {
            return ZonedDateTime.ofInstant(start, SHANGHAI).plusMonths(value).toInstant();
        }
        return start.plus(value, ChronoUnit.DAYS);
    }

    public static Date addToDate(Date anchor, int intervalValue, String intervalUnit)
    {
        Instant start = anchor == null ? Instant.now() : anchor.toInstant();
        return Date.from(add(start, intervalValue, intervalUnit));
    }
}
