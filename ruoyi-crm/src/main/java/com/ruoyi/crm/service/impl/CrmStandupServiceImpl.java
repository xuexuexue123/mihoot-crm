package com.ruoyi.crm.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.crm.domain.CrmLeadPoolConfig;
import com.ruoyi.crm.domain.CrmSalesgoal;
import com.ruoyi.crm.domain.CrmTimeperiod;
import com.ruoyi.crm.domain.dto.StandupActivityRow;
import com.ruoyi.crm.domain.dto.StandupBookingAgendaRow;
import com.ruoyi.crm.domain.dto.StandupBookingCountRow;
import com.ruoyi.crm.domain.dto.StandupContractWinRow;
import com.ruoyi.crm.domain.dto.StandupIdleLeadRow;
import com.ruoyi.crm.domain.dto.StandupStaleOptyRow;
import com.ruoyi.crm.domain.enums.SalesKpiCode;
import com.ruoyi.crm.domain.vo.AssignableUserVo;
import com.ruoyi.crm.domain.vo.StandupActivityBrief;
import com.ruoyi.crm.domain.vo.StandupAgendaItem;
import com.ruoyi.crm.domain.vo.StandupDailyVo;
import com.ruoyi.crm.domain.vo.StandupDayMetrics;
import com.ruoyi.crm.domain.vo.StandupKpiSlice;
import com.ruoyi.crm.domain.vo.StandupMemberDetailVo;
import com.ruoyi.crm.domain.vo.StandupMemberKpi;
import com.ruoyi.crm.domain.vo.StandupMemberRow;
import com.ruoyi.crm.domain.vo.StandupMemberToday;
import com.ruoyi.crm.domain.vo.StandupPeriodBrief;
import com.ruoyi.crm.domain.vo.StandupRiskItem;
import com.ruoyi.crm.domain.vo.StandupTeamSummary;
import com.ruoyi.crm.domain.vo.StandupWinItem;
import com.ruoyi.crm.mapper.CrmSalesgoalMapper;
import com.ruoyi.crm.mapper.StandupMapper;
import com.ruoyi.crm.service.IAssignableUserService;
import com.ruoyi.crm.service.ICrmLeadPoolConfigService;
import com.ruoyi.crm.service.ICrmStandupService;
import com.ruoyi.crm.service.ICrmTimeperiodService;
import com.ruoyi.crm.service.ISalesKpiActualService;
import com.ruoyi.crm.service.support.LeadRecyclePolicyDefaults;
import com.ruoyi.crm.service.support.OptyStageKpiSupport;
import com.ruoyi.crm.service.support.StandupSupport;
import com.ruoyi.system.domain.dto.AssignableUserQuery;
import com.ruoyi.system.service.ISysUserService;

/**
 * 销售晨会报告聚合（实时，不落库）。
 */
@Service
public class CrmStandupServiceImpl implements ICrmStandupService
{
    private static final String PERIOD_TYPE_MONTHLY = "Monthly";
    private static final String PERM_TEAM = "crm:standup:team";
    private static final String PERM_ALL = "*:*:*";
    private static final long IS_ACTIVE_ASSESSING = 0L;
    private static final int ACTIVITY_LIMIT = 100;
    private static final int RISK_LIMIT = 200;
    private static final int STALE_OPTY_DAYS = 7;
    private static final int POOL_IMMINENT_HOURS = 24;

    private static final SalesKpiCode[] DAY_KPIS = {
            SalesKpiCode.CONTRACT_AMOUNT,
            SalesKpiCode.WON_CONTRACT_COUNT,
            SalesKpiCode.FOLLOWUP_COUNT,
            SalesKpiCode.FOLLOWED_CUSTOMERS,
            SalesKpiCode.MET_CUSTOMERS,
            SalesKpiCode.ASSIGNED_CUSTOMERS,
            SalesKpiCode.OPTY_COUNT,
            SalesKpiCode.OPTY_AMOUNT
    };

    @Autowired
    private ISalesKpiActualService salesKpiActualService;

    @Autowired
    private IAssignableUserService assignableUserService;

    @Autowired
    private ICrmTimeperiodService crmTimeperiodService;

    @Autowired
    private CrmSalesgoalMapper crmSalesgoalMapper;

    @Autowired
    private StandupMapper standupMapper;

    @Autowired
    private ICrmLeadPoolConfigService leadPoolConfigService;

    @Autowired
    private OptyStageKpiSupport optyStageKpiSupport;

    @Autowired
    private ISysUserService sysUserService;

    @Value("${crm.salesgoal.ensure-role-keys:common,managers}")
    private String ensureRoleKeys;

