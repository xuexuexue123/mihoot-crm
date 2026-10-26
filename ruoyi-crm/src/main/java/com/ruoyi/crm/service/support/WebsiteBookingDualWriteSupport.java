package com.ruoyi.crm.service.support;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmBooking;
import com.ruoyi.crm.domain.dto.WebsiteLeadSubmitRequest;
import com.ruoyi.crm.service.ICrmBookingService;

/**
 * Online Register → crm_booking 双写（Lead 成功后 best-effort）。
 */
@Component
public class WebsiteBookingDualWriteSupport
{
    private static final Logger log = LoggerFactory.getLogger(WebsiteBookingDualWriteSupport.class);

    public static final ZoneId SHANGHAI = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final Pattern TIME_RANGE = Pattern.compile(
            "^(\\d{1,2}):(\\d{2})\\s*-\\s*(\\d{1,2}):(\\d{2})$");
    private static final Pattern TIME_SINGLE = Pattern.compile("^(\\d{1,2}):(\\d{2})$");
    private static final int DEFAULT_DURATION_MINUTES = 30;

    @Autowired
    private ICrmBookingService crmBookingService;

    /**
     * Lead 已成功后调用；失败只记日志，不抛出。
     */
    public void dualWriteAfterLeadSuccess(WebsiteLeadSubmitRequest request, String leadId)
    {
        try
        {
            if (request == null || StringUtils.isBlank(leadId))
            {
                return;
            }
            if (StringUtils.isBlank(request.getAppointmentDate())
                    || StringUtils.isBlank(request.getAppointmentTime()))
            {
                log.warn("Skip booking dual-write: missing appointmentDate/Time, leadId={}", leadId);
                return;
            }
            TimeRange range = parseSlot(request.getAppointmentDate().trim(),
                    request.getAppointmentTime().trim());
            CrmBooking booking = new CrmBooking();
            booking.setLeadId(leadId);
            booking.setOrgId(request.getOrgId());
            booking.setBookingName(request.getLeadName());
            booking.setMobilePhone(request.getMobilePhone());
            booking.setStartTime(Date.from(range.start.toInstant()));
            booking.setEndTime(Date.from(range.end.toInstant()));
            booking.setStatus(CrmBookingStatus.SCHEDULED);
            // 显式 website，避免 insert 默认 manual
            booking.setSource("website");
            booking.setSourceRef("website:" + leadId + ":" + request.getAppointmentDate().trim()
                    + ":" + range.start.toLocalTime());
            booking.setOwner(request.getOwner());
            booking.setCreateBy(request.getCreateBy());
            booking.setUserId(request.getUserId());
            booking.setDeptId(request.getDeptId());
            crmBookingService.insertCrmBooking(booking);
        }
        catch (Exception ex)
        {
            log.error("Booking dual-write failed after websitelead, leadId={}, date={}, time={}: {}",
                    leadId,
                    request == null ? null : request.getAppointmentDate(),
                    request == null ? null : request.getAppointmentTime(),
                    ex.getMessage(), ex);
        }
    }

    /**
     * 解析预约时段。仅起点时 end = start + 30m。
     */
    public static TimeRange parseSlot(String appointmentDate, String appointmentTime)
    {
        if (StringUtils.isBlank(appointmentDate) || StringUtils.isBlank(appointmentTime))
        {
            throw new IllegalArgumentException("appointmentDate/appointmentTime 不能为空");
        }
        LocalDate day;
        try
        {
            day = LocalDate.parse(appointmentDate.trim(), DATE_FMT);
        }
        catch (DateTimeParseException ex)
        {
            throw new IllegalArgumentException("appointmentDate 格式须为 YYYY-MM-DD", ex);
        }
        String time = appointmentTime.trim();
        Matcher range = TIME_RANGE.matcher(time);
        if (range.matches())
        {
            LocalTime start = LocalTime.of(Integer.parseInt(range.group(1)), Integer.parseInt(range.group(2)));
            LocalTime end = LocalTime.of(Integer.parseInt(range.group(3)), Integer.parseInt(range.group(4)));
            ZonedDateTime startZ = ZonedDateTime.of(day, start, SHANGHAI);
            ZonedDateTime endZ = ZonedDateTime.of(day, end, SHANGHAI);
            if (!endZ.isAfter(startZ))
            {
                throw new IllegalArgumentException("appointmentTime 结束须晚于开始");
            }
            return new TimeRange(startZ, endZ);
        }
        Matcher single = TIME_SINGLE.matcher(time);
        if (single.matches())
        {
            LocalTime start = LocalTime.of(Integer.parseInt(single.group(1)), Integer.parseInt(single.group(2)));
            ZonedDateTime startZ = ZonedDateTime.of(day, start, SHANGHAI);
            return new TimeRange(startZ, startZ.plusMinutes(DEFAULT_DURATION_MINUTES));
        }
        throw new IllegalArgumentException("appointmentTime 格式无效: " + appointmentTime);
    }

    public static final class TimeRange
    {
        public final ZonedDateTime start;
        public final ZonedDateTime end;

        public TimeRange(ZonedDateTime start, ZonedDateTime end)
        {
            this.start = start;
            this.end = end;
        }
    }
}
