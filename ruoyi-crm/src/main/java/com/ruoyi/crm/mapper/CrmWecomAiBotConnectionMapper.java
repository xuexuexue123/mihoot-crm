package com.ruoyi.crm.mapper;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmWecomAiBotConnection;

public interface CrmWecomAiBotConnectionMapper
{
    CrmWecomAiBotConnection selectFirst();

    CrmWecomAiBotConnection selectByTenant(@Param("tenant") String tenant);

    int upsert(CrmWecomAiBotConnection row);

    /**
     * 安全地设为 connecting 状态。仅当现状为 disconnected 或无记录时生效，
     * 避免覆盖 connecting / connected / reconnecting 等活跃状态。
     */
    int upsertConnecting(CrmWecomAiBotConnection row);

    int updateStatus(CrmWecomAiBotConnection row);
}
