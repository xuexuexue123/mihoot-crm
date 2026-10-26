package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.dto.AnalyticsQueryRequest;
import com.ruoyi.crm.domain.vo.AnalyticsQueryResponse;

public interface ICrmAnalyticsQueryService
{
    AnalyticsQueryResponse query(String tenantId, AnalyticsQueryRequest request);
}
