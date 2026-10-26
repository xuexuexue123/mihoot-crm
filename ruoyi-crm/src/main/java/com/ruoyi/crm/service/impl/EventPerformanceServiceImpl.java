package com.ruoyi.crm.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.domain.CrmEventPerformanceSnapshot;
import com.ruoyi.crm.domain.dto.EventPerformanceQuery;
import com.ruoyi.crm.domain.dto.EventPerformanceTrendsQuery;
import com.ruoyi.crm.domain.vo.ContactClvAggregateVo;
import com.ruoyi.crm.domain.vo.EventPerformanceActivityWindowVo;
import com.ruoyi.crm.domain.vo.EventPerformanceChannelRowVo;
import com.ruoyi.crm.domain.vo.EventPerformanceFunnelVo;
import com.ruoyi.crm.domain.vo.EventPerformanceListRowVo;
import com.ruoyi.crm.domain.vo.EventPerformanceRevenueVo;
import com.ruoyi.crm.domain.vo.EventPerformanceSummaryVo;
import com.ruoyi.crm.domain.vo.EventPerformanceTrendsVo;
import com.ruoyi.crm.domain.vo.EventAudienceSummaryVo;
import com.ruoyi.crm.mapper.CrmEventMapper;
import com.ruoyi.crm.mapper.CrmEventCheckInMapper;
import com.ruoyi.crm.mapper.CrmEventRegistrationMapper;
import com.ruoyi.crm.mapper.CrmEventSpendItemMapper;
import com.ruoyi.crm.mapper.EventAudienceMapper;
import com.ruoyi.crm.mapper.EventPerformanceMapper;
import com.ruoyi.crm.service.IEventAudienceService;
import com.ruoyi.crm.service.IEventPerformanceService;
import com.ruoyi.crm.service.IPatientClvService;
import com.ruoyi.crm.service.support.AudienceLeadVisibility;
import com.ruoyi.crm.service.support.AudienceSourceRow;
import com.ruoyi.crm.service.support.AudienceStatusResolver;
import com.ruoyi.crm.service.support.DataTableTenantSupport;
import com.ruoyi.crm.service.support.EventPerformanceMetricsSupport;
import com.ruoyi.crm.service.support.EventPerformanceTrendsSupport;
import com.ruoyi.crm.service.support.EventTrendDailyRow;
import com.ruoyi.crm.service.support.WecomChannelContactRow;
import com.ruoyi.crm.wecom.WecomStateParseResult;
import com.ruoyi.crm.wecom.WecomStateParser;

@Service
public class EventPerformanceServiceImpl implements IEventPerformanceService
{
    private static final String CURRENCY_DEFAULT = "CNY";

    @Autowired
    private CrmEventMapper crmEventMapper;

    @Autowired
    private EventPerformanceMapper eventPerformanceMapper;

    @Autowired
    private EventAudienceMapper eventAudienceMapper;

    @Autowired
    private IEventAudienceService eventAudienceService;

    @Autowired
    private IPatientClvService patientClvService;

    @Autowired
    private WecomStateParser wecomStateParser;

    @Autowired
    private CrmEventSpendItemMapper spendItemMapper;

    @Autowired
    private CrmEventRegistrationMapper registrationMapper;

    @Autowired
    private CrmEventCheckInMapper checkInMapper;

    @Override
    public EventPerformanceSummaryVo summarize(String tenantId, String eventId, EventPerformanceQuery query)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        CrmEvent event = requireEvent(eventId);

        EventAudienceSummaryVo audience = eventAudienceService.getSummary(tenantId, eventId);
        Set<String> attributedContacts = new HashSet<>(eventPerformanceMapper.selectAttributedContactIds(eventId));

