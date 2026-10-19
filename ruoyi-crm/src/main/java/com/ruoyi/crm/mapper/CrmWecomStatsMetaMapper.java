package com.ruoyi.crm.mapper;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmWecomStatsMeta;

public interface CrmWecomStatsMetaMapper
{
    CrmWecomStatsMeta selectMeta();

    int insertDefaultIfAbsent();

    int tryAcquireColdStartLock(@Param("lockMinutes") int lockMinutes);

    int forceAcquireBackfillLock(@Param("lockMinutes") int lockMinutes);

    int markDone(@Param("backfillDays") int backfillDays);

    int markFailed(@Param("lastError") String lastError);
}
