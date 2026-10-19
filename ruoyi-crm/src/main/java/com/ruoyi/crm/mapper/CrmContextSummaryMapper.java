package com.ruoyi.crm.mapper;

import java.util.List;
import java.util.Map;
import com.ruoyi.crm.domain.ContextSummaryScope;

public interface CrmContextSummaryMapper
{
    Long countAccessibleOrg(ContextSummaryScope scope);

    Long countAccessibleAccount(ContextSummaryScope scope);

    Long countAccessibleContact(ContextSummaryScope scope);

    Long countAccessibleOpty(ContextSummaryScope scope);

    Long countAccessibleLead(ContextSummaryScope scope);

    Long countOptyByOrgId(ContextSummaryScope scope);

    Long countOpenTaskByOrgId(ContextSummaryScope scope);

    Long countContactByOrgId(ContextSummaryScope scope);

    Long countContractByOrgId(ContextSummaryScope scope);

    Long countOptyByAccountId(ContextSummaryScope scope);

    Long countOpenTaskByAccountId(ContextSummaryScope scope);

    Long countContactByAccountId(ContextSummaryScope scope);

    Long countContractByAccountId(ContextSummaryScope scope);

    Long countOptyByContactId(ContextSummaryScope scope);

    Long countOpenTaskByContactId(ContextSummaryScope scope);

    Long countOpenTaskByOptyId(ContextSummaryScope scope);

    Long countOptyByLeadId(ContextSummaryScope scope);

    List<Map<String, Object>> selectRecentPulse(ContextSummaryScope scope);
}
