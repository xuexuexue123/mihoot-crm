package com.ruoyi.crm.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.crm.domain.ContextSummaryScope;

public interface ICrmContextSummaryScopeService
{
    Long countAccessibleOrg(ContextSummaryScope scope);

    Long countAccessibleAccount(ContextSummaryScope scope);

    Long countAccessibleContact(ContextSummaryScope scope);

    Long countAccessibleOpty(ContextSummaryScope scope);

    Long countAccessibleLead(ContextSummaryScope scope);

    List<Map<String, Object>> selectRecentPulse(ContextSummaryScope scope);
}
