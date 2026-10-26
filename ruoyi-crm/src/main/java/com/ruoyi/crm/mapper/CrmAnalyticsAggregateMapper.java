package com.ruoyi.crm.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import com.ruoyi.crm.domain.AnalyticsQueryScope;

public interface CrmAnalyticsAggregateMapper
{
    BigDecimal selectOptyPipelineTotal(AnalyticsQueryScope scope);

    List<Map<String, Object>> selectOptyCountByStage(AnalyticsQueryScope scope);

    Long selectTaskOverdueCount(AnalyticsQueryScope scope);

    Long selectLeadCreatedCount(AnalyticsQueryScope scope);

    Long selectLeadConvertedCount(AnalyticsQueryScope scope);

    Map<String, Object> selectClvSummary(AnalyticsQueryScope scope);
}
