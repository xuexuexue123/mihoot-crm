package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.dto.DashboardSnapshotResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomCustomerTotalResponse;

/**
 * 企业仪表盘统一快照读写服务
 */
public interface ICrmDashboardSnapshotService
{
    /**
     * UPSERT 当日客户总数快照
     */
    void saveCustomerTotalToday(String corpId, WecomCustomerTotalResponse data, String rawJson);

    /**
     * 读取当日客户总数快照（可能为 null）
     */
    WecomCustomerTotalResponse getCustomerTotalToday();

    /**
     * 读取当日所有指标快照
     */
    DashboardSnapshotResponse getTodaySnapshot();
}
