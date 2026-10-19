package com.ruoyi.crm.service.followup;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmFollowupRule;
import com.ruoyi.crm.domain.dto.FollowupRuleDispatchEvent;

/**
 * 计算 enrollment.next_run_at（Asia/Shanghai）
 */
public final class FollowupRuleDelayCalculator
{
    public static final ZoneId SHANGHAI = ZoneId.of(FollowupRuleConstants.ZONE_SHANGHAI);
    private static final LocalTime NINE_AM = LocalTime.of(9, 0);

    private FollowupRuleDelayCalculator()
    {
    }

    public static Instant calculate(CrmFollowupRule rule, FollowupRuleDispatchEvent event)
    {
        Instant occurred = event.getOccurredAt() != null ? event.getOccurredAt() : Instant.now();
        String mode = rule.getDelayMode();
        if (StringUtils.isBlank(mode) || FollowupRuleConstants.DELAY_IMMEDIATE.equals(mode))
        {
            return occurred;
        }
        if (FollowupRuleConstants.DELAY_FIXED.equals(mode))
        {
            int value = rule.getDelayValue() == null ? 0 : rule.getDelayValue();
            String unit = rule.getDelayUnit();
            if (FollowupRuleConstants.UNIT_MINUTES.equals(unit))
            {
                return occurred.plusSeconds(value * 60L);
            }
            if (FollowupRuleConstants.UNIT_DAYS.equals(unit))
            {
                return occurred.plusSeconds(value * 86400L);
            }
            return occurred.plusSeconds(value * 3600L);
        }
        if (FollowupRuleConstants.DELAY_BUSINESS_DAYS.equals(mode))
        {
            int days = rule.getDelayValue() == null ? 0 : rule.getDelayValue();
            return addBusinessDays(occurred, days);
        }
        if (FollowupRuleConstants.DELAY_UNTIL_FIELD.equals(mode))
        {
            Instant field = readFieldInstant(event.getPayload(), rule.getDelayField());
            if (field == null)
            {
                return occurred;
            }
            ZonedDateTime zoned = field.atZone(SHANGHAI);
            return zoned.toLocalDate().atTime(NINE_AM).atZone(SHANGHAI).toInstant();
        }
        return occurred;
    }

    public static Instant addBusinessDays(Instant start, int businessDays)
    {
        if (businessDays <= 0)
        {
            return start;
        }
        ZonedDateTime cursor = start.atZone(SHANGHAI);
        int added = 0;
        while (added < businessDays)
        {
            cursor = cursor.plusDays(1);
            DayOfWeek dow = cursor.getDayOfWeek();
            if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY)
            {
                added++;
            }
        }
        return cursor.toInstant();
    }

    public static Instant untilFieldNineAm(Instant fieldInstant)
    {
        if (fieldInstant == null)
        {
            return Instant.now();
        }
        LocalDate date = fieldInstant.atZone(SHANGHAI).toLocalDate();
        return date.atTime(NINE_AM).atZone(SHANGHAI).toInstant();
    }

    public static Date toDate(Instant instant)
    {
        return instant == null ? new Date() : Date.from(instant);
    }

    static Instant readFieldInstant(JSONObject payload, String path)
    {
        if (payload == null || StringUtils.isBlank(path))
        {
            return null;
        }
        Object current = payload;
        for (String part : path.split("\\."))
        {
            if (!(current instanceof JSONObject))
            {
                return null;
            }
            current = ((JSONObject) current).get(part);
        }
        if (current == null)
        {
            return null;
        }
        if (current instanceof Date)
        {
            return ((Date) current).toInstant();
        }
        if (current instanceof Instant)
        {
            return (Instant) current;
        }
        String text = String.valueOf(current);
        try
        {
            return Instant.parse(text);
        }
        catch (Exception ex)
        {
            try
            {
                return java.time.OffsetDateTime.parse(text).toInstant();
            }
            catch (Exception ignored)
            {
                try
                {
                    if (text.length() >= 10)
                    {
                        LocalDate date = LocalDate.parse(text.substring(0, 10));
                        return date.atTime(NINE_AM).atZone(SHANGHAI).toInstant();
                    }
                }
                catch (Exception ignored2)
                {
                    return null;
                }
                return null;
            }
        }
    }
}
