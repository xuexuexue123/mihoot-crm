package com.ruoyi.crm.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import com.ruoyi.crm.domain.AnalyticsQueryScope;

public interface ICrmAnalyticsAggregateService
{
    BigDecimal queryOptyPipelineTotal(AnalyticsQueryScope scope);

    List<Map<String, Object>> queryOptyCountByStage(AnalyticsQueryScope scope);

    Long queryTaskOverdueCount(AnalyticsQueryScope scope);

    Long queryLeadCreatedCount(AnalyticsQueryScope scope);

    Long queryLeadConvertedCount(AnalyticsQueryScope scope);

    Map<String, Object> queryClvSummary(AnalyticsQueryScope scope);
}
