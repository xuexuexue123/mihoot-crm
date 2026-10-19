package com.ruoyi.crm.service.support;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmAccount;
import com.ruoyi.crm.domain.CrmAccountValue;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmPatientValue;
import com.ruoyi.crm.mapper.CrmClvAggregateMapper;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysDictTypeService;

@Component
public class PatientClvCalculator
{
    private static final String DICT_CLOSED_WON_STAGES = "crm_opty_stage_closed_won";
    private static final String[] TREATMENT_KEYS = { "implant", "ortho", "restorative", "preventive", "pediatric" };

    @Autowired
    private CrmClvAggregateMapper clvAggregateMapper;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private ISysDictTypeService dictTypeService;

    @Value("${crm.dental.closed-won-stages:}")
    private String closedWonStagesFallback;

    public List<String> resolveClosedWonStages()
    {
        List<String> stages = new ArrayList<>();
        List<SysDictData> dict = dictTypeService.selectDictDataByType(DICT_CLOSED_WON_STAGES);
        if (dict != null)
        {
            for (SysDictData d : dict)
            {
                if ("0".equals(d.getStatus()) && StringUtils.isNotEmpty(d.getDictValue()))
                {
                    stages.add(d.getDictValue());
                }
            }
        }
        if (stages.isEmpty() && StringUtils.isNotEmpty(closedWonStagesFallback))
        {
            for (String p : closedWonStagesFallback.split(","))
            {
                if (StringUtils.isNotEmpty(p.trim()))
                {
                    stages.add(p.trim());
                }
            }
        }
        return stages;
    }

    public void allocateCacForAllContacts()
    {
        clvAggregateMapper.resetAllContactAcquisitionCost();
        Map<String, BigDecimal> contactCac = new HashMap<>();
        for (Map<String, Object> row : clvAggregateMapper.selectAllEventCacCampaigns())
        {
            accumulateCampaignCac(contactCac, String.valueOf(row.get("campaign_id")), toBigDecimal(row.get("spend")), "Marketing Event");
        }
        for (Map<String, Object> row : clvAggregateMapper.selectAllBulkemailCacCampaigns())
        {
            accumulateCampaignCac(contactCac, String.valueOf(row.get("campaign_id")), toBigDecimal(row.get("spend")), "Email Marketing");
        }
        for (Map.Entry<String, BigDecimal> e : contactCac.entrySet())
        {
            clvAggregateMapper.updateContactAcquisitionCost(e.getKey(), e.getValue());
        }
    }

    private void accumulateCampaignCac(Map<String, BigDecimal> contactCac, String campaignId, BigDecimal spend, String conversionType)
    {
        List<Map<String, Object>> contacts = clvAggregateMapper.selectContactIdsForCampaign(campaignId, conversionType);
        if (contacts == null || contacts.isEmpty())
        {
            return;
        }
        BigDecimal perContact = spend.divide(BigDecimal.valueOf(contacts.size()), 2, RoundingMode.HALF_UP);
        for (Map<String, Object> row : contacts)
        {
            String contactId = String.valueOf(row.get("contact_id"));
            contactCac.merge(contactId, perContact, BigDecimal::add);
        }
    }

