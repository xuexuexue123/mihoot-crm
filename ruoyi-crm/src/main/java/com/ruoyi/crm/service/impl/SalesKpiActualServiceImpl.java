package com.ruoyi.crm.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.ruoyi.crm.domain.dto.SalesKpiActualRow;
import com.ruoyi.crm.domain.dto.SalesKpiOptyCloseRow;
import com.ruoyi.crm.domain.enums.SalesKpiCode;
import com.ruoyi.crm.mapper.SalesKpiActualMapper;
import com.ruoyi.crm.service.ISalesKpiActualService;
import com.ruoyi.crm.service.support.OptyStageKpiSupport;
import com.ruoyi.crm.service.support.SalesKpiActivityCriteria;

/**
 * KPI 实际值实时聚合。
 */
@Service
public class SalesKpiActualServiceImpl implements ISalesKpiActualService
{
    @Autowired
    private SalesKpiActualMapper salesKpiActualMapper;

    @Autowired
    private OptyStageKpiSupport optyStageKpiSupport;

    @Override
    public Map<String, BigDecimal> aggregate(List<String> ownerIds, Date startDate, Date endDate, SalesKpiCode kpi)
    {
        if (CollectionUtils.isEmpty(ownerIds) || startDate == null || endDate == null || kpi == null)
        {
            return Collections.emptyMap();
        }
        switch (kpi)
        {
            case CONTRACT_AMOUNT:
                return toMap(salesKpiActualMapper.sumContractAmount(ownerIds, startDate, endDate));
            case WON_CONTRACT_COUNT:
                return toMap(salesKpiActualMapper.countWonContracts(ownerIds, startDate, endDate));
            case WIN_RATE:
                return aggregateWinRate(ownerIds, startDate, endDate);
            case FOLLOWED_CUSTOMERS:
                return toMap(salesKpiActualMapper.countFollowedCustomers(ownerIds, startDate, endDate,
                        SalesKpiActivityCriteria.FOLLOWUP_EVENT_TYPES));
            case MET_CUSTOMERS:
                return toMap(salesKpiActualMapper.countMetCustomers(ownerIds, startDate, endDate,
                        SalesKpiActivityCriteria.MET_CUSTOMER_EVENT_TYPES,
                        SalesKpiActivityCriteria.MET_EXCLUDED_EVENT_TYPES));
            case ASSIGNED_CUSTOMERS:
                return toMap(salesKpiActualMapper.countAssignedCustomers(ownerIds, startDate, endDate));
            case FOLLOWUP_COUNT:
                return toMap(salesKpiActualMapper.countFollowups(ownerIds, startDate, endDate,
                        SalesKpiActivityCriteria.FOLLOWUP_EVENT_TYPES));
            case OPTY_COUNT:
                return toMap(salesKpiActualMapper.countOpties(ownerIds, startDate, endDate));
            case OPTY_AMOUNT:
                return toMap(salesKpiActualMapper.sumOptyAmount(ownerIds, startDate, endDate));
            default:
                return Collections.emptyMap();
        }
    }

    private Map<String, BigDecimal> aggregateWinRate(List<String> ownerIds, Date startDate, Date endDate)
    {
        List<SalesKpiOptyCloseRow> rows = salesKpiActualMapper.selectClosedOpties(ownerIds, startDate, endDate);
        Map<String, int[]> counters = new HashMap<>();
        if (rows != null)
        {
            for (SalesKpiOptyCloseRow row : rows)
            {
                if (row.getOwnerId() == null || !optyStageKpiSupport.isTerminalOpty(row.getStage(), row.getCloseReason()))
                {
                    continue;
                }
                int[] pair = counters.computeIfAbsent(row.getOwnerId(), k -> new int[2]);
                pair[1]++; // denominator
                if (optyStageKpiSupport.isClosedWonStage(row.getStage()))
                {
                    pair[0]++; // numerator
                }
            }
        }
        Map<String, BigDecimal> result = new HashMap<>();
        for (Map.Entry<String, int[]> e : counters.entrySet())
        {
            int denom = e.getValue()[1];
            if (denom <= 0)
            {
                continue;
            }
            BigDecimal rate = BigDecimal.valueOf(e.getValue()[0])
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(denom), 2, RoundingMode.HALF_UP);
            result.put(e.getKey(), rate);
        }
        return result;
    }

    private static Map<String, BigDecimal> toMap(List<SalesKpiActualRow> rows)
    {
        if (CollectionUtils.isEmpty(rows))
        {
            return Collections.emptyMap();
        }
        Map<String, BigDecimal> map = new HashMap<>(rows.size());
        for (SalesKpiActualRow row : rows)
        {
            if (row.getOwnerId() != null)
            {
                map.put(row.getOwnerId(), row.getActualValue() != null ? row.getActualValue() : BigDecimal.ZERO);
            }
        }
        return map;
    }
}
