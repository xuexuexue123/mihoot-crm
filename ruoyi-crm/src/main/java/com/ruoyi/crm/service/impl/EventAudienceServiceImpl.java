package com.ruoyi.crm.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.crm.domain.CrmCustomerIdentity;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.domain.dto.EventAudienceQuery;
import com.ruoyi.crm.domain.vo.EventAudienceOpenIdResultVo;
import com.ruoyi.crm.domain.vo.EventAudienceRowVo;
import com.ruoyi.crm.domain.vo.EventAudienceSummaryVo;
import com.ruoyi.crm.mapper.CrmEventMapper;
import com.ruoyi.crm.mapper.CrmEventCheckInMapper;
import com.ruoyi.crm.mapper.CrmEventRegistrationMapper;
import com.ruoyi.crm.mapper.EventAudienceMapper;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.IEventAudienceService;
import com.ruoyi.crm.service.support.AudienceIdentityUnionRow;
import com.ruoyi.crm.service.support.AudienceLeadVisibility;
import com.ruoyi.crm.service.support.AudienceMerger;
import com.ruoyi.crm.service.support.AudienceSourceRow;
import com.ruoyi.crm.service.support.AudienceStatusResolver;
import com.ruoyi.crm.service.support.ContactNameRow;
import com.ruoyi.crm.service.support.DataTableTenantSupport;
import com.ruoyi.crm.service.support.EventAudienceCheckInResolver;
import com.ruoyi.crm.service.support.EventAudienceRegistrationResolver;
import com.ruoyi.crm.service.support.EventCheckInAggRow;
import com.ruoyi.crm.service.support.EventRegistrationBestRow;
import com.ruoyi.crm.service.support.MpOutreachSentAtRow;
import com.ruoyi.crm.wechatmp.mapper.CrmEventMpOutreachLogMapper;

@Service
public class EventAudienceServiceImpl implements IEventAudienceService
{
    private static final String SUMMARY_CACHE_PREFIX = "event_audience:summary:";
    private static final int SUMMARY_CACHE_SECONDS = 60;
    private static final int MAX_PAGE_SIZE = 500;

    @Autowired
    private EventAudienceMapper eventAudienceMapper;

    @Autowired
    private CrmEventMapper crmEventMapper;

    @Autowired
    private AudienceMerger audienceMerger;

    @Autowired
    private ICrmCustomerIdentityService identityService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private CrmEventMpOutreachLogMapper outreachLogMapper;

    @Autowired
    private CrmEventCheckInMapper checkInMapper;

    @Autowired
    private CrmEventRegistrationMapper registrationMapper;

    @Autowired
    private EventAudienceCheckInResolver checkInResolver;

    @Autowired
    private EventAudienceRegistrationResolver registrationResolver;