    public CrmPatientValue computeContactValue(CrmContact contact, List<String> closedWonStages, BigDecimal familyNetClv)
    {
        String contactId = contact.getContactId();
        List<String> stages = closedWonStages;

        BigDecimal booked = nz(clvAggregateMapper.selectBookedTotalByContactId(contactId));
        BigDecimal inflight = nz(clvAggregateMapper.selectInflightTotalByContactId(contactId, stages));
        BigDecimal refund = nz(clvAggregateMapper.selectRefundTotalByContactId(contactId));
        BigDecimal netClv = booked.add(inflight).subtract(refund);

        Integer visit12m = nzInt(clvAggregateMapper.selectVisitCount12mByContactId(contactId));
        BigDecimal booked12m = nz(clvAggregateMapper.selectBookedTotalLast12mByContactId(contactId));
        BigDecimal avgVisit = booked12m.divide(BigDecimal.valueOf(Math.max(visit12m, 1)), 2, RoundingMode.HALF_UP);

        Date firstVisit = clvAggregateMapper.selectFirstVisitAtByContactId(contactId);
        Date lastVisit = clvAggregateMapper.selectLastVisitAtByContactId(contactId);
        Integer totalVisits = nzInt(clvAggregateMapper.selectTotalVisitCountByContactId(contactId));
        clvAggregateMapper.updateContactVisitStats(contactId, firstVisit, lastVisit, totalVisits);

        int daysSince = daysSince(lastVisit);
        BigDecimal retention = retentionCoef(daysSince);
        BigDecimal pltv12m = BigDecimal.valueOf(visit12m).multiply(avgVisit).multiply(retention).setScale(2, RoundingMode.HALF_UP);
        BigDecimal decay = configDecimal("clv.pltv36.decay", new BigDecimal("0.85"));
        BigDecimal pltv36m = pltv12m.multiply(new BigDecimal("3")).multiply(decay).setScale(2, RoundingMode.HALF_UP);
        BigDecimal churn = BigDecimal.ONE.subtract(retention).setScale(6, RoundingMode.HALF_UP);

        BigDecimal acqCost = contact.getAcquisitionCost() != null ? contact.getAcquisitionCost() : BigDecimal.ZERO;
        String tier = resolveTier(netClv, daysSince, totalVisits);

        BigDecimal familyRatio = null;
        if (familyNetClv != null && familyNetClv.compareTo(BigDecimal.ZERO) > 0)
        {
            familyRatio = netClv.divide(familyNetClv, 6, RoundingMode.HALF_UP);
        }

        int recallDays = configInt("clv.recall.interval.days", 180);
        Date nextRecall = lastVisit != null ? new Date(lastVisit.getTime() + recallDays * 86400000L) : null;

        CrmPatientValue row = new CrmPatientValue();
        row.setContactId(contactId);
        row.setAccountId(contact.getAccountId());
        row.setCurrency(resolveCurrency(contact));
        row.setBookedTotal(booked);
        row.setInflightTotal(inflight);
        row.setRefundTotal(refund);
        row.setNetClv(netClv);
        row.setAcquisitionCost(acqCost);
        row.setPltv12m(pltv12m);
        row.setPltv36m(pltv36m);
        row.setTier(tier);
        row.setChurnRiskScore(churn);
        row.setVisitCount12m(visit12m);
        row.setAvgVisitAmount(avgVisit);
        row.setByTreatment(JSON.toJSONString(buildByTreatment(contactId, stages)));
        row.setFirstVisitAt(firstVisit);
        row.setLastVisitAt(lastVisit);
        row.setNextRecallDueAt(nextRecall);
        row.setOriginalSource(contact.getOriginalSource());
        row.setCalculatedAt(new Date());
        return row;
    }

    public CrmAccountValue computeAccountValue(String accountId, CrmAccount account, List<CrmPatientValue> memberSnapshots)
    {
        List<String> stages = resolveClosedWonStages();
        BigDecimal booked = nz(clvAggregateMapper.selectBookedTotalByAccountIdDistinct(accountId));
        BigDecimal inflight = nz(clvAggregateMapper.selectInflightTotalByAccountIdDistinct(accountId, stages));
        BigDecimal refund = nz(clvAggregateMapper.selectRefundTotalByAccountId(accountId));
        BigDecimal netClv = booked.add(inflight).subtract(refund);
        BigDecimal paid = nz(clvAggregateMapper.selectPaidTotalByAccountId(accountId));
        BigDecimal pending = nz(clvAggregateMapper.selectPendingReceivableByAccountId(accountId));
        Integer members = nzInt(clvAggregateMapper.selectMemberCountByAccountId(accountId));

        BigDecimal pltv12m = BigDecimal.ZERO;
        BigDecimal acqTotal = BigDecimal.ZERO;
        Map<String, BigDecimal> byTreatment = emptyTreatmentMap();
        Date lastVisit = null;
        for (CrmPatientValue m : memberSnapshots)
        {
            pltv12m = pltv12m.add(nz(m.getPltv12m()));
            acqTotal = acqTotal.add(nz(m.getAcquisitionCost()));
            mergeTreatment(byTreatment, m.getByTreatment());
            if (m.getLastVisitAt() != null && (lastVisit == null || m.getLastVisitAt().after(lastVisit)))
            {
                lastVisit = m.getLastVisitAt();
            }
        }

        BigDecimal roi = null;
        if (acqTotal.compareTo(BigDecimal.ZERO) > 0)
        {
            roi = netClv.subtract(acqTotal).divide(acqTotal, 6, RoundingMode.HALF_UP);
        }

        int daysSince = daysSince(lastVisit);
        String tier = resolveTier(netClv, daysSince, members > 0 ? 1 : 0);

        CrmAccountValue row = new CrmAccountValue();
        row.setAccountId(accountId);
        row.setAccountName(account != null ? account.getName() : null);
        row.setCurrency(account != null && StringUtils.isNotEmpty(account.getCurrency()) ? account.getCurrency() : "CNY");
        row.setMemberCount(members);
        row.setBookedTotal(booked);
        row.setInflightTotal(inflight);
        row.setRefundTotal(refund);
        row.setNetClv(netClv);
        row.setPaidTotal(paid);
        row.setPendingReceivable(pending);
        row.setPltv12m(pltv12m);
        row.setAcquisitionCost(acqTotal);
        row.setRoi(roi);
        row.setTier(tier);
        row.setByTreatment(JSON.toJSONString(byTreatment));
        row.setLastVisitAt(lastVisit);
        row.setDaysSinceLastVisit(daysSince >= 0 ? daysSince : null);
        row.setCalculatedAt(new Date());
        return row;
    }

