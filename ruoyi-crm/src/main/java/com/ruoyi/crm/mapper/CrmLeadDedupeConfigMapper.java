package com.ruoyi.crm.mapper;

import com.ruoyi.crm.domain.CrmLeadDedupeConfig;

public interface CrmLeadDedupeConfigMapper
{
    CrmLeadDedupeConfig selectFirst();

    int insertCrmLeadDedupeConfig(CrmLeadDedupeConfig config);

    int updateCrmLeadDedupeConfig(CrmLeadDedupeConfig config);
}
