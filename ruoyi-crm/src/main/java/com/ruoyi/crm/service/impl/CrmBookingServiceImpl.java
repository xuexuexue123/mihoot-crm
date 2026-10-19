package com.ruoyi.crm.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmBooking;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.mapper.CrmBookingMapper;
import com.ruoyi.crm.service.ICrmBookingService;
import com.ruoyi.crm.service.followup.FollowupRuleHookSupport;
import com.ruoyi.crm.service.support.CrmBookingStatus;
import com.ruoyi.crm.service.webhook.OutboundWebhookHookSupport;
import com.ruoyi.crm.service.wecom.contactgroup.WecomContactGroupService;
import com.ruoyi.crm.service.wecom.contactgroup.WecomGroupReminderMaterializer;

/**
 * 诊所上门预约 Service 实现
 */
@Service
public class CrmBookingServiceImpl implements ICrmBookingService
{
    private static final ZoneId SHANGHAI = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    @Autowired
    private CrmBookingMapper crmBookingMapper;

    @Autowired
    private ActivityEventProducer activityEventProducer;

    @Autowired
    private FollowupRuleHookSupport followupRuleHookSupport;

    @Autowired
    private OutboundWebhookHookSupport outboundWebhookHookSupport;

    @Autowired
    private WecomContactGroupService wecomContactGroupService;

    @Autowired
    private WecomGroupReminderMaterializer wecomGroupReminderMaterializer;

    @Override
    public CrmBooking selectCrmBookingByBookingId(String bookingId)
    {
        return crmBookingMapper.selectCrmBookingByBookingId(bookingId);
    }

    /**
     * 详情查询（带 DataScope，满足已有主体约诊验收 S8）。
     * 须传入含 bookingId 的 CrmBooking，以便 DataScopeAspect 写入 params。
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public CrmBooking selectCrmBookingByBookingIdScoped(CrmBooking query)
    {
        if (query == null || StringUtils.isBlank(query.getBookingId()))
        {
            return null;
        }
        List<CrmBooking> list = crmBookingMapper.selectCrmBookingList(query);
        return list == null || list.isEmpty() ? null : list.get(0);
    }

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmBooking> selectCrmBookingList(CrmBooking crmBooking)
    {
        prepareListFilters(crmBooking);
        return crmBookingMapper.selectCrmBookingList(crmBooking);
    }

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmBooking> selectCrmBookingListByDay(CrmBooking crmBooking, String date)
    {
        if (StringUtils.isBlank(date))
        {
            throw new ServiceException("date 不能为空", HttpStatus.BAD_REQUEST);
        }
        LocalDate day;
        try
        {
            day = LocalDate.parse(date.trim(), DAY_FMT);
        }
        catch (DateTimeParseException ex)
        {
            throw new ServiceException("date 格式须为 YYYY-MM-DD", HttpStatus.BAD_REQUEST);
        }
        ZonedDateTime start = day.atStartOfDay(SHANGHAI);
        ZonedDateTime end = start.plusDays(1);
        crmBooking.setDayStart(Date.from(start.toInstant()));
        crmBooking.setDayEnd(Date.from(end.toInstant()));
        prepareStatusFilter(crmBooking);
        if (crmBooking.getIncludeCancelled() == null)
        {
            crmBooking.setIncludeCancelled(Boolean.FALSE);
        }
        return crmBookingMapper.selectCrmBookingListByDay(crmBooking);
    }

    @Override
    @Transactional
    public int insertCrmBooking(CrmBooking crmBooking)
    {
        normalizeSubjectIds(crmBooking);
        validateSubjectAndTime(crmBooking);
        if (StringUtils.isBlank(crmBooking.getBookingId()))
        {
            crmBooking.setBookingId(IdUtils.fastUUID());
        }
        if (StringUtils.isBlank(crmBooking.getSource()))
        {
            crmBooking.setSource("manual");
        }
        if (StringUtils.isBlank(crmBooking.getStatus()))
        {
            crmBooking.setStatus(CrmBookingStatus.SCHEDULED);
        }
        else
        {
            String status = CrmBookingStatus.normalize(crmBooking.getStatus());
            if (!CrmBookingStatus.isValid(status))
            {
                throw new ServiceException("非法预约状态: " + crmBooking.getStatus(), HttpStatus.BAD_REQUEST);
            }
            crmBooking.setStatus(status);
        }
        crmBooking.setCreateTime(DateUtils.getNowDate());
        try
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null && loginUser.getUser() != null)
            {
                if (crmBooking.getUserId() == null)
                {
                    crmBooking.setUserId(loginUser.getUserId());
                }
                if (crmBooking.getDeptId() == null)
                {
                    crmBooking.setDeptId(loginUser.getDeptId());
                }
                if (StringUtils.isBlank(crmBooking.getOwner()))
                {
                    crmBooking.setOwner(loginUser.getUser().getNickName());
                }
                if (StringUtils.isBlank(crmBooking.getCreateBy()))
                {
                    crmBooking.setCreateBy(loginUser.getUsername());
                }
            }
        }
        catch (Exception ignored)
        {
            // public / dual-write paths may have no login user
        }
        int rows = crmBookingMapper.insertCrmBooking(crmBooking);
        if (rows > 0)
        {
            tryProvisionAndRemind(crmBooking);
        }
        return rows;
    }

    /** after insert/update hooks for wecom group + reminders */
    private void tryProvisionAndRemind(CrmBooking crmBooking)
    {
        try
        {
            if (StringUtils.isNotBlank(crmBooking.getContactId()))
            {
                try
                {
                    wecomContactGroupService.provision(crmBooking.getContactId());
                }
                catch (Exception e)
                {
                    // provision may fail if no wecom id — ignore
                }
                wecomGroupReminderMaterializer.onBookingUpsert(crmBooking);
            }
        }
        catch (Exception ignored)
        {
        }
    }

