package com.ruoyi.crm.service.wecom.contactgroup;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmBooking;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmOpty;
import com.ruoyi.crm.domain.CrmRecallSchedule;
import com.ruoyi.crm.domain.CrmWecomContactGroup;
import com.ruoyi.crm.domain.CrmWecomGroupReminder;
import com.ruoyi.crm.domain.CrmWecomGroupReminderRule;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmWecomContactGroupMapper;
import com.ruoyi.crm.mapper.CrmWecomGroupReminderMapper;
import com.ruoyi.crm.mapper.CrmWecomGroupReminderRuleMapper;
import com.ruoyi.crm.service.support.CrmBookingStatus;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.crm.service.recall.TreatmentItemCodeNormalizer;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.mapper.SysUserMapper;

/**
 * 按锚点物化 / 取消群提醒队列
 */
@Service
public class WecomGroupReminderMaterializer
{
    private static final Logger log = LoggerFactory.getLogger(WecomGroupReminderMaterializer.class);
    private static final ZoneId SHANGHAI = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    private CrmWecomGroupReminderRuleMapper ruleMapper;

    @Autowired
    private CrmWecomGroupReminderMapper reminderMapper;

    @Autowired
    private CrmWecomContactGroupMapper contactGroupMapper;

    @Autowired
    private CrmContactMapper contactMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private WecomGroupReminderRuleSystemSeeder seeder;

    public void onBookingUpsert(CrmBooking booking)
    {
        if (booking == null || StringUtils.isBlank(booking.getContactId()) || booking.getStartTime() == null)
        {
            return;
        }
        String st = CrmBookingStatus.normalize(booking.getStatus());
        if (!CrmBookingStatus.SCHEDULED.equals(st) && !CrmBookingStatus.CONFIRMED.equals(st))
        {
            cancelBySourceRef("booking:" + booking.getBookingId());
            return;
        }
        try
        {
            String tenantId = TenantDataSourceSupport.currentTenantKey();
            seeder.ensure(tenantId);
            CrmContact contact = contactMapper.selectCrmContactByContactId(booking.getContactId());
            if (contact == null)
            {
                return;
            }
            List<CrmWecomGroupReminderRule> rules = ruleMapper.selectEnabledByAnchor(tenantId,
                    WecomContactGroupConstants.ANCHOR_BOOKING_START);
            CrmWecomContactGroup group = contactGroupMapper.selectByContactId(tenantId, booking.getContactId());
            String staff = resolveStaffName(contact);
            for (CrmWecomGroupReminderRule rule : rules)
            {
                Date dueAt = computeDueAt(booking.getStartTime().toInstant(), rule);
                if (dueAt == null)
                {
                    continue;
                }
                Map<String, String> vars = baseVars(contact, staff);
                ZonedDateTime startZ = booking.getStartTime().toInstant().atZone(SHANGHAI);
                vars.put("time", startZ.format(DATETIME_FMT));
                vars.put("date", startZ.format(DATE_FMT));
                vars.put("booking_name", nullToEmpty(booking.getBookingName()));
                vars.put("anchor_date", startZ.format(DATE_FMT));
                vars.put("offset_days", String.valueOf(Math.abs(rule.getOffsetValue() == null ? 0 : rule.getOffsetValue())));
                upsertReminder(tenantId, booking.getContactId(), rule, "booking:" + booking.getBookingId(),
                        group != null ? group.getChatId() : null, dueAt, vars);
            }
        }
        catch (Exception e)
        {
            log.warn("group reminder booking materialize failed: {}", e.getMessage());
        }
    }

    public void onBookingCancel(String bookingId)
    {
        if (StringUtils.isBlank(bookingId))
        {
            return;
        }
        cancelBySourceRef("booking:" + bookingId);
    }

