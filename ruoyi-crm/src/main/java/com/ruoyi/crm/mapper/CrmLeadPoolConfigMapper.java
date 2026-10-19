package com.ruoyi.crm.mapper;

import com.ruoyi.crm.domain.CrmLeadPoolConfig;

public interface CrmLeadPoolConfigMapper
{
    CrmLeadPoolConfig selectFirst();

    int insertCrmLeadPoolConfig(CrmLeadPoolConfig config);

    int updateCrmLeadPoolConfig(CrmLeadPoolConfig config);

    int countClaimsTodayByUserId(Long userId);
}