    @Override
    @Transactional
    public int updateCrmBooking(CrmBooking crmBooking)
    {
        if (StringUtils.isBlank(crmBooking.getBookingId()))
        {
            throw new ServiceException("bookingId 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmBooking existing = crmBookingMapper.selectCrmBookingByBookingId(crmBooking.getBookingId());
        if (existing == null)
        {
            throw new ServiceException("预约不存在", HttpStatus.NOT_FOUND);
        }

        normalizeSubjectIds(crmBooking);

        String nextStatus = crmBooking.getStatus() != null
                ? CrmBookingStatus.normalize(crmBooking.getStatus())
                : existing.getStatus();
        if (crmBooking.getStatus() != null)
        {
            CrmBookingStatus.assertTransition(existing.getStatus(), nextStatus);
            crmBooking.setStatus(nextStatus);
        }

        Date start = crmBooking.getStartTime() != null ? crmBooking.getStartTime() : existing.getStartTime();
        Date end = crmBooking.getEndTime() != null ? crmBooking.getEndTime() : existing.getEndTime();
        if (start == null || end == null || !end.after(start))
        {
            throw new ServiceException("结束时间必须晚于开始时间", HttpStatus.BAD_REQUEST);
        }

        String leadId = crmBooking.getLeadId() != null ? crmBooking.getLeadId() : existing.getLeadId();
        String contactId = crmBooking.getContactId() != null ? crmBooking.getContactId() : existing.getContactId();
        if (StringUtils.isBlank(leadId) && StringUtils.isBlank(contactId))
        {
            throw new ServiceException("leadId 与 contactId 至少填一个", HttpStatus.BAD_REQUEST);
        }

        crmBooking.setUpdateTime(DateUtils.getNowDate());
        try
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null && StringUtils.isBlank(crmBooking.getUpdateBy()))
            {
                crmBooking.setUpdateBy(loginUser.getUsername());
            }
        }
        catch (Exception ignored)
        {
            // ignore
        }

        int rows = crmBookingMapper.updateCrmBooking(crmBooking);
        if (rows > 0 && CrmBookingStatus.NOSHOW.equals(nextStatus)
                && !CrmBookingStatus.NOSHOW.equals(CrmBookingStatus.normalize(existing.getStatus())))
        {
            CrmBooking updated = resolveBookingAfterUpdate(crmBooking, existing);
            String sourceRef = activityEventProducer.produceBookingNoshow(updated);
            followupRuleHookSupport.onBookingNoshow(updated, sourceRef);
            outboundWebhookHookSupport.onBookingNoshow(updated, sourceRef);
        }
        if (rows > 0 && CrmBookingStatus.COMPLETED.equals(nextStatus)
                && !CrmBookingStatus.COMPLETED.equals(CrmBookingStatus.normalize(existing.getStatus())))
        {
            CrmBooking updated = resolveBookingAfterUpdate(crmBooking, existing);
            activityEventProducer.produceBookingCompleted(updated);
        }
        if (rows > 0)
        {
            CrmBooking latest = resolveBookingAfterUpdate(crmBooking, existing);
            if (StringUtils.isBlank(latest.getStatus()))
            {
                latest.setStatus(nextStatus);
            }
            try
            {
                wecomGroupReminderMaterializer.onBookingUpsert(latest);
            }
            catch (Exception ignored)
            {
            }
        }
        return rows;
    }

