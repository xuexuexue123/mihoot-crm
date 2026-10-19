package com.ruoyi.crm.service;

import java.math.BigDecimal;

import com.ruoyi.crm.domain.CustomerMetrics;

/**
 * 客户聚合指标 Service 接口
 *
 * @author luohd
 * @date 2026-06-16
 */
public interface ICustomerMetricsService
{
    /**
     * 按 contactId 查询指标快照
     */
    CustomerMetrics getMetrics(String contactId);

    /**
     * 全量重算所有客户指标（供定时任务调用）
     */
    void recomputeAll();

    /**
     * 增量更新单个客户的指标
     *
     * @param contactId 客户ID
     * @param eventType 事件类型
     * @param channel   渠道
     * @param amount    金额（opportunity.won 时传入成交金额，其余事件传 null）
     */
    void incrementMetric(String contactId, String eventType, String channel, BigDecimal amount);
}
