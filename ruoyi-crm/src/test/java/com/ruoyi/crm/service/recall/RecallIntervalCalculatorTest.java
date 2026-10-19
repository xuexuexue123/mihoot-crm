package com.ruoyi.crm.service.recall;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

public class RecallIntervalCalculatorTest
{
    private static final ZoneId SHANGHAI = ZoneId.of("Asia/Shanghai");

    @Test
    public void daysAddsWholeDays()
    {
        Instant anchor = Instant.parse("2026-08-18T02:00:00Z");
        Instant next = RecallIntervalCalculator.add(anchor, 90, RecallPolicyConstants.UNIT_DAYS);
        assertEquals(anchor.plusSeconds(90L * 86400), next);
    }

    @Test
    public void monthsUsesShanghaiCalendar()
    {
        ZonedDateTime jan31 = ZonedDateTime.of(2026, 1, 31, 10, 0, 0, 0, SHANGHAI);
        Instant next = RecallIntervalCalculator.add(jan31.toInstant(), 1, RecallPolicyConstants.UNIT_MONTHS);
        LocalDate date = next.atZone(SHANGHAI).toLocalDate();
        assertEquals(LocalDate.of(2026, 2, 28), date);
    }
}
