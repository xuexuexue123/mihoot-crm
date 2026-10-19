package com.ruoyi.crm.service.followup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.domain.CrmFollowupRule;
import com.ruoyi.crm.domain.dto.FollowupRuleDispatchEvent;

public class FollowupRuleDelayCalculatorTest
{
    private static final ZoneId SHANGHAI = ZoneId.of("Asia/Shanghai");

    @Test
    public void immediateUsesOccurredAt()
    {
        Instant occurred = Instant.parse("2026-08-18T06:00:00Z");
        Instant next = FollowupRuleDelayCalculator.calculate(rule(FollowupRuleConstants.DELAY_IMMEDIATE, null, null, null),
                event(occurred, new JSONObject()));
        assertEquals(occurred, next);
    }

    @Test
    public void fixedDelayHours()
    {
        Instant occurred = Instant.parse("2026-08-18T06:00:00Z");
        CrmFollowupRule rule = rule(FollowupRuleConstants.DELAY_FIXED, 4, FollowupRuleConstants.UNIT_HOURS, null);
        Instant next = FollowupRuleDelayCalculator.calculate(rule, event(occurred, new JSONObject()));
        assertEquals(occurred.plusSeconds(4 * 3600), next);
    }

    @Test
    public void businessDaysSkipWeekend()
    {
        // Friday 2026-08-14 10:00 +08
        ZonedDateTime friday = ZonedDateTime.of(2026, 8, 14, 10, 0, 0, 0, SHANGHAI);
        Instant next = FollowupRuleDelayCalculator.addBusinessDays(friday.toInstant(), 2);
        ZonedDateTime result = next.atZone(SHANGHAI);
        assertEquals(DayOfWeek.TUESDAY, result.getDayOfWeek());
        assertEquals(LocalDate.of(2026, 8, 18), result.toLocalDate());
    }

    @Test
    public void untilFieldUsesShanghaiNineAm()
    {
        JSONObject payload = new JSONObject();
        JSONObject pv = new JSONObject();
        pv.put("nextRecallDueAt", "2026-08-20T00:00:00+08:00");
        payload.put("patientValue", pv);
        CrmFollowupRule rule = rule(FollowupRuleConstants.DELAY_UNTIL_FIELD, null, null, "patientValue.nextRecallDueAt");
        Instant next = FollowupRuleDelayCalculator.calculate(rule, event(Instant.parse("2026-08-18T00:00:00Z"), payload));
        ZonedDateTime zoned = next.atZone(SHANGHAI);
        assertEquals(LocalDate.of(2026, 8, 20), zoned.toLocalDate());
        assertEquals(LocalTime.of(9, 0), zoned.toLocalTime());
    }

    @Test
    public void untilFieldReadsRecallScheduleNextDueAt()
    {
        JSONObject payload = new JSONObject();
        JSONObject schedule = new JSONObject();
        schedule.put("nextDueAt", "2026-11-16T00:00:00+08:00");
        payload.put("recallSchedule", schedule);
        CrmFollowupRule rule = rule(FollowupRuleConstants.DELAY_UNTIL_FIELD, null, null, "recallSchedule.nextDueAt");
        Instant next = FollowupRuleDelayCalculator.calculate(rule, event(Instant.parse("2026-08-18T00:00:00Z"), payload));
        ZonedDateTime zoned = next.atZone(SHANGHAI);
        assertEquals(LocalDate.of(2026, 11, 16), zoned.toLocalDate());
        assertEquals(LocalTime.of(9, 0), zoned.toLocalTime());
    }

    @Test
    public void untilFieldNineAmHelper()
    {
        Instant field = Instant.parse("2026-08-20T16:00:00Z");
        Instant nine = FollowupRuleDelayCalculator.untilFieldNineAm(field);
        assertTrue(nine.atZone(SHANGHAI).getHour() == 9);
        assertEquals(LocalDate.of(2026, 8, 21), nine.atZone(SHANGHAI).toLocalDate());
    }

    private static CrmFollowupRule rule(String mode, Integer value, String unit, String field)
    {
        CrmFollowupRule rule = new CrmFollowupRule();
        rule.setDelayMode(mode);
        rule.setDelayValue(value);
        rule.setDelayUnit(unit);
        rule.setDelayField(field);
        return rule;
    }

    private static FollowupRuleDispatchEvent event(Instant occurred, JSONObject payload)
    {
        FollowupRuleDispatchEvent event = new FollowupRuleDispatchEvent();
        event.setOccurredAt(occurred);
        event.setPayload(payload);
        return event;
    }
}
