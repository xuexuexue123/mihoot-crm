package com.ruoyi.crm.service.support;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmAccount;
import com.ruoyi.crm.domain.CrmAccountValue;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmPatientValue;
import com.ruoyi.crm.domain.dto.PatientClvRecomputeRequest;
import com.ruoyi.crm.domain.vo.CampaignRoiVo;
import com.ruoyi.crm.domain.vo.ContactClvAggregateVo;
import com.ruoyi.crm.domain.vo.ContactClvVo;
import com.ruoyi.crm.domain.vo.FamilyClvVo;
import com.ruoyi.crm.domain.vo.RecomputeResultVo;
import com.ruoyi.crm.mapper.CrmAccountValueMapper;
import com.ruoyi.crm.mapper.CrmClvAggregateMapper;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmPatientValueMapper;
import com.ruoyi.crm.mapper.EventPerformanceMapper;
import com.ruoyi.crm.service.ICrmAccountService;
import com.ruoyi.crm.service.IPatientClvService;

@Service
public class PatientClvServiceImpl implements IPatientClvService
{
    private static final int BATCH_SIZE = 500;
    private static final long CLV_AGGREGATE_TIMEOUT_MS = 3000L;

    @Autowired
    private PatientClvCalculator calculator;

    @Autowired
    private CrmPatientValueMapper patientValueMapper;

    @Autowired
    private CrmAccountValueMapper accountValueMapper;

    @Autowired
    private CrmClvAggregateMapper clvAggregateMapper;

    @Autowired
    private CrmContactMapper contactMapper;

    @Autowired
    private EventPerformanceMapper eventPerformanceMapper;

    @Autowired
    private ICrmAccountService accountService;

