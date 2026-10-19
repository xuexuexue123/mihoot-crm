package com.ruoyi.crm.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmDashboardSnapshot;

public interface CrmDashboardSnapshotMapper
{
    /**
     * UPSERT 快照行：ON CONFLICT (corp_id, snapshot_date, metric_type) DO UPDATE
     */
    int upsert(CrmDashboardSnapshot row);

    /**
     * 查询指定日期指定指标的快照
     */
    CrmDashboardSnapshot selectByDateAndType(@Param("snapshotDate") Date snapshotDate,
                                              @Param("metricType") String metricType);

    /**
     * 查询指定日期所有指标的快照列表
     */
    List<CrmDashboardSnapshot> selectByDate(@Param("snapshotDate") Date snapshotDate);
}
