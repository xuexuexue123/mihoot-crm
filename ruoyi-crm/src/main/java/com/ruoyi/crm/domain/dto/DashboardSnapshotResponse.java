package com.ruoyi.crm.domain.dto;

import com.ruoyi.crm.domain.dto.wecom.WecomCustomerTotalResponse;

/**
 * 企业仪表盘统一快照响应（按 metric_type 分组）
 * 后续扩展：storeVisit, wechatMpFans, dealRetention, communityReach 等
 */
public class DashboardSnapshotResponse
{
    private String snapshotDate;
    private WecomCustomerTotalResponse wecomCustomerTotal;

    public String getSnapshotDate()
    {
        return snapshotDate;
    }

    public void setSnapshotDate(String snapshotDate)
    {
        this.snapshotDate = snapshotDate;
    }

    public WecomCustomerTotalResponse getWecomCustomerTotal()
    {
        return wecomCustomerTotal;
    }

    public void setWecomCustomerTotal(WecomCustomerTotalResponse wecomCustomerTotal)
    {
        this.wecomCustomerTotal = wecomCustomerTotal;
    }
}
