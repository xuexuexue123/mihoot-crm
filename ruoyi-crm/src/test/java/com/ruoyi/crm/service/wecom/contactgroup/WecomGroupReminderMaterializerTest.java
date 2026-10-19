package com.ruoyi.crm.service.wecom.contactgroup;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import com.ruoyi.crm.domain.CrmWecomGroupReminderRule;
import static org.junit.jupiter.api.Assertions.*;

class WecomGroupReminderMaterializerTest
{
    @Test
    void computeDueAtDaysUsesShanghaiSendTime()
    {
        CrmWecomGroupReminderRule rule = new CrmWecomGroupReminderRule();
        rule.setOffsetValue(-1);
        rule.setOffsetUnit("days");
        rule.setSendTime("09:00");
        Instant anchor = ZonedDateTime.of(2026, 8, 29, 10, 0, 0, 0, ZoneId.of("Asia/Shanghai")).toInstant();
        Date due = WecomGroupReminderMaterializer.computeDueAt(anchor, rule);
        ZonedDateTime z = due.toInstant().atZone(ZoneId.of("Asia/Shanghai"));
        assertEquals(28, z.getDayOfMonth());
        assertEquals(9, z.getHour());
    }

    @Test
    void computeDueAtHours()
    {
        CrmWecomGroupReminderRule rule = new CrmWecomGroupReminderRule();
        rule.setOffsetValue(-2);
        rule.setOffsetUnit("hours");
        Instant anchor = Instant.parse("2026-08-28T10:00:00Z");
        Date due = WecomGroupReminderMaterializer.computeDueAt(anchor, rule);
        assertEquals(anchor.minusSeconds(7200), due.toInstant());
    }

    @Test
    void renderReplacesVars()
    {
        Map<String, String> vars = new HashMap<>();
        vars.put("name", "张三");
        vars.put("time", "10:00");
        String out = WecomGroupReminderMaterializer.render("您好{{name}}，明天 {{time}}", vars);
        assertEquals("您好张三，明天 10:00", out);
    }

    @Test
    void matchTreatmentEmptyMeansAll()
    {
        assertTrue(WecomGroupReminderMaterializer.matchTreatment(null, "种植牙"));
        assertTrue(WecomGroupReminderMaterializer.matchTreatment("{\"treatmentItemCodes\":[]}", "x"));
        assertTrue(WecomGroupReminderMaterializer.matchTreatment(
                "{\"treatmentItemCodes\":[\"implant\"]}", "种植牙"));
        assertTrue(WecomGroupReminderMaterializer.matchTreatment(
                "{\"treatmentItemCodes\":[\"implant\"]}", "implant"));
        assertFalse(WecomGroupReminderMaterializer.matchTreatment(
                "{\"treatmentItemCodes\":[\"implant\"]}", "洁牙"));
    }
}
