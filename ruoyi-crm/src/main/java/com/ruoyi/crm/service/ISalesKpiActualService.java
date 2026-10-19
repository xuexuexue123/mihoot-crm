package com.ruoyi.crm.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ruoyi.crm.domain.enums.SalesKpiCode;

/**
 * KPI 实际值聚合（不落库）。
 */
public interface ISalesKpiActualService
{
    /**
     * @return ownerId → actualValue；赢单率分母为 0 的 owner 不入 map（list 侧填 null）
     */
    Map<String, BigDecimal> aggregate(List<String> ownerIds, Date startDate, Date endDate, SalesKpiCode kpi);
}
