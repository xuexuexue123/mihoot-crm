package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.ruoyi.crm.domain.vo.EventPerformanceTrendPointVo;

public class EventPerformanceTrendsSupportTest
{
    @Test
    public void cumulativeSeriesIsMonotonic()
    {
        Map<String, Integer> touch = new HashMap<>();
        touch.put("2026-06-01", 2);
        touch.put("2026-06-02", 3);
        List<EventPerformanceTrendPointVo> series = EventPerformanceTrendsSupport.mergeSeries(
                LocalDate.parse("2026-06-01"), LocalDate.parse("2026-06-02"), "day",
                touch, touch, touch, touch, true);
        assertEquals(2, series.size());
        assertEquals(2, series.get(0).getTouchCount());
        assertEquals(5, series.get(1).getTouchCount());
        assertTrue(series.get(1).getTouchCount() >= series.get(0).getTouchCount());
    }
}
