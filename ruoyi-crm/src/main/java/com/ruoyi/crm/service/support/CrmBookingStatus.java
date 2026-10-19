package com.ruoyi.crm.service.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;

/**
 * crm_booking 状态机（docs/crm-booking-backend-plan.md §4）
 */
public final class CrmBookingStatus
{
    public static final String SCHEDULED = "scheduled";
    public static final String CONFIRMED = "confirmed";
    public static final String ARRIVED = "arrived";
    /** 到诊/就诊完成（写入 appointment.completed Activity，供 KPI 见面客户数） */
    public static final String COMPLETED = "completed";
    public static final String NOSHOW = "noshow";
    public static final String CANCELLED = "cancelled";

    private static final Set<String> ALL = new HashSet<>(Arrays.asList(
            SCHEDULED, CONFIRMED, ARRIVED, COMPLETED, NOSHOW, CANCELLED));

    private static final Map<String, Set<String>> ALLOWED = new HashMap<>();

    static
    {
        ALLOWED.put(SCHEDULED, set(CONFIRMED, CANCELLED, NOSHOW, COMPLETED));
        ALLOWED.put(CONFIRMED, set(ARRIVED, CANCELLED, NOSHOW, COMPLETED));
        ALLOWED.put(ARRIVED, set(COMPLETED));
        ALLOWED.put(COMPLETED, Collections.emptySet());
        ALLOWED.put(NOSHOW, Collections.emptySet());
        ALLOWED.put(CANCELLED, Collections.emptySet());
    }

    private CrmBookingStatus()
    {
    }

    public static boolean isValid(String status)
    {
        return StringUtils.isNotBlank(status) && ALL.contains(status.trim().toLowerCase());
    }

    public static String normalize(String status)
    {
        return status == null ? null : status.trim().toLowerCase();
    }

    /**
     * 允许同状态幂等；否则须符合状态机边。
     */
    public static void assertTransition(String from, String to)
    {
        String fromN = normalize(from);
        String toN = normalize(to);
        if (!isValid(toN))
        {
            throw new ServiceException("非法预约状态: " + to, HttpStatus.BAD_REQUEST);
        }
        if (fromN == null || fromN.equals(toN))
        {
            return;
        }
        if (!isValid(fromN))
        {
            throw new ServiceException("当前预约状态非法: " + from, HttpStatus.BAD_REQUEST);
        }
        Set<String> next = ALLOWED.getOrDefault(fromN, Collections.emptySet());
        if (!next.contains(toN))
        {
            throw new ServiceException("不允许的状态变更: " + fromN + " -> " + toN, HttpStatus.BAD_REQUEST);
        }
    }

    private static Set<String> set(String... values)
    {
        return new HashSet<>(Arrays.asList(values));
    }
}