    @Override
    public StandupDailyVo daily(String date, Long deptId, String ownerId, Boolean includeKpi, String kpiCodes)
    {
        LocalDate reportDay = StandupSupport.parseReportDate(date);
        LocalDate yesterday = reportDay.minusDays(1);
        MemberScope scope = resolveMemberScope(deptId, ownerId);

        StandupDailyVo vo = new StandupDailyVo();
        vo.setDate(reportDay.toString());
        vo.setTimezone(StandupSupport.TIMEZONE_ID);
        vo.setYesterday(StandupSupport.windowOf(yesterday));
        vo.setToday(StandupSupport.windowOf(reportDay));

        List<String> ownerIds = scope.ownerIds;
        Map<String, StandupDayMetrics> yesterdayMap = aggregateDayMetrics(ownerIds, yesterday);
        Map<String, BigDecimal> todayBookings = aggregateBookingCounts(ownerIds, reportDay, false);

        List<StandupMemberRow> members = new ArrayList<>();
        StandupDayMetrics teamYesterday = new StandupDayMetrics();
        long todayBookingTotal = 0L;
        for (MemberInfo mi : scope.members)
        {
            StandupMemberRow row = new StandupMemberRow();
            row.setOwnerId(mi.ownerId);
            row.setOwnerName(mi.ownerName);
            row.setDeptId(mi.deptId);
            row.setDeptName(mi.deptName);
            row.setAvatar(mi.avatar);

            StandupDayMetrics ym = yesterdayMap.getOrDefault(mi.ownerId, emptyMetrics());
            ym.refreshZeroAction();
            row.setYesterday(ym);
            teamYesterday.add(ym);

            StandupMemberToday today = new StandupMemberToday();
            BigDecimal bc = todayBookings.getOrDefault(mi.ownerId, BigDecimal.ZERO);
            today.setBookingCount(bc);
            row.setToday(today);
            todayBookingTotal += bc.longValue();

            List<String> flags = new ArrayList<>();
            if (ym.isZeroAction())
            {
                flags.add(StandupSupport.FLAG_ZERO_ACTION);
            }
            row.setFlags(flags);
            members.add(row);
        }

        boolean doKpi = includeKpi == null || includeKpi.booleanValue();
        CrmTimeperiod period = resolveMonthlyPeriod(reportDay);
        if (period != null)
        {
            StandupPeriodBrief brief = new StandupPeriodBrief();
            brief.setPeriodId(period.getPeriodId());
            brief.setPeriodName(period.getPeriodName());
            brief.setPeriodType(period.getPeriodType());
            vo.setPeriod(brief);
        }
        else
        {
            vo.setPeriod(null);
        }

        List<SalesKpiCode> codes = parseAndValidateKpiCodes(kpiCodes);
        if (doKpi && period != null)
        {
            fillMonthKpis(members, period, codes, ownerIds);
            vo.setMonthProgress(buildMonthProgress(period, codes, ownerIds));
        }
        else
        {
            vo.setMonthProgress(Collections.emptyList());
            for (StandupMemberRow row : members)
            {
                row.setMonthKpis(Collections.emptyList());
            }
        }

        StandupSupport.sortMembers(members);
        vo.setMembers(members);

        StandupTeamSummary summary = new StandupTeamSummary();
        summary.setYesterday(teamYesterday);
        summary.setTodayBookingCount(todayBookingTotal);
        vo.setTeamSummary(summary);
        return vo;
    }

    @Override
    public List<StandupAgendaItem> agenda(String date, String type, Long deptId, String ownerId,
            Boolean includeCancelled)
    {
        String t = StringUtils.hasText(type) ? type.trim() : "booking";
        if ("followup_due".equalsIgnoreCase(t))
        {
            return Collections.emptyList();
        }
        if (!"booking".equalsIgnoreCase(t) && !"all".equalsIgnoreCase(t))
        {
            return Collections.emptyList();
        }
        LocalDate reportDay = StandupSupport.parseReportDate(date);
        MemberScope scope = resolveMemberScope(deptId, ownerId);
        if (CollectionUtils.isEmpty(scope.ownerIds))
        {
            return Collections.emptyList();
        }
        List<StandupBookingAgendaRow> rows = standupMapper.selectAgendaBookings(
                scope.ownerIds,
                StandupSupport.dayStart(reportDay),
                StandupSupport.dayEndExclusive(reportDay),
                includeCancelled != null && includeCancelled,
                null);
        return mapAgendaItems(rows, scope.nameById);
    }

