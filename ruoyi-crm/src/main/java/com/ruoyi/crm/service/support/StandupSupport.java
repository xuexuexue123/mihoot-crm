package com.ruoyi.crm.service.support;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.vo.StandupDayMetrics;
import com.ruoyi.crm.domain.vo.StandupMemberRow;
import com.ruoyi.crm.domain.vo.StandupTimeWindow;

/**
 * 晨会日期窗口与成员排序。
 */
public final class StandupSupport
{
    public static final String TIMEZONE_ID = "Asia/Shanghai";
    public static final ZoneId ZONE = ZoneId.of(TIMEZONE_ID);
    public static final String FLAG_ZERO_ACTION = "zero_action";
    public static final String DEFAULT_KPI_CODES = "contract_amount,met_customers,followup_count";

    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter ISO_OFFSET = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private StandupSupport()
    {
    }

    public static LocalDate parseReportDate(String date)
    {
        if (date == null || date.trim().isEmpty())
        {
            throw new ServiceException("date 不能为空", HttpStatus.BAD_REQUEST);
        }
        try
        {
            return LocalDate.parse(date.trim(), DAY_FMT);
        }
        catch (DateTimeParseException ex)
        {
            throw new ServiceException("date 格式须为 YYYY-MM-DD", HttpStatus.BAD_REQUEST);
        }
    }

    public static StandupTimeWindow windowOf(LocalDate day)
    {
        ZonedDateTime start = day.atStartOfDay(ZONE);
        ZonedDateTime end = start.plusDays(1);
        return new StandupTimeWindow(ISO_OFFSET.format(start), ISO_OFFSET.format(end));
    }

    /** KPI 闭区间 ::date 用：取该自然日任意时刻的 Date（JVM 本地序列化后 PG ::date 对齐上海日时需 DB TZ 一致）。 */
    public static Date calendarDayDate(LocalDate day)
    {
        return Date.from(day.atStartOfDay(ZONE).toInstant());
    }

    public static Date dayStart(LocalDate day)
    {
        return Date.from(day.atStartOfDay(ZONE).toInstant());
    }

    public static Date dayEndExclusive(LocalDate day)
    {
        return Date.from(day.plusDays(1).atStartOfDay(ZONE).toInstant());
    }

    public static String formatIso(Date date)
    {
        if (date == null)
        {
            return null;
        }
        return ISO_OFFSET.format(date.toInstant().atZone(ZONE));
    }

    /**
     * 零动作整组置顶，组内 contractAmount DESC → followupCount DESC → ownerName ASC。
     */
    public static void sortMembers(List<StandupMemberRow> members)
    {
        if (members == null || members.isEmpty())
        {
            return;
        }
        members.sort(Comparator
                .comparing((StandupMemberRow m) -> !isZeroAction(m))
                .thenComparing(m -> contractAmount(m), Comparator.reverseOrder())
                .thenComparing(m -> followupCount(m), Comparator.reverseOrder())
                .thenComparing(m -> ownerName(m), String.CASE_INSENSITIVE_ORDER));
    }

    public static boolean isZeroAction(StandupMemberRow m)
    {
        if (m == null)
        {
            return false;
        }
        if (m.getFlags() != null && m.getFlags().contains(FLAG_ZERO_ACTION))
        {
            return true;
        }
        StandupDayMetrics y = m.getYesterday();
        return y != null && y.isZeroAction();
    }

    private static java.math.BigDecimal contractAmount(StandupMemberRow m)
    {
        StandupDayMetrics y = m.getYesterday();
        return y != null && y.getContractAmount() != null ? y.getContractAmount() : java.math.BigDecimal.ZERO;
    }

    private static java.math.BigDecimal followupCount(StandupMemberRow m)
    {
        StandupDayMetrics y = m.getYesterday();
        return y != null && y.getFollowupCount() != null ? y.getFollowupCount() : java.math.BigDecimal.ZERO;
    }

    private static String ownerName(StandupMemberRow m)
    {
        return m.getOwnerName() != null ? m.getOwnerName() : "";
    }

    public static List<String> parseKpiCodes(String raw)
    {
        String src = (raw == null || raw.trim().isEmpty()) ? DEFAULT_KPI_CODES : raw.trim();
        String[] parts = src.split(",");
        List<String> codes = new ArrayList<>();
        for (String p : parts)
        {
            if (p != null && !p.trim().isEmpty())
            {
                codes.add(p.trim());
            }
        }
        return codes;
    }
}
