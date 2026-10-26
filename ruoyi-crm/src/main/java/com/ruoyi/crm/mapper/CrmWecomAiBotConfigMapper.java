package com.ruoyi.crm.mapper;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmWecomAiBotConfig;

public interface CrmWecomAiBotConfigMapper
{
    CrmWecomAiBotConfig selectFirst();

    CrmWecomAiBotConfig selectByTenant(@Param("tenant") String tenant);

    int upsert(CrmWecomAiBotConfig row);
}