    @Override
    public List<StandupRiskItem> risks(String date, String types, Long deptId, String ownerId)
    {
        assertTeamOrSelf(ownerId);
        LocalDate reportDay = StandupSupport.parseReportDate(date);
        MemberScope scope = resolveMemberScope(deptId, ownerId);
        if (CollectionUtils.isEmpty(scope.ownerIds))
        {
            return Collections.emptyList();
        }
        Set<String> typeSet = parseRiskTypes(types);
        List<StandupRiskItem> items = new ArrayList<>();

        if (typeSet.contains("today_unconfirmed"))
        {
            List<StandupBookingAgendaRow> bookings = standupMapper.selectAgendaBookings(
                    scope.ownerIds,
                    StandupSupport.dayStart(reportDay),
                    StandupSupport.dayEndExclusive(reportDay),
                    false,
                    null);
            for (StandupBookingAgendaRow b : bookings)
            {
                if (b.getStatus() == null || !"scheduled".equalsIgnoreCase(b.getStatus().trim()))
                {
                    continue;
                }
                StandupRiskItem item = new StandupRiskItem();
                item.setRiskId("today_unconfirmed:" + b.getBookingId());
                item.setRiskType("today_unconfirmed");
                item.setSeverity("medium");
                item.setReason("今日预约尚未确认");
                fillBookingRiskSubject(item, b);
                String oid = b.getUserId() != null ? String.valueOf(b.getUserId()) : null;
                item.setOwnerId(oid);
                item.setOwnerName(scope.nameById.getOrDefault(oid, b.getOwner()));
                items.add(item);
            }
        }

        CrmLeadPoolConfig pool = leadPoolConfigService.getConfig();
        int idleHours = LeadRecyclePolicyDefaults.resolveIdleHours(
                pool != null ? pool.getIdleToPoolHours() : null,
                pool != null ? pool.getProtectDays() : null);

        if (typeSet.contains("idle_followup"))
        {
            List<StandupIdleLeadRow> leads = standupMapper.selectIdleLeads(scope.ownerIds, idleHours, RISK_LIMIT);
            for (StandupIdleLeadRow lead : leads)
            {
                StandupRiskItem item = new StandupRiskItem();
                item.setRiskId("idle_followup:" + lead.getLeadId());
                item.setRiskType("idle_followup");
                item.setSeverity("high");
                item.setReason("线索超过 " + idleHours + " 小时未跟进");
                item.setSubjectType("lead");
                item.setSubjectId(lead.getLeadId());
                item.setSubjectName(lead.getLeadName());
                item.setOwnerId(lead.getOwnerId());
                item.setOwnerName(scope.nameById.getOrDefault(lead.getOwnerId(), lead.getOwnerName()));
                item.setIdleHours(lead.getIdleHours());
                item.setLastFollowupAt(StandupSupport.formatIso(lead.getLastFollowupAt()));
                item.setDeepLink("/leads/" + lead.getLeadId());
                items.add(item);
            }
        }

        if (typeSet.contains("pool_imminent"))
        {
            List<StandupIdleLeadRow> leads = standupMapper.selectPoolImminentLeads(
                    scope.ownerIds, idleHours, POOL_IMMINENT_HOURS, RISK_LIMIT);
            for (StandupIdleLeadRow lead : leads)
            {
                StandupRiskItem item = new StandupRiskItem();
                item.setRiskId("pool_imminent:" + lead.getLeadId());
                item.setRiskType("pool_imminent");
                item.setSeverity("high");
                item.setReason("即将进入公海（剩余不足 " + POOL_IMMINENT_HOURS + " 小时）");
                item.setSubjectType("lead");
                item.setSubjectId(lead.getLeadId());
                item.setSubjectName(lead.getLeadName());
                item.setOwnerId(lead.getOwnerId());
                item.setOwnerName(scope.nameById.getOrDefault(lead.getOwnerId(), lead.getOwnerName()));
                item.setIdleHours(lead.getIdleHours());
                item.setPoolEtaHours(lead.getPoolEtaHours());
                item.setLastFollowupAt(StandupSupport.formatIso(lead.getLastFollowupAt()));
                item.setDeepLink("/leads/" + lead.getLeadId());
                items.add(item);
            }
        }

        if (typeSet.contains("stale_opty"))
        {
            List<StandupStaleOptyRow> opties = standupMapper.selectStaleOpties(scope.ownerIds, STALE_OPTY_DAYS, RISK_LIMIT);
            for (StandupStaleOptyRow o : opties)
            {
                if (optyStageKpiSupport.isTerminalOpty(o.getStage(), o.getCloseReason()))
                {
                    continue;
                }
                StandupRiskItem item = new StandupRiskItem();
                item.setRiskId("stale_opty:" + o.getOptyId());
                item.setRiskType("stale_opty");
                item.setSeverity("medium");
                item.setReason("商机超过 " + STALE_OPTY_DAYS + " 天未推进");
                item.setSubjectType("opty");
                item.setSubjectId(o.getOptyId());
                item.setSubjectName(o.getOptyName());
                item.setOwnerId(o.getOwnerId());
                item.setOwnerName(scope.nameById.getOrDefault(o.getOwnerId(), o.getOwnerName()));
                item.setOptyAmount(o.getAmount());
                item.setLastFollowupAt(StandupSupport.formatIso(o.getLastFollowupAt()));
                item.setDeepLink("/opportunities/" + o.getOptyId());
                items.add(item);
            }
        }
        return items;
    }

