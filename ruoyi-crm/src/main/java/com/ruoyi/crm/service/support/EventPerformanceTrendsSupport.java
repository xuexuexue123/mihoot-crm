package com.ruoyi.crm.service.support;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.ruoyi.crm.domain.vo.EventPerformanceTrendPointVo;

/**
 * 活动效果趋势序列合并与累计
 */
public final class EventPerformanceTrendsSupport
{
    private EventPerformanceTrendsSupport()
    {
    }

    public static List<EventPerformanceTrendPointVo> mergeSeries(
            LocalDate from, LocalDate to, String granularity,
            Map<String, Integer> touch, Map<String, Integer> h5,
            Map<String, Integer> leadReleased, Map<String, Integer> contactConverted,
            boolean cumulative)
    {
        Set<String> keys = new TreeSet<>();
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1))
        {
            keys.add(bucketKey(d, granularity));
        }
        if (touch != null) keys.addAll(touch.keySet());
        if (h5 != null) keys.addAll(h5.keySet());
        if (leadReleased != null) keys.addAll(leadReleased.keySet());
        if (contactConverted != null) keys.addAll(contactConverted.keySet());

        List<EventPerformanceTrendPointVo> series = new ArrayList<>();
        for (String key : keys)
        {
            if (key.compareTo(bucketKey(from, granularity)) < 0 || key.compareTo(bucketKey(to, granularity)) > 0)
            {
                continue;
            }
            EventPerformanceTrendPointVo point = new EventPerformanceTrendPointVo();
            point.setDate(key);
            point.setTouchCount(touch != null ? touch.getOrDefault(key, 0) : 0);
            point.setH5RegistrationCount(h5 != null ? h5.getOrDefault(key, 0) : 0);
            point.setLeadReleasedCount(leadReleased != null ? leadReleased.getOrDefault(key, 0) : 0);
            point.setContactConvertedCount(contactConverted != null ? contactConverted.getOrDefault(key, 0) : 0);
            series.add(point);
        }
        if (cumulative)
        {
            int touchSum = 0;
            int h5Sum = 0;
            int leadSum = 0;
            int contactSum = 0;
            for (EventPerformanceTrendPointVo point : series)
            {
                touchSum += point.getTouchCount();
                h5Sum += point.getH5RegistrationCount();
                leadSum += point.getLeadReleasedCount();
                contactSum += point.getContactConvertedCount();
                point.setTouchCount(touchSum);
                point.setH5RegistrationCount(h5Sum);
                point.setLeadReleasedCount(leadSum);
                point.setContactConvertedCount(contactSum);
            }
        }
        return series;
    }

    public static Map<String, Integer> toCountMap(List<EventTrendDailyRow> rows, String granularity)
    {
        java.util.Map<String, Integer> map = new java.util.LinkedHashMap<>();
        if (rows == null)
        {
            return map;
        }
        for (EventTrendDailyRow row : rows)
        {
            if (row.getBucketDate() == null)
            {
                continue;
            }
            LocalDate date = LocalDate.parse(row.getBucketDate().substring(0, 10));
            String key = bucketKey(date, granularity);
            map.merge(key, row.getCount(), Integer::sum);
        }
        return map;
    }

    public static String bucketKey(LocalDate date, String granularity)
    {
        if ("week".equalsIgnoreCase(granularity))
        {
            LocalDate monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            return monday.toString();
        }
        return date.toString();
    }
}
