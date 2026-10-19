package com.ruoyi.crm.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.crm.domain.ContextSummaryScope;
import com.ruoyi.crm.mapper.CrmContextSummaryMapper;
import com.ruoyi.crm.service.ICrmContextSummaryScopeService;

@Service
public class CrmContextSummaryScopeServiceImpl implements ICrmContextSummaryScopeService
{
    @Autowired
    private CrmContextSummaryMapper crmContextSummaryMapper;

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public Long countAccessibleOrg(ContextSummaryScope scope)
    {
        return crmContextSummaryMapper.countAccessibleOrg(scope);
    }

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public Long countAccessibleAccount(ContextSummaryScope scope)
    {
        return crmContextSummaryMapper.countAccessibleAccount(scope);
    }

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public Long countAccessibleContact(ContextSummaryScope scope)
    {
        return crmContextSummaryMapper.countAccessibleContact(scope);
    }

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public Long countAccessibleOpty(ContextSummaryScope scope)
    {
        return crmContextSummaryMapper.countAccessibleOpty(scope);
    }

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public Long countAccessibleLead(ContextSummaryScope scope)
    {
        return crmContextSummaryMapper.countAccessibleLead(scope);
    }

    @Override
    public List<Map<String, Object>> selectRecentPulse(ContextSummaryScope scope)
    {
        return crmContextSummaryMapper.selectRecentPulse(scope);
    }
}