    @Override
    public List<StandupWinItem> wins(String date, Long deptId, String ownerId)
    {
        assertTeamOrSelf(ownerId);
        LocalDate reportDay = StandupSupport.parseReportDate(date);
        LocalDate yesterday = reportDay.minusDays(1);
        MemberScope scope = resolveMemberScope(deptId, ownerId);
        if (CollectionUtils.isEmpty(scope.ownerIds))
        {
            return Collections.emptyList();
        }
        List<StandupContractWinRow> rows = standupMapper.selectWins(
                scope.ownerIds,
                StandupSupport.calendarDayDate(yesterday),
                StandupSupport.calendarDayDate(yesterday));
        List<StandupWinItem> list = new ArrayList<>();
        for (StandupContractWinRow row : rows)
        {
            StandupWinItem item = new StandupWinItem();
            item.setContractId(row.getContractId());
            item.setContractName(row.getContractName());
            item.setSignAmount(row.getSignAmount());
            item.setSignTime(StandupSupport.formatIso(row.getSignTime()));
            item.setOwnerId(row.getOwnerId());
            item.setOwnerName(scope.nameById.getOrDefault(row.getOwnerId(), row.getOwnerName()));
            item.setContactId(row.getContactId());
            item.setContactName(row.getContactName());
            item.setAccountName(row.getAccountName());
            item.setDeepLink("/contracts/" + row.getContractId());
            list.add(item);
        }
        return list;
    }

    @Override
    public StandupMemberDetailVo memberDetail(String ownerId, String date)
    {
        if (!StringUtils.hasText(ownerId))
        {
            throw new ServiceException("ownerId 不能为空", HttpStatus.BAD_REQUEST);
        }
        assertCanViewMember(ownerId.trim());
        LocalDate reportDay = StandupSupport.parseReportDate(date);
        LocalDate yesterday = reportDay.minusDays(1);
        String oid = ownerId.trim();

        StandupMemberDetailVo vo = new StandupMemberDetailVo();
        vo.setOwnerId(oid);
        vo.setOwnerName(resolveOwnerName(oid));

        Map<String, StandupDayMetrics> metrics = aggregateDayMetrics(Collections.singletonList(oid), yesterday);
        StandupDayMetrics ym = metrics.getOrDefault(oid, emptyMetrics());
        ym.refreshZeroAction();
        vo.setYesterday(ym);

        List<StandupActivityRow> acts = standupMapper.selectYesterdayActivities(
                oid,
                StandupSupport.dayStart(yesterday),
                StandupSupport.dayEndExclusive(yesterday),
                ACTIVITY_LIMIT);
        List<StandupActivityBrief> briefs = new ArrayList<>();
        if (acts != null)
        {
            for (StandupActivityRow a : acts)
            {
                StandupActivityBrief b = new StandupActivityBrief();
                b.setPulseId(a.getPulseId());
                b.setEventType(a.getEventType());
                b.setEventCategory(a.getEventCategory());
                b.setStartedDate(StandupSupport.formatIso(a.getStartedDate()));
                b.setRelateName(a.getRelateName());
                b.setSubjectType(a.getSubjectType());
                b.setSubjectId(a.getSubjectId());
                briefs.add(b);
            }
        }
        vo.setYesterdayActivities(briefs);

        List<StandupBookingAgendaRow> bookings = standupMapper.selectAgendaBookings(
                Collections.singletonList(oid),
                StandupSupport.dayStart(reportDay),
                StandupSupport.dayEndExclusive(reportDay),
                false,
                null);
        Map<String, String> names = new HashMap<>();
        names.put(oid, vo.getOwnerName());
        vo.setTodayBookings(mapAgendaItems(bookings, names));

        CrmTimeperiod period = resolveMonthlyPeriod(reportDay);
        if (period != null)
        {
            List<SalesKpiCode> codes = parseAndValidateKpiCodes(null);
            List<StandupMemberRow> tmp = new ArrayList<>();
            StandupMemberRow row = new StandupMemberRow();
            row.setOwnerId(oid);
            tmp.add(row);
            fillMonthKpis(tmp, period, codes, Collections.singletonList(oid));
            vo.setMonthKpis(tmp.get(0).getMonthKpis());
        }
        else
        {
            vo.setMonthKpis(Collections.emptyList());
        }
        return vo;
    }

    // -------------------------------------------------------------------------
    // helpers
    // -------------------------------------------------------------------------