    public void onOpportunityWon(CrmOpty opty)
    {
        if (opty == null || StringUtils.isBlank(opty.getContactId()))
        {
            return;
        }
        try
        {
            String tenantId = TenantDataSourceSupport.currentTenantKey();
            seeder.ensure(tenantId);
            CrmContact contact = contactMapper.selectCrmContactByContactId(opty.getContactId());
            if (contact == null)
            {
                return;
            }
            Instant anchor = opty.getCloseDate() != null ? opty.getCloseDate().toInstant() : Instant.now();
            List<CrmWecomGroupReminderRule> rules = ruleMapper.selectEnabledByAnchor(tenantId,
                    WecomContactGroupConstants.ANCHOR_OPPORTUNITY_WON);
            CrmWecomContactGroup group = contactGroupMapper.selectByContactId(tenantId, opty.getContactId());
            String staff = resolveStaffName(contact);
            for (CrmWecomGroupReminderRule rule : rules)
            {
                if (!matchTreatment(rule.getMatchJson(), opty.getTreatmentItemCode()))
                {
                    continue;
                }
                Date dueAt = computeDueAt(anchor, rule);
                if (dueAt == null)
                {
                    continue;
                }
                Map<String, String> vars = baseVars(contact, staff);
                ZonedDateTime az = anchor.atZone(SHANGHAI);
                vars.put("treatment", nullToEmpty(opty.getTreatmentItemCode()));
                vars.put("anchor_date", az.format(DATE_FMT));
                vars.put("date", dueAt.toInstant().atZone(SHANGHAI).format(DATE_FMT));
                vars.put("time", dueAt.toInstant().atZone(SHANGHAI).format(DATETIME_FMT));
                vars.put("offset_days", String.valueOf(Math.abs(rule.getOffsetValue() == null ? 0 : rule.getOffsetValue())));
                upsertReminder(tenantId, opty.getContactId(), rule, "opty:" + opty.getOptyId(),
                        group != null ? group.getChatId() : null, dueAt, vars);
            }
        }
        catch (Exception e)
        {
            log.warn("group reminder opty materialize failed: {}", e.getMessage());
        }
    }

    public void onRecallScheduleUpsert(CrmRecallSchedule schedule)
    {
        if (schedule == null || StringUtils.isBlank(schedule.getContactId()) || schedule.getNextDueAt() == null)
        {
            return;
        }
        String sourceRef = "recall-sched:" + schedule.getScheduleId();
        if (!"scheduled".equalsIgnoreCase(schedule.getStatus()))
        {
            cancelBySourceRef(sourceRef);
            return;
        }
        try
        {
            String tenantId = TenantDataSourceSupport.currentTenantKey();
            seeder.ensure(tenantId);
            CrmContact contact = contactMapper.selectCrmContactByContactId(schedule.getContactId());
            if (contact == null)
            {
                return;
            }
            List<CrmWecomGroupReminderRule> rules = ruleMapper.selectEnabledByAnchor(tenantId,
                    WecomContactGroupConstants.ANCHOR_RECALL_DUE);
            CrmWecomContactGroup group = contactGroupMapper.selectByContactId(tenantId, schedule.getContactId());
            String staff = resolveStaffName(contact);
            Instant anchor = schedule.getNextDueAt().toInstant();
            for (CrmWecomGroupReminderRule rule : rules)
            {
                Date dueAt = computeDueAt(anchor, rule);
                if (dueAt == null)
                {
                    continue;
                }
                Map<String, String> vars = baseVars(contact, staff);
                ZonedDateTime az = anchor.atZone(SHANGHAI);
                vars.put("treatment", nullToEmpty(schedule.getTreatmentItemCode()));
                vars.put("anchor_date", az.format(DATE_FMT));
                vars.put("date", az.format(DATE_FMT));
                vars.put("time", dueAt.toInstant().atZone(SHANGHAI).format(DATETIME_FMT));
                vars.put("offset_days", String.valueOf(Math.abs(rule.getOffsetValue() == null ? 0 : rule.getOffsetValue())));
                upsertReminder(tenantId, schedule.getContactId(), rule, sourceRef,
                        group != null ? group.getChatId() : null, dueAt, vars);
            }
        }
        catch (Exception e)
        {
            log.warn("group reminder recall materialize failed: {}", e.getMessage());
        }
    }

    public void cancelBySourceRef(String sourceRef)
    {
        try
        {
            String tenantId = TenantDataSourceSupport.currentTenantKey();
            if (StringUtils.isBlank(tenantId) || StringUtils.isBlank(sourceRef))
            {
                return;
            }
            reminderMapper.cancelBySourceRef(tenantId, sourceRef);
        }
        catch (Exception e)
        {
            log.warn("cancel reminder failed ref={}: {}", sourceRef, e.getMessage());
        }
    }

