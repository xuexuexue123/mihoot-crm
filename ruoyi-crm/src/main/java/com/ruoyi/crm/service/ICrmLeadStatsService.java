package com.ruoyi.crm.service;

import java.util.Map;

import com.ruoyi.crm.domain.dto.CrmLeadStatsQuery;
import com.ruoyi.crm.domain.vo.CrmLeadStatsSummaryVo;

public interface ICrmLeadStatsService
{
    CrmLeadStatsSummaryVo summary(CrmLeadStatsQuery query);

    Map<String, Object> funnel(CrmLeadStatsQuery query);

    Map<String, Object> conversionByOwner(CrmLeadStatsQuery query);

    Map<String, Object> trend(CrmLeadStatsQuery query);
}