    private Map<String, StandupDayMetrics> aggregateDayMetrics(List<String> ownerIds, LocalDate day)
    {
        Map<String, StandupDayMetrics> result = new HashMap<>();
        if (CollectionUtils.isEmpty(ownerIds))
        {
            return result;
        }
        for (String oid : ownerIds)
        {
            result.put(oid, emptyMetrics());
        }
        java.util.Date d = StandupSupport.calendarDayDate(day);
        for (SalesKpiCode kpi : DAY_KPIS)
        {
            Map<String, BigDecimal> map = salesKpiActualService.aggregate(ownerIds, d, d, kpi);
            for (Map.Entry<String, BigDecimal> e : map.entrySet())
            {
                StandupDayMetrics m = result.computeIfAbsent(e.getKey(), k -> emptyMetrics());
                applyKpi(m, kpi, e.getValue());
            }
        }
        Map<String, BigDecimal> bookings = aggregateBookingCounts(ownerIds, day, false);
        for (Map.Entry<String, BigDecimal> e : bookings.entrySet())
        {
            StandupDayMetrics m = result.computeIfAbsent(e.getKey(), k -> emptyMetrics());
            m.setBookingCount(e.getValue());
        }
        for (StandupDayMetrics m : result.values())
        {
            m.refreshZeroAction();
        }
        return result;
    }

    private void applyKpi(StandupDayMetrics m, SalesKpiCode kpi, BigDecimal value)
    {
        BigDecimal v = value != null ? value : BigDecimal.ZERO;
        switch (kpi)
        {
            case CONTRACT_AMOUNT:
                m.setContractAmount(v);
                break;
            case WON_CONTRACT_COUNT:
                m.setWonContractCount(v);
                break;
            case FOLLOWUP_COUNT:
                m.setFollowupCount(v);
                break;
            case FOLLOWED_CUSTOMERS:
                m.setFollowedCustomers(v);
                break;
            case MET_CUSTOMERS:
                m.setMetCustomers(v);
                break;
            case ASSIGNED_CUSTOMERS:
                m.setAssignedCustomers(v);
                break;
            case OPTY_COUNT:
                m.setOptyCount(v);
                break;
            case OPTY_AMOUNT:
                m.setOptyAmount(v);
                break;
            default:
                break;
        }
    }

    private Map<String, BigDecimal> aggregateBookingCounts(List<String> ownerIds, LocalDate day,
            boolean includeCancelled)
    {
        if (CollectionUtils.isEmpty(ownerIds))
        {
            return Collections.emptyMap();
        }
        List<StandupBookingCountRow> rows = standupMapper.countBookingsByOwner(
                ownerIds,
                StandupSupport.dayStart(day),
                StandupSupport.dayEndExclusive(day),
                includeCancelled);
        Map<String, BigDecimal> map = new HashMap<>();
        if (rows != null)
        {
            for (StandupBookingCountRow row : rows)
            {
                if (row.getOwnerId() != null)
                {
                    map.put(row.getOwnerId(),
                            row.getBookingCount() != null ? row.getBookingCount() : BigDecimal.ZERO);
                }
            }
        }
        return map;
    }

    private void fillMonthKpis(List<StandupMemberRow> members, CrmTimeperiod period,
            List<SalesKpiCode> codes, List<String> ownerIds)
    {
        java.util.Date start = period.getStartDate();
        java.util.Date end = period.getEndDate();
        Map<String, Map<String, CrmSalesgoal>> goalsByOwner = loadGoalsByOwner(period.getPeriodId(), codes, ownerIds);

        Map<SalesKpiCode, Map<String, BigDecimal>> actuals = new HashMap<>();
        for (SalesKpiCode code : codes)
        {
            actuals.put(code, salesKpiActualService.aggregate(ownerIds, start, end, code));
        }

        for (StandupMemberRow row : members)
        {
            List<StandupMemberKpi> kpis = new ArrayList<>();
            Map<String, CrmSalesgoal> ownerGoals = goalsByOwner.getOrDefault(row.getOwnerId(), Collections.emptyMap());
            for (SalesKpiCode code : codes)
            {
                StandupMemberKpi mk = new StandupMemberKpi();
                mk.setKpiCode(code.getCode());
                CrmSalesgoal goal = ownerGoals.get(code.getCode());
                if (goal != null)
                {
                    mk.setGoalValue(goal.getGoalValue());
                    mk.setIsActive(goal.getIsActive() != null && goal.getIsActive() == IS_ACTIVE_ASSESSING ? "0" : "1");
                }
                else
                {
                    mk.setGoalValue(null);
                    mk.setIsActive("1");
                }
                BigDecimal actual = actuals.get(code).get(row.getOwnerId());
                if (code == SalesKpiCode.WIN_RATE)
                {
                    mk.setActualValue(actual);
                }
                else
                {
                    mk.setActualValue(actual != null ? actual : BigDecimal.ZERO);
                }
                mk.setCompletionRate(CrmSalesgoalServiceImpl.computeCompletionRate(mk.getGoalValue(), mk.getActualValue()));
                kpis.add(mk);
            }
            row.setMonthKpis(kpis);
        }
    }