        EventPerformanceFunnelVo funnel = new EventPerformanceFunnelVo();
        funnel.setTouchCount(audience.getTotalUnique());
        funnel.setH5RegistrationCount(audience.getH5RegistrationCount());
        funnel.setRegistrationPendingCount(audience.getRegistrationPendingCount());
        funnel.setLeadReleasedCount(audience.getH5ReleasedLeadCount());
        funnel.setLeadActiveCount(eventAudienceService.countByAudienceStatus(
                tenantId, eventId, AudienceStatusResolver.LEAD_ACTIVE));
        funnel.setContactKnownCount(audience.getContactKnownCount());
        funnel.setContactConvertedCount(attributedContacts.size());
        EventPerformanceQuery effective = normalizeQuery(query);
        String sessionId = effective != null ? effective.getSessionId() : null;
        funnel.setRegistrationConfirmedCount(
                registrationMapper.countDistinctConfirmedByEvent(tenantId, eventId, sessionId));
        funnel.setWaitlistCount(registrationMapper.countWaitlistByEvent(tenantId, eventId, sessionId));
        if (StringUtils.isNotBlank(sessionId))
        {
            funnel.setCheckedInCount(checkInMapper.countDistinctBySession(tenantId, eventId, sessionId));
        }
        else
        {
            funnel.setCheckedInCount(checkInMapper.countDistinctByEvent(tenantId, eventId));
        }

        ContactClvAggregateVo clv = patientClvService.aggregateContactClv(attributedContacts);
        EventPerformanceRevenueVo revenue = buildRevenue(tenantId, eventId, attributedContacts, clv, effective);
        BigDecimal roiClv = resolveRoiClvBasis(revenue, effective);