    @Override
    public EventAudienceSummaryVo getSummary(String tenantId, String eventId)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        assertEventExists(eventId);
        String cacheKey = SUMMARY_CACHE_PREFIX + tenantId + ":" + eventId;
        EventAudienceSummaryVo cached = redisCache.getCacheObject(cacheKey);
        if (cached != null)
        {
            return cached;
        }
        List<EventAudienceRowVo> merged = loadMergedRows(tenantId, eventId, true);
        EventAudienceSummaryVo summary = buildSummary(merged, eventId);
        redisCache.setCacheObject(cacheKey, summary, SUMMARY_CACHE_SECONDS, TimeUnit.SECONDS);
        return summary;
    }

    @Override
    public List<EventAudienceRowVo> listAudience(String tenantId, String eventId, EventAudienceQuery query)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        assertEventExists(eventId);
        EventAudienceQuery effective = normalizeQuery(query);
        List<EventAudienceRowVo> merged = loadMergedRows(tenantId, eventId, Boolean.TRUE.equals(effective.getDedupe()));
        List<EventAudienceRowVo> filtered = audienceMerger.applyFilters(merged, effective);
        return paginate(filtered, effective.getPageNum(), effective.getPageSize());
    }

    @Override
    public long countAudience(String tenantId, String eventId, EventAudienceQuery query)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        assertEventExists(eventId);
        EventAudienceQuery effective = normalizeQuery(query);
        List<EventAudienceRowVo> merged = loadMergedRows(tenantId, eventId, Boolean.TRUE.equals(effective.getDedupe()));
        return audienceMerger.applyFilters(merged, effective).size();
    }

    @Override
    public EventAudienceOpenIdResultVo resolveMpOpenIds(String tenantId, String eventId)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        assertEventExists(eventId);
        List<EventAudienceRowVo> merged = loadMergedRows(tenantId, eventId, true);
        EventAudienceOpenIdResultVo result = new EventAudienceOpenIdResultVo();
        Set<String> seen = new HashSet<>();
        int skipCount = 0;
        for (EventAudienceRowVo row : merged)
        {
            String openId = row.getWechatMpOpenid();
            if (StringUtils.isEmpty(openId) && StringUtils.isNotEmpty(row.getContactId()))
            {
                openId = findOpenId(row.getContactId());
            }
            if (StringUtils.isNotEmpty(openId))
            {
                if (seen.add(openId))
                {
                    result.getOpenIds().add(openId);
                }
            }
            else if (StringUtils.isNotEmpty(row.getContactId())
                    || StringUtils.isNotEmpty(row.getWechatMpOpenid()))
            {
                skipCount++;
            }
        }
        result.setSkipCount(skipCount);
        return result;
    }

    @Override
    public int countByAudienceStatus(String tenantId, String eventId, String audienceStatus)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        assertEventExists(eventId);
        if (StringUtils.isEmpty(audienceStatus))
        {
            return 0;
        }
        List<EventAudienceRowVo> merged = loadMergedRows(tenantId, eventId, true);
        return (int) merged.stream()
                .filter(r -> audienceStatus.equals(r.getAudienceStatus()))
                .count();
    }

    @Override
    public Map<String, Date> resolveFirstTouchByContactId(String tenantId, String eventId)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        assertEventExists(eventId);
        List<EventAudienceRowVo> merged = loadMergedRows(tenantId, eventId, true);
        Map<String, Date> map = new HashMap<>();
        for (EventAudienceRowVo row : merged)
        {
            if (StringUtils.isEmpty(row.getContactId()) || row.getFirstTouchAt() == null)
            {
                continue;
            }
            Date existing = map.get(row.getContactId());
            if (existing == null || row.getFirstTouchAt().before(existing))
            {
                map.put(row.getContactId(), row.getFirstTouchAt());
            }
        }
        return map;
    }

    private List<EventAudienceRowVo> loadMergedRows(String tenantId, String eventId, boolean dedupe)
    {
        List<AudienceSourceRow> h5 = eventAudienceMapper.selectH5Sources(eventId);
        List<AudienceSourceRow> website = eventAudienceMapper.selectWebsiteSources(eventId);
        List<AudienceSourceRow> wecom = eventAudienceMapper.selectWecomSources(eventId);
        List<AudienceSourceRow> mp = eventAudienceMapper.selectMpSources(eventId);

        List<String> openids = collectOpenids(mp);
        List<String> externalUserids = collectExternalUserids(wecom);

        Map<String, String> openidToUnionid = toUnionidMap(
                openids.isEmpty() ? Collections.emptyList()
                        : eventAudienceMapper.selectUnionidByOpenids(openids));
        Map<String, String> externalUseridToUnionid = toUnionidMap(
                externalUserids.isEmpty() ? Collections.emptyList()
                        : eventAudienceMapper.selectUnionidByExternalUserids(externalUserids));

        Set<String> contactIds = new HashSet<>();
        addContactIds(contactIds, h5);
        addContactIds(contactIds, website);
        addContactIds(contactIds, wecom);
        addContactIds(contactIds, mp);
        Map<String, String> contactNames = loadContactNames(new ArrayList<>(contactIds));

        List<EventAudienceRowVo> merged = audienceMerger.merge(h5, wecom, mp, website, openidToUnionid,
                externalUseridToUnionid, contactNames, !dedupe);
        enrichMpOutreachSentAt(eventId, merged);
        enrichCheckInAndRegistration(tenantId, eventId, merged);
        return merged;
    }

    private void enrichCheckInAndRegistration(String tenantId, String eventId, List<EventAudienceRowVo> rows)
    {
        if (rows == null || rows.isEmpty())
        {
            return;
        }
        List<EventCheckInAggRow> checkInRows = checkInMapper.selectCheckInAggByEvent(tenantId, eventId);
        List<EventRegistrationBestRow> registrationRows = registrationMapper.selectBestRegistrationByEvent(tenantId, eventId);
        Map<String, Date> checkInIndex = checkInResolver.buildIndex(checkInRows);
        Map<String, EventRegistrationBestRow> registrationIndex = registrationResolver.buildIndex(registrationRows);
        for (EventAudienceRowVo row : rows)
        {
            registrationResolver.enrichRow(row, registrationIndex);
            EventRegistrationBestRow registration = registrationResolver.match(row, registrationIndex);
            checkInResolver.enrichRow(row, checkInIndex, registration);
        }
    }

    private void enrichMpOutreachSentAt(String eventId, List<EventAudienceRowVo> rows)
    {
        if (rows == null || rows.isEmpty())
        {
            return;
        }
        Set<String> openIdsToQuery = new HashSet<>();
        Map<EventAudienceRowVo, String> rowOpenId = new HashMap<>();
        Map<EventAudienceRowVo, String> rowAppId = new HashMap<>();
        for (EventAudienceRowVo row : rows)
        {
            String openId = row.getWechatMpOpenid();
            if (StringUtils.isEmpty(openId) && StringUtils.isNotEmpty(row.getContactId()))
            {
                openId = findOpenId(row.getContactId());
            }
            if (StringUtils.isNotEmpty(openId))
            {
                rowOpenId.put(row, openId);
                rowAppId.put(row, row.getWechatMpAppId());
                openIdsToQuery.add(openId);
            }
        }
        if (openIdsToQuery.isEmpty())
        {
            return;
        }
        List<MpOutreachSentAtRow> sentRows = outreachLogMapper.selectLatestSentAtByEventAndOpenIds(
                eventId,
                new ArrayList<>(openIdsToQuery));
        if (sentRows == null || sentRows.isEmpty())
        {
            return;
        }
        Map<String, Date> sentAtByKey = new HashMap<>();
        for (MpOutreachSentAtRow sentRow : sentRows)
        {
            if (StringUtils.isNotEmpty(sentRow.getOpenId()) && sentRow.getSentAt() != null)
            {
                String key = outreachSentAtKey(sentRow.getAppId(), sentRow.getOpenId());
                sentAtByKey.put(key, sentRow.getSentAt());
            }
        }
        for (Map.Entry<EventAudienceRowVo, String> entry : rowOpenId.entrySet())
        {
            String openId = entry.getValue();
            String appId = rowAppId.get(entry.getKey());
            Date sentAt = sentAtByKey.get(outreachSentAtKey(appId, openId));
            if (sentAt == null && StringUtils.isNotEmpty(openId))
            {
                sentAt = sentAtByKey.get(outreachSentAtKey(null, openId));
            }
            if (sentAt != null)
            {
                entry.getKey().setMpOutreachSentAt(sentAt);
            }
        }
    }

    private static String outreachSentAtKey(String appId, String openId)
    {
        if (StringUtils.isNotEmpty(appId))
        {
            return appId + ":" + openId;
        }
        return openId;
    }

    private EventAudienceSummaryVo buildSummary(List<EventAudienceRowVo> merged, String eventId)
    {
        List<AudienceSourceRow> h5 = eventAudienceMapper.selectH5Sources(eventId);
        List<AudienceSourceRow> website = eventAudienceMapper.selectWebsiteSources(eventId);
        List<AudienceSourceRow> wecom = eventAudienceMapper.selectWecomSources(eventId);
        List<AudienceSourceRow> mp = eventAudienceMapper.selectMpSources(eventId);

        EventAudienceSummaryVo summary = new EventAudienceSummaryVo();
        summary.setTotalUnique(merged.size());
        summary.setH5RegistrationCount(h5.size());
        summary.setH5ReleasedLeadCount((int) h5.stream()
                .filter(r -> !AudienceLeadVisibility.isInitialStatus(r.getLeadStatus()))
                .count());
        summary.setWebsiteRegistrationCount(website.size());
        summary.setWebsiteReleasedLeadCount((int) website.stream()
                .filter(r -> !AudienceLeadVisibility.isInitialStatus(r.getLeadStatus()))
                .count());
        summary.setWecomContactCount((int) wecom.stream()
                .map(AudienceSourceRow::getWecomExternalUserid)
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .count());
        summary.setMpTouchCount((int) mp.stream()
                .map(AudienceSourceRow::getWechatMpOpenid)
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .count());
        summary.setContactKnownCount((int) merged.stream()
                .filter(r -> StringUtils.isNotEmpty(r.getContactId()))
                .count());
        summary.setRegistrationPendingCount((int) merged.stream()
                .filter(r -> AudienceStatusResolver.REGISTRATION_PENDING.equals(r.getAudienceStatus()))
                .count());
        return summary;
    }

    private Map<String, String> toUnionidMap(List<AudienceIdentityUnionRow> rows)
    {
        Map<String, String> map = new HashMap<>();
        if (rows == null)
        {
            return map;
        }
        for (AudienceIdentityUnionRow row : rows)
        {
            if (StringUtils.isNotEmpty(row.getChannelValue()) && StringUtils.isNotEmpty(row.getUnionid()))
            {
                map.put(row.getChannelValue(), row.getUnionid());
            }
        }
        return map;
    }

    private Map<String, String> loadContactNames(List<String> contactIds)
    {
        if (contactIds == null || contactIds.isEmpty())
        {
            return Collections.emptyMap();
        }
        List<ContactNameRow> rows = eventAudienceMapper.selectContactNames(contactIds);
        if (rows == null)
        {
            return Collections.emptyMap();
        }
        return rows.stream()
                .filter(r -> StringUtils.isNotEmpty(r.getContactId()))
                .collect(Collectors.toMap(ContactNameRow::getContactId, ContactNameRow::getName, (a, b) -> a));
    }

    private void addContactIds(Set<String> set, List<AudienceSourceRow> rows)
    {
        if (rows == null)
        {
            return;
        }
        for (AudienceSourceRow row : rows)
        {
            if (StringUtils.isNotEmpty(row.getContactId()))
            {
                set.add(row.getContactId());
            }
        }
    }

    private List<String> collectOpenids(List<AudienceSourceRow> mp)
    {
        if (mp == null)
        {
            return Collections.emptyList();
        }
        return mp.stream()
                .map(AudienceSourceRow::getWechatMpOpenid)
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<String> collectExternalUserids(List<AudienceSourceRow> wecom)
    {
        if (wecom == null)
        {
            return Collections.emptyList();
        }
        return wecom.stream()
                .map(AudienceSourceRow::getWecomExternalUserid)
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .collect(Collectors.toList());
    }

    private void assertEventExists(String eventId)
    {
        CrmEvent event = crmEventMapper.selectCrmEventByEventId(eventId);
        if (event == null)
        {
            throw new ServiceException("活动不存在", HttpStatus.NOT_FOUND);
        }
    }

    private EventAudienceQuery normalizeQuery(EventAudienceQuery query)
    {
        EventAudienceQuery effective = query != null ? query : new EventAudienceQuery();
        if (effective.getPageNum() == null || effective.getPageNum() < 1)
        {
            effective.setPageNum(1);
        }
        if (effective.getPageSize() == null || effective.getPageSize() < 1)
        {
            effective.setPageSize(20);
        }
        if (effective.getPageSize() > MAX_PAGE_SIZE)
        {
            effective.setPageSize(MAX_PAGE_SIZE);
        }
        if (effective.getDedupe() == null)
        {
            effective.setDedupe(true);
        }
        return effective;
    }

    private List<EventAudienceRowVo> paginate(List<EventAudienceRowVo> rows, int pageNum, int pageSize)
    {
        if (rows == null || rows.isEmpty())
        {
            return Collections.emptyList();
        }
        int from = (pageNum - 1) * pageSize;
        if (from >= rows.size())
        {
            return Collections.emptyList();
        }
        int to = Math.min(from + pageSize, rows.size());
        return new ArrayList<>(rows.subList(from, to));
    }

    private String findOpenId(String contactId)
    {
        List<CrmCustomerIdentity> identities = identityService.listIdentitiesByContact(contactId);
        for (CrmCustomerIdentity id : identities)
        {
            if ("wechat_mp_openid".equals(id.getIdentityType()))
            {
                return id.getIdentityValue();
            }
        }
        return null;
    }

    @Override
    public EventAudienceRowVo findByAudienceKey(String tenantId, String eventId, String audienceKey)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        assertEventExists(eventId);
        if (StringUtils.isEmpty(audienceKey))
        {
            return null;
        }
        List<EventAudienceRowVo> merged = loadMergedRows(tenantId, eventId, true);
        for (EventAudienceRowVo row : merged)
        {
            if (audienceKey.equals(row.getAudienceKey()))
            {
                return row;
            }
        }
        return null;
    }

    @Override
    public EventAudienceRowVo findByLeadId(String tenantId, String eventId, String leadId)
    {
        DataTableTenantSupport.requireTenantId(tenantId);
        assertEventExists(eventId);
        if (StringUtils.isEmpty(leadId))
        {
            return null;
        }
        List<EventAudienceRowVo> merged = loadMergedRows(tenantId, eventId, true);
        for (EventAudienceRowVo row : merged)
        {
            if (leadId.equals(row.getLeadId()))
            {
                return row;
            }
        }
        return null;
    }
}