    private List<StandupKpiSlice> buildMonthProgress(CrmTimeperiod period, List<SalesKpiCode> codes,
            List<String> ownerIds)
    {
        java.util.Date start = period.getStartDate();
        java.util.Date end = period.getEndDate();
        Map<String, Map<String, CrmSalesgoal>> goalsByOwner = loadGoalsByOwner(period.getPeriodId(), codes, ownerIds);
        List<StandupKpiSlice> slices = new ArrayList<>();
        for (SalesKpiCode code : codes)
        {
            Map<String, BigDecimal> actualMap = salesKpiActualService.aggregate(ownerIds, start, end, code);
            BigDecimal goalSum = null;
            BigDecimal actualSum = null;
            int assessed = 0;
            for (String oid : ownerIds)
            {
                CrmSalesgoal g = goalsByOwner.getOrDefault(oid, Collections.emptyMap()).get(code.getCode());
                if (g == null || g.getIsActive() == null || g.getIsActive() != IS_ACTIVE_ASSESSING)
                {
                    continue;
                }
                assessed++;
                if (g.getGoalValue() != null)
                {
                    goalSum = (goalSum == null ? BigDecimal.ZERO : goalSum).add(g.getGoalValue());
                }
                BigDecimal a = actualMap.get(oid);
                if (code == SalesKpiCode.WIN_RATE)
                {
                    // 团队赢单率：对考核中成员有值的取平均（与求和口径不同，避免百分比相加）
                    if (a != null)
                    {
                        actualSum = (actualSum == null ? BigDecimal.ZERO : actualSum).add(a);
                    }
                }
                else
                {
                    BigDecimal av = a != null ? a : BigDecimal.ZERO;
                    actualSum = (actualSum == null ? BigDecimal.ZERO : actualSum).add(av);
                }
            }
            StandupKpiSlice slice = new StandupKpiSlice();
            slice.setKpiCode(code.getCode());
            slice.setAssessedMemberCount(assessed);
            if (assessed == 0)
            {
                slice.setGoalValue(null);
                slice.setActualValue(null);
                slice.setCompletionRate(null);
            }
            else
            {
                slice.setGoalValue(goalSum);
                if (code == SalesKpiCode.WIN_RATE && actualSum != null && assessed > 0)
                {
                    // 简化：考核中成员赢单率算术平均
                    int withRate = 0;
                    BigDecimal rateSum = BigDecimal.ZERO;
                    for (String oid : ownerIds)
                    {
                        CrmSalesgoal g = goalsByOwner.getOrDefault(oid, Collections.emptyMap()).get(code.getCode());
                        if (g == null || g.getIsActive() == null || g.getIsActive() != IS_ACTIVE_ASSESSING)
                        {
                            continue;
                        }
                        BigDecimal a = actualMap.get(oid);
                        if (a != null)
                        {
                            rateSum = rateSum.add(a);
                            withRate++;
                        }
                    }
                    slice.setActualValue(withRate > 0
                            ? rateSum.divide(BigDecimal.valueOf(withRate), 2, java.math.RoundingMode.HALF_UP)
                            : null);
                }
                else
                {
                    slice.setActualValue(actualSum);
                }
                slice.setCompletionRate(
                        CrmSalesgoalServiceImpl.computeCompletionRate(slice.getGoalValue(), slice.getActualValue()));
            }
            slices.add(slice);
        }
        return slices;
    }

    private Map<String, Map<String, CrmSalesgoal>> loadGoalsByOwner(String periodId, List<SalesKpiCode> codes,
            List<String> ownerIds)
    {
        Map<String, Map<String, CrmSalesgoal>> result = new HashMap<>();
        if (!StringUtils.hasText(periodId) || CollectionUtils.isEmpty(codes))
        {
            return result;
        }
        for (SalesKpiCode code : codes)
        {
            CrmSalesgoal query = new CrmSalesgoal();
            query.setPeriodId(periodId);
            query.setKpiCode(code.getCode());
            query.setPeriodType(PERIOD_TYPE_MONTHLY);
            List<CrmSalesgoal> rows = crmSalesgoalMapper.selectCrmSalesgoalList(query);
            if (rows == null)
            {
                continue;
            }
            Set<String> ownerSet = new HashSet<>(ownerIds);
            for (CrmSalesgoal row : rows)
            {
                if (row.getOwnerId() == null || !ownerSet.contains(row.getOwnerId()))
                {
                    continue;
                }
                result.computeIfAbsent(row.getOwnerId(), k -> new HashMap<>()).put(code.getCode(), row);
            }
        }
        return result;
    }