        EventPerformanceSummaryVo summary = new EventPerformanceSummaryVo();
        summary.setEventId(eventId);
        summary.setSubject(event.getSubject());
        summary.setBudget(event.getBudget());
        summary.setPaid(EventPerformanceMetricsSupport.nz(event.getPaid()));
        summary.setBalance(EventPerformanceMetricsSupport.computeBalance(event.getBudget(), event.getPaid()));
        summary.setCurrency(CURRENCY_DEFAULT);
        summary.setActivityWindow(buildActivityWindow(event));
        summary.setFunnel(funnel);
        summary.setRates(EventPerformanceMetricsSupport.computeRates(funnel));
        summary.setRevenue(revenue);
        summary.setEfficiency(EventPerformanceMetricsSupport.computeEfficiency(
                event.getPaid(), event.getBudget(), roiClv,
                funnel.getLeadReleasedCount(), funnel.getContactConvertedCount()));
        summary.setCalculatedAt(new Date());
        return summary;
    }

    @Override
    public List<EventPerformanceChannelRowVo> listChannels(String tenantId, String eventId)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        CrmEvent event = requireEvent(eventId);
        BigDecimal paid = EventPerformanceMetricsSupport.nz(event.getPaid());

        List<AudienceSourceRow> h5Sources = eventAudienceMapper.selectH5Sources(eventId);
        List<AudienceSourceRow> mpSources = eventAudienceMapper.selectMpSources(eventId);
        List<WecomChannelContactRow> wecomRows = eventPerformanceMapper.selectWecomContactsByEvent(eventId);
        Set<String> h5Converted = new HashSet<>(eventPerformanceMapper.selectH5AttributedContactIds(eventId));
        Set<String> mpConverted = new HashSet<>(eventPerformanceMapper.selectMpAttributedContactIds(eventId));
        Map<String, BigDecimal> spendByChannel = loadSpendByChannel(tenantId, eventId);

        Map<String, ChannelAccumulator> channels = new LinkedHashMap<>();

        ChannelAccumulator h5 = channels.computeIfAbsent("h5", k -> new ChannelAccumulator("h5", "H5 报名"));
        h5.touchCount = h5Sources.size();
        h5.h5RegistrationCount = h5Sources.size();
        for (AudienceSourceRow row : h5Sources)
        {
            if (!AudienceLeadVisibility.isInitialStatus(row.getLeadStatus()))
            {
                h5.leadReleasedCount++;
            }
            if (StringUtils.isNotEmpty(row.getContactId()) && h5Converted.contains(row.getContactId()))
            {
                h5.convertedContactIds.add(row.getContactId());
            }
        }

        Map<String, List<WecomChannelContactRow>> wecomByKey = new TreeMap<>();
        for (WecomChannelContactRow row : wecomRows)
        {
            String key = resolveWecomChannelKey(row.getState());
            wecomByKey.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
        }
        for (Map.Entry<String, List<WecomChannelContactRow>> entry : wecomByKey.entrySet())
        {
            String key = entry.getKey();
            ChannelAccumulator acc = channels.computeIfAbsent(key, k -> new ChannelAccumulator(key, channelLabel(key)));
            acc.touchCount = entry.getValue().size();
            for (WecomChannelContactRow row : entry.getValue())
            {
                if (!AudienceLeadVisibility.isInitialStatus(row.getLeadStatus())
                        || StringUtils.isNotEmpty(row.getCrmContactId()))
                {
                    acc.leadReleasedCount++;
                }
                if (StringUtils.isNotEmpty(row.getCrmContactId()))
                {
                    acc.convertedContactIds.add(row.getCrmContactId());
                }
            }
        }

        if (!mpSources.isEmpty())
        {
            Set<String> mpOpenIds = mpSources.stream()
                    .map(AudienceSourceRow::getWechatMpOpenid)
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toSet());
            ChannelAccumulator mp = channels.computeIfAbsent("mp", k -> new ChannelAccumulator("mp", "公众号"));
            mp.touchCount = mpOpenIds.isEmpty() ? mpSources.size() : mpOpenIds.size();
            mp.convertedContactIds.addAll(mpConverted);
        }

        int totalTouch = channels.values().stream().mapToInt(a -> a.touchCount).sum();
        boolean useSpendItems = !spendByChannel.isEmpty();
        List<EventPerformanceChannelRowVo> rows = new ArrayList<>();
        for (ChannelAccumulator acc : channels.values())
        {
            EventPerformanceChannelRowVo row = new EventPerformanceChannelRowVo();
            row.setChannelKey(acc.channelKey);
            row.setChannelLabel(acc.channelLabel);
            row.setTouchCount(acc.touchCount);
            row.setH5RegistrationCount(acc.h5RegistrationCount);
            row.setLeadReleasedCount(acc.leadReleasedCount);
            row.setContactConvertedCount(acc.convertedContactIds.size());
            row.setShareOfTouch(EventPerformanceMetricsSupport.rate(acc.touchCount, totalTouch));
            if (useSpendItems && spendByChannel.containsKey(acc.channelKey))
            {
                row.setSpendAllocated(spendByChannel.get(acc.channelKey));
            }
            else if (useSpendItems && acc.channelKey.startsWith("wecom:") && spendByChannel.containsKey("wecom"))
            {
                row.setSpendAllocated(spendByChannel.get("wecom"));
            }
            else
            {
                row.setSpendAllocated(EventPerformanceMetricsSupport.allocateSpend(paid, acc.touchCount, totalTouch));
            }
            row.setCpl(EventPerformanceMetricsSupport.divide(row.getSpendAllocated(), Math.max(acc.leadReleasedCount, 1)));
            row.setCac(EventPerformanceMetricsSupport.divide(row.getSpendAllocated(), Math.max(acc.convertedContactIds.size(), 1)));
            if (acc.convertedContactIds.isEmpty())
            {
                row.setCac(null);
            }
            if (acc.leadReleasedCount == 0)
            {
                row.setCpl(null);
            }
            rows.add(row);
        }
        return rows;
    }

    @Override
    public List<EventPerformanceListRowVo> listPerformance(String tenantId, EventPerformanceQuery query)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        EventPerformanceQuery effective = normalizeQuery(query);
        int offset = (effective.getPageNum() - 1) * effective.getPageSize();
        boolean useSnapshot = !Boolean.TRUE.equals(effective.getFresh());
        List<EventPerformanceListRowVo> rows = new ArrayList<>();
        if (useSnapshot)
        {
            List<CrmEventPerformanceSnapshot> snapshots = eventPerformanceMapper.selectSnapshotsForList(
                    effective.getStage(), effective.getDateFrom(), effective.getDateTo(),
                    effective.getSortBy(), effective.getSortOrder(), offset, effective.getPageSize());
            for (CrmEventPerformanceSnapshot snap : snapshots)
            {
                rows.add(toListRowFromSnapshot(snap));
            }
            if (!rows.isEmpty())
            {
                return rows;
            }
        }
        List<CrmEvent> events = eventPerformanceMapper.selectEventsForPerformanceList(
                effective.getStage(), effective.getDateFrom(), effective.getDateTo(),
                effective.getSortBy(), effective.getSortOrder(), offset, effective.getPageSize());
        for (CrmEvent event : events)
        {
            rows.add(toListRow(tenantId, event, effective));
        }
        return rows;
    }

    @Override
    public long countPerformanceList(String tenantId, EventPerformanceQuery query)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        EventPerformanceQuery effective = normalizeQuery(query);
        if (!Boolean.TRUE.equals(effective.getFresh()))
        {
            long snapCount = eventPerformanceMapper.countSnapshotsForList(
                    effective.getStage(), effective.getDateFrom(), effective.getDateTo());
            if (snapCount > 0)
            {
                return snapCount;
            }
        }
        return eventPerformanceMapper.countEventsForPerformanceList(
                effective.getStage(), effective.getDateFrom(), effective.getDateTo());
    }

    @Override
    public EventPerformanceTrendsVo getTrends(String tenantId, String eventId, EventPerformanceTrendsQuery query)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        CrmEvent event = requireEvent(eventId);
        EventPerformanceTrendsQuery effective = query != null ? query : new EventPerformanceTrendsQuery();
        String granularity = StringUtils.isEmpty(effective.getGranularity()) ? "day" : effective.getGranularity();
        Date dateFrom = parseTrendDate(effective.getDateFrom(), event.getStartTime(), event.getCreateTime());
        Date dateTo = parseTrendDate(effective.getDateTo(), event.getEndTime(), new Date());
        if (dateFrom.after(dateTo))
        {
            EventPerformanceTrendsVo empty = new EventPerformanceTrendsVo();
            empty.setGranularity(granularity);
            return empty;
        }
        List<EventTrendDailyRow> touch = eventPerformanceMapper.selectTrendTouchDaily(eventId, dateFrom, dateTo);
        List<EventTrendDailyRow> h5 = eventPerformanceMapper.selectTrendH5RegistrationDaily(eventId, dateFrom, dateTo);
        List<EventTrendDailyRow> leadReleased = eventPerformanceMapper.selectTrendLeadReleasedDaily(eventId, dateFrom, dateTo);
        List<EventTrendDailyRow> contactConverted = eventPerformanceMapper.selectTrendContactConvertedDaily(eventId, dateFrom, dateTo);
        LocalDate from = dateFrom.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate to = dateTo.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        boolean cumulative = Boolean.TRUE.equals(effective.getCumulative());
        EventPerformanceTrendsVo result = new EventPerformanceTrendsVo();
        result.setGranularity(granularity);
        result.setSeries(EventPerformanceTrendsSupport.mergeSeries(from, to, granularity,
                EventPerformanceTrendsSupport.toCountMap(touch, granularity),
                EventPerformanceTrendsSupport.toCountMap(h5, granularity),
                EventPerformanceTrendsSupport.toCountMap(leadReleased, granularity),
                EventPerformanceTrendsSupport.toCountMap(contactConverted, granularity),
                cumulative));
        return result;
    }

    @Override
    public void refreshSnapshot(String tenantId, String eventId)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        EventPerformanceSummaryVo summary = summarize(tenantId, eventId, new EventPerformanceQuery());
        CrmEventPerformanceSnapshot snap = new CrmEventPerformanceSnapshot();
        snap.setTenantId(tenantId);
        snap.setEventId(eventId);
        if (summary.getFunnel() != null)
        {
            snap.setTouchCount(summary.getFunnel().getTouchCount());
            snap.setLeadReleasedCount(summary.getFunnel().getLeadReleasedCount());
            snap.setContactConvertedCount(summary.getFunnel().getContactConvertedCount());
        }
        if (summary.getRevenue() != null)
        {
            snap.setNetClvTotal(summary.getRevenue().getNetClvTotal());
        }
        snap.setPaid(summary.getPaid());
        if (summary.getEfficiency() != null)
        {
            snap.setRoi(summary.getEfficiency().getRoi());
        }
        snap.setCalculatedAt(new Date());
        eventPerformanceMapper.upsertPerformanceSnapshot(snap);
    }

    private EventPerformanceListRowVo toListRowFromSnapshot(CrmEventPerformanceSnapshot snap)
    {
        EventPerformanceListRowVo row = new EventPerformanceListRowVo();
        row.setEventId(snap.getEventId());
        row.setSubject(snap.getSubject());
        row.setStage(snap.getStage());
        row.setPaid(snap.getPaid());
        row.setTouchCount(snap.getTouchCount());
        row.setLeadReleasedCount(snap.getLeadReleasedCount());
        row.setContactConvertedCount(snap.getContactConvertedCount());
        row.setNetClvTotal(snap.getNetClvTotal());
        row.setRoi(snap.getRoi());
        row.setStartTime(snap.getStartTime());
        row.setEndTime(snap.getEndTime());
        if (snap.getPaid() != null && snap.getContactConvertedCount() > 0)
        {
            row.setCac(EventPerformanceMetricsSupport.divide(snap.getPaid(), snap.getContactConvertedCount()));
        }
        return row;
    }

    private Map<String, BigDecimal> loadSpendByChannel(String tenantId, String eventId)
    {
        Map<String, BigDecimal> map = new HashMap<>();
        List<Map<String, Object>> items = spendItemMapper.selectSpendByChannel(tenantId, eventId);
        if (items == null)
        {
            return map;
        }
        for (Map<String, Object> item : items)
        {
            Object keyObj = item.get("channelkey");
            if (keyObj == null)
            {
                keyObj = item.get("channelKey");
            }
            String key = keyObj != null ? String.valueOf(keyObj) : null;
            if (StringUtils.isEmpty(key))
            {
                key = "_uncategorized";
            }
            Object amountObj = item.get("amount");
            BigDecimal amount = amountObj instanceof BigDecimal ? (BigDecimal) amountObj
                    : new BigDecimal(String.valueOf(amountObj));
            map.merge(key, amount, BigDecimal::add);
        }
        return map;
    }

    private BigDecimal resolveRoiClvBasis(EventPerformanceRevenueVo revenue, EventPerformanceQuery query)
    {
        if (revenue == null)
        {
            return BigDecimal.ZERO;
        }
        if (query != null && "window".equalsIgnoreCase(query.getRoiBasis())
                && revenue.getWindowNetClvTotal() != null)
        {
            return revenue.getWindowNetClvTotal();
        }
        return revenue.getNetClvTotal();
    }

    private Date parseTrendDate(String text, Date primary, Date fallback)
    {
        if (StringUtils.isNotEmpty(text))
        {
            try
            {
                if (text.length() <= 10)
                {
                    return Date.from(LocalDate.parse(text.substring(0, 10))
                            .atStartOfDay(ZoneId.systemDefault()).toInstant());
                }
                return java.sql.Timestamp.valueOf(text.replace("T", " ").substring(0, 19));
            }
            catch (Exception ignored)
            {
                // fall through
            }
        }
        if (primary != null)
        {
            return primary;
        }
        return fallback != null ? fallback : new Date();
    }

    private EventPerformanceListRowVo toListRow(String tenantId, CrmEvent event, EventPerformanceQuery query)
    {
        EventPerformanceSummaryVo summary = summarize(tenantId, event.getEventId(), query);
        EventPerformanceListRowVo row = new EventPerformanceListRowVo();
        row.setEventId(event.getEventId());
        row.setSubject(event.getSubject());
        row.setStage(event.getStage());
        row.setPaid(EventPerformanceMetricsSupport.nz(event.getPaid()));
        row.setStartTime(event.getStartTime());
        row.setEndTime(event.getEndTime());
        if (summary.getFunnel() != null)
        {
            row.setTouchCount(summary.getFunnel().getTouchCount());
            row.setLeadReleasedCount(summary.getFunnel().getLeadReleasedCount());
            row.setContactConvertedCount(summary.getFunnel().getContactConvertedCount());
        }
        if (summary.getRevenue() != null)
        {
            row.setNetClvTotal(summary.getRevenue().getNetClvTotal());
        }
        if (summary.getEfficiency() != null)
        {
            row.setCac(summary.getEfficiency().getCac());
            row.setRoi(summary.getEfficiency().getRoi());
        }
        return row;
    }

    private CrmEvent requireEvent(String eventId)
    {
        CrmEvent event = crmEventMapper.selectCrmEventByEventId(eventId);
        if (event == null)
        {
            throw new ServiceException("活动不存在", HttpStatus.NOT_FOUND);
        }
        return event;
    }

    private EventPerformanceActivityWindowVo buildActivityWindow(CrmEvent event)
    {
        EventPerformanceActivityWindowVo window = new EventPerformanceActivityWindowVo();
        window.setStartTime(event.getStartTime());
        window.setEndTime(event.getEndTime());
        return window;
    }

    private EventPerformanceRevenueVo buildRevenue(String tenantId, String eventId, Set<String> attributedContacts,
            ContactClvAggregateVo clv, EventPerformanceQuery query)
    {
        EventPerformanceRevenueVo revenue = new EventPerformanceRevenueVo();
        revenue.setAttributedContactCount(attributedContacts.size());
        revenue.setBookedClvTotal(clv.getBookedClvTotal());
        revenue.setInflightClvTotal(clv.getInflightClvTotal());
        revenue.setRefundTotal(clv.getRefundTotal());
        revenue.setNetClvTotal(clv.getNetClvTotal());
        revenue.setAttributionMode("lifetime");
        revenue.setEstimate(clv.isEstimate());
        if (query != null && query.getAttributionWindowDays() != null && query.getAttributionWindowDays() > 0)
        {
            Map<String, Date> firstTouch = eventAudienceService.resolveFirstTouchByContactId(tenantId, eventId);
            ContactClvAggregateVo windowClv = patientClvService.aggregateContactClvWindow(
                    eventId, attributedContacts, firstTouch, query.getAttributionWindowDays());
            revenue.setWindowDays(query.getAttributionWindowDays());
            revenue.setWindowNetClvTotal(windowClv.getNetClvTotal());
            revenue.setAttributionMode("window");
        }
        return revenue;
    }

    private String resolveWecomChannelKey(String state)
    {
        WecomStateParseResult parsed = wecomStateParser.parse(state);
        if (StringUtils.isNotEmpty(parsed.getChannel()))
        {
            return "wecom:" + parsed.getChannel();
        }
        return "wecom";
    }

    private static String channelLabel(String channelKey)
    {
        if ("h5".equals(channelKey))
        {
            return "H5 报名";
        }
        if ("mp".equals(channelKey))
        {
            return "公众号";
        }
        if ("wecom".equals(channelKey))
        {
            return "企微";
        }
        if (channelKey.startsWith("wecom:"))
        {
            return "企微 · " + channelKey.substring("wecom:".length());
        }
        return channelKey;
    }

    private EventPerformanceQuery normalizeQuery(EventPerformanceQuery query)
    {
        EventPerformanceQuery effective = query != null ? query : new EventPerformanceQuery();
        if (effective.getPageNum() == null || effective.getPageNum() < 1)
        {
            effective.setPageNum(1);
        }
        if (effective.getPageSize() == null || effective.getPageSize() < 1)
        {
            effective.setPageSize(20);
        }
        if (StringUtils.isEmpty(effective.getSortBy()))
        {
            effective.setSortBy("createTime");
        }
        if (StringUtils.isEmpty(effective.getSortOrder()))
        {
            effective.setSortOrder("desc");
        }
        if (StringUtils.isEmpty(effective.getRoiBasis()))
        {
            effective.setRoiBasis("lifetime");
        }
        return effective;
    }

    private static final class ChannelAccumulator
    {
        private final String channelKey;
        private final String channelLabel;
        private int touchCount;
        private int h5RegistrationCount;
        private int leadReleasedCount;
        private final Set<String> convertedContactIds = new HashSet<>();

        private ChannelAccumulator(String channelKey, String channelLabel)
        {
            this.channelKey = channelKey;
            this.channelLabel = channelLabel;
        }
    }
}
