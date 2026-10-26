package com.ruoyi.crm.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.crm.domain.AnalyticsQueryScope;
import com.ruoyi.crm.mapper.CrmAnalyticsAggregateMapper;
import com.ruoyi.crm.service.ICrmAnalyticsAggregateService;

@Service
public class CrmAnalyticsAggregateServiceImpl implements ICrmAnalyticsAggregateService
{
    @Autowired
    private CrmAnalyticsAggregateMapper crmAnalyticsAggregateMapper;

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public BigDecimal queryOptyPipelineTotal(AnalyticsQueryScope scope)
    {
        return crmAnalyticsAggregateMapper.selectOptyPipelineTotal(scope);
    }

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<Map<String, Object>> queryOptyCountByStage(AnalyticsQueryScope scope)
    {
        return crmAnalyticsAggregateMapper.selectOptyCountByStage(scope);
    }

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public Long queryTaskOverdueCount(AnalyticsQueryScope scope)
    {
        return crmAnalyticsAggregateMapper.selectTaskOverdueCount(scope);
    }

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public Long queryLeadCreatedCount(AnalyticsQueryScope scope)
    {
        return crmAnalyticsAggregateMapper.selectLeadCreatedCount(scope);
    }

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public Long queryLeadConvertedCount(AnalyticsQueryScope scope)
    {
        return crmAnalyticsAggregateMapper.selectLeadConvertedCount(scope);
    }

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public Map<String, Object> queryClvSummary(AnalyticsQueryScope scope)
    {
        return crmAnalyticsAggregateMapper.selectClvSummary(scope);
    }
}