    private CrmTimeperiod resolveMonthlyPeriod(LocalDate date)
    {
        CrmTimeperiod query = new CrmTimeperiod();
        query.setPeriodType(PERIOD_TYPE_MONTHLY);
        List<CrmTimeperiod> list = crmTimeperiodService.selectCrmTimeperiodList(query);
        if (CollectionUtils.isEmpty(list))
        {
            return null;
        }
        for (CrmTimeperiod p : list)
        {
            if (p.getStartDate() == null || p.getEndDate() == null)
            {
                continue;
            }
            LocalDate start = toLocalDate(p.getStartDate());
            LocalDate end = toLocalDate(p.getEndDate());
            if (start != null && end != null && !date.isBefore(start) && !date.isAfter(end))
            {
                return p;
            }
        }
        return null;
    }

    private static LocalDate toLocalDate(java.util.Date d)
    {
        if (d == null)
        {
            return null;
        }
        if (d instanceof java.sql.Date)
        {
            return ((java.sql.Date) d).toLocalDate();
        }
        return d.toInstant().atZone(StandupSupport.ZONE).toLocalDate();
    }

    private List<SalesKpiCode> parseAndValidateKpiCodes(String raw)
    {
        List<String> codes = StandupSupport.parseKpiCodes(raw);
        List<SalesKpiCode> result = new ArrayList<>();
        for (String c : codes)
        {
            try
            {
                result.add(SalesKpiCode.fromCode(c));
            }
            catch (ServiceException ex)
            {
                throw new ServiceException(ex.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
        return result;
    }

    private MemberScope resolveMemberScope(Long deptId, String ownerId)
    {
        boolean hasTeam = hasPerm(PERM_TEAM);
        String selfId = String.valueOf(SecurityUtils.getUserId());
        String forcedOwner = ownerId;
        if (!hasTeam)
        {
            forcedOwner = selfId;
        }
        else if (StringUtils.hasText(ownerId))
        {
            forcedOwner = ownerId.trim();
        }
        else
        {
            forcedOwner = null;
        }

        List<MemberInfo> members = new ArrayList<>();
        Map<String, String> nameById = new HashMap<>();

        if (forcedOwner != null)
        {
            MemberInfo mi = loadSingleMember(forcedOwner);
            members.add(mi);
            nameById.put(mi.ownerId, mi.ownerName);
        }
        else
        {
            AssignableUserQuery query = new AssignableUserQuery();
            query.setPurpose("opty_owner_dropdown");
            query.setRoleKeys(ensureRoleKeys);
            query.setScope("tenant");
            query.setWecomBound(false);
            query.setPageSize(500);
            query.setPageNum(1);
            if (deptId != null)
            {
                query.setDeptId(deptId);
            }
            List<AssignableUserVo> users = assignableUserService.selectAssignableUserList(query);
            if (users != null)
            {
                for (AssignableUserVo u : users)
                {
                    if (u.getUserId() == null)
                    {
                        continue;
                    }
                    MemberInfo mi = new MemberInfo();
                    mi.ownerId = String.valueOf(u.getUserId());
                    mi.ownerName = StringUtils.hasText(u.getNickName()) ? u.getNickName()
                            : (u.getUserName() != null ? u.getUserName() : mi.ownerId);
                    mi.deptId = u.getDeptId();
                    if (u.getDept() != null)
                    {
                        mi.deptName = u.getDept().getDeptName();
                    }
                    members.add(mi);
                    nameById.put(mi.ownerId, mi.ownerName);
                }
            }
        }

        List<String> ownerIds = new ArrayList<>();
        for (MemberInfo m : members)
        {
            ownerIds.add(m.ownerId);
        }
        MemberScope scope = new MemberScope();
        scope.members = members;
        scope.ownerIds = ownerIds;
        scope.nameById = nameById;
        scope.hasTeam = hasTeam;
        return scope;
    }

    private MemberInfo loadSingleMember(String ownerId)
    {
        MemberInfo mi = new MemberInfo();
        mi.ownerId = ownerId;
        mi.ownerName = ownerId;
        try
        {
            Long uid = Long.valueOf(ownerId);
            SysUser user = sysUserService.selectUserById(uid);
            if (user != null)
            {
                mi.ownerName = StringUtils.hasText(user.getNickName()) ? user.getNickName()
                        : (user.getUserName() != null ? user.getUserName() : ownerId);
                mi.deptId = user.getDeptId();
                mi.avatar = user.getAvatar();
                if (user.getDept() != null)
                {
                    mi.deptName = user.getDept().getDeptName();
                }
            }
        }
        catch (NumberFormatException ignored)
        {
        }
        return mi;
    }

    private String resolveOwnerName(String ownerId)
    {
        return loadSingleMember(ownerId).ownerName;
    }

    private void assertTeamOrSelf(String ownerId)
    {
        if (hasPerm(PERM_TEAM))
        {
            return;
        }
        // personal view: allow only self-scoped calls (ownerId null or self)
        String selfId = String.valueOf(SecurityUtils.getUserId());
        if (StringUtils.hasText(ownerId) && !selfId.equals(ownerId.trim()))
        {
            throw new ServiceException("无权限查看他人晨会数据", HttpStatus.FORBIDDEN);
        }
    }

    private void assertCanViewMember(String ownerId)
    {
        String selfId = String.valueOf(SecurityUtils.getUserId());
        if (selfId.equals(ownerId))
        {
            return;
        }
        if (!hasPerm(PERM_TEAM))
        {
            throw new ServiceException("无权限查看他人晨会明细", HttpStatus.FORBIDDEN);
        }
    }

    private boolean hasPerm(String perm)
    {
        try
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser == null || loginUser.getPermissions() == null)
            {
                return false;
            }
            return loginUser.getPermissions().contains(PERM_ALL)
                    || loginUser.getPermissions().contains(perm);
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    private Set<String> parseRiskTypes(String types)
    {
        Set<String> all = new LinkedHashSet<>();
        all.add("idle_followup");
        all.add("pool_imminent");
        all.add("today_unconfirmed");
        all.add("stale_opty");
        if (!StringUtils.hasText(types))
        {
            return all;
        }
        Set<String> selected = new LinkedHashSet<>();
        for (String p : types.split(","))
        {
            if (p != null && !p.trim().isEmpty() && all.contains(p.trim()))
            {
                selected.add(p.trim());
            }
        }
        return selected.isEmpty() ? all : selected;
    }

    private List<StandupAgendaItem> mapAgendaItems(List<StandupBookingAgendaRow> rows,
            Map<String, String> nameById)
    {
        List<StandupAgendaItem> items = new ArrayList<>();
        if (rows == null)
        {
            return items;
        }
        for (StandupBookingAgendaRow b : rows)
        {
            StandupAgendaItem item = new StandupAgendaItem();
            item.setItemId("booking:" + b.getBookingId());
            item.setType("booking");
            item.setStartTime(StandupSupport.formatIso(b.getStartTime()));
            item.setEndTime(StandupSupport.formatIso(b.getEndTime()));
            item.setStatus(b.getStatus());
            item.setTitle(StringUtils.hasText(b.getSubjectName()) ? b.getSubjectName() : b.getBookingName());
            if (StringUtils.hasText(b.getContactId()))
            {
                item.setSubjectType("contact");
                item.setSubjectId(b.getContactId());
                item.setDeepLink("/contacts/" + b.getContactId());
            }
            else
            {
                item.setSubjectType("lead");
                item.setSubjectId(b.getLeadId());
                item.setDeepLink(StringUtils.hasText(b.getLeadId()) ? "/leads/" + b.getLeadId() : null);
            }
            item.setSubjectName(b.getSubjectName());
            item.setMobilePhone(b.getMobilePhone());
            String oid = b.getUserId() != null ? String.valueOf(b.getUserId()) : null;
            item.setOwnerId(oid);
            item.setOwnerName(nameById != null ? nameById.getOrDefault(oid, b.getOwner()) : b.getOwner());
            item.setOrgId(b.getOrgId());
            item.setOrgName(b.getOrgName());
            items.add(item);
        }
        return items;
    }

    private void fillBookingRiskSubject(StandupRiskItem item, StandupBookingAgendaRow b)
    {
        if (StringUtils.hasText(b.getContactId()))
        {
            item.setSubjectType("contact");
            item.setSubjectId(b.getContactId());
            item.setDeepLink("/contacts/" + b.getContactId());
        }
        else
        {
            item.setSubjectType("lead");
            item.setSubjectId(b.getLeadId());
            item.setDeepLink(StringUtils.hasText(b.getLeadId()) ? "/leads/" + b.getLeadId() : null);
        }
        item.setSubjectName(b.getSubjectName());
    }

    private static StandupDayMetrics emptyMetrics()
    {
        StandupDayMetrics m = new StandupDayMetrics();
        m.refreshZeroAction();
        return m;
    }

    private static final class MemberInfo
    {
        String ownerId;
        String ownerName;
        Long deptId;
        String deptName;
        String avatar;
    }

    private static final class MemberScope
    {
        List<MemberInfo> members;
        List<String> ownerIds;
        Map<String, String> nameById;
        boolean hasTeam;
    }
}