    private void upsertReminder(String tenantId, String contactId, CrmWecomGroupReminderRule rule, String sourceRef,
            String chatId, Date dueAt, Map<String, String> vars)
    {
        Date now = DateUtils.getNowDate();
        CrmWecomGroupReminder row = new CrmWecomGroupReminder();
        row.setId(IdUtils.fastUUID());
        row.setTenantId(tenantId);
        row.setContactId(contactId);
        row.setRuleId(rule.getRuleId());
        row.setSourceRef(sourceRef);
        row.setChatId(chatId);
        row.setDueAt(dueAt);
        row.setStatus(WecomContactGroupConstants.REMINDER_PENDING);
        row.setPayloadJson(JSON.toJSONString(vars));
        row.setRenderedText(render(rule.getTemplateBody(), vars));
        row.setRetryCount(0);
        row.setCreateTime(now);
        row.setUpdateTime(now);
        reminderMapper.upsertPending(row);
    }

    static Date computeDueAt(Instant anchor, CrmWecomGroupReminderRule rule)
    {
        if (anchor == null || rule == null)
        {
            return null;
        }
        int offset = rule.getOffsetValue() == null ? 0 : rule.getOffsetValue();
        String unit = rule.getOffsetUnit() == null ? WecomContactGroupConstants.OFFSET_DAYS : rule.getOffsetUnit();
        if (WecomContactGroupConstants.OFFSET_HOURS.equalsIgnoreCase(unit))
        {
            return Date.from(anchor.plusSeconds(offset * 3600L));
        }
        LocalDate day = anchor.atZone(SHANGHAI).toLocalDate().plusDays(offset);
        LocalTime send = LocalTime.of(9, 0);
        if (StringUtils.isNotBlank(rule.getSendTime()))
        {
            try
            {
                send = LocalTime.parse(rule.getSendTime().trim(), TIME_FMT);
            }
            catch (Exception ignored)
            {
            }
        }
        ZonedDateTime zdt = ZonedDateTime.of(day, send, SHANGHAI);
        return Date.from(zdt.toInstant());
    }

    static boolean matchTreatment(String matchJson, String treatmentItemCode)
    {
        if (StringUtils.isBlank(matchJson))
        {
            return true;
        }
        try
        {
            JSONObject obj = JSON.parseObject(matchJson);
            JSONArray arr = obj.getJSONArray("treatmentItemCodes");
            if (arr == null || arr.isEmpty())
            {
                return true;
            }
            if (StringUtils.isBlank(treatmentItemCode))
            {
                return false;
            }
            String code = TreatmentItemCodeNormalizer.normalize(treatmentItemCode);
            for (int i = 0; i < arr.size(); i++)
            {
                String item = arr.getString(i);
                if (item != null && code.equals(TreatmentItemCodeNormalizer.normalize(item)))
                {
                    return true;
                }
            }
            return false;
        }
        catch (Exception e)
        {
            return true;
        }
    }

    static String render(String template, Map<String, String> vars)
    {
        if (template == null)
        {
            return "";
        }
        String out = template;
        if (vars != null)
        {
            for (Map.Entry<String, String> e : vars.entrySet())
            {
                out = out.replace("{{" + e.getKey() + "}}", e.getValue() == null ? "" : e.getValue());
            }
        }
        return out.replaceAll("\\{\\{[^}]+\\}\\}", "");
    }

    private Map<String, String> baseVars(CrmContact contact, String staff)
    {
        Map<String, String> vars = new HashMap<>();
        vars.put("name", nullToEmpty(contact.getName()));
        vars.put("staff", nullToEmpty(staff));
        vars.put("clinic", "");
        vars.put("treatment", "");
        vars.put("booking_name", "");
        return vars;
    }

    private String resolveStaffName(CrmContact contact)
    {
        if (contact.getUserId() != null)
        {
            SysUser user = sysUserMapper.selectUserById(contact.getUserId());
            if (user != null)
            {
                if (StringUtils.isNotBlank(user.getNickName()))
                {
                    return user.getNickName();
                }
                return user.getUserName();
            }
        }
        return nullToEmpty(contact.getOwner());
    }

    private static String nullToEmpty(String s)
    {
        return s == null ? "" : s;
    }
}
