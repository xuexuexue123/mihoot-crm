package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.vo.ContextSummaryVo;

public interface ICrmContextSummaryService
{
    ContextSummaryVo getSummary(String tenantId, String entityType, String entityId);
}
