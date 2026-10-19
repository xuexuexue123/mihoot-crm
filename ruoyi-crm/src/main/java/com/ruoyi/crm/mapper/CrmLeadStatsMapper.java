package com.ruoyi.crm.mapper;

import java.util.List;
import java.util.Map;

import com.ruoyi.crm.domain.dto.CrmLeadStatsQuery;

public interface CrmLeadStatsMapper
{
    Long countNewLeads(CrmLeadStatsQuery query);

    Long countConvertedInRange(CrmLeadStatsQuery query);

    Long countActiveLeads(CrmLeadStatsQuery query);

    Long countOpenPool(CrmLeadStatsQuery query);

    Long countAssignedPool(CrmLeadStatsQuery query);

    List<Map<String, Object>> selectFunnelByStage(CrmLeadStatsQuery query);

    List<Map<String, Object>> selectConversionByOwner(CrmLeadStatsQuery query);

    List<Map<String, Object>> selectTrendSeries(CrmLeadStatsQuery query);
}