    private Map<String, BigDecimal> buildByTreatment(String contactId, List<String> stages)
    {
        Map<String, BigDecimal> result = emptyTreatmentMap();
        List<Map<String, Object>> rows = clvAggregateMapper.selectTreatmentAmountsByContactId(contactId, stages);
        if (rows == null)
        {
            return result;
        }
        for (Map<String, Object> row : rows)
        {
            String code = String.valueOf(row.get("treatment_code"));
            BigDecimal amt = toBigDecimal(row.get("amount"));
            String cat = mapTreatmentCategory(code);
            result.put(cat, result.get(cat).add(amt));
        }
        return result;
    }

    public String mapTreatmentCategory(String treatmentCode)
    {
        if (StringUtils.isEmpty(treatmentCode))
        {
            return "restorative";
        }
        String lower = treatmentCode.toLowerCase();
        if (lower.startsWith("impl") || lower.contains("implant"))
        {
            return "implant";
        }
        if (lower.startsWith("orth") || lower.contains("ortho"))
        {
            return "ortho";
        }
        if (lower.startsWith("prev") || lower.contains("clean") || lower.contains("check"))
        {
            return "preventive";
        }
        if (lower.startsWith("ped") || lower.contains("child"))
        {
            return "pediatric";
        }
        if (lower.startsWith("rest") || lower.contains("fill") || lower.contains("crown"))
        {
            return "restorative";
        }
        return "restorative";
    }

    private void mergeTreatment(Map<String, BigDecimal> target, String json)
    {
        if (StringUtils.isEmpty(json))
        {
            return;
        }
        Map<String, Object> parsed = JSON.parseObject(json);
        for (String key : TREATMENT_KEYS)
        {
            Object v = parsed.get(key);
            if (v != null)
            {
                target.put(key, target.get(key).add(toBigDecimal(v)));
            }
        }
    }

    private Map<String, BigDecimal> emptyTreatmentMap()
    {
        Map<String, BigDecimal> m = new LinkedHashMap<>();
        for (String k : TREATMENT_KEYS)
        {
            m.put(k, BigDecimal.ZERO);
        }
        return m;
    }

    public String resolveTier(BigDecimal netClv, int daysSinceLastVisit, int visitCount)
    {
        if (daysSinceLastVisit > 365 && visitCount == 0)
        {
            return "DORMANT";
        }
        BigDecimal n = nz(netClv);
        if (n.compareTo(configDecimal("clv.tier.vip.min", new BigDecimal("500000"))) >= 0)
        {
            return "VIP";
        }
        if (n.compareTo(configDecimal("clv.tier.gold.min", new BigDecimal("200000"))) >= 0)
        {
            return "GOLD";
        }
        if (n.compareTo(configDecimal("clv.tier.silver.min", new BigDecimal("50000"))) >= 0)
        {
            return "SILVER";
        }
        return "BRONZE";
    }

    private BigDecimal retentionCoef(int daysSinceLastVisit)
    {
        double raw = 1.0 - (daysSinceLastVisit / 365.0);
        raw = Math.max(0.1, Math.min(0.9, raw));
        return BigDecimal.valueOf(raw).setScale(6, RoundingMode.HALF_UP);
    }

    private int daysSince(Date date)
    {
        if (date == null)
        {
            return Integer.MAX_VALUE;
        }
        long diff = System.currentTimeMillis() - date.getTime();
        return (int) (diff / 86400000L);
    }

    private String resolveCurrency(CrmContact contact)
    {
        return "CNY";
    }

    private BigDecimal configDecimal(String key, BigDecimal fallback)
    {
        String v = configService.selectConfigByKey(key);
        if (StringUtils.isEmpty(v))
        {
            return fallback;
        }
        try
        {
            return new BigDecimal(v.trim());
        }
        catch (NumberFormatException e)
        {
            return fallback;
        }
    }

    private int configInt(String key, int fallback)
    {
        String v = configService.selectConfigByKey(key);
        if (StringUtils.isEmpty(v))
        {
            return fallback;
        }
        try
        {
            return Integer.parseInt(v.trim());
        }
        catch (NumberFormatException e)
        {
            return fallback;
        }
    }

    private BigDecimal nz(BigDecimal v)
    {
        return v != null ? v : BigDecimal.ZERO;
    }

    private int nzInt(Integer v)
    {
        return v != null ? v : 0;
    }

    private int toInt(Object v)
    {
        if (v == null)
        {
            return 0;
        }
        if (v instanceof Number)
        {
            return ((Number) v).intValue();
        }
        try
        {
            return Integer.parseInt(String.valueOf(v));
        }
        catch (NumberFormatException e)
        {
            return 0;
        }
    }

    private BigDecimal toBigDecimal(Object v)
    {
        if (v == null)
        {
            return BigDecimal.ZERO;
        }
        if (v instanceof BigDecimal)
        {
            return (BigDecimal) v;
        }
        try
        {
            return new BigDecimal(String.valueOf(v));
        }
        catch (NumberFormatException e)
        {
            return BigDecimal.ZERO;
        }
    }
}
