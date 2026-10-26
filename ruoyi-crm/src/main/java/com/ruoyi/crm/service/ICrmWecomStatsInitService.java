package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.dto.wecom.CrmWecomStatsBackfillResult;
import com.ruoyi.crm.domain.dto.wecom.CrmWecomStatsStatusResponse;

/**
 * 企微统计冷启动初始化
 */
public interface ICrmWecomStatsInitService
{
    /**
     * 查询接口懒触发：未初始化时异步启动 180 天回补
     */
    void ensureInitialized();

    /**
     * 返回对前端友好的初始化状态
     */
    CrmWecomStatsStatusResponse getStatus();

    /**
     * 冷启动回补成功后更新 meta
     */
    void completeColdStart(CrmWecomStatsBackfillResult result, int backfillDays);

    /**
     * 冷启动回补失败后更新 meta
     */
    void failColdStart(String error);

    /**
     * 手动 force 回补前抢占锁
     */
    void forceAcquireForManualBackfill();
}