    /**
     * 更新后取库中最新行；失败时回退到请求体并补齐 existing 字段。
     */
    private CrmBooking resolveBookingAfterUpdate(CrmBooking crmBooking, CrmBooking existing)
    {
        CrmBooking updated = crmBookingMapper.selectCrmBookingByBookingId(crmBooking.getBookingId());
        if (updated != null)
        {
            return updated;
        }
        updated = crmBooking;
        if (updated.getStartTime() == null)
        {
            updated.setStartTime(existing.getStartTime());
        }
        if (StringUtils.isBlank(updated.getLeadId()))
        {
            updated.setLeadId(existing.getLeadId());
        }
        if (StringUtils.isBlank(updated.getContactId()))
        {
            updated.setContactId(existing.getContactId());
        }
        if (StringUtils.isBlank(updated.getBookingName()))
        {
            updated.setBookingName(existing.getBookingName());
        }
        if (StringUtils.isBlank(updated.getOwner()))
        {
            updated.setOwner(existing.getOwner());
        }
        if (updated.getUserId() == null)
        {
            updated.setUserId(existing.getUserId());
        }
        if (updated.getDeptId() == null)
        {
            updated.setDeptId(existing.getDeptId());
        }
        return updated;
    }

    @Override
    @Transactional
    public int cancelCrmBookingByBookingId(String bookingId)
    {
        CrmBooking existing = crmBookingMapper.selectCrmBookingByBookingId(bookingId);
        if (existing == null)
        {
            throw new ServiceException("预约不存在", HttpStatus.NOT_FOUND);
        }
        CrmBookingStatus.assertTransition(existing.getStatus(), CrmBookingStatus.CANCELLED);
        CrmBooking patch = new CrmBooking();
        patch.setBookingId(bookingId);
        patch.setStatus(CrmBookingStatus.CANCELLED);
        patch.setUpdateTime(DateUtils.getNowDate());
        try
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null)
            {
                patch.setUpdateBy(loginUser.getUsername());
            }
        }
        catch (Exception ignored)
        {
            // ignore
        }
        int rows = crmBookingMapper.cancelCrmBookingByBookingId(patch);
        if (rows > 0)
        {
            try
            {
                wecomGroupReminderMaterializer.onBookingCancel(bookingId);
            }
            catch (Exception ignored)
            {
            }
        }
        return rows;
    }

    private void validateSubjectAndTime(CrmBooking crmBooking)
    {
        if (crmBooking.getStartTime() == null || crmBooking.getEndTime() == null)
        {
            throw new ServiceException("startTime / endTime 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (!crmBooking.getEndTime().after(crmBooking.getStartTime()))
        {
            throw new ServiceException("结束时间必须晚于开始时间", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(crmBooking.getLeadId()) && StringUtils.isBlank(crmBooking.getContactId()))
        {
            throw new ServiceException("leadId 与 contactId 至少填一个", HttpStatus.BAD_REQUEST);
        }
    }

    /** 空白主体 ID 规范为 null，保证「仅 contactId」写入时 lead_id 真正为空（S2） */
    private static void normalizeSubjectIds(CrmBooking crmBooking)
    {
        if (crmBooking == null)
        {
            return;
        }
        if (StringUtils.isBlank(crmBooking.getLeadId()))
        {
            crmBooking.setLeadId(null);
        }
        if (StringUtils.isBlank(crmBooking.getContactId()))
        {
            crmBooking.setContactId(null);
        }
    }

    private void prepareListFilters(CrmBooking crmBooking)
    {
        prepareStatusFilter(crmBooking);
        Object leadIdsRaw = crmBooking.getParams() != null ? crmBooking.getParams().get("leadIds") : null;
        if (leadIdsRaw != null && StringUtils.isNotBlank(String.valueOf(leadIdsRaw)))
        {
            crmBooking.setLeadIdList(splitCsv(String.valueOf(leadIdsRaw)));
        }
    }

    private void prepareStatusFilter(CrmBooking crmBooking)
    {
        if (StringUtils.isNotBlank(crmBooking.getStatus()) && crmBooking.getStatus().contains(","))
        {
            crmBooking.setStatusList(splitCsv(crmBooking.getStatus()));
        }
    }

    private static List<String> splitCsv(String csv)
    {
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }
}