    private final ScheduledExecutorService debounceExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "patient-clv-debounce");
        t.setDaemon(true);
        return t;
    });

    private final ConcurrentHashMap<String, Long> pendingContacts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> pendingAccountByContact = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> pendingAccounts = new ConcurrentHashMap<>();

    @PreDestroy
    public void shutdown()
    {
        debounceExecutor.shutdown();
    }

    @Override
    public FamilyClvVo getFamilyClv(String accountId)
    {
        CrmAccountValue snap = accountValueMapper.selectByAccountId(accountId);
        if (snap == null)
        {
            recomputeAccounts(Collections.singletonList(accountId));
            snap = accountValueMapper.selectByAccountId(accountId);
        }
        return toFamilyVo(snap, accountId);
    }

    @Override
    public ContactClvVo getContactClv(String contactId)
    {
        CrmPatientValue snap = patientValueMapper.selectByContactId(contactId);
        if (snap == null)
        {
            recomputeContacts(Collections.singletonList(contactId));
            snap = patientValueMapper.selectByContactId(contactId);
        }
        return toContactVo(snap);
    }

    @Override
    public List<CrmPatientValue> listPatientValues(CrmPatientValue query)
    {
        return patientValueMapper.selectCrmPatientValueList(query);
    }

    @Override
    public List<CrmAccountValue> listAccountValues(CrmAccountValue query)
    {
        return accountValueMapper.selectCrmAccountValueList(query);
    }

    @Override
    public List<CampaignRoiVo> listCampaignRoi(String campaignType, String campaignId, java.util.Date dateFrom, java.util.Date dateTo)
    {
        return clvAggregateMapper.selectCampaignRoiList(campaignType, campaignId, dateFrom, dateTo, calculator.resolveClosedWonStages());
    }

    @Override
    public RecomputeResultVo recompute(PatientClvRecomputeRequest request)
    {
        long start = System.currentTimeMillis();
        RecomputeResultVo result = new RecomputeResultVo();
        String scope = request != null && StringUtils.isNotEmpty(request.getScope()) ? request.getScope() : "all";

        calculator.allocateCacForAllContacts();

        List<String> contactIds = resolveContactIds(scope, request);
        int affectedContacts = recomputeContacts(contactIds);

        List<String> accountIds = resolveAccountIds(scope, request, contactIds);
        int affectedAccounts = recomputeAccounts(accountIds);

        result.setAffectedContacts(affectedContacts);
        result.setAffectedAccounts(affectedAccounts);
        result.setCostMs(System.currentTimeMillis() - start);
        return result;
    }

    @Override
    public void enqueueRecomputeForContact(String contactId, String accountId)
    {
        if (StringUtils.isNotEmpty(contactId))
        {
            pendingContacts.put(contactId, System.currentTimeMillis());
            if (StringUtils.isNotEmpty(accountId))
            {
                pendingAccountByContact.put(contactId, accountId);
            }
        }
        else if (StringUtils.isNotEmpty(accountId))
        {
            pendingAccounts.put(accountId, System.currentTimeMillis());
        }
        else
        {
            return;
        }
        debounceExecutor.schedule(this::flushPending, 5, TimeUnit.SECONDS);
    }

    @Override
    public ContactClvAggregateVo aggregateContactClv(Set<String> contactIds)
    {
        ContactClvAggregateVo result = new ContactClvAggregateVo();
        if (contactIds == null || contactIds.isEmpty())
        {
            return result;
        }
        long deadline = System.currentTimeMillis() + CLV_AGGREGATE_TIMEOUT_MS;
        List<String> ids = new ArrayList<>(contactIds);
        List<CrmPatientValue> snapshots = patientValueMapper.selectByContactIds(ids);
        Map<String, CrmPatientValue> snapByContact = new HashMap<>();
        for (CrmPatientValue snap : snapshots)
        {
            if (snap != null && StringUtils.isNotEmpty(snap.getContactId()))
            {
                snapByContact.put(snap.getContactId(), snap);
            }
        }
        List<String> stages = calculator.resolveClosedWonStages();
        Map<String, BigDecimal> familyNetCache = new HashMap<>();
        BigDecimal booked = BigDecimal.ZERO;
        BigDecimal inflight = BigDecimal.ZERO;
        BigDecimal refund = BigDecimal.ZERO;
        BigDecimal net = BigDecimal.ZERO;
        boolean estimate = false;
        for (String contactId : ids)
        {
            if (System.currentTimeMillis() > deadline)
            {
                estimate = true;
                break;
            }
            CrmPatientValue pv = snapByContact.get(contactId);
            if (pv == null)
            {
                CrmContact contact = contactMapper.selectCrmContactByContactId(contactId);
                if (contact == null)
                {
                    continue;
                }
                BigDecimal familyNet = familyNetCache.computeIfAbsent(contact.getAccountId(), this::quickFamilyNet);
                pv = calculator.computeContactValue(contact, stages, familyNet);
                estimate = true;
            }
            booked = booked.add(nz(pv.getBookedTotal()));
            inflight = inflight.add(nz(pv.getInflightTotal()));
            refund = refund.add(nz(pv.getRefundTotal()));
            net = net.add(nz(pv.getNetClv()));
        }
        result.setBookedClvTotal(booked);
        result.setInflightClvTotal(inflight);
        result.setRefundTotal(refund);
        result.setNetClvTotal(net);
        result.setEstimate(estimate);
        return result;
    }

    @Override
    public ContactClvAggregateVo aggregateContactClvWindow(String eventId, Set<String> contactIds,
            Map<String, Date> firstTouchByContact, int windowDays)
    {
        ContactClvAggregateVo result = new ContactClvAggregateVo();
        if (contactIds == null || contactIds.isEmpty() || windowDays <= 0)
        {
            return result;
        }
        List<String> stages = calculator.resolveClosedWonStages();
        long windowMs = windowDays * 24L * 60L * 60L * 1000L;
        BigDecimal booked = BigDecimal.ZERO;
        BigDecimal inflight = BigDecimal.ZERO;
        BigDecimal refund = BigDecimal.ZERO;
        for (String contactId : contactIds)
        {
            Date firstTouch = firstTouchByContact != null ? firstTouchByContact.get(contactId) : null;
            if (firstTouch == null)
            {
                firstTouch = eventPerformanceMapper.selectContactConversionTime(eventId, contactId);
            }
            if (firstTouch == null)
            {
                continue;
            }
            Date windowEnd = new Date(firstTouch.getTime() + windowMs);
            BigDecimal b = eventPerformanceMapper.selectWindowBookedClv(contactId, firstTouch, windowEnd);
            BigDecimal i = eventPerformanceMapper.selectWindowInflightClv(contactId, firstTouch, windowEnd, stages);
            BigDecimal r = eventPerformanceMapper.selectWindowRefundClv(contactId, firstTouch, windowEnd);
            booked = booked.add(nz(b));
            inflight = inflight.add(nz(i));
            refund = refund.add(nz(r));
        }
        result.setBookedClvTotal(booked);
        result.setInflightClvTotal(inflight);
        result.setRefundTotal(refund);
        result.setNetClvTotal(booked.add(inflight).subtract(refund));
        return result;
    }

    private static BigDecimal nz(BigDecimal value)
    {
        return value != null ? value : BigDecimal.ZERO;
    }

    private void flushPending()
    {
        Set<String> contacts = new HashSet<>(pendingContacts.keySet());
        pendingContacts.keySet().removeAll(contacts);
        Set<String> accounts = new HashSet<>(pendingAccounts.keySet());
        pendingAccounts.keySet().removeAll(accounts);
        for (String cid : contacts)
        {
            String aid = pendingAccountByContact.remove(cid);
            if (StringUtils.isNotEmpty(aid))
            {
                accounts.add(aid);
            }
            else
            {
                CrmContact c = contactMapper.selectCrmContactByContactId(cid);
                if (c != null && StringUtils.isNotEmpty(c.getAccountId()))
                {
                    accounts.add(c.getAccountId());
                }
            }
        }
        for (String accountId : accounts)
        {
            contacts.addAll(patientValueMapper.selectContactIdsByAccountId(accountId));
        }
        if (!contacts.isEmpty())
        {
            recomputeContacts(new ArrayList<>(contacts));
        }
        if (!accounts.isEmpty())
        {
            recomputeAccounts(new ArrayList<>(accounts));
        }
    }

    private List<String> resolveContactIds(String scope, PatientClvRecomputeRequest request)
    {
        if ("contact".equals(scope) && request != null && request.getContactIds() != null && !request.getContactIds().isEmpty())
        {
            return request.getContactIds();
        }
        if ("account".equals(scope) && request != null && request.getAccountIds() != null && !request.getAccountIds().isEmpty())
        {
            return patientValueMapper.selectContactIdsByAccountIds(request.getAccountIds());
        }
        return patientValueMapper.selectAllContactIds();
    }

    private List<String> resolveAccountIds(String scope, PatientClvRecomputeRequest request, List<String> contactIds)
    {
        if ("account".equals(scope) && request != null && request.getAccountIds() != null && !request.getAccountIds().isEmpty())
        {
            return request.getAccountIds();
        }
        if ("contact".equals(scope))
        {
            return accountValueMapper.selectAccountIdsByContactIds(contactIds);
        }
        return accountValueMapper.selectAllAccountIds();
    }

    private int recomputeContacts(List<String> contactIds)
    {
        if (contactIds == null || contactIds.isEmpty())
        {
            return 0;
        }
        List<String> stages = calculator.resolveClosedWonStages();
        int count = 0;
        for (int i = 0; i < contactIds.size(); i += BATCH_SIZE)
        {
            List<String> batch = contactIds.subList(i, Math.min(i + BATCH_SIZE, contactIds.size()));
            Map<String, BigDecimal> familyNetCache = new HashMap<>();
            for (String contactId : batch)
            {
                CrmContact contact = contactMapper.selectCrmContactByContactId(contactId);
                if (contact == null)
                {
                    continue;
                }
                BigDecimal familyNet = familyNetCache.computeIfAbsent(contact.getAccountId(), this::quickFamilyNet);
                CrmPatientValue row = calculator.computeContactValue(contact, stages, familyNet);
                row.setTenantId(TenantDataSourceSupport.currentTenantKey());
                patientValueMapper.upsertCrmPatientValue(row);
                count++;
            }
        }
        return count;
    }

    private int recomputeAccounts(List<String> accountIds)
    {
        if (accountIds == null || accountIds.isEmpty())
        {
            return 0;
        }
        int count = 0;
        for (String accountId : accountIds)
        {
            if (StringUtils.isEmpty(accountId))
            {
                continue;
            }
            List<String> memberIds = patientValueMapper.selectContactIdsByAccountId(accountId);
            List<CrmPatientValue> members = new ArrayList<>();
            for (String cid : memberIds)
            {
                CrmPatientValue pv = patientValueMapper.selectByContactId(cid);
                if (pv != null)
                {
                    members.add(pv);
                }
            }
            CrmAccount account = accountService.selectCrmAccountByAccountId(accountId);
            CrmAccountValue row = calculator.computeAccountValue(accountId, account, members);
            row.setTenantId(TenantDataSourceSupport.currentTenantKey());
            accountValueMapper.upsertCrmAccountValue(row);
            count++;
        }
        return count;
    }

    private BigDecimal quickFamilyNet(String accountId)
    {
        if (StringUtils.isEmpty(accountId))
        {
            return BigDecimal.ZERO;
        }
        CrmAccountValue av = accountValueMapper.selectByAccountId(accountId);
        return av != null && av.getNetClv() != null ? av.getNetClv() : BigDecimal.ZERO;
    }

    private FamilyClvVo toFamilyVo(CrmAccountValue snap, String accountId)
    {
        FamilyClvVo vo = new FamilyClvVo();
        if (snap == null)
        {
            vo.setAccountId(accountId);
            vo.setCurrency("CNY");
            vo.setMemberCount(0);
            vo.setBookedTotal(BigDecimal.ZERO);
            vo.setInflightTotal(BigDecimal.ZERO);
            vo.setRefundTotal(BigDecimal.ZERO);
            vo.setNetClv(BigDecimal.ZERO);
            return vo;
        }
        vo.setAccountId(snap.getAccountId());
        vo.setAccountName(snap.getAccountName());
        vo.setCurrency(snap.getCurrency());
        vo.setMemberCount(snap.getMemberCount());
        vo.setBookedTotal(snap.getBookedTotal());
        vo.setInflightTotal(snap.getInflightTotal());
        vo.setRefundTotal(snap.getRefundTotal());
        vo.setNetClv(snap.getNetClv());
        vo.setPaidTotal(snap.getPaidTotal());
        vo.setPendingReceivable(snap.getPendingReceivable());
        vo.setPltv12m(snap.getPltv12m());
        vo.setAcquisitionCost(snap.getAcquisitionCost());
        vo.setRoi(snap.getRoi());
        vo.setTier(snap.getTier());
        vo.setByTreatment(parseTreatment(snap.getByTreatment()));
        vo.setLastVisitAt(snap.getLastVisitAt());
        vo.setDaysSinceLastVisit(snap.getDaysSinceLastVisit());
        vo.setCalculatedAt(snap.getCalculatedAt());
        return vo;
    }

    private ContactClvVo toContactVo(CrmPatientValue snap)
    {
        ContactClvVo vo = new ContactClvVo();
        if (snap == null)
        {
            return vo;
        }
        vo.setContactId(snap.getContactId());
        vo.setAccountId(snap.getAccountId());
        vo.setCurrency(snap.getCurrency());
        vo.setBookedTotal(snap.getBookedTotal());
        vo.setInflightTotal(snap.getInflightTotal());
        vo.setRefundTotal(snap.getRefundTotal());
        vo.setNetClv(snap.getNetClv());
        vo.setAcquisitionCost(snap.getAcquisitionCost());
        vo.setCacPending(snap.getAcquisitionCost() == null || snap.getAcquisitionCost().compareTo(BigDecimal.ZERO) == 0);
        vo.setPltv12m(snap.getPltv12m());
        vo.setPltv36m(snap.getPltv36m());
        vo.setTier(snap.getTier());
        vo.setChurnRiskScore(snap.getChurnRiskScore());
        vo.setFirstVisitAt(snap.getFirstVisitAt());
        vo.setLastVisitAt(snap.getLastVisitAt());
        vo.setNextRecallDueAt(snap.getNextRecallDueAt());
        vo.setVisitCount12m(snap.getVisitCount12m());
        vo.setAvgVisitAmount(snap.getAvgVisitAmount());
        vo.setByTreatment(parseTreatment(snap.getByTreatment()));
        vo.setOriginalSource(snap.getOriginalSource());
        vo.setCalculatedAt(snap.getCalculatedAt());
        return vo;
    }

    private Map<String, BigDecimal> parseTreatment(String json)
    {
        if (StringUtils.isEmpty(json))
        {
            return new HashMap<>();
        }
        return JSON.parseObject(json, new TypeReference<Map<String, BigDecimal>>() {});
    }
}
