package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;
import org.junit.jupiter.api.Test;

class WebsiteBookingDualWriteSupportTest
{
    @Test
    void parseSlot_range()
    {
        WebsiteBookingDualWriteSupport.TimeRange range =
                WebsiteBookingDualWriteSupport.parseSlot("2026-07-15", "12:30-13:00");
        assertEquals(LocalTime.of(12, 30), range.start.toLocalTime());
        assertEquals(LocalTime.of(13, 0), range.end.toLocalTime());
        assertEquals(WebsiteBookingDualWriteSupport.SHANGHAI, range.start.getZone());
    }

    @Test
    void parseSlot_singleDefaultsTo30Minutes()
    {
        WebsiteBookingDualWriteSupport.TimeRange range =
                WebsiteBookingDualWriteSupport.parseSlot("2026-07-15", "12:30");
        assertEquals(LocalTime.of(12, 30), range.start.toLocalTime());
        assertEquals(LocalTime.of(13, 0), range.end.toLocalTime());
    }

    @Test
    void parseSlot_rejectsInvalidTime()
    {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> WebsiteBookingDualWriteSupport.parseSlot("2026-07-15", "noon"));
        assertTrue(ex.getMessage().contains("appointmentTime"));
    }

    @Test
    void parseSlot_rejectsEndBeforeStart()
    {
        assertThrows(IllegalArgumentException.class,
                () -> WebsiteBookingDualWriteSupport.parseSlot("2026-07-15", "13:00-12:00"));
    }
}
