package com.ruoyi.crm.service;

import java.util.function.Consumer;

import com.ruoyi.crm.domain.dto.wecom.CrmWecomStatsBackfillResult;

/**
 * 企业微信统计数据快照写入（定时任务与回补共用）
 */
public interface ICrmWecomStatsSnapshotService
{
    /**
     * 快照昨天各成员/群主统计数据
     */
    void snapshotYesterday();

    /**
     * 回补历史数据（最多 180 天）
     *
     * @param days    回补天数，最大 180
     * @param dataset behavior / groupchat / both
     */
    CrmWecomStatsBackfillResult backfill(int days, String dataset);

    /**
     * 核心回补逻辑（同步），供冷启动异步任务与手动回补共用
     */
    CrmWecomStatsBackfillResult backfillTenant(int days, String dataset);

    /**
     * 异步回补（冷启动用），在独立线程中绑定租户数据源后执行
     */
    void backfillTenantAsync(String tenantKey, int days, String dataset,
            Consumer<CrmWecomStatsBackfillResult> onSuccess, Consumer<Exception> onFailure);
}
